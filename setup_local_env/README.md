**Context**

The first iteration of the local setup assumes that the `GHS` application is running as a standalone but outside of
docker engine.

**How to run GHS application**
In order to run the `GHS` application, follow the next steps:

* Build artefact
```
sbt assembly
```

* Run the application
```
find . -name "GHS-assembly*.jar" | xargs java -jar
```

To test the application is working and publishing metrics:
```
curl -v  -H "Authorization: Bearer testToken" http://localhost:9095/metrics
```
**How to run Prometheus and Grafana**

Run the command `docker compose up` to run `Prometheus` and `Graphana`. `Prometheus` is accessible in
http://localhost:9090 and Graphana is accessible in http://localhost:3000 with user `admin` and password `admin`.

In order to see metrics in Graphana, a new `Prometheus` datasource needs to be created and the
`Prometheus Settings/HTTP/Prometheus server URL` should be `http://prometheus:9090`.

**What is next**

- Dockerize the `GHS` application and added in the docker-compose.yaml.
- Provide basic dashboard to visualise `Git` metrics and `Filesystem` metrics.
