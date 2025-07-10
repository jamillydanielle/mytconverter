"use client";

import React from 'react';
import { Box, Typography, Paper, CircularProgress } from '@mui/material';
import Input from '@/components/ui/Input';
import Button from '@/components/ui/Button';
import { usePasswordReset } from '@/hooks/usePasswordReset';

const PasswordResetForm: React.FC = () => {
  const {
    isValidating,
    isTokenValid,
    isLoading,
    resetSuccess,
    password,
    confirmPassword,
    passwordError,
    setPassword,
    setConfirmPassword,
    handleSubmit,
    handleBackToLogin
  } = usePasswordReset();

  if (isValidating) {
    return (
      <Paper elevation={3} sx={{ maxWidth: 400, mx: 'auto', mt: 8, p: 4, textAlign: 'center' }}>
        <Typography variant="h4" component="h2" align="center" gutterBottom sx={{ fontWeight: 'bold', color: 'text.primary' }}>
          Redefinir Senha
        </Typography>
        <Box sx={{ display: 'flex', justifyContent: 'center', my: 4 }}>
          <CircularProgress />
        </Box>
        <Typography variant="body1">
          Validando seu token de recuperação...
        </Typography>
      </Paper>
    );
  }

  if (!isTokenValid) {
    return (
      <Paper elevation={3} sx={{ maxWidth: 400, mx: 'auto', mt: 8, p: 4, textAlign: 'center' }}>
        <Typography variant="h4" component="h2" align="center" gutterBottom sx={{ fontWeight: 'bold', color: 'text.primary' }}>
          Token Inválido
        </Typography>
        <Typography variant="body1" sx={{ mb: 3 }}>
          O link de recuperação é inválido ou expirou.
        </Typography>
        <Button
          type="button"
          variant="contained"
          onClick={handleBackToLogin}
          sx={{ mt: 2 }}
        >
          Voltar para Login
        </Button>
      </Paper>
    );
  }

  if (resetSuccess) {
    return (
      <Paper elevation={3} sx={{ maxWidth: 400, mx: 'auto', mt: 8, p: 4, textAlign: 'center' }}>
        <Typography variant="h4" component="h2" align="center" gutterBottom sx={{ fontWeight: 'bold', color: 'text.primary' }}>
          Senha Redefinida!
        </Typography>
        <Typography variant="body1" sx={{ mb: 3 }}>
          Sua senha foi redefinida com sucesso. Você será redirecionado para a página de login em instantes.
        </Typography>
        <Button
          type="button"
          variant="contained"
          onClick={handleBackToLogin}
          sx={{ mt: 2 }}
        >
          Ir para Login
        </Button>
      </Paper>
    );
  }

  return (
    <Paper elevation={3} sx={{ maxWidth: 400, mx: 'auto', mt: 8, p: 4 }}>
      <Typography variant="h4" component="h2" align="center" gutterBottom sx={{ fontWeight: 'bold', color: 'text.primary' }}>
        Redefinir Senha
      </Typography>
      
      <Typography variant="body1" sx={{ mb: 3 }}>
        Digite sua nova senha abaixo.
      </Typography>
      
      <Box component="form" onSubmit={handleSubmit} sx={{ mt: 1 }}>
        <Input
          label="Nova Senha"
          id="password"
          name="password"
          type="password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          placeholder="Digite sua nova senha"
          required
          disabled={isLoading}
          helperText="Mín. 8 caracteres, maiúscula, minúscula, número, especial."
        />
        
        <Input
          label="Confirmar Senha"
          id="confirmPassword"
          name="confirmPassword"
          type="password"
          value={confirmPassword}
          onChange={(e) => setConfirmPassword(e.target.value)}
          placeholder="Confirme sua nova senha"
          required
          disabled={isLoading}
        />
        
        {passwordError && (
          <Typography color="error" variant="body2" sx={{ mt: 1, mb: 2 }}>
            {passwordError}
          </Typography>
        )}
        
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
            "Redefinir Senha"
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
          Cancelar
        </Button>
      </Box>
    </Paper>
  );
};

export default PasswordResetForm;