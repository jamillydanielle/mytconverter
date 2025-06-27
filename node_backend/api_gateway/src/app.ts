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
    console.log(`[VERIFY TOKEN] Checking token for path: ${currentPath}`);

    if (currentPath === '/users/auth/login' ||
        currentPath === '/login' ||
        currentPath === '/users/users/createUser' ||
        currentPath === '/register') {
        console.log(`[VERIFY TOKEN] Skipping token verification for public path: ${currentPath}`);
        return next();
    }

    if (req.method === 'POST' && req.path === '/users/users/createUser') {
        console.log(`[VERIFY TOKEN] Skipping token verification for user creation`);
        return next();
    }else{

        const token = req.cookies?.token || req.headers.authorization?.split(' ')[1];
        console.log(`[VERIFY TOKEN] Token present: ${!!token}`);

        if (!token) {
            console.log(`[VERIFY TOKEN] No token provided`);
            return res.status(401).json({ message: 'Token não fornecido!' });
        }

        jwt.verify(token, SECRET_KEY, { issuer: ISSUER }, (err: any, decoded: any) => {
            if (err) {
                console.error('[VERIFY TOKEN] Error verifying token:', err);
                return res.status(401).json({ message: 'Token inválido ou expirado!' });
            }

            console.log(`[VERIFY TOKEN] Token verified successfully for user: ${decoded.sub}`);
            
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
                    
                    if (req.user) {
                        console.log(`[VERIFY TOKEN] User parsed: ${req.user.email || req.user.username}, authorities: ${JSON.stringify(req.user.authorities)}`);
                    }
                }
            } catch (e) {
                if (e instanceof Error) {
                    console.log(`[VERIFY TOKEN] Failed to parse user: ${e.message}`);
                } else {
                    console.log(`[VERIFY TOKEN] Failed to parse user: Unknown error`);
                }
                console.log(`[VERIFY TOKEN] Raw user data: ${decoded.user}`);
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
    console.log(`[ACCESS CONTROL] Checking access for path: ${req.path}`);
    
    const user = req.user;
    console.log(`[ACCESS CONTROL] User: ${JSON.stringify(user)}`);

    if (!user || !user.authorities || user.authorities.length === 0) {
        console.log(`[ACCESS CONTROL] No user or authorities`);
        return res.status(401).json({ message: 'Usuário não autenticado' });
    }

    const userType = user.authorities[0]?.authority?.toUpperCase();
    console.log(`[ACCESS CONTROL] User type: ${userType}`);

    if (!userType) {
        console.log(`[ACCESS CONTROL] No user type`);
        return res.status(403).json({ message: 'Acesso negado' });
    }

    const path = req.path;
    console.log(`[ACCESS CONTROL] Checking path: ${path}`);
    
    // Special handling for conversion endpoints
    if (path === '/listforadm' || path.startsWith('/listforadm')) {
        console.log(`[ACCESS CONTROL] Checking admin access for ${path}`);
        if (userType === 'ADMIN') {
            console.log(`[ACCESS CONTROL] Admin access granted`);
            return next();
        } else {
            console.log(`[ACCESS CONTROL] Admin access denied`);
            return res.status(403).json({ message: 'Acesso negado: Apenas administradores podem acessar esta rota' });
        }
    }
    
    if (path === '/listforuser' || path.startsWith('/listforuser')) {
        console.log(`[ACCESS CONTROL] Checking user access for ${path}`);
        if (userType === 'ADMIN' || userType === 'USER') {
            console.log(`[ACCESS CONTROL] User access granted`);
            return next();
        } else {
            console.log(`[ACCESS CONTROL] User access denied`);
            return res.status(403).json({ message: 'Acesso negado: Apenas usuários autenticados podem acessar esta rota' });
        }
    }

    // General path-based access control
    const isAllowed = (allowedTypes: string[], allowedPaths: string[]): boolean => {
        const allowed = allowedTypes.includes(userType) && allowedPaths.some(p => path.startsWith(p));
        console.log(`[ACCESS CONTROL] Checking if ${userType} can access ${path}: ${allowed}`);
        return allowed;
    };

    if (isAllowed(['ADMIN'], ['/']) ||
        isAllowed(['USER', 'ADMIN'], ['/converter', '/users/users/createUser', '/conversions'])) {
        console.log(`[ACCESS CONTROL] Access granted`);
        return next();
    }

    console.log(`[ACCESS CONTROL] Access denied`);
    return res.status(403).json({ message: 'Acesso negado' });
};


app.use((req: Request, res: Response, next: NextFunction) => {
    const excludedPaths = ['/users/auth', '/users/users/createUser'];
    const isExcludedPath = excludedPaths.some(path => req.path.startsWith(path));
    console.log(`[MIDDLEWARE] Request to ${req.path}, excluded: ${isExcludedPath}`);

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
            console.log(`[PROXY] Users - Proxying request to: ${req.method} ${(req as any).path}`);
            
            if (req.headers['x-user']) {
                proxyReq.setHeader('x-user', req.headers['x-user']);
                console.log(`[PROXY] Users - Setting x-user header`);
            }
            
            if (req.headers['authorization']) {
                console.log(`[PROXY] Users - Authorization header present`);
                proxyReq.setHeader('authorization', req.headers['authorization']);
            }
        },
        proxyRes: (proxyRes, req, res) => {
            if (proxyRes.statusCode !== undefined) {
                console.log(`[PROXY] Users - Response status: ${proxyRes.statusCode}`);
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
            console.log(`[PROXY] Conversions - Proxying request to: ${req.method} ${(req as any).path}`);
            console.log(`[PROXY] Conversions - Full URL: ${req.url}`);
            console.log(`[PROXY] Conversions - Headers: ${JSON.stringify(req.headers)}`);
            
            if (req.headers['x-user']) {
                proxyReq.setHeader('x-user', req.headers['x-user']);
                console.log(`[PROXY] Conversions - Setting x-user header`);
            }
            
            if (req.headers['authorization']) {
                console.log(`[PROXY] Conversions - Authorization header present`);
                proxyReq.setHeader('authorization', req.headers['authorization']);
            } else {
                console.log(`[PROXY] Conversions - No Authorization header present`);
            }
        },
        proxyRes: (proxyRes, req, res) => {
            if (proxyRes.statusCode !== undefined) {
                console.log(`[PROXY] Conversions - Received response from datamanagement: ${proxyRes.statusCode}`);
                if (proxyRes.statusCode >= 400) {
                    console.log(`[PROXY] Conversions - Error response headers: ${JSON.stringify(proxyRes.headers)}`);
                }
            }
        }
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
            console.log(`[PROXY] Converter - Proxying request to: ${req.method} ${(req as any).path}`);
            
            if (req.headers['x-user']) {
                proxyReq.setHeader('x-user', req.headers['x-user']);
                console.log(`[PROXY] Converter - Setting x-user header`);
            }
            
            if (req.headers['authorization']) {
                console.log(`[PROXY] Converter - Authorization header present`);
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