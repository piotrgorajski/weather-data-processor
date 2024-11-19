package org.goraj.weatherapp.wdp

import scopt.OParser

import java.time.LocalDate
import java.time.format.{DateTimeFormatter, DateTimeParseException}
import scala.sys.exit

case class Config(processingDate: LocalDate = LocalDate.now())


object Entrypoint {
  def main(args: Array[String]): Unit = {
    val argParser: OParser[Unit, Config] = prepareArgParser
    OParser.parse(argParser, args, Config()) match {
      case Some(config) =>
        println(s"Config: $config")
        val app = new Application(config)
        app.run()
      case _ =>
        println("Failed to parse arguments.")
        exit(1)
    }
  }

  private def prepareArgParser = {
    val builder = OParser.builder[Config]
    val argParser = {
      import builder._
      OParser.sequence(
        programName("Weather Data Processor"),
        head("wdp", "0.1"),
        opt[String]('d', "date")
          .required()
          .valueName("<yyyy-MM-dd>")
          .action((x, c) =>
            try {
              c.copy(processingDate = LocalDate.parse(x, DateTimeFormatter.ISO_LOCAL_DATE))
            } catch {
              case _: DateTimeParseException =>
                throw new IllegalArgumentException(s"Invalid date format: $x. Expected format is yyyy-MM-dd.")
            }
          )
          .text("Processing date in yyyy-MM-dd format is required.")
      )
    }
    argParser
  }
}
