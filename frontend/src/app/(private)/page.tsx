"use client";
import { useEffect } from 'react';
import { useRouter } from 'next/navigation';
import jwt from 'jsonwebtoken';
import Cookies from 'js-cookie';

const RootRedirect = () => {
    const router = useRouter();

    useEffect(() => {
        const token = Cookies.get('token');
        if (token) {
            try {
                const decoded = jwt.decode(token);
                if (decoded && typeof decoded === 'object' && 'scope' in decoded) {
                    const userType = decoded.scope.toUpperCase();
                    if (userType === 'ADMIN') {
                        router.push('/users');
                    } else if (userType === 'USER') {
                        router.push('/myconvertions');
                    } else {
                        console.error('Tipo de usuário desconhecido');
                        router.push('/login');
                    }
                } else {
                    throw new Error('Token inválido');
                }
            } catch (error) {
                console.error('Erro ao decodificar o token:', error);
                router.push('/login');
            }
        } else {
            router.push('/login');
        }
    }, [router]);

    return null; // Este componente não renderiza nada, apenas redireciona
};

export default RootRedirect;