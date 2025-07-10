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
                return res.status(401).json({ message: 'Token inválido ou expirado!' });
            }

            
            // Parse the user data from the token
            try {
                // Store the raw user data in the headers for proxying
                req.headers['x-user'] = decoded.user;
                
                // Parse the user object for local use
                const userData = JSON.parse(decoded.user);
                
                // Handle both structures: direct user object or nested user object
                if (userData) {
                    // If userData has a 'user' property, use that as the user object
                    if (userData.user) {
                        req.user = userData;
                    } else {
                        // Otherwise, use userData directly as the user object
                        req.user = userData;
                    }
                    
                    
                }
            } catch (e) {
                
                req.user = decoded.user;
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
        return res.status(403).json({ message: 'Acesso negado: Houve falha ao recuperar o userType' });
    }

    const path = req.path;
    
    // Special handling for conversion endpoints
    if (path === '/listforadm' || path.startsWith('/listforadm')) {
        if (userType === 'ADMIN') {
            return next();
        } else {
            return res.status(403).json({ message: 'Acesso negado: Apenas administradores podem acessar esta rota' });
        }
    }
    
    if (path === '/listforuser' || path.startsWith('/listforuser')) {
        if (userType === 'ADMIN' || userType === 'USER') {
            return next();
        } else {
            return res.status(403).json({ message: 'Acesso negado: Apenas usuários autenticados podem acessar esta rota' });
        }
    }

    // General path-based access control
    const isAllowed = (allowedTypes: string[], allowedPaths: string[]): boolean => {
        const allowed = allowedTypes.includes(userType) && allowedPaths.some(p => path.startsWith(p));
        return allowed;
    };

    if (isAllowed(['ADMIN'], ['/']) ||
        isAllowed(['USER'], ['/converter', '/conversions', '/users/users/getCurrentUserData', '/users/users/createUser', '/users/users/activate', '/users/users/deactivate', '/users/users/edit'])) {
        return next();
    }

    return res.status(403).json({ message: 'Acesso negado' });
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
    target: process.env.DATA_MANAGEMENT_API || 'http://datamanagement:8080',
    changeOrigin: true,
    pathRewrite: {
        '^/users/users(.*)$': '/users$1',
        '^/users/auth/login': '/auth/login'    },
    on: {
        proxyReq: (proxyReq, req, res) => {
            
            if (req.headers['x-user']) {
                proxyReq.setHeader('x-user', req.headers['x-user']);
            }
            
            if (req.headers['authorization']) {
                proxyReq.setHeader('authorization', req.headers['authorization']);
            }
        },
        proxyRes: (proxyRes, req, res) => {
            if (proxyRes.statusCode !== undefined) {
            }
        }
    },
    logger: console
}));

// Add new route for conversions endpoints
app.use('/conversions', createProxyMiddleware({
    target: process.env.DATA_MANAGEMENT_API || 'http://datamanagement:8080',
    changeOrigin: true,
    pathRewrite: {
        // Garantir que o caminho seja corretamente reescrito
        '^/conversions/conversions/listforuser': '/conversions/listforuser',
        '^/conversions/conversions/listforadm': '/conversions/listforadm',
        '^/conversions/conversions;listforuser': '/conversions/listforuser',
        '^/conversions/conversions;listforadm': '/conversions/listforadm',
        '^/conversions;listforuser': '/conversions/listforuser',
        '^/conversions;listforadm': '/conversions/listforadm'
    },
    on: {
        proxyReq: (proxyReq, req, res) => {
           
            if (req.headers['x-user']) {
                proxyReq.setHeader('x-user', req.headers['x-user']);
            }
            
            if (req.headers['authorization']) {
                proxyReq.setHeader('authorization', req.headers['authorization']);
            } else {
            }
        },
    },
    logger: console
}));

app.use('/converter', createProxyMiddleware({
    target: process.env.CONVERSION_API || 'http://converter:8081',
    changeOrigin: true,
    pathRewrite: {
        '^/converter/converter(.*)$': '/converter$1'
    },
    on: {
        proxyReq: (proxyReq, req, res) => {
            
            if (req.headers['x-user']) {
                proxyReq.setHeader('x-user', req.headers['x-user']);
            }
            
            if (req.headers['authorization']) {
                proxyReq.setHeader('authorization', req.headers['authorization']);
            }
        },
    },
    
    logger: console
}));

declare global {
    namespace Express {
        interface Request {
            user?: {
                email?: string;
                username?: string;
                authorities: Array<{ authority: string }>;
                [key: string]: any;
            };
        }
    }
}

export default app;