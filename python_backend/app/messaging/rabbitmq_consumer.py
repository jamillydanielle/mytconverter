import asyncio
import aio_pika
import json
import os
import yt_dlp

from app.database import database
from app.crud import update_conversion_details
from app.storage.s3 import upload_file_to_s3

DOWNLOAD_DIR = "downloads"
os.makedirs(DOWNLOAD_DIR, exist_ok=True)

AUDIO_FORMATS = ['mp3', 'm4a', 'aac', 'wav', 'opus']
VIDEO_FORMATS = ['mp4', 'webm', 'mkv']

RABBITMQ_URL = "amqp://guest:guest@rabbitmq/"

async def handle_message(message: aio_pika.IncomingMessage):
    async with message.process():
        data = json.loads(message.body.decode())

        conversion_id = data["id"]
        url = data["url"]
        format_requested = data["format"]

        output_template = os.path.join(DOWNLOAD_DIR, f"{conversion_id}.%(ext)s")

        if format_requested in AUDIO_FORMATS:
            ydl_opts = {
                'format': 'bestaudio/best',
                'noplaylist': True,
                'postprocessors': [{
                    'key': 'FFmpegExtractAudio',
                    'preferredcodec': format_requested,
                    'preferredquality': '192',
                }],
                'outtmpl': output_template,
            }
        else:
            ydl_opts = {
                'format': 'bestvideo+bestaudio/best',
                'noplaylist': True,
                'merge_output_format': format_requested,
                'outtmpl': output_template,
                'postprocessors': [{
                    'key': 'FFmpegMerger'
                }],
            }


        try:
            print(f"⏬ Iniciando download: {url} ({format_requested})")
            with yt_dlp.YoutubeDL(ydl_opts) as ydl:
                info_dict = ydl.extract_info(url, download=True)

            # Pegando o título do vídeo e a duração
            video_title = info_dict.get("title", "Unknown Title")
            duration = info_dict.get("duration", 0)  # Em segundos

            downloaded_files = os.listdir(DOWNLOAD_DIR)
            matching_files = [f for f in downloaded_files if str(conversion_id) in f]

            if not matching_files:
                raise Exception("Arquivo não encontrado após download.")

            file_path = os.path.join(DOWNLOAD_DIR, matching_files[0])
            internal_file_name = matching_files[0]

            # Envia para o S3
            upload_file_to_s3(file_path, internal_file_name)

            # Atualiza o nome do arquivo interno no banco de dados
            await update_conversion_details(conversion_id, internal_file_name, video_title, duration)

            # Remove o arquivo local
            os.remove(file_path)

            print(f"✅ Upload para S3 finalizado: {internal_file_name}")

        except Exception as e:
            print(f"❌ Erro no download {conversion_id}: {e}")

async def main():
    await database.connect()
    connection = await aio_pika.connect_robust(RABBITMQ_URL)
    channel = await connection.channel()
    queue = await channel.declare_queue("download_queue", durable=True)

    await queue.consume(handle_message)
    print("🎧 Worker escutando a fila 'download_queue'...")
    await asyncio.Future()  # loop eterno

if __name__ == "__main__":
    asyncio.run(main())
