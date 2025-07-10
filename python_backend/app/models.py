from sqlalchemy import Column, Integer, String
from sqlalchemy.ext.declarative import declarative_base
import enum

Base = declarative_base()

class Conversion(Base):
    __tablename__ = "conversions"

    id = Column(Integer, primary_key=True, index=True)
    created_at = Column(Integer, nullable=False)
    format = Column(String, nullable=False)
    internal_file_name = Column(String, nullable=False)
    length = Column(Integer, nullable=False)
    youtube_url = Column(String, nullable=False)
    youtube_video_name = Column(String, nullable=False)
    user_id = Column(String, index=True)

