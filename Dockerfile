# ===============================
# Etapa 1: Construir la aplicación
# ===============================
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copiamos el pom y luego el código
COPY pom.xml .
COPY src ./src

# Compilamos el proyecto y generamos el JAR
RUN mvn -DskipTests clean package

# ===============================
# Etapa 2: Ejecutar la aplicación
# ===============================
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copiamos el JAR construido desde la etapa anterior
COPY --from=build /app/target/*.jar app.jar

# Render pasará el puerto en la variable PORT y ya lo tienes configurado en application.properties
EXPOSE 8080

# Permite usar JAVA_OPTS para tunear la JVM si algún día quieres
ENV JAVA_OPTS=""

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
