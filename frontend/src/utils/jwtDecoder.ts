import { jwtDecode } from 'jwt-decode';
import Cookies from 'js-cookie';
import { User, UserType } from '@/types';

export interface DecodedToken {
  sub: string;
  scope: string;
  iss: string;
  exp: number;
  iat: number;
  user: string;
}

export interface UserData {
  user: {
    id: number;
    name: string;
    email: string;
    type: UserType;
  };
  enabled: boolean;
  username: string;
  authorities: { authority: string }[];
}

export function decodeJwtToken(): UserData | null {
  const token = Cookies.get('token');
  if (!token) return null;

  try {
    const decodedToken = jwtDecode<DecodedToken>(token);
    const userData: UserData = JSON.parse(decodedToken.user);
    console.log(userData);
    return userData;
  } catch (error) {
    console.error('Error decoding token:', error);
    return null;
  }
}