FROM piotrgorajski/spark-minio-local:latest

WORKDIR /app

COPY target/scala-2.12/weather-data-processor-assembly-0.1.jar /app/
COPY data /app/data/
