# Imagen del front (Vaadin + Spring Boot). Construir desde esta carpeta:
#
#   docker build -t columbia-viajes-front .
#
# La URL del backend se pasa con la variable BACKEND_URL (Spring la toma como backend.url).
# Dentro de un contenedor, "localhost" es el propio contenedor: si el backend corre en la
# máquina host, usar host.docker.internal:
#
#   docker run -p 8081:8081 -e BACKEND_URL=http://host.docker.internal:8080/api \
#       --add-host=host.docker.internal:host-gateway columbia-viajes-front

FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY . .
# El paquete incluye el build de producción del frontend (goal build-frontend del vaadin-maven-plugin)
RUN --mount=type=cache,target=/root/.m2 ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
COPY --from=build /app/target/*.jar /app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app.jar"]
