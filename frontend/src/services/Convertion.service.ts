import { fetchWrapper } from "@/providers/fetchApi";
import { Convertion } from '@/types/Convertion';

interface ConvertionResponse {
    content: Convertion[];
    totalPages : number;
}

export const getConvertion = async (page : number, pageSize : number): Promise<ConvertionResponse> => {
    const response = await fetchWrapper<ConvertionResponse>(`/convert/convert/list?page=${page}&size=${pageSize}`, {
        method: 'GET'
    });
    return response;
};

export const getConvertionById = async(userId : string): Promise<Convertion> => {
    const response =  await fetchWrapper<Convertion>(`/convert/Convert/list/${userId}`, {
        method:'GET'
    });
    return response;
}

export const deactivateConvertion = async (userId: string): Promise<void> => {
    await fetchWrapper<void>(`/convert/convert/${userId}/deactivate`, {
        method: 'PUT'
    });
};