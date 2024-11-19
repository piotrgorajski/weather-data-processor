# weather-data-processor
Process weather data (temperature, humidity and air quality)

## Local Setup

### Component versions:
- Spark 3.5.3
- Hadoop 3.3.6
- Java 11

### Hadoop
Download winutils.exe and hadoop.dll

Place under hadoop_home/bin

### MinIO

Start MinIO:
`docker run -d --name minio \
-p 9000:9000 -p 9001:9001 \
-e "MINIO_ROOT_USER=admin" \
-e "MINIO_ROOT_PASSWORD=password" \
minio/minio server /data --console-address ":9001"`