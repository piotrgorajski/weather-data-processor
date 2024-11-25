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
```
docker run -d --name minio \
-p 9000:9000 -p 9001:9001 \
-e "MINIO_ROOT_USER=admin" \
-e "MINIO_ROOT_PASSWORD=password" \
minio/minio server /data --console-address ":9001"
```

### Kubernetes setup

1. Start Minikube. Spark requires more resources than Minikube's defaults:
    `minikube start --cpus=4 --memory=8g`
2. Enable Docker in Minikube:
    `eval $(minikube docker-env)`
3. Build a Docker Image for Your Spark Application
   1. Write a Dockerfile to include your Spark application:
    ```
    FROM bitnami/spark:latest
    COPY target/scala-2.13/my-spark-app_2.13-1.0.jar /app/my-spark-app.jar
    ENTRYPOINT ["/opt/bitnami/spark/bin/spark-submit"]
   ```
   2. Build the Docker image:
    `docker build -t my-spark-app:latest .`
4. Create service account for spark:
    `kubectl create serviceaccount spark-sa`
5. Grant service account a ClusterRole:
    `kubectl create clusterrolebinding spark-role --clusterrole=edit --serviceaccount=default:spark-sa --namespace=default`
6. Submit spark application:
    ```
   ./bin/spark-submit \
    --master k8s://https://localhost:32786 \
    --deploy-mode cluster \
    --name spark-pi \
    --class org.apache.spark.examples.SparkPi \
    --conf spark.executor.instances=2 \
    --conf spark.kubernetes.container.image=spark:local \
    --conf spark.kubernetes.context=minikube \
    --conf spark.kubernetes.namespace=default \
    --conf spark.kubernetes.authenticate.driver.serviceAccountName=spark-sa \
    local:///opt/spark/examples/jars/spark-examples_2.12-3.5.3.jar
   ```
7. Monitor the app:
   1. Check running pods:
   `kubectl get pods`
   2. View logs of the Spark driver:
  `kubectl logs <spark-driver-pod-name>`
   3. Access Spark UI:
   `kubectl port-forward <driver-pod-name> 4040:4040`
