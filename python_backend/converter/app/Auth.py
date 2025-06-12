from dotenv import load_dotenv
from fastapi import Depends, HTTPException
from fastapi.security import OAuth2PasswordBearer
import jwt
import os
import logging
import json

load_dotenv ()


logging.basicConfig(
    level=logging.DEBUG,
    format='%(asctime)s - %(levelname)s - %(message)s',
    datefmt='%Y-%m-%d %H:%M:%S'
)

DB_HOST = os.environ.get("DB_HOST")
DB_PORT = os.environ.get("DB_PORT")
DATABASE_NAME = os.environ.get("DATABASE_NAME")
DB_USER = os.environ.get("DB_USER")
DB_PASS = os.environ.get("DB_PASS")
JWT_SECRET_KEY = os.environ.get("JWT_SECRET_KEY")
JWT_ISSUER = os.environ.get("JWT_ISSUER")

ALGORITHMS = ["HS256"]

# OAuth2 Password Bearer
oauth2_scheme = OAuth2PasswordBearer(tokenUrl="token")

async def verify_token(token: str = Depends(oauth2_scheme)):
    try:
        # Decodificar o token
        payload = jwt.decode(
            token,
            JWT_SECRET_KEY,
            algorithms=ALGORITHMS,
            issuer=JWT_ISSUER
        )
        
        
        
        # Obter o campo 'user' que é uma string JSON
        user_json_str = payload.get('user')
        if not user_json_str:
            raise HTTPException(status_code=401, detail="Dados do usuário não encontrados no token")
        
        # Converter a string JSON para um objeto Python
        try:
            user_data = json.loads(user_json_str)
        except json.JSONDecodeError as e:
            raise HTTPException(status_code=401, detail="Formato inválido dos dados do usuário")
        
        # Extrair o objeto 'user' do objeto decodificado
        user = user_data.get('user')
        if not user:
            raise HTTPException(status_code=401, detail="Estrutura de dados do usuário inválida")
        
        
        # Extrair ID do usuário
        user_id = user.get('id')
        if user_id is None:
            raise HTTPException(status_code=401, detail="ID do usuário não encontrado")
        
        # Verificar se o usuário está ativo
        is_active = user.get('active')
        if is_active is not True:
            raise HTTPException(status_code=403, detail="Usuário não está ativo")
        
        # Verificar se o usuário está desativado
        deactivated_at = user.get('deactivatedAt')
        if deactivated_at is not None:
            raise HTTPException(status_code=403, detail="Usuário está desativado")
        
        return user
        
    except jwt.ExpiredSignatureError:
        raise HTTPException(status_code=401, detail="Token expirado")
    except jwt.InvalidTokenError as e:
        raise HTTPException(status_code=401, detail=f"Token inválido: {str(e)}")
    except HTTPException as e:
        # Repassar exceções HTTP
        raise e
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Erro interno ao verificar token: {str(e)}")