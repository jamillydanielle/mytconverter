import express, { Request, Response, NextFunction } from 'express';
import { createProxyMiddleware } from 'http-proxy-middleware';
import http from 'http';

const router = express.Router();
const dataManagementAPI = process.env.DATA_MANAGEMENT_API || 'http://datamanagement:8080';

/**
 * Endpoint para verificar se o servidor está funcionando
 */
router.get('/health', (req: Request, res: Response) => {
  res.status(200).json({ status: 'ok', message: 'Users service is running' });
});

/**
 * Rota específica para criação de usuário (sem autenticação)
 * Nota: Esta rota lida com /users/createUser
 */
router.post('/createUser', createProxyMiddleware({
  target: dataManagementAPI,
  changeOrigin: true,
  pathRewrite: {
    '^/users/createUser': '/users/createUser'
  },
  onProxyReq: (proxyReq: http.ClientRequest, req: Request, res: http.ServerResponse) => {
    console.log(`[UsersRoute] User creation request: ${req.method} ${req.url} -> ${proxyReq.path}`);
    
    // Garantir que o corpo da requisição seja enviado corretamente
    if (req.body && req.method === 'POST') {
      const bodyData = JSON.stringify(req.body);
      console.log(`[UsersRoute] Forwarding user creation request body: ${bodyData}`);
      proxyReq.setHeader('Content-Type', 'application/json');
      proxyReq.setHeader('Content-Length', Buffer.byteLength(bodyData));
      proxyReq.write(bodyData);
      proxyReq.end();
    }
    
    // Log all headers being sent
    console.log(`[UsersRoute] User creation proxy request headers:`, proxyReq.getHeaders());
  },
  onProxyRes: (proxyRes: http.IncomingMessage, req: Request, res: http.ServerResponse) => {
    console.log(`[UsersRoute] Proxy response for user creation: ${proxyRes.statusCode}`);
    
    // Log response headers
    console.log(`[UsersRoute] Proxy response headers for user creation:`, proxyRes.headers);
    
    // Capture response body for logging
    let responseBody = '';
    proxyRes.on('data', (chunk) => {
      responseBody += chunk;
    });
    
    proxyRes.on('end', () => {
      if (responseBody) {
        try {
          // Try to parse as JSON for better logging
          const jsonBody = JSON.parse(responseBody);
          console.log(`[UsersRoute] Proxy response body for user creation:`, jsonBody);
        } catch (e) {
          // If not JSON, log as string
          console.log(`[UsersRoute] Proxy response body for user creation (raw):`, responseBody);
        }
      } else {
        console.log(`[UsersRoute] Empty proxy response body for user creation`);
      }
    });
  },
  onError: (err: Error, req: Request, res: Response) => {
    console.error('[UsersRoute] User creation proxy error:', err);
    res.status(500).json({ message: 'Erro interno do servidor', error: err.message });
  }
} as any));

/**
 * Rota específica para criação de usuário (sem autenticação)
 * Nota: Esta rota lida com /users/users/createUser para compatibilidade com o frontend
 */
router.post('/users/createUser', createProxyMiddleware({
  target: dataManagementAPI,
  changeOrigin: true,
  pathRewrite: {
    '^/users/users/createUser': '/users/createUser'
  },
  onProxyReq: (proxyReq: http.ClientRequest, req: Request, res: http.ServerResponse) => {
    console.log(`[UsersRoute] User creation request (compat): ${req.method} ${req.url} -> ${proxyReq.path}`);
    
    // Garantir que o corpo da requisição seja enviado corretamente
    if (req.body && req.method === 'POST') {
      const bodyData = JSON.stringify(req.body);
      console.log(`[UsersRoute] Forwarding user creation request body (compat): ${bodyData}`);
      proxyReq.setHeader('Content-Type', 'application/json');
      proxyReq.setHeader('Content-Length', Buffer.byteLength(bodyData));
      proxyReq.write(bodyData);
      proxyReq.end();
    }
    
    // Log all headers being sent
    console.log(`[UsersRoute] User creation proxy request headers (compat):`, proxyReq.getHeaders());
  },
  onProxyRes: (proxyRes: http.IncomingMessage, req: Request, res: http.ServerResponse) => {
    console.log(`[UsersRoute] Proxy response for user creation (compat): ${proxyRes.statusCode}`);
    
    // Log response headers
    console.log(`[UsersRoute] Proxy response headers for user creation (compat):`, proxyRes.headers);
    
    // Capture response body for logging
    let responseBody = '';
    proxyRes.on('data', (chunk) => {
      responseBody += chunk;
    });
    
    proxyRes.on('end', () => {
      if (responseBody) {
        try {
          // Try to parse as JSON for better logging
          const jsonBody = JSON.parse(responseBody);
          console.log(`[UsersRoute] Proxy response body for user creation (compat):`, jsonBody);
        } catch (e) {
          // If not JSON, log as string
          console.log(`[UsersRoute] Proxy response body for user creation (compat) (raw):`, responseBody);
        }
      } else {
        console.log(`[UsersRoute] Empty proxy response body for user creation (compat)`);
      }
    });
  },
  onError: (err: Error, req: Request, res: Response) => {
    console.error('[UsersRoute] User creation proxy error (compat):', err);
    res.status(500).json({ message: 'Erro interno do servidor', error: err.message });
  }
} as any));

/**
 * Rota para listar todos os usuários (requer autenticação)
 */
router.get('/list', createProxyMiddleware({
  target: dataManagementAPI,
  changeOrigin: true,
  pathRewrite: {
    '^/users/list': '/users/list'
  },
  onProxyReq: (proxyReq: http.ClientRequest, req: Request, res: http.ServerResponse) => {
    console.log(`[UsersRoute] List users request: ${req.method} ${req.url} -> ${proxyReq.path}`);
    
    if (req.headers['x-user']) {
      proxyReq.setHeader('x-user', req.headers['x-user'] as string);
    }
    
    if (req.headers['authorization']) {
      proxyReq.setHeader('authorization', req.headers['authorization'] as string);
    }
  }
} as any));

/**
 * Rota para obter sessões de usuários (requer autenticação)
 */
router.get('/sessions', createProxyMiddleware({
  target: dataManagementAPI,
  changeOrigin: true,
  pathRewrite: {
    '^/users/sessions': '/users/sessions'
  },
  onProxyReq: (proxyReq: http.ClientRequest, req: Request, res: http.ServerResponse) => {
    console.log(`[UsersRoute] User sessions request: ${req.method} ${req.url} -> ${proxyReq.path}`);
    
    if (req.headers['x-user']) {
      proxyReq.setHeader('x-user', req.headers['x-user'] as string);
    }
    
    if (req.headers['authorization']) {
      proxyReq.setHeader('authorization', req.headers['authorization'] as string);
    }
  }
} as any));

/**
 * Rota para obter um usuário específico por ID (requer autenticação)
 */
router.get('/list/:id', createProxyMiddleware({
  target: dataManagementAPI,
  changeOrigin: true,
  pathRewrite: {
    '^/users/list/(.*)': '/users/list/$1'
  },
  onProxyReq: (proxyReq: http.ClientRequest, req: Request, res: http.ServerResponse) => {
    console.log(`[UsersRoute] Get user by ID request: ${req.method} ${req.url} -> ${proxyReq.path}`);
    
    if (req.headers['x-user']) {
      proxyReq.setHeader('x-user', req.headers['x-user'] as string);
    }
    
    if (req.headers['authorization']) {
      proxyReq.setHeader('authorization', req.headers['authorization'] as string);
    }
  }
} as any));

/**
 * Rota para obter uma sessão de usuário específica por ID (requer autenticação)
 */
router.get('/session/:id', createProxyMiddleware({
  target: dataManagementAPI,
  changeOrigin: true,
  pathRewrite: {
    '^/users/session/(.*)': '/users/session/$1'
  },
  onProxyReq: (proxyReq: http.ClientRequest, req: Request, res: http.ServerResponse) => {
    console.log(`[UsersRoute] Get user session by ID request: ${req.method} ${req.url} -> ${proxyReq.path}`);
    
    if (req.headers['x-user']) {
      proxyReq.setHeader('x-user', req.headers['x-user'] as string);
    }
    
    if (req.headers['authorization']) {
      proxyReq.setHeader('authorization', req.headers['authorization'] as string);
    }
  }
} as any));

/**
 * Rota para atualizar um usuário (requer autenticação)
 */
router.put('/edit/:id', createProxyMiddleware({
  target: dataManagementAPI,
  changeOrigin: true,
  pathRewrite: {
    '^/users/edit/(.*)': '/users/edit/$1'
  },
  onProxyReq: (proxyReq: http.ClientRequest, req: Request, res: http.ServerResponse) => {
    console.log(`[UsersRoute] Update user request: ${req.method} ${req.url} -> ${proxyReq.path}`);
    
    // Garantir que o corpo da requisição seja enviado corretamente
    if (req.body && req.method === 'PUT') {
      const bodyData = JSON.stringify(req.body);
      console.log(`[UsersRoute] Forwarding update user request body: ${bodyData}`);
      proxyReq.setHeader('Content-Type', 'application/json');
      proxyReq.setHeader('Content-Length', Buffer.byteLength(bodyData));
      proxyReq.write(bodyData);
      proxyReq.end();
    }
    
    if (req.headers['x-user']) {
      proxyReq.setHeader('x-user', req.headers['x-user'] as string);
    }
    
    if (req.headers['authorization']) {
      proxyReq.setHeader('authorization', req.headers['authorization'] as string);
    }
  }
} as any));

/**
 * Rota para desativar um usuário (requer autenticação)
 */
router.put('/deactivate', createProxyMiddleware({
  target: dataManagementAPI,
  changeOrigin: true,
  pathRewrite: {
    '^/users/deactivate': '/users/deactivate'
  },
  onProxyReq: (proxyReq: http.ClientRequest, req: Request, res: http.ServerResponse) => {
    console.log(`[UsersRoute] Deactivate user request: ${req.method} ${req.url} -> ${proxyReq.path}`);
    
    if (req.headers['x-user']) {
      proxyReq.setHeader('x-user', req.headers['x-user'] as string);
    }
    
    if (req.headers['authorization']) {
      proxyReq.setHeader('authorization', req.headers['authorization'] as string);
    }
  }
} as any));

// Função auxiliar para lidar com erros de proxy
const handleProxyError = (err: Error, req: Request, res: Response) => {
  console.error('[UsersRoute] Proxy error:', err);
  res.status(500).json({ message: 'Erro interno do servidor', error: err.message });
};

export default router;