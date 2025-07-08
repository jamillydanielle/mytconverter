
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
                const decoded = jwt.decode(token); // Decodifica usando jsonwebtoken
                // Verifica se 'decoded' não é null e é um objeto antes de acessar 'scope'
                if (decoded && typeof decoded === 'object' && 'scope' in decoded && typeof decoded.scope === 'string') {
                    const userType = decoded.scope.toUpperCase();
                    if (userType === 'ADMIN') {
                        router.push('/admin/users');
                    } else if (userType === 'USER') {
                        router.push('/myconversions');
                    } else {
                        console.error('Tipo de usuário desconhecido no token (scope):', decoded.scope);
                        router.push('/login'); // Fallback se o scope não for reconhecido
                    }
                } else {
                    // 'scope' não existe ou não é uma string, ou 'decoded' não é um objeto
                    console.error('Token decodificado inválido ou sem campo "scope" esperado:', decoded);
                    router.push('/login'); // Fallback
                }
            } catch (error) {
                console.error('Erro ao decodificar o token:', error);
                router.push('/login'); // Fallback em caso de erro na decodificação
            }
        } else {
            router.push('/login'); // Redireciona para login se não houver token
        }
    }, [router]);

    return null; // Este componente não renderiza nada visível, apenas redireciona
};

export default RootRedirect;