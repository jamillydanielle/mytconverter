import { decodeJwtToken } from "@/utils/jwtDecoder";
import { getToken, removeToken } from "@/utils/token";

const baseUrl: string = "http://localhost:3001";

interface CustomRequestInit extends RequestInit {
    responseType?: 'json' | 'blob';
}

// Lista de endpoints que não requerem verificação de token
const AUTH_FREE_ENDPOINTS = [
    '/login',
    '/users/auth/login',
    '/users/users/createUser',
    '/users/users/activate',
    '/auth/check-token',
    '/register',
    '/health'
];

/**
 * Verifica se um endpoint está na lista de endpoints que não requerem autenticação
 * @param endpoint Endpoint a ser verificado
 * @returns true se o endpoint não requer autenticação
 */
const isAuthFreeEndpoint = (endpoint: string): boolean => {
    return AUTH_FREE_ENDPOINTS.some(authFreeEndpoint => 
        endpoint === authFreeEndpoint || endpoint.startsWith(authFreeEndpoint)
    );
};

/**
 * Wrapper para fetch que adiciona o token de autenticação e trata erros comuns
 * @param endpoint Endpoint da API
 * @param options Opções da requisição
 * @returns Resposta da API
 */
export async function fetchWrapper<T>(
    endpoint: string,
    options: CustomRequestInit = {}
): Promise<T> {
    console.log(`[fetchApi] Iniciando requisição para: ${endpoint}`, { method: options.method || 'GET' });
    
    const token = getToken();
    if (token) {
        console.log(`[fetchApi] Token encontrado para requisição: ${token.substring(0, 15)}...`);
    } else {
        console.log(`[fetchApi] Nenhum token encontrado para requisição`);
    }

    // Verificação da lista negra removida para simplificar
    if (isAuthFreeEndpoint(endpoint)) {
        console.log(`[fetchApi] Endpoint ${endpoint} não requer verificação de token`);
    }

    let mergedOptions: RequestInit = { ...options };

    // Configurar cabeçalhos padrão
    let defaultHeaders: HeadersInit = {};
    
    // Adicionar token de autorização apenas se não for um endpoint de autenticação livre
    if (token && !isAuthFreeEndpoint(endpoint)) {
        defaultHeaders["Authorization"] = `Bearer ${token}`;
    }

    let requiresAuth = true;

    if (options.method?.toUpperCase() === 'POST' && endpoint === '/users/users/createUser'){
        requiresAuth = false;
    }

    if(options.method?.toUpperCase() === 'PUT' && endpoint === '/users/users/activate'){
        requiresAuth = false;
    }

    if(requiresAuth.valueOf() === false) {
        console.log(`[fetchApi] Configurando cabeçalhos sem token`);
        mergedOptions = {
            ...options,
            headers: {
                ...options.headers,
                'Content-Type': 'application/json'
            },
        };
    } else {
        console.log(`[fetchApi] Configurando cabeçalhos padrão`);
        // Verificar se há um corpo e se o Content-Type já foi definido
        const hasContentType = options.headers && 
            Object.keys(options.headers).some(key => key.toLowerCase() === 'content-type');
        
        if (options.body && !hasContentType) {
            console.log(`[fetchApi] Adicionando Content-Type: application/json`);
            defaultHeaders['Content-Type'] = 'application/json';
        }

        mergedOptions = {
            ...options,
            headers: {
                ...defaultHeaders,
                ...options.headers,
            },
        };
    }

    console.log(`[fetchApi] Opções finais da requisição:`, { 
        url: `${baseUrl}${endpoint}`,
        method: mergedOptions.method || 'GET',
        headers: mergedOptions.headers,
        bodyLength: mergedOptions.body ? JSON.stringify(mergedOptions.body).length : 0
    });

    try {
        console.log(`[fetchApi] Enviando requisição para ${baseUrl}${endpoint}`);
        const response = await fetch(`${baseUrl}${endpoint}`, mergedOptions);
        console.log(`[fetchApi] Resposta recebida - Status: ${response.status}, StatusText: ${response.statusText}`);
        
        // Verificação simplificada para 401
        if (response.status === 401) {
            console.warn(`[fetchApi] Resposta 401 Unauthorized`);
            try {
                const data = await response.json();
                console.log(`[fetchApi] Detalhes da resposta 401:`, data);
                
                // Redirecionamento simples para login
                console.warn("[fetchApi] Acesso não autorizado. Redirecionando para login...");
                removeToken();
                window.location.href = "/login";
                throw new Error("Acesso não autorizado. Por favor, faça login novamente.");
            } catch (parseError) {
                console.error("[fetchApi] Erro ao analisar resposta 401:", parseError);
                removeToken();
                window.location.href = "/login";
                throw new Error("Acesso não autorizado. Por favor, faça login novamente.");
            }
        }

        if (!response.ok) {
            console.warn(`[fetchApi] Resposta não-OK: ${response.status}`);
            let errorData: any = { message: `Request failed with status ${response.status}` };
            try {
                const responseText = await response.text();
                console.log(`[fetchApi] Texto da resposta de erro:`, responseText);
                
                if (responseText) {
                    try {
                        errorData = JSON.parse(responseText);
                        console.log(`[fetchApi] Dados de erro parseados:`, errorData);
                    } catch (e) {
                        console.warn("[fetchApi] Failed to parse error response as JSON:", e);
                    }
                }
            } catch (e) {
                console.warn("[fetchApi] Failed to read error response text:", e);
            }
            
            if (response.status !== 409) {
                console.log(
                    `[fetchApi] API Error (Status: ${response.status}):`,
                    errorData.message || errorData.error || errorData
                );
            }

            const error = new Error(
                errorData.message || errorData.error || `Ocorreu um erro (Status: ${response.status})`
            ) as any;
            error.status = response.status;
            error.data = errorData;
            error.response = response;
            throw error;
        }

        if (response.status === 204) {
            console.log(`[fetchApi] Resposta 204 No Content`);
            return undefined as T; 
        }

        if (options.responseType === 'blob') {
            console.log(`[fetchApi] Retornando resposta como blob`);
            return await response.blob() as T;
        }
        
        try {
            const responseText = await response.text();
            console.log(`[fetchApi] Texto da resposta:`, responseText.substring(0, 200) + (responseText.length > 200 ? '...' : ''));
            
            if (!responseText) {
                console.warn(`[fetchApi] Resposta vazia recebida`);
                return {} as T;
            }
            
            try {
                const jsonData = JSON.parse(responseText);
                console.log(`[fetchApi] Dados JSON parseados com sucesso`);
                return jsonData as T;
            } catch (jsonError) {
                console.error(`[fetchApi] Erro ao fazer parse da resposta como JSON:`, jsonError);
                throw new Error(`Erro ao processar resposta do servidor: ${jsonError instanceof Error ? jsonError.message : 'Formato inválido'}`);
            }
        } catch (textError) {
            console.error(`[fetchApi] Erro ao ler texto da resposta:`, textError);
            throw new Error(`Erro ao ler resposta do servidor: ${textError instanceof Error ? textError.message : 'Desconhecido'}`);
        }
    } catch (error) {
        console.error(`[fetchApi] Erro na requisição para ${endpoint}:`, error);
        throw error;
    }
}