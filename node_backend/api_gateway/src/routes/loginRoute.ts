import express, { Request, Response } from 'express';
import axios from 'axios';

const router = express.Router();

/**
 * Endpoint para verificar se o servidor está funcionando
 */
router.get('/health', (req: Request, res: Response) => {
  res.status(200).json({ status: 'ok', message: 'Login service is running' });
});

/**
 * Rota específica para login que não usa o proxy
 * Isso pode ajudar a contornar problemas com o proxy
 */
router.post('/login', async (req: Request, res: Response) => {
  try {
    console.log('[LoginRoute] Recebida requisição de login:', JSON.stringify(req.body));
    
    const { email, password, rememberMe } = req.body;
    
    if (!email || !password) {
      console.log('[LoginRoute] Requisição de login inválida - faltando email ou senha');
      return res.status(400).json({ message: 'Email e senha são obrigatórios' });
    }
    
    console.log('[LoginRoute] Enviando requisição para o serviço de autenticação');
    
    // Fazer a requisição diretamente para o serviço de autenticação
    const dataManagementUrl = process.env.DATA_MANAGEMENT_API || 'http://datamanagement:8080';
    const loginUrl = `${dataManagementUrl}/auth/login`;
    
    console.log(`[LoginRoute] URL de login: ${loginUrl}`);
    console.log(`[LoginRoute] Dados de login: ${JSON.stringify({ email, password: '***', rememberMe })}`);
    
    try {
      console.log('[LoginRoute] Iniciando requisição axios para o serviço de autenticação');
      
      const axiosConfig = {
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json'
        },
        timeout: 10000 // 10 segundos de timeout
      };
      
      console.log('[LoginRoute] Configuração axios:', JSON.stringify(axiosConfig));
      
      const response = await axios.post(loginUrl, {
        email,
        password,
        rememberMe
      }, axiosConfig);
      
      console.log('[LoginRoute] Resposta recebida do serviço de autenticação - Status:', response.status);
      console.log('[LoginRoute] Resposta recebida do serviço de autenticação - Headers:', JSON.stringify(response.headers));
      console.log('[LoginRoute] Dados da resposta:', JSON.stringify(response.data));
      
      // Retornar a resposta para o cliente
      return res.status(response.status).json(response.data);
    } catch (error: any) {
      console.error('[LoginRoute] Erro ao fazer login:', error.message);
      
      // Logs detalhados para depuração
      if (error.code) {
        console.error('[LoginRoute] Código de erro:', error.code);
      }
      
      if (error.syscall) {
        console.error('[LoginRoute] Syscall:', error.syscall);
      }
      
      if (error.address) {
        console.error('[LoginRoute] Endereço:', error.address);
      }
      
      if (error.port) {
        console.error('[LoginRoute] Porta:', error.port);
      }
      
      if (error.config) {
        console.error('[LoginRoute] Configuração da requisição:', {
          url: error.config.url,
          method: error.config.method,
          headers: error.config.headers,
          timeout: error.config.timeout
        });
      }
      
      // Se temos uma resposta do serviço de autenticação, repassar para o cliente
      if (error.response) {
        console.log('[LoginRoute] Erro com resposta do serviço - Status:', error.response.status);
        console.log('[LoginRoute] Erro com resposta do serviço - Headers:', JSON.stringify(error.response.headers));
        console.log('[LoginRoute] Dados do erro:', JSON.stringify(error.response.data));
        
        // Caso especial para senha que precisa ser trocada
        if (error.response.status === 403 && error.response.data?.message === 'A senha precisa ser trocada') {
          return res.status(403).json(error.response.data);
        }
        
        return res.status(error.response.status).json(error.response.data);
      }
      
      // Erro de conexão ou outro erro
      console.error('[LoginRoute] Erro detalhado:', error);
      return res.status(500).json({ 
        message: 'Erro ao conectar ao serviço de autenticação',
        error: error.message,
        code: error.code || 'UNKNOWN_ERROR'
      });
    }
  } catch (error: any) {
    console.error('[LoginRoute] Erro não tratado:', error);
    return res.status(500).json({ 
      message: 'Erro interno do servidor',
      error: error.message
    });
  }
});

export default router;