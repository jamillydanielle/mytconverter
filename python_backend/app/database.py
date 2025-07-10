from databases import Database
from app.core.settings import settings

# URL do banco de dados configurada
DATABASE_URL = settings.DATABASE_URL

# Banco de dados assíncrono para FastAPI
database = Database(DATABASE_URL)
