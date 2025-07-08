import { fetchWrapper } from "@/providers/fetchApi";

export interface UserSession {
  userId: number | string;
  username: string;
  lastSession: string | null;
}

export const getUserSessions = async (): Promise<UserSession[]> => {
  try {
    const response = await fetchWrapper<UserSession[]>('/users/users/sessions', {
      method: 'GET'
    });
    return response;
  } catch (error) {
    console.error("Erro ao buscar informações de sessão:", error);
    throw error;
  }
};

export const getUserSessionById = async (userId: string): Promise<UserSession> => {
  try {
    const response = await fetchWrapper<UserSession>(`/users/users/session/${userId}`, {
      method: 'GET'
    });
    return response;
  } catch (error) {
    console.error(`Erro ao buscar informações de sessão para o usuário ${userId}:`, error);
    throw error;
  }
};