package org.scalex
package elastic

import com.sksamuel.elastic4s.source.JsonDocumentSource

import scala.concurrent.{ Future }
import scala.util.{ Success, Failure }

import akka.actor._
import akka.pattern.{ pipe }
import com.sksamuel.elastic4s.{ElasticDsl ⇒ ES, CountDefinition, ElasticClient, SearchDefinition}
import com.sksamuel.elastic4s.ElasticDsl._
import com.typesafe.config.Config
import play.api.libs.json.{JsString, Json, JsObject}

private[scalex] final class ElasticActor(
    config: Config,
    indexName: String,
    indexSettings: Map[String, String]) extends Actor with ActorLogging {

  import ElasticActor._

  var client: ElasticClient = _

  override def preStart {
    client = instanciateElasticClient
    self ! EnsureIndex
  }

  override def postStop {
    println("[search] Stop elastic client")
    Option(client) foreach { _.close() }
  }

  def receive = {

    case EnsureIndex ⇒ client execute {
      ES.create index indexName // mappings (typeName as mapping)
    }

    case api.Clear(typeName, mapping) ⇒ Future {
      try {
        client execute { ES.delete from s"$indexName/typeName" where ES.matchall }
      }
      catch {
        case e: org.elasticsearch.indices.TypeMissingException ⇒
      }
      // TODO set type mappings
    }

    case api.Optimize ⇒ client execute { ES optimize indexName } pipeTo sender

    case api.IndexMany(typeName, docs) ⇒
      val commands = docs map {
        case (id, source) ⇒ {
          val json = JsObject(Seq("id" -> JsString(id), "source" -> source))
          ES.index into s"$indexName/$typeName" doc (JsonDocumentSource(Json.stringify(json)))
        }
      }
      client execute { bulk(commands: _*) }

    case search: SearchDefinition ⇒ execute(client execute search, sender)

    case count: CountDefinition ⇒ {
      val replyTo = sender
      client execute count onComplete {
        case Success(response) ⇒ replyTo ! response.getCount.toInt
        case Failure(_: org.elasticsearch.indices.TypeMissingException) ⇒ sender ! 0
        case Failure(exception) ⇒ throw exception
      }
    }
  }

  private def execute[A](action: Future[A], replyTo: ActorRef) {
    action onComplete {
      case Success(response)  ⇒ replyTo ! response
      case Failure(exception) ⇒ throw exception
    }
  }

  private def instanciateElasticClient = {
    println("[search] Start elastic client")
    // Or ElasticClient.local
    val client = ElasticClient.remote(config getString "host", config getInt "port")
    client ~ { c ⇒
      try {
        // TODO what about indexSettings??
        c execute { ES.create index indexName }
      }
      catch {
        case e: org.elasticsearch.indices.IndexAlreadyExistsException ⇒
      }
      println("[search] Elastic client running")
    }
  }
}

private[elastic] object ElasticActor {

  case object EnsureIndex
}
