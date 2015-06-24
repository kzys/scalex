package org.scalex
package elastic

import scala.concurrent.duration._
import scala.concurrent.{ Future, Await }
import scala.util.{ Try, Success, Failure }

import akka.actor._
import akka.pattern.{ ask, pipe }
import com.sksamuel.elastic4s.ElasticClient
import com.sksamuel.elastic4s.{ ElasticDsl ⇒ ES }
import com.sksamuel.elastic4s.ElasticDsl._
import com.typesafe.config.Config
import org.elasticsearch.action.search.SearchResponse
import play.api.libs.json.{ Json, JsObject }

import api._
import util.Timer._

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
        case (id, source) ⇒
          ES.index into s"$indexName/$typeName" fields("id" -> id, "source" -> source)
      }
      client execute { bulk(commands: _*) }

    case search: ES.SearchDefinition ⇒ execute(client execute search, sender)

    case count: ES.CountDefinition ⇒ {
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
