package org.goraj.weatherapp.wdp

import com.holdenkarau.spark.testing.DataFrameSuiteBase
import org.scalatest.funsuite.AnyFunSuite

import java.time.{LocalDate, LocalDateTime}

case class WeatherData(country: String,
                       city: String,
                       device_id: String,
                       reading_ts: LocalDateTime,
                       temperature: Double,
                       humidity: Double
                      )

case class AirQualityData(country: String,
                          city: String,
                          device_id: String,
                          reading_ts: LocalDateTime,
                          pm10: Integer,
                          pm25: Integer
                         )

case class CombinedData(country: String,
                        city: String,
                        reading_date: LocalDate,
                        reading_hour: Integer,
                        avg_temperature: Double,
                        avg_humidity: Double,
                        avg_pm10: Double,
                        avg_pm25: Double
                       )

class ApplicationTest extends AnyFunSuite with DataFrameSuiteBase {

  import spark.implicits._

  test("Transform should aggregate inputs by location and hour and join together") {
    //given
    val weatherDf = Seq(
      WeatherData("Poland", "Lodz", "W001", LocalDateTime.of(2020, 7, 10, 15, 0, 0), 5.8, 0.2),
      WeatherData("Poland", "Lodz", "W001", LocalDateTime.of(2020, 7, 10, 15, 15, 0), 6.8, 0.1),
      WeatherData("Poland", "Lodz", "W001", LocalDateTime.of(2020, 7, 10, 16, 0, 0), 5.7, 0.4),
      WeatherData("Poland", "Kielce", "W028", LocalDateTime.of(2020, 7, 10, 15, 0, 0), 4.7, 0.21),
    ).toDF()

    val airQualityDf = Seq(
      AirQualityData("Poland", "Lodz", "W001", LocalDateTime.of(2020, 7, 10, 15, 0, 0), 44, 13),
      AirQualityData("Poland", "Lodz", "W001", LocalDateTime.of(2020, 7, 10, 14, 0, 0), 84, 43),
      AirQualityData("Poland", "Lodz", "W001", LocalDateTime.of(2020, 7, 10, 14, 5, 0), 18, 23),
      AirQualityData("Poland", "Kielce", "W028", LocalDateTime.of(2020, 7, 10, 15, 0, 0), 21, 17),
    ).toDF()

    //when
    val app = new Application(Config(processingDate = LocalDate.of(2020, 7, 10)))
    val actualDf = app.transform(weatherData = weatherDf, airQualityData = airQualityDf)

    //then
    val expectedDf = Seq(
      CombinedData("Poland", "Lodz", LocalDate.of(2020, 7, 10), 15, 6.3, 0.15000000000000002, 44.0, 13.0),
      CombinedData("Poland", "Kielce", LocalDate.of(2020, 7, 10), 15, 4.7, 0.21, 21.0, 17.0)
    ).toDF()
    assertDataFrameDataEquals(expectedDf, actualDf)
  }
}
