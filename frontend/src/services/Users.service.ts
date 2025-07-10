import { fetchWrapper } from "@/providers/fetchApi";
import { User } from '@/types/User';
import { UserSession } from '@/types/UserSession';

interface UsersResponse {
    content: User[];
    totalPages : number;
}

export const createUser = async (user: Omit<User, 'id' | 'createdAt' | 'updatedAt'>): Promise<User> => {
    const response = await fetchWrapper<User>('/users/users/createUser', {
        method: 'POST',
        body: JSON.stringify(user)
    });
    return response;
};

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

export const getCurrentUserData = async(): Promise<User> => {
    const response =  await fetchWrapper<User>(`/users/users/getCurrentUserData`, {
        method:'GET'
    });
    return response;
}

export const updateUser = async (userData: Partial<User>): Promise<User> => {
    const response = await fetchWrapper<User>(`/users/users/edit`, {
        method: 'PUT',
        body: JSON.stringify(userData)
    });
    return response;
};

export const deactivateUser = async (): Promise<void> => {
    await fetchWrapper<void>(`/users/users/deactivate`, {
        method: 'PUT'
    });
};

export const activateUser = async (): Promise<void> => {
    await fetchWrapper<void>(`/users/users/activate`, {
        method: 'PUT'
    });
};

// Simulação de API para obter informações de sessão dos usuários
// Em um ambiente real, isso seria substituído por uma chamada real à API
export const getUserSessions = async (): Promise<UserSession[]> => {
    // Simulando uma resposta da API com dados fictícios
    // Em um ambiente real, isso seria uma chamada à API
    return new Promise((resolve) => {
        setTimeout(() => {
            resolve([
                { userId: 1, isLoggedIn: true, lastSession: new Date() },
                { userId: 2, isLoggedIn: false, lastSession: new Date(Date.now() - 24 * 60 * 60 * 1000) }, // 1 dia atrás
                { userId: 3, isLoggedIn: true, lastSession: new Date() },
                // Adicione mais dados simulados conforme necessário
            ]);
        }, 300);
    });
};