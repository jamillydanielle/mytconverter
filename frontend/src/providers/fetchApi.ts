import { decodeJwtToken } from "@/utils/jwtDecoder";
import { getToken, removeToken } from "@/utils/token";
import { jwtDecode } from "jwt-decode";

const baseUrl: string = "http://localhost:3001";

interface CustomRequestInit extends RequestInit {
    responseType?: 'json' | 'blob';
}


export async function fetchWrapper<T>(
    endpoint: string,
    options: CustomRequestInit = {}
): Promise<T> {


    const token = getToken();

    let mergedOptions: RequestInit = { ...options };

    const defaultHeaders: HeadersInit = {
        "Authorization": `Bearer ${token}`,
    };

    if (options.method?.toUpperCase() === 'POST' && endpoint === '/users/users/createUser') {
        const { Authorization, ...remainingHeaders } = defaultHeaders;
        mergedOptions = {
            ...options,
            headers: {
                ...remainingHeaders,
                ...options.headers,
                'Content-Type': 'application/json'
            },
        };
    } else {
        if (options.body && !options.headers?.['Content-Type']) {
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


    const response = await fetch(`${baseUrl}${endpoint}`, mergedOptions);
    /*if (response.status === 401) {
        alert("Acesso nao autorizado. Por favor faça o login novamente.");
        removeToken();
        window.location.href = "/login";
        return {} as T;
    }*/

    if (!response.ok) {
        let errorData: any = { message: `Request failed with status ${response.status}` };
        try {
            errorData = await response.json();
        } catch (e) {
            console.warn("Failed to parse error response as JSON:", e);
        }
        if (response.status !== 409) {
            console.log(
                `API Error (Status: ${response.status}):`,
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
        return undefined as T; 
    }

    if (options.responseType === 'blob') {
        return await response.blob() as T;
    }
    return await response.json() as T;
}