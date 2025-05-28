# Usar uma imagem base do Maven para construir os microserviÃ§os
FROM maven:3-openjdk-17-slim AS build

# Definir o diretÃ³rio de trabalho
WORKDIR /app

# Copiar todo o conteÃºdo do projeto
COPY . .

# Definir o argumento de construÃ§Ã£o para escolher qual microserviÃ§o compilar
ARG SERVICE_NAME

# Construir o microserviÃ§o escolhido
RUN mvn clean install -pl microservices/${SERVICE_NAME} -am -DskipTests

# Usar uma imagem base do OpenJDK para rodar o microserviÃ§o
FROM openjdk:17-jdk

# Definir o argumento de construÃ§Ã£o para escolher qual microserviÃ§o compilar
ARG SERVICE_NAME
ENV SERVICE_NAME=${SERVICE_NAME}

# Definir o diretÃ³rio de trabalho
WORKDIR /app

# Copiar o JAR construÃ­do para o diretÃ³rio de trabalho
COPY --from=build /app/microservices/${SERVICE_NAME}/target/${SERVICE_NAME}-*.jar ${SERVICE_NAME}.jar

# Comando para rodar o microserviÃ§o escolhido
CMD ["sh", "-c", "java -jar ${SERVICE_NAME}.jar"]