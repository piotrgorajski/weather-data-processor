package org.goraj.weatherapp.wdp

import org.apache.spark.sql.SparkSession

class Application(config: Config) {

  def run(): Unit = {
    println(s"Running application with processing date: ${config.processingDate}")
    val spark: SparkSession = getOrCreateSparkSession

    val weatherData = spark.read.option("header", true).csv("data/weather.csv")
    println(s"We have ${weatherData.count()} records in our weather data")
    weatherData.show(truncate = false)

    val bucket = "raw-data"
    val filePath = s"s3a://$bucket/weather-data"

    weatherData.write.mode("overwrite").json(filePath)

    spark.close()
  }

  private def getOrCreateSparkSession = {
    val spark = SparkSession.builder()
      .appName("Weather Data Processor")
      .config("spark.hadoop.fs.s3a.access.key", "admin")
      .config("spark.hadoop.fs.s3a.secret.key", "password")
      .config("spark.hadoop.fs.s3a.endpoint", "http://localhost:9000")
      .config("spark.hadoop.fs.s3a.path.style.access", "true")
      .master("local[*]").getOrCreate()
    spark
  }
}
