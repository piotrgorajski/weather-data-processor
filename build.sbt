name := "Weather Data Processor"

version := "0.1"

scalaVersion := "2.13.15"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-sql" % "3.5.3",
  "org.apache.hadoop" % "hadoop-common" % "3.3.6",
  "org.apache.hadoop" % "hadoop-aws" % "3.3.6",
  "com.github.scopt" %% "scopt" % "4.1.0",
  "org.scalatest" %% "scalatest" % "3.2.19" % Test,
  "com.holdenkarau" %% "spark-testing-base" % "3.5.1_1.5.3" % Test
)
