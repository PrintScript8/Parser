# Usa la imagen base de OpenJDK
FROM openjdk:21-jdk-slim

# Establece el directorio de trabajo dentro del contenedor
WORKDIR /app

# Instala curl y descarga el agente de New Relic
RUN apt-get update && apt-get install -y curl && \
    curl --retry 5 --retry-delay 5 -o newrelic.jar https://download.newrelic.com/newrelic/java-agent/newrelic-agent/current/newrelic.jar && \
    apt-get clean && rm -rf /var/lib/apt/lists/*  # Limpieza después de la instalación

# Copia el archivo JAR de la aplicación al contenedor
COPY build/libs/parser-0.0.1-SNAPSHOT.jar app.jar

# Copia el archivo newrelic.yml al contenedor
COPY /src/main/resources/newrelic.yml /app/newrelic.yml

# Exponer el puerto 8080 para la aplicación
EXPOSE 8080

# Establece la variable de entorno NEW_RELIC_HOME para apuntar al directorio de configuración de New Relic
ENV NEW_RELIC_HOME=/app

# Establece la variable de entorno para la clave de licencia (puede sobrescribirse en docker-compose.yml)
ENV NEW_RELIC_LICENSE_KEY=${NEW_RELIC_LICENSE_KEY}

# Ejecuta la aplicación con el agente de New Relic
CMD ["java", "-javaagent:/app/newrelic.jar", "-Dnewrelic.config.file=/app/newrelic.yml", "-jar", "app.jar"]
