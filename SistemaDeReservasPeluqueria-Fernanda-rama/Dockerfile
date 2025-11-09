# Imagen base estable de Java 21 (oficial Eclipse Temurin)
FROM eclipse-temurin:21-jdk

# Directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiamos el archivo JAR generado por Maven
ARG JAR_FILE=target/reservas-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app_peluqueria.jar

# Exponemos el puerto del backend
EXPOSE 8080

# Comando que ejecutará el contenedor
ENTRYPOINT ["java", "-jar", "app_peluqueria.jar"]
