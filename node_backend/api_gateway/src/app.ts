    import express, { NextFunction, Request, Response } from 'express';
    import { createProxyMiddleware } from 'http-proxy-middleware';
    import cookieParser from 'cookie-parser';
    import jwt from 'jsonwebtoken';
    import cors from 'cors';

    
    const corsOptions = {
        origin: ['http://frontend:3000', 'http://localhost:3000', 'http://192.168.1.21:3000']
    };
    const app = express();
    app.use(cors(corsOptions));
    app.use(cookieParser());

    const SECRET_KEY = process.env.JWT_SECRET_KEY;
    const ISSUER = process.env.JWT_ISSUER;

    if (!SECRET_KEY || !ISSUER) {
        throw new Error("No secret key or issuer for JWT on environment!");
    }

    //Middleware para verificar token JWT
    const verifyToken = (req: Request, res: Response, next: NextFunction) => {
        const currentPath = req.path;

        // Ignorar verificação de token para as rotas de login
        if (currentPath === '/users/auth/login' || currentPath === '/login') {
            console.log(`Rota ${currentPath} detectada. Ignorando verificação de token.`);
            return next();
        }

        const token = req.cookies?.token || req.headers.authorization?.split(' ')[1]; // Busca no cookie ou no header

        if (!token) {
            return res.status(401).json({ message: 'Token não fornecido!' });
        }

        jwt.verify(token, SECRET_KEY, { issuer: ISSUER }, (err: any, decoded: any) => {
            if (err) {
                console.error('Erro na verificação do token:', err);
                return res.status(401).json({ message: 'Token inválido ou expirado!' });
            }

            req.headers['x-user'] = JSON.stringify(decoded.user);
            try{
                req.user = JSON.parse(decoded.user);
            } catch {
                req.user = decoded.user
            }
            next();
        });
    };
    app.get('/users/auth/login', (req: Request, res: Response) => {
        console.log('Rota /users/auth/login foi acessada.');
        res.status(200).send({ message: 'Login permitido sem autenticação.' });
    });

    //Middleware para controle de acesso baseado no tipo de usuário
    const accessControl = (req: Request, res: Response, next: NextFunction) => {
        const user = req.user; // A estrutura é baseada em JSON.parse(decoded.user)

        if (!user || !user.authorities || user.authorities.length === 0) {
            console.log('Usuário não autenticado ou sem permissões:', user);
            return res.status(401).json({ message: 'Usuário não autenticado' });
        }

        const userType = user.authorities[0]?.authority?.toUpperCase();

        if (!userType) {
            console.log('Tipo de usuário inválido:', userType);
            return res.status(403).json({ message: 'Acesso negado' });
        }

        const path = req.path;
        const isAllowed = (allowedTypes: string[], allowedPaths: string[]): boolean => {
            return allowedTypes.includes(userType) && allowedPaths.some(p => path.startsWith(p));
        };

    if (isAllowed(['ADMIN'], ['/']) ||
        isAllowed(['USER'], ['/'])) {
        console.log('Acesso permitido ao usuário:', userType);
        return next();
    }

        console.log('Acesso negado ao usuário:', userType);
        return res.status(403).json({ message: 'Acesso negado' });
    };

    // Proxy para a API de gerenciamento de usuários (incluindo autenticação)
    app.use('/users', createProxyMiddleware({
        target: process.env.USER_MANAGEMENT_API || 'http://usermanagement:8080',
        changeOrigin: true,
        pathRewrite: { 
            '^/users/users(.*)$': '/users$1',
            '^/users/auth/login': '/auth/login'  // Redireciona /users/auth/login para /auth/login no backend
        },
        on: {
            proxyReq: (proxyReq, req, res) => {
                if (req.headers['x-user']) {
                    proxyReq.setHeader('x-user', req.headers['x-user']);
                }
            },
        },
        logger: console
    }));

    // Proxy for the convertion request API
    app.use('/convertion', createProxyMiddleware({
        target: process.env.CONVERTION_API || 'http://convertion:8081',
        changeOrigin: true,
        pathRewrite: {
        '^/convertion/convertion(.*)$': '/convertion$1'
        },
        on:{
            proxyReq: (proxyReq, req, res) => {
                if (req.headers['x-user']) {
                proxyReq.setHeader('x-user', req.headers['x-user']);
            }
        },
    },
    logger: console
}));

    // Declaração global
    declare global {
        namespace Express {
            interface Request {
                user?: {
                    email: string;
                    authorities: Array<{ authority: string }>;
                    [key: string]: any;
                };
            }
        }
    }

    export default app;