package org.scalex
package server

import akka.actor.ActorSystem
import akka.http.scaladsl.model.{HttpEntity, ContentTypes, HttpResponse}
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.server.{RequestContext, Route}
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorFlowMaterializer
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes._

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.{Try, Success, Failure}

class Server {
  implicit val system = ActorSystem()
  implicit val materializer = ActorFlowMaterializer()

  val searcher = new search.Search(system)

  def handleSearch(ctx: RequestContext, q: String) = {
    import play.api.libs.json._
    import org.scalex.search.result.Writer.resultsWrites

    Try(Await.result(searcher(q), 20.second)) match {
      case Success(res) =>
        res.fold(
          err => ctx.complete(HttpResponse(BadRequest, entity = err.toString)),
          res => ctx.complete(HttpResponse(OK,
            entity = HttpEntity(ContentTypes.`application/json`, Json.toJson(res).toString)
          ))
        )
      case Failure(err) =>
        ctx.complete(HttpResponse(
          InternalServerError,
          entity = s"""Failure while searching for "$q": $err"""
        ))
    }
  }

  val route: Route = {
    get {
      parameterMap {
        map => map.get("q") match {
          case None =>
            _.complete(HttpResponse(BadRequest, entity = "Empty query"))
          case Some(query) => handleSearch(_, query)
        }
      }
    }
  }

  def run(port: Int) = {
    Http().bindAndHandle(route, "localhost", port)
  }
}
