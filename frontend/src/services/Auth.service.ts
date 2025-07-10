interface ChangePasswordRequest {
  newPassword: string;
}

interface LoginUserResponse {
  message: string;
  token: string;
  deactivated?: boolean;  // Novo campo para indicar conta desativada
  email?: string;         // Email da conta desativada
}

interface LoginRequest {
  email: string;
  password: string;
  rememberMe: boolean;
}

const API_BASE_URL = "http://localhost:3001";

export const changeUserPassword = async (
  email: string,
  newPassword: string,
  token: string
): Promise<string> => {
  try {
    const response = await fetch(
      `${API_BASE_URL}/users/auth/change-password/${encodeURIComponent(email)}`,
      {
        method: "PUT",
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ newPassword } as ChangePasswordRequest),
      }
    );

    if (!response.ok) {
      const errorMessage = await response.text();
      throw new Error(errorMessage);
    }

    const data = await response.text();
    return data;
  } catch (error) {
    console.error("Error changing password:", error);
    if (error instanceof Error) {
      throw error;
    }
    throw new Error("An unknown error occurred");
  }
};

export const loginUser = async (
  username: string,
  password: string,
  rememberMe: boolean
): Promise<LoginUserResponse> => {
  try {
    console.log("[Auth] Iniciando tentativa de login com:", { username, rememberMe });
    
    // Criar o objeto de requisição para poder adicionar logs detalhados
    const requestBody = JSON.stringify({
      email: username,
      password,
      rememberMe,
    });
    
    // Usar a nova rota direta para login
    console.log("[Auth] Enviando requisição para:", `${API_BASE_URL}/users/auth/login`);
    console.log("[Auth] Corpo da requisição:", requestBody);
    
    // Usar try/catch específico para a requisição fetch
    let response;
    try {
      response = await fetch(`${API_BASE_URL}/users/auth/login`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: requestBody,
      });
      
      console.log("[Auth] Resposta recebida - Status:", response.status);
      console.log("[Auth] Resposta recebida - Status Text:", response.statusText);
      console.log("[Auth] Resposta recebida - Headers:", Object.fromEntries(response.headers.entries()));
      
    } catch (fetchError) {
      console.error("[Auth] Erro na requisição fetch:", fetchError);
      throw new Error(`Erro na conexão: ${fetchError instanceof Error ? fetchError.message : 'Desconhecido'}`);
    }
    
    // Tentar obter o corpo da resposta
    let data;
    try {
      const responseText = await response.text();
      console.log("[Auth] Resposta em texto:", responseText);
      
      if (responseText) {
        try {
          data = JSON.parse(responseText);
          console.log("[Auth] Resposta parseada como JSON:", data);
        } catch (jsonError) {
          console.error("[Auth] Erro ao fazer parse da resposta como JSON:", jsonError);
          throw new Error("Erro ao processar resposta do servidor: formato inválido");
        }
      } else {
        console.error("[Auth] Resposta vazia do servidor");
        throw new Error("O servidor retornou uma resposta vazia");
      }
    } catch (textError) {
      console.error("[Auth] Erro ao obter texto da resposta:", textError);
      throw new Error("Erro ao ler resposta do servidor");
    }

    if (!response.ok) {
      // Caso especial: senha precisa ser trocada
      if (response.status === 403 && data.message === "A senha precisa ser trocada") {
        console.log("[Auth] Senha precisa ser trocada");
        return { token: data.token || "", message: data.message };
      }
      
      // Caso especial: conta desativada
      if (response.status === 401 && data.message === "Conta desativada") {
        console.log("[Auth] Conta desativada");
        return { 
          token: "", 
          message: data.message,
          deactivated: true,
          email: username
        };
      }
      
      console.error("[Auth] Resposta de erro:", data);
      throw new Error(data.message || `Erro no login: ${response.status}`);
    }

    console.log("[Auth] Login bem-sucedido:", data);
    return { 
      token: data.token || "", 
      message: data.message || "Login realizado com sucesso" 
    };
  } catch (error) {
    console.error("[Auth] Erro durante o login:", error);
    if (error instanceof Error) {
      throw error;
    }
    throw new Error("Ocorreu um erro desconhecido durante o login");
  }
};

export const activateAccount = async (email: string, password: string): Promise<void> => {
  try {
    console.log("[Auth] Reativando conta:", email);
    const response = await fetch(`${API_BASE_URL}/users/users/activate`, {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ email, password }),
    });

    // Verificar se a resposta não está ok
    if (!response.ok) {
      // Tentar obter o corpo da resposta como texto primeiro
      const responseText = await response.text();
      console.log("[Auth] Resposta de erro em texto:", responseText);
      
      let errorMessage = `Erro ao reativar conta (${response.status})`;
      
      // Tentar analisar como JSON se houver conteúdo
      if (responseText && responseText.trim()) {
        try {
          const errorData = JSON.parse(responseText);
          errorMessage = errorData.message || errorMessage;
        } catch (jsonError) {
          console.error("[Auth] Erro ao analisar resposta de erro como JSON:", jsonError);
          // Se não for JSON válido, usar o texto da resposta como mensagem de erro
          errorMessage = responseText || errorMessage;
        }
      }
      
      throw new Error(errorMessage);
    }

    console.log("[Auth] Conta reativada com sucesso");
  } catch (error) {
    console.error("[Auth] Erro ao reativar conta:", error);
    throw error;
  }
};

export const logoutUser = async (token: string): Promise<void> => {
  try {
    console.log("[Auth] Iniciando logout");
    await fetch(`${API_BASE_URL}/auth/logout`, {
      method: "POST",
      headers: {
        Authorization: `Bearer ${token}`,
        "Content-Type": "application/json",
      },
    });
    console.log("[Auth] Logout concluído com sucesso");
  } catch (error) {
    console.error("[Auth] Erro durante logout:", error);
  }
};