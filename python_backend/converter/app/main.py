from fastapi import FastAPI, HTTPException, Depends, File, UploadFile, Form, Query, Request
from fastapi.responses import FileResponse, JSONResponse
from fastapi.middleware.cors import CORSMiddleware
from typing import Optional, List
import os
import logging
import yt_dlp
import uuid
import shutil
from datetime import datetime

# Import Auth dependencies
from .Auth import verify_token  # Import the verify_token function
from .Auth import JWT_SECRET_KEY

# Configuração de logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

app = FastAPI()

# Configuração CORS
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # Permitir todas as origens
    allow_credentials=True,
    allow_methods=["*"],  # Permitir todos os métodos
    allow_headers=["*"],  # Permitir todos os cabeçalhos
)

# Configuração de pastas
BASE_DIR = os.path.dirname(os.path.abspath(__file__))
DOWNLOAD_FOLDER = os.path.join(BASE_DIR, "downloads")
os.makedirs(DOWNLOAD_FOLDER, exist_ok=True)

@app.get("/")
def read_root(user: dict = Depends(verify_token)):
    return {"message": "API de Conversão de Vídeos"}

@app.post("/converter/download")
async def download_video(request: Request, user: dict = Depends(verify_token)):
        
    # Obter dados do corpo da requisição
    data = await request.json()
    url = data.get("url")
    format_type = data.get("format", "mp3")
    
    if not url:
        raise HTTPException(status_code=400, detail="URL é obrigatória")
    
    logging.info(f"Iniciando download da URL: {url} no formato: {format_type}")
    
    # Gerar um nome de arquivo único para evitar conflitos
    unique_id = uuid.uuid4().hex
    output_template = os.path.join(DOWNLOAD_FOLDER, f"{unique_id}.%(ext)s")
    print ('caminho do arquivo:' + output_template)

    ydl_opts = {
        'outtmpl': output_template,
        'quiet': False,
        'no_warnings': False,
        'ffmpeg_location': None,  # Ajuste o caminho do FFmpeg
        'verbose': True,  # Adicionar logs detalhados
    }
    
    if format_type.lower() == 'mp3':
        ydl_opts.update({
            'format': 'bestaudio/best',
            'postprocessors': [{
                'key': 'FFmpegExtractAudio',
                'preferredcodec': 'mp3',
                'preferredquality': '192',
            }],
        })
    elif format_type.lower() == 'mp4':
        ydl_opts.update({
            'format': 'best[ext=mp4]',
        })
    else:
        logging.error(f"Formato não suportado: {format_type}")
        raise HTTPException(status_code=400, detail=f"Formato não suportado: {format_type}")
    
    try:
        logging.info("Iniciando o processo com yt-dlp...")
        with yt_dlp.YoutubeDL(ydl_opts) as ydl:
            info_dict = ydl.extract_info(url, download=True)
            
            if not info_dict:
                logging.error("Nenhuma informação retornada pelo yt-dlp")
                raise HTTPException(status_code=500, detail="Falha ao obter informações do vídeo")
            
            video_title = info_dict.get('title', 'video')
            
            # Encontrar o arquivo baixado
            downloaded_files = os.listdir(DOWNLOAD_FOLDER)
            if not downloaded_files:
                logging.error("Nenhum arquivo foi baixado")
                raise HTTPException(status_code=500, detail="Nenhum arquivo foi baixado")
            
            # Obter o primeiro arquivo na pasta de downloads
            downloaded_file = downloaded_files[0]
            file_path = os.path.join(DOWNLOAD_FOLDER, downloaded_file)
            
            # Verificar se o arquivo existe
            if not os.path.exists(file_path):
                logging.error(f"Arquivo não encontrado: {file_path}")
                raise HTTPException(status_code=500, detail="Arquivo não encontrado após o download")
            
            # Determinar o nome do arquivo para download
            if format_type.lower() == 'mp3':
                download_filename = f"{video_title}.mp3"
            else:
                download_filename = f"{video_title}.mp4"
            
            logging.info(f"Download concluído: {download_filename}")
            
            # Criar URL para download do arquivo
            internal_filename = f"{unique_id}.{format_type}"

            
            # Retornar link de download e nome do arquivo como JSON
            return JSONResponse(content={
                "data": {
                    "internal_filename": internal_filename,
                    "file_name": download_filename
                },
                "message": "Conversão concluída com sucesso"
            })
    
    except yt_dlp.utils.DownloadError as e:
        logging.error(f"Falha no download do yt-dlp para a URL: {url}")
        logging.error(f"Erro: {str(e)}")
        import traceback
        logging.error(f"Traceback: {traceback.format_exc()}")
        raise HTTPException(status_code=500, detail=f"Falha no download: {str(e)}")
    except Exception as e:
        logging.error(f"Erro inesperado: {str(e)}")
        import traceback
        logging.error(f"Traceback: {traceback.format_exc()}")
        raise HTTPException(status_code=500, detail=f"Erro inesperado: {str(e)}")

@app.get("/converter/file/{file_name}")
async def get_file(file_name: str, filename: Optional[str] = None, user: dict = Depends(verify_token)):
    file_path = os.path.join(DOWNLOAD_FOLDER, file_name)
    if not os.path.exists(file_path):
        raise HTTPException(status_code=404, detail="Arquivo não encontrado")
    
    return FileResponse(
        path=file_path,
        filename=filename or file_name,
        media_type='application/octet-stream'
    )

@app.post("/converter/converter/download")
async def create_convertion_record(request: Request, user: dict = Depends(verify_token)):
    # Endpoint para registrar uma conversão no banco de dados
    data = await request.json()
    
    # Aqui você implementaria a lógica para salvar no banco de dados
    # Por enquanto, apenas retornamos os dados recebidos
    return {
        "id": 1,  # ID fictício
        "url": data.get("url"),
        "format": data.get("format"),
        "file_name": data.get("file_name"),
        "user_id": user.get('id'),
        "created_at": datetime.now().isoformat()
    }

@app.get("/converter/converter/getConvertions")
async def get_convertions(page: int = Query(0), size: int = Query(10), user: dict = Depends(verify_token)):
    # Endpoint para buscar conversões do banco de dados
    # Por enquanto, retornamos dados fictícios
    return {
        "content": [
            {
                "id": 1,
                "url": "https://www.youtube.com/watch?v=example",
                "format": "mp3",
                "file_name": "Example Video.mp3",
                "user_id": user.get('id'),
                "created_at": datetime.now().isoformat()
            }
        ],
        "totalPages": 1,
        "totalElements": 1
    }

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8081)