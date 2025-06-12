// frontend/src/services/Convertion.service.ts

import { Convertion } from "@/types/Convertion";
import { fetchWrapper } from "@/providers/fetchApi"; // Import fetchWrapper

interface GetConvertionsResponse {
    content: Convertion[];
    totalPages: number;
    totalElements: number;
    // Add other properties from your API response if needed
}

export const getConvertions = async (page: number = 0, size: number = 10): Promise<GetConvertionsResponse> => {
    try {
        const response = await fetchWrapper<GetConvertionsResponse>(`/convertions/convertions/getConvertions?page=${page}&size=${size}`, { // Adjust the URL
            method: 'GET',
        });
        return response;
    } catch (error) {
        console.error("Erro ao buscar conversões:", error);
        throw error; // Re-throw for the component to handle
    }
};

// Example POST Method for Creating a Convertion.
export const createConvertion = async (convertionData: Omit<Convertion, 'id' | 'createdAt' | 'updatedAt' | 'user_name'>): Promise<Convertion> => {
    try {
        const response = await fetchWrapper<Convertion>(`/convertions/convertions/createConvertion`, { // Adjust the URL
            method: 'POST',
            body: JSON.stringify(convertionData),
        });
        return response;
    } catch (error) {
        console.error("Erro ao criar conversão:", error);
        throw error;
    }
};