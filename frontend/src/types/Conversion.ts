export interface Conversion {
  id?: number | string;
  url?: string;
  youtubeUrl?: string;         // Added field
  youtubeVideoName?: string;   // Added field
  user?: {
    id: number;
    name: string;
    email: string;
  };
  internalFileName: string;
  format: string;
  createdAt: Date;
  length: number;
  file_size?: number;
  Type?: string;
}