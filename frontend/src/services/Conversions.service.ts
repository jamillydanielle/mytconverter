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
        const response = await fetchWrapper<DownloadResponse>(
            '/converter/converter/download',
            {
                method: 'POST',
                body: JSON.stringify({ url, format }),
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