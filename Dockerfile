
FROM eclipse-temurin:17-jdk-jammy

# Directorio de trabajo en el contenedor
WORKDIR /app

# Copia el JAR de tu proyecto (generado por Maven/Gradle)
COPY target/*.jar app.jar

# Puerto expuesto (debe coincidir con server.port de tu aplicación)
EXPOSE 10000

# Comando para ejecutar la app
ENTRYPOINT ["java", "-jar", "app.jar"]