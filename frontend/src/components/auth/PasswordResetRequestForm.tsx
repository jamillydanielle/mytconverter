"use client";

import React from 'react';
import { Box, Typography, Paper, CircularProgress } from '@mui/material';
import Input from '@/components/ui/Input';
import Button from '@/components/ui/Button';
import { usePasswordResetRequest } from '@/hooks/usePasswordResetRequest';

const PasswordResetRequestForm: React.FC = () => {
  const {
    email,
    isLoading,
    requestSent,
    handleEmailChange,
    handleSubmit,
    handleBackToLogin
  } = usePasswordResetRequest();

  return (
    <Paper elevation={3} sx={{ maxWidth: 400, mx: 'auto', mt: 8, p: 4 }}>
      <Typography variant="h4" component="h2" align="center" gutterBottom sx={{ fontWeight: 'bold', color: 'text.primary' }}>
        Recuperar Senha
      </Typography>
      
      {!requestSent ? (
        <>
          <Typography variant="body1" sx={{ mb: 3 }}>
            Digite seu email para receber um link de recuperação de senha.
          </Typography>
          
          <Box component="form" onSubmit={handleSubmit} sx={{ mt: 1 }}>
            <Input
              label="Email"
              id="email"
              name="email"
              type="email"
              value={email}
              onChange={handleEmailChange}
              placeholder="seuemail@exemplo.com"
              required
              disabled={isLoading}
            />
            
            <Button
              type="submit"
              variant="contained"
              fullWidth
              disabled={isLoading}
              sx={{ mt: 3, mb: 2 }}
            >
              {isLoading ? (
                <CircularProgress size={24} color="inherit" />
              ) : (
                "Enviar Link de Recuperação"
              )}
            </Button>
            
            <Button
              type="button"
              variant="outlined"
              fullWidth
              onClick={handleBackToLogin}
              disabled={isLoading}
              sx={{ mt: 1 }}
            >
              Voltar para Login
            </Button>
          </Box>
        </>
      ) : (
        <Box sx={{ textAlign: 'center' }}>
          <Typography variant="body1" sx={{ mb: 3 }}>
            Se o email estiver cadastrado, você receberá um link para redefinir sua senha.
          </Typography>
          <Typography variant="body2" sx={{ mb: 3, color: 'text.secondary' }}>
            Verifique sua caixa de entrada e pasta de spam.
          </Typography>
          
          <Button
            type="button"
            variant="outlined"
            onClick={handleBackToLogin}
            sx={{ mt: 2 }}
          >
            Voltar para Login
          </Button>
        </Box>
      )}
    </Paper>
  );
};

export default PasswordResetRequestForm;