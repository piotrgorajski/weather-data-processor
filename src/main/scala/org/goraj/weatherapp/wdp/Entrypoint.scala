package org.goraj.weatherapp.wdp

import org.apache.spark.sql.SparkSession

object Entrypoint {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().appName("Weather Data Processor").master("local[*]").getOrCreate()
    val weatherData = spark.read.option("header", true).csv("data/weather.csv")
    println(s"We have ${weatherData.count()} records in our weather data")
    weatherData.show(truncate = false)
    spark.close()
  }
}
