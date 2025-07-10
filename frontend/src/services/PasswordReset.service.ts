import { fetchWrapper } from "@/providers/fetchApi";

interface PasswordResetResponse {
  message: string;
}

export const requestPasswordReset = async (email: string): Promise<PasswordResetResponse> => {
  try {
    const response = await fetchWrapper<PasswordResetResponse>('/users/auth/passwordReset/resetRequest', {
      method: 'POST',
      body: JSON.stringify({ email })
    });
    return response;
  } catch (error) {
    console.error('Erro ao solicitar recuperação de senha:', error);
    throw error;
  }
};

export const validateResetToken = async (token: string): Promise<PasswordResetResponse> => {
  try {
    const response = await fetchWrapper<PasswordResetResponse>('/users/auth/passwordReset/validateToken', {
      method: 'POST',
      body: JSON.stringify({ token })
    });
    return response;
  } catch (error) {
    console.error('Erro ao validar token de recuperação:', error);
    throw error;
  }
};

export const resetPassword = async (token: string, newPassword: string): Promise<PasswordResetResponse> => {
  try {
    const response = await fetchWrapper<PasswordResetResponse>('/users/auth/passwordReset/resetPassword', {
      method: 'POST',
      body: JSON.stringify({ token, newPassword })
    });
    return response;
  } catch (error) {
    console.error('Erro ao redefinir senha:', error);
    throw error;
  }
};