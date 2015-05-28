package org.scalex
package search

import scala.util.{ Try, Success, Failure }

import akka.actor.{ActorSystem, ActorRef, Props}
import akka.pattern.ask
import scalaz.\/

final class Search(system: ActorSystem) {

  private implicit val timeout = makeTimeout.veryLarge

  private val actor: ActorRef = system.actorOf(Props(
    new SearchActor(Env.defaultConfig)
  ), name = "search")

  def apply(expression: String): Fu[String \/ result.Results] = {
    println("Search for \"%s\"" format expression)
    actor ? expression mapTo manifest[String \/ result.Results] 
  }
}
