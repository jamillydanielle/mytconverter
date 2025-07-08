export interface UserConversionStats {
  userId: number | string;
  userName: string;
  userEmail: string;
  mp3Conversions: number;
  mp4Conversions: number;
  mp3TotalMinutes: number;
  mp4TotalMinutes: number;
  preferredFormat: 'MP3' | 'MP4';
}

export interface UserConversionStatsResponse {
  content: UserConversionStats[];
  totalPages: number;
  totalElements: number;
}