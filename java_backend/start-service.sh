#!/bin/bash
set -e

echo "== Iniciando o serviço ${SERVICE_NAME} =="

if [ -z "${SERVICE_NAME}" ]; then
  echo "Erro: Variável SERVICE_NAME não está definida"
  exit 1
fi

echo "== Navegando para o diretório do microserviço ${SERVICE_NAME} =="
cd /app/microservices/${SERVICE_NAME}

echo "== Listando arquivos no diretório atual =="
ls -la

echo "== Executando o microserviço ${SERVICE_NAME} =="
mvn spring-boot:run