from fastapi import Depends, HTTPException
from fastapi.security import OAuth2PasswordBearer
import jwt
import os
import json

# Configurações de ambiente
JWT_SECRET_KEY = os.getenv("JWT_SECRET_KEY")
JWT_ISSUER = os.getenv("JWT_ISSUER")
ALGORITHMS = ["HS256"]

oauth2_scheme = OAuth2PasswordBearer(tokenUrl="token")

async def verify_token(token: str = Depends(oauth2_scheme)):
    try:
        payload = jwt.decode(
            token,
            JWT_SECRET_KEY,
            algorithms=ALGORITHMS,
            issuer=JWT_ISSUER
        )
        
        
        user_json_str = payload.get('user')
        if not user_json_str:
            raise HTTPException(status_code=401, detail="Dados do usuário não encontrados no token")
        
        try:
            user_data = json.loads(user_json_str)
        except json.JSONDecodeError as e:
            raise HTTPException(status_code=401, detail="Formato inválido dos dados do usuário")
        
        user = user_data.get('user')
        if not user:
            raise HTTPException(status_code=401, detail="Estrutura de dados do usuário inválida")
        
        user_id = user.get('id')
        if user_id is None:
            raise HTTPException(status_code=401, detail="ID do usuário não encontrado")
        
        is_active = user.get('active')
        if is_active is not True:
            raise HTTPException(status_code=403, detail="Usuário não está ativo")
        
        deactivated_at = user.get('deactivatedAt')
        if deactivated_at is not None:
            raise HTTPException(status_code=403, detail="Usuário está desativado")
        
        return user
        
    except jwt.ExpiredSignatureError:
        raise HTTPException(status_code=401, detail="Token expirado")
    except jwt.InvalidTokenError as e:
        raise HTTPException(status_code=401, detail=f"Token inválido: {str(e)}")
    except HTTPException as e:
        raise e
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Erro interno ao verificar token: {str(e)}")