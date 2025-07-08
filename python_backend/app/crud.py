from app.models import Conversion
from app.database import database
from sqlalchemy.future import select

from datetime import datetime

async def create_conversion(user_id: str, youtube_url: str, format: str, youtube_video_name: str):
    query = Conversion.__table__.insert().values(
        user_id=user_id,
        youtube_url=youtube_url,
        format=format,
        youtube_video_name=youtube_video_name,
        internal_file_name= "",
        created_at=datetime.utcnow(),
        length=0,  # Placeholder for length, can be updated later
    )
    return await database.execute(query)

async def update_conversion_details(conversion_id: str, internal_file_name: str, youtube_video_name: str, length: int):
    query = select(Conversion).where(Conversion.id == conversion_id)
    result = await database.fetch_one(query)
    
    if result:
        update_query = (
            Conversion.__table__
            .update()
            .where(Conversion.id == conversion_id)
            .values(
                internal_file_name=internal_file_name,
                youtube_video_name=youtube_video_name,
                length=length
            )
        )
        await database.execute(update_query)

async def get_conversion_by_file_name(file_name: str):
    query = select(Conversion).where(Conversion.internal_file_name == file_name)
    return await database.fetch_one(query)