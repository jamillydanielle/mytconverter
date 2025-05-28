import { fetchWrapper } from "@/providers/fetchApi";
import { User } from '@/types/User';

interface UsersResponse {
    content: User[];
    totalPages : number;
}

export const getUsers = async (page : number, pageSize : number): Promise<UsersResponse> => {
    const response = await fetchWrapper<UsersResponse>(`/users/users/list?page=${page}&size=${pageSize}`, {
        method: 'GET'
    });
    return response;
};

export const getUserById = async(userId : string): Promise<User> => {
    const response =  await fetchWrapper<User>(`/users/users/list/${userId}`, {
        method:'GET'
    });
    return response;
}

export const deactivateUser = async (userId: string): Promise<void> => {
    await fetchWrapper<void>(`/users/users/${userId}/deactivate`, {
        method: 'PUT'
    });
};