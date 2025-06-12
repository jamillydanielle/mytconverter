import express, { NextFunction, Request, Response } from 'express';
import { createProxyMiddleware } from 'http-proxy-middleware';
import cookieParser from 'cookie-parser';
import jwt from 'jsonwebtoken';
import cors from 'cors';


const corsOptions = {
    origin: ['http://frontend:3000', 'http://localhost:3000']
};
const app = express();
app.use(cors(corsOptions));
app.use(cookieParser());

const SECRET_KEY = process.env.JWT_SECRET_KEY;
const ISSUER = process.env.JWT_ISSUER;

if (!SECRET_KEY || !ISSUER) {
    throw new Error("No secret key or issuer for JWT on environment!");
}
const verifyToken = (req: Request, res: Response, next: NextFunction) => {
    const currentPath = req.path;

    if (currentPath === '/users/auth/login' ||
        currentPath === '/login' ||
        currentPath === '/users/users/createUser' ||
        currentPath === '/register') {
        return next();
    }

    if (req.method === 'POST' && req.path === '/users/users/createUser') {
        return next();
    }else{

        const token = req.cookies?.token || req.headers.authorization?.split(' ')[1];

        if (!token) {
            return res.status(401).json({ message: 'Token não fornecido!' });
        }

        jwt.verify(token, SECRET_KEY, { issuer: ISSUER }, (err: any, decoded: any) => {
            if (err) {
                console.error('Erro na verificação do token:', err);
                return res.status(401).json({ message: 'Token inválido ou expirado!' });
            }

            req.headers['x-user'] = JSON.stringify(decoded.user);
            try {
                req.user = JSON.parse(decoded.user);
            } catch {
                req.user = decoded.user
            }
            next();
        });
    }
};

app.get('/users/auth/login', (req: Request, res: Response) => {
    res.status(200).send({ message: 'Login permitido sem autenticação.' });
});


const accessControl = (req: Request, res: Response, next: NextFunction) => {
    const user = req.user;

    if (!user || !user.authorities || user.authorities.length === 0) {
        return res.status(401).json({ message: 'Usuário não autenticado' });
    }

    const userType = user.authorities[0]?.authority?.toUpperCase();

    if (!userType) {
        return res.status(403).json({ message: 'Acesso negado' });
    }

    const path = req.path;
    const isAllowed = (allowedTypes: string[], allowedPaths: string[]): boolean => {
        return allowedTypes.includes(userType) && allowedPaths.some(p => path.startsWith(p));
    };

    if (isAllowed(['ADMIN'], ['/']) ||
        isAllowed(['USER'], ['/converter', '/users/users/createUser'])) {
        return next();
    }

    return res.status(401).json({ message: 'Acesso negado' });
};


    app.use((req: Request, res: Response, next: NextFunction) => {
        const excludedPaths = ['/users/auth', '/users/users/createUser'];
        const isExcludedPath = excludedPaths.some(path => req.path.startsWith(path));

        if (!isExcludedPath) {
            verifyToken(req, res, (err) => {
                if (err) return next(err);
                accessControl(req, res, next);
            });
        } else {
            next();
        }
    });


app.use('/users', createProxyMiddleware({
    target: process.env.USER_MANAGEMENT_API || 'http://usermanagement:8080',
    changeOrigin: true,
    pathRewrite: {
        '^/users/users(.*)$': '/users$1',
        '^/users/auth/login': '/auth/login'    },
    on: {
        proxyReq: (proxyReq, req, res) => {
            if (req.headers['x-user']) {
                proxyReq.setHeader('x-user', req.headers['x-user']);
            }
        },
    },
    logger: console
}));

app.use('/converter', createProxyMiddleware({
    target: process.env.CONVERTION_API || 'http://converter:8081',
    changeOrigin: true,
    pathRewrite: {
        '^/converter/converter(.*)$': '/converter$1'
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