**Context**

The first iteration of the local setup assumes that the `GHS` application  is running as a standalone but outside of
docker engine.

**How to run GHS application**
In order to run the `GHS` application, follow the next steps:

* Build artefact
```
sbt assembly
```

* Run the application
```
java -jar target/scala-2.13/GHS-assembly-0.1.0-SNAPSHOT.jar
```

To test the application is working and publishing metrics:
```
curl -v  -H "Authorization: Bearer testToken" http://127.0.0.1:9095/metrics
```
**How to run Prometheus and Grafana**

Run the command `docker-compose up` to run `Prometheus` and `Graphana`. `Prometheus` is accesible in
http://localhost:9090 and Graphana is accessible in http://localhost:3000 with user `admin` and password `admin`.

In order to see metrics in Graphana, a new `Prometheus` datasource needs to be created and the
`Prometheus Settings/HTTP/Prometheus server URL` should be `http://prometheus:9090`.

**What is next**

Dockerize the `GHS` application and added in the docker-compose.yaml.

