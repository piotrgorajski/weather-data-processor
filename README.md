# weather-data-processor
Process weather data (temperature, humidity and air quality).

## Local Setup

### Component versions:
- Java 11
- Spark 3.5.3
- Hadoop 3.3.6

### Prerequisites

#### Java
1. Install Java 11
2. Set JAVA_HOME
3. Add JAVA_HOME/bin to PATH

#### Spark
1. Download Spark 3.5.3 for Hadoop 3 and Scala 2.12 (https://spark.apache.org/downloads.html)
2. Set SPARK_HOME
3. Add SPARK_HOME/bin to PATH

#### Hadoop
1. From https://github.com/cdarlint/winutils download (hadoop-3.3.6/bin):
    - winutils.exe
    - hadoop.dll
2. Move files to C:\hadoop\bin
3. Set HADOOP_HOME
4. Add HADOOP_HOME/bin to PATH

#### Docker (to run app in a container)
1. Install Docker engine

### Building the app

#### Uber JAR
`sbt assembly`

#### Docker image
`docker build -t weather-data-processor .`

### Running the app

#### On local host
```
docker run --rm -it --network spark-minio-network weather-data-processor \
--master local[*] \
--name weather-data-processor \
--class org.goraj.weatherapp.wdp.Entrypoint \
/app/weather-data-processor-assembly-0.1.jar \
-d 2020-07-10
```

#### In Docker
`docker compose up -d`
