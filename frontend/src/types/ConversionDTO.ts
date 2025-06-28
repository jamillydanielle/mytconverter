export interface ConversionDTO {
  id?: number | string;
  userId?: number;
  userName?: string;
  userEmail?: string;
  youtubeVideoName: string;
  youtubeUrl: string;
  hasMP3: boolean;
  hasMP4: boolean;
  mp3InternalFileName?: string;
  mp4InternalFileName?: string;
  createdAt: Date;
  length: number;
}