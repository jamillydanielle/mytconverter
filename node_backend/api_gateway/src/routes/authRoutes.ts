import express, { Request, Response } from 'express';
import { createProxyMiddleware } from 'http-proxy-middleware';
import http from 'http';

const router = express.Router();
const dataManagementAPI = process.env.DATA_MANAGEMENT_API || 'http://datamanagement:8080';

/**
 * Endpoint para verificar se o servidor está funcionando
 */
router.get('/health', (req: Request, res: Response) => {
  res.status(200).json({ status: 'ok', message: 'Auth service is running' });
});

/**
 * Rota específica para login
 */
router.post('/login', createProxyMiddleware({
  target: dataManagementAPI,
  changeOrigin: true,
  pathRewrite: {
    '^/auth/login': '/auth/login'
  },
  onProxyReq: (proxyReq: http.ClientRequest, req: Request, res: http.ServerResponse) => {
    console.log(`[AuthRoutes] Login request: ${req.method} ${req.url} -> ${proxyReq.path}`);
    
    // Garantir que o corpo da requisição seja enviado corretamente
    if (req.body && req.method === 'POST') {
      const bodyData = JSON.stringify(req.body);
      console.log(`[AuthRoutes] Forwarding login request body: ${bodyData}`);
      proxyReq.setHeader('Content-Type', 'application/json');
      proxyReq.setHeader('Content-Length', Buffer.byteLength(bodyData));
      proxyReq.write(bodyData);
      proxyReq.end();
    }
    
    // Log all headers being sent
    console.log(`[AuthRoutes] Login proxy request headers:`, proxyReq.getHeaders());
  },
  onProxyRes: (proxyRes: http.IncomingMessage, req: Request, res: http.ServerResponse) => {
    console.log(`[AuthRoutes] Login proxy response: ${proxyRes.statusCode}`);
    console.log(`[AuthRoutes] Login proxy response headers:`, proxyRes.headers);
    
    // Capture response body for logging
    let responseBody = '';
    proxyRes.on('data', (chunk) => {
      responseBody += chunk;
    });
    
    proxyRes.on('end', () => {
      if (responseBody) {
        try {
          const jsonBody = JSON.parse(responseBody);
          console.log(`[AuthRoutes] Login proxy response body:`, jsonBody);
        } catch (e) {
          console.log(`[AuthRoutes] Login proxy response body (raw):`, responseBody);
        }
      }
    });
  },
  onError: (err: Error, req: Request, res: Response) => {
    console.error('[AuthRoutes] Login proxy error:', err);
    res.status(500).json({ message: 'Erro interno do servidor', error: err.message });
  }
} as any));

/**
 * Endpoint para verificar se o usuário está logado
 */
router.get('/is-logged-in', createProxyMiddleware({
  target: dataManagementAPI,
  changeOrigin: true,
  pathRewrite: {
    '^/auth/is-logged-in': '/auth/is-logged-in'
  },
  onProxyReq: (proxyReq: http.ClientRequest, req: Request, res: http.ServerResponse) => {
    console.log(`[AuthRoutes] Is logged in request: ${req.method} ${req.url} -> ${proxyReq.path}`);
    
    if (req.headers['x-user']) {
      proxyReq.setHeader('x-user', req.headers['x-user'] as string);
    }
    
    if (req.headers['authorization']) {
      proxyReq.setHeader('authorization', req.headers['authorization'] as string);
    }
  }
} as any));

/**
 * Endpoint para alterar a senha
 */
router.put('/change-password/:email', createProxyMiddleware({
  target: dataManagementAPI,
  changeOrigin: true,
  pathRewrite: {
    '^/auth/change-password/(.*)': '/auth/change-password/$1'
  },
  onProxyReq: (proxyReq: http.ClientRequest, req: Request, res: http.ServerResponse) => {
    console.log(`[AuthRoutes] Change password request: ${req.method} ${req.url} -> ${proxyReq.path}`);
    
    // Garantir que o corpo da requisição seja enviado corretamente
    if (req.body && req.method === 'PUT') {
      const bodyData = JSON.stringify(req.body);
      console.log(`[AuthRoutes] Forwarding change password request body: ${bodyData}`);
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
 * Endpoint para logout (simplificado - sem lista negra)
 */
router.post('/logout', async (req: Request, res: Response) => {
  try {
    // Limpar o cookie do token, se existir
    res.clearCookie('token');
    
    return res.status(200).json({ message: 'Logout realizado com sucesso' });
  } catch (error) {
    console.error('[AuthRoutes] Erro ao realizar logout:', error);
    return res.status(500).json({ message: 'Erro ao processar a solicitação de logout' });
  }
});

export default router;