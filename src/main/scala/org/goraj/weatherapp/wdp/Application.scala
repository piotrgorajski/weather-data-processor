package org.goraj.weatherapp.wdp

import org.apache.spark.sql.functions.{avg, col, hour}
import org.apache.spark.sql.types.DateType
import org.apache.spark.sql.{DataFrame, Dataset, RelationalGroupedDataset, Row, SparkSession}

class Application(config: Config) {

  def run(): Unit = {
    println(s"Running application with processing date: ${config.processingDate}")
    val spark: SparkSession = getOrCreateSparkSession
    val weatherData = spark.read.option("header", true).option("inferSchema", true).csv("data/weather.csv")
    val airQualityData = spark.read.option("header", true).option("inferSchema", true).csv("data/air-quality.csv")
    val outputData = transform(weatherData, airQualityData)
    writeOutputData(outputData)
    spark.close()
  }

  private def getOrCreateSparkSession: SparkSession = {
    val spark = SparkSession.builder()
      .appName("Weather Data Processor")
      .master("local[*]").getOrCreate()
    spark
  }

  def transform(weatherData: DataFrame, airQualityData: DataFrame): DataFrame = {
    val hourlyWeatherData: Dataset[Row] = getHourlyWeatherData(weatherData)
    val hourlyAirQualityData: Dataset[Row] = getHourlyAirQualityData(airQualityData)
    hourlyWeatherData.join(hourlyAirQualityData, Seq("country", "city", "reading_date", "reading_hour"))
  }

  private def getHourlyWeatherData(weatherData: DataFrame): DataFrame = {
    weatherData
      .transform(filterByProcessingDate)
      .transform(df =>
        groupByCountryCityAndHour(df)
          .agg(avg("temperature").as("avg_temperature"), avg("humidity").as("avg_humidity"))
      )
  }

  private def getHourlyAirQualityData(airQualityData: DataFrame): DataFrame = {
    airQualityData
      .transform(filterByProcessingDate)
      .transform(df =>
        groupByCountryCityAndHour(df)
          .agg(avg("pm10").as("avg_pm10"), avg("pm25").as("avg_pm25"))
      )
  }

  private def filterByProcessingDate(df: DataFrame): DataFrame = {
    df.filter(col("reading_ts").cast(DateType).equalTo(config.processingDate))
  }

  private def groupByCountryCityAndHour(df: DataFrame): RelationalGroupedDataset = {
    df
      .withColumn("reading_date", col("reading_ts").cast(DateType))
      .withColumn("reading_hour", hour(col("reading_ts")))
      .groupBy("country", "city", "reading_date", "reading_hour")
  }

  private def writeOutputData(outputData: DataFrame): Unit = {
    val filePath = "s3a://output-bucket/data"
    outputData.write.mode("overwrite").json(filePath)
  }
}
