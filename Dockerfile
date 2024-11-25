FROM spark:3.5.2

WORKDIR /app

COPY target/scala-2.13/weather-data-processor-assembly-0.1.jar /app/

CMD ["spark-submit", "--class", "org.goraj.weatherapp.wdp.Entrypoint", "--master", "local[4]", "/app/weather-data-processor-assembly-0.1.jar"]
