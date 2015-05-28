package org.scalex
package cli

import scala.concurrent.duration._
import scala.concurrent.{ Future, Await }
import scala.util.{ Try, Success, Failure }

object Main {
  case class MainConfig(port: Int = -1)

  def main(args: Array[String]): Unit = {
    val parser = new scopt.OptionParser[MainConfig]("scalex") {
      opt[Int]("listen") action {
        (x, config) => config.copy(port = x)
      }
    }
    parser.parse(args, MainConfig()) match {
      case Some(config) if config.port != -1 => {
        (new server.Server).run(config.port)
        return
      }
      case None =>
        println("err")
    }

    sys exit {
      Await.result(process(args) map (_ ⇒ 0) recover {
        case e: IllegalArgumentException ⇒ {
          println("! %s: %s".format("Illegal argument", e.getMessage))
          1
        }
        case e: Exception ⇒ {
          println("! " + e)
          1
        }
      }, 1 hour)
    }
  }

  private def process(args: Array[String]): Fu[Unit] = (args.toList match {
    // TODO real option to set optional scaladoc url
    case "index" :: name :: version :: rest ⇒ Future {
      index Indexer api.Index(name, version, Some("http://www.scala-lang.org/api/2.10.3"), rest)
    }
  }).void
}
