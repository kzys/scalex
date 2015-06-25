package org.scalex
package cli

import com.beust.jcommander.{Parameters, ParameterException, JCommander, Parameter}

object Main {
  class GeneralConfig {
    @Parameter(names = Array("--help"))
    var help: Boolean = false
  }

  @Parameters(commandNames = Array("http"))
  class HttpConfig {
    @Parameter(names = Array("--port"))
    var port: Int = 0
  }

  @Parameters(commandNames = Array("index"))
  class IndexConfig {
    @Parameter(names = Array("--name"))
    var name: String = ""

    @Parameter(names = Array("--version"))
    var version: String = ""

    @Parameter(names = Array("--directory"))
    var directory: String = ""
  }

  def main(args: Array[String]): Unit = {
    val config = new GeneralConfig
    val parser = new JCommander
    parser.addObject(config)

    val httpConfig = new HttpConfig
    parser.addCommand(httpConfig)

    val indexConfig = new IndexConfig
    parser.addCommand(indexConfig)

    try {
      parser.parse(args: _*)
    } catch {
      case e: ParameterException =>
        println(e.getLocalizedMessage)
        println(parser.usage)
        sys.exit(1)
    }

    parser.getParsedCommand match {
      case "index" =>
        index Indexer api.Index(
          indexConfig.name, indexConfig.version, Some("http://www.scala-lang.org/api/2.10.3"),
          List("-input-dir", indexConfig.directory)
        )
      case "http" =>
        (new server.Server).run(httpConfig.port)
      case _ => ???
    }
  }
}
