import { setCookie, parseCookies, destroyCookie } from 'nookies';

// Função para salvar o token nos cookies
export const setToken = (token: string) => {
  setCookie(null, 'token', token, {
    maxAge: 30 * 24 * 60 * 60,
    path: '/',
  });
};

// Função para obter o token dos cookies
export const getToken = (): string | null => {
  const cookies = parseCookies();
  return cookies.token || null;
};

// Função para remover o token dos cookies
export const removeToken = () => {
  destroyCookie(null, 'token', { path: '/' });
};