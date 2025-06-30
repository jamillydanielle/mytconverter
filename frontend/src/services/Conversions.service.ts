import { Conversion } from "@/types/Conversion";
import { fetchWrapper } from "@/providers/fetchApi";

interface GetConversionsResponse {
    content: Conversion[];
    totalPages: number;
    totalElements: number;
}

interface DownloadResponse {
    data: {
        internal_filename: string;
        file_name: string;
    }
}

export const getConversions = async (page: number, size: number): Promise<GetConversionsResponse> => {
    try {
        const response = await fetchWrapper<GetConversionsResponse>(
            `/conversions/conversions/listforuser?page=${page}&size=${size}`,
            {
                method: 'GET',
            }
        );
        return response;
    } catch (error) {
        console.error("Erro ao buscar conversões:", error);
        throw error;
    }
};

export const downloadMedia = async (url: string, format: 'mp3' | 'mp4'): Promise<DownloadResponse> => {
    try {
        // Extract video name from URL (this is a simple implementation, might need improvement)
        let videoName = "Unknown Video";
        
        // Try to extract video name from YouTube URL
        // This is a simple implementation that assumes the URL contains a title or ID
        if (url.includes('youtube.com') || url.includes('youtu.be')) {
            // Extract video ID or title from URL
            const urlParts = url.split(/[/?&]/);
            for (let i = 0; i < urlParts.length; i++) {
                if (urlParts[i] === 'v=' && i + 1 < urlParts.length) {
                    videoName = urlParts[i + 1];
                    break;
                }
            }
            
            // If we couldn't extract a name, use part of the URL
            if (videoName === "Unknown Video" && url.length > 10) {
                videoName = url.substring(0, 30) + "...";
            }
        }

        const response = await fetchWrapper<DownloadResponse>(
            '/converter/converter/download',
            {
                method: 'POST',
                body: JSON.stringify({ 
                    url, 
                    format,
                    youtube_video_name: videoName 
                }),
            }
        );

        return response;
    } catch (error) {
        console.error("Erro ao solicitar o download:", error);
        throw error;
    }
};

export const getFile = async (internalFileName: string): Promise<Blob> => {
    try {
        const response = await fetchWrapper<Blob>(
            `/converter/converter/file/${internalFileName}`,
            {
                method: 'GET',
                responseType: 'blob'
            }
        );

        return response;


    } catch (error: any) {
        if (error.response) {
            console.error("[getFile] Response status:", error.response.status);
        }
        throw error;
    }
};