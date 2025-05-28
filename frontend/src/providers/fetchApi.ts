import { decodeJwtToken } from "@/utils/jwtDecoder";
import { getToken, removeToken } from "@/utils/token";
import { jwtDecode } from "jwt-decode";

const baseUrl: string = "http://localhost:3001";

export async function fetchWrapper<T>(
    endpoint: string,
    options: RequestInit = {}
): Promise<T> {
    let headers: HeadersInit | undefined = {}; // Initialize headers as undefined

    if (!(options.method === 'POST' && endpoint === '/users/users/createUser')) {
        const token = getToken();
        const decodedUser = decodeJwtToken();

        const defaultHeaders = {
            "Authorization": `Bearer ${token}`,
            "UserData": `${decodedUser}`,
            "Content-Type": "application/json"
        };

        headers = {
            ...defaultHeaders,
            ...options.headers
        };
    } else {
        headers = {
            "Content-Type": "application/json" // Only Content-Type for createUser
        };
    }

    const mergedOptions: RequestInit = {
        ...options,
        headers: headers
    };

    const response = await fetch(`${baseUrl}${endpoint}`, mergedOptions);


    if (!response.ok) {
        let errorData: any = { message: `Request failed with status ${response.status}` }; // Fallback
        try {
            errorData = await response.json();
        } catch (e) {
            console.warn("Failed to parse error response as JSON:", e);
        }
        if (response.status !== 409) {
            console.error(
                `API Error (Status: ${response.status}):`,
                errorData.message || errorData.error || errorData
            );
        }

        const error = new Error(
            errorData.message || errorData.error || `An error occurred (Status: ${response.status})`
        ) as any;
        error.status = response.status;
        error.data = errorData;
        error.response = response;
        throw error;
    }


    if (response.status === 204) {
        return undefined as T; // Or return null as T;
    }

    return response.json();
}