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

from .Auth import verify_token
from .Auth import JWT_SECRET_KEY

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

app = FastAPI()

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

BASE_DIR = os.path.dirname(os.path.abspath(__file__))
DOWNLOAD_FOLDER = os.path.join(BASE_DIR, "downloads")
os.makedirs(DOWNLOAD_FOLDER, exist_ok=True)

@app.get("/")
def read_root(user: dict = Depends(verify_token)):
    return {"message": "API de Conversão de Vídeos"}

@app.post("/converter/download")
async def download_video(request: Request, user: dict = Depends(verify_token)):
        
    data = await request.json()
    url = data.get("url")
    format_type = data.get("format", "mp3")
    
    if not url:
        raise HTTPException(status_code=400, detail="URL é obrigatória")
    
    logging.info(f"Iniciando download da URL: {url} no formato: {format_type}")
    
    unique_id = uuid.uuid4().hex
    output_template = os.path.join(DOWNLOAD_FOLDER, f"{unique_id}.%(ext)s")
    print ('caminho do arquivo:' + output_template)

    ydl_opts = {
        'outtmpl': output_template,
        'quiet': False,
        'no_warnings': False,
        'ffmpeg_location': None, 
        'verbose': True,
    }
    
    if format_type == 'mp3':
        ydl_opts.update({
            'format': 'bestaudio/best',
            'postprocessors': [{
                'key': 'FFmpegExtractAudio',
                'preferredcodec': 'mp3',
                'preferredquality': '192',
            }],
        })
    elif format_type == "mp4":
        ydl_opts['format'] = 'bestvideo+bestaudio/best'
        ydl_opts['videoformat'] = 'mp4'
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
            
            # Procurar especificamente pelo arquivo com o unique_id e a extensão correta
            file_found = False
            file_path = None
            
            # Para mp3, procuramos especificamente por arquivos .mp3
            if format_type.lower() == 'mp3':
                expected_file = f"{unique_id}.mp3"
                file_path = os.path.join(DOWNLOAD_FOLDER, expected_file)
                if os.path.exists(file_path):
                    file_found = True
                    internal_filename = expected_file
            else:
                # Para mp4, procuramos por arquivos .mp4
                expected_file = f"{unique_id}.mp4"
                file_path = os.path.join(DOWNLOAD_FOLDER, expected_file)
                if os.path.exists(file_path):
                    file_found = True
                    internal_filename = expected_file
            
            # Se não encontramos o arquivo com a extensão esperada, procuramos por qualquer arquivo com o unique_id
            if not file_found:
                for filename in os.listdir(DOWNLOAD_FOLDER):
                    if filename.startswith(unique_id):
                        file_path = os.path.join(DOWNLOAD_FOLDER, filename)
                        file_found = True
                        
                        # Se for mp3 mas o arquivo não tem extensão .mp3, renomeamos
                        if format_type.lower() == 'mp3' and not filename.endswith('.mp3'):
                            new_file_path = os.path.join(DOWNLOAD_FOLDER, f"{unique_id}.mp3")
                            os.rename(file_path, new_file_path)
                            file_path = new_file_path
                            internal_filename = f"{unique_id}.mp3"
                        else:
                            internal_filename = filename
                        break
            
            if not file_found:
                logging.error("Arquivo não encontrado após o download")
                raise HTTPException(status_code=500, detail="Arquivo não encontrado após o download")
            
            if format_type.lower() == 'mp3':
                download_filename = f"{video_title}.mp3"
            else:
                download_filename = f"{video_title}.mp4"
            
            logging.info(f"Download concluído: {video_title}")
            
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

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8081)