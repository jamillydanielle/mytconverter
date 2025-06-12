FROM python:3.12-alpine

ARG APP_NAME=converter
WORKDIR /app

RUN apk update && apk add --no-cache \
    gcc \
    musl-dev \
    postgresql-dev \
    python3-dev \
    ffmpeg

RUN ffmpeg -version && ffprobe -version

RUN pip install --no-cache-dir poetry

COPY ${APP_NAME}/pyproject.toml ${APP_NAME}/poetry.lock* ./

RUN poetry config virtualenvs.create false

RUN poetry install --no-interaction

COPY ${APP_NAME} ./

EXPOSE 8081

CMD ["uvicorn", "converter.app.main:app", "--host", "0.0.0.0", "--port", "8081", "--reload"]