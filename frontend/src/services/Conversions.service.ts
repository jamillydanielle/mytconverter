import { Conversion } from "@/types/Conversion";
import { UserConversionStats, UserConversionStatsResponse } from "@/types/UserConversionStats";
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

// Interface to match the backend UserConversionMetricsDTO
interface BackendUserMetrics {
    username: string;
    totalMp3Conversions: number;
    totalMp4Conversions: number;
    totalMinutesConverted: number;
    preferredFormat: 'MP3' | 'MP4';
}

interface BackendUserMetricsResponse {
    content: BackendUserMetrics[];
    totalPages: number;
    totalElements: number;
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

export const getUserConversionStats = async (page: number, size: number): Promise<UserConversionStatsResponse> => {
    try {
        // Fetch data from backend
        const response = await fetchWrapper<BackendUserMetricsResponse>(
            `/conversions/conversions/listforadm?page=${page}&size=${size}`,
            {
                method: 'GET',
            }
        );

        // Transform backend data to match frontend interface
        const transformedContent: UserConversionStats[] = response.content.map((metric, index) => {
            // Distribuir o tempo total entre MP3 e MP4 com base no formato preferido e contagem
            let mp3Minutes = 0;
            let mp4Minutes = 0;
            
            // Se temos conversões de ambos os tipos, distribuímos proporcionalmente
            if (metric.totalMp3Conversions > 0 && metric.totalMp4Conversions > 0) {
                const totalConversions = metric.totalMp3Conversions + metric.totalMp4Conversions;
                mp3Minutes = (metric.totalMp3Conversions / totalConversions) * metric.totalMinutesConverted;
                mp4Minutes = (metric.totalMp4Conversions / totalConversions) * metric.totalMinutesConverted;
            } 
            // Se só temos um tipo, atribuímos todo o tempo a ele
            else if (metric.totalMp3Conversions > 0) {
                mp3Minutes = metric.totalMinutesConverted;
            } 
            else if (metric.totalMp4Conversions > 0) {
                mp4Minutes = metric.totalMinutesConverted;
            }
            
            return {
                userId: index, // Using index as userId since it's not provided by backend
                userName: metric.username,
                userEmail: '', // Email not provided by backend
                mp3Conversions: metric.totalMp3Conversions,
                mp4Conversions: metric.totalMp4Conversions,
                mp3TotalMinutes: mp3Minutes,
                mp4TotalMinutes: mp4Minutes,
                preferredFormat: metric.preferredFormat
            };
        });

        return {
            content: transformedContent,
            totalPages: response.totalPages,
            totalElements: response.totalElements
        };
    } catch (error) {
        console.error("Erro ao buscar as métricas de uso do sistema:", error);
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