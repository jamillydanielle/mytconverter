# Dockerfile para o FastAPI Backend

# Usar uma imagem oficial do Python como base
FROM python:3.11-slim

# Instala ffmpeg
RUN apt-get update && apt-get install -y ffmpeg curl && rm -rf /var/lib/apt/lists/*

# Define o diretório de trabalho dentro do container
WORKDIR /app

# Copia o requirements.txt para o container
COPY requirements.txt .

# Instala as dependências
RUN pip install --no-cache-dir -r requirements.txt

# Copia o restante da aplicação para dentro do container
COPY . .

# Ajusta as permissões de execução para os arquivos que precisam de execução
RUN chmod +x /app/app/messaging/rabbitmq_consumer.py

EXPOSE 8081

ENV PYTHONPATH=/app

# Comando para rodar o FastAPI com Uvicorn
CMD ["uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "8081", "--reload"]