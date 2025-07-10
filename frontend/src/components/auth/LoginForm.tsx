"use client";

import React, { useEffect } from 'react';
import { useRouter } from 'next/navigation';
import NextLink from 'next/link';
import { Box, Typography, Paper, Divider, FormControlLabel, Checkbox, CircularProgress, Dialog, DialogTitle, DialogContent, DialogContentText, DialogActions, Link } from '@mui/material';
import Input from '@/components/ui/Input';
import Button from '@/components/ui/Button';
import { useLoginForm } from '@/hooks/useLoginForm'; 

const LoginForm: React.FC = () => {
    const router = useRouter();
    const {
        credentials,
        error: loginHookError,
        handleChange,
        handleLogin,
        loginSuccess,
        changePassword,
        password,
        setPassword,
        confirmPassword,
        setConfirmPassword,
        isLoading,
        showActivateDialog,
        handleActivateAccount,
        handleCancelReactivation
    } = useLoginForm();

    useEffect(() => {
        console.log("[LoginForm] Componente montado");
        return () => {
            console.log("[LoginForm] Componente desmontado");
        };
    }, []);

    useEffect(() => {
        console.log("[LoginForm] Estado de loginSuccess alterado:", loginSuccess);
        if (loginSuccess) {
            console.log("[LoginForm] Login bem-sucedido, redirecionando para a página inicial");
            router.push('/');
            router.refresh();
        }
    }, [loginSuccess, router]);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        console.log("[LoginForm] Formulário enviado");
        await handleLogin(); // handleLogin já é async
    };

    console.log("[LoginForm] Renderizando com estado:", { 
        changePassword, 
        hasError: !!loginHookError, 
        isLoading,
        showActivateDialog
    });

    return (
        <>
            <Paper elevation={3} sx={{ maxWidth: 400, mx: 'auto', mt: 8, p: 4 }}>
                <Typography variant="h4" component="h2" align="center" gutterBottom sx={{ fontWeight: 'bold', color: 'text.primary' }}>
                    {changePassword ? "Alterar Senha" : "Login"}
                </Typography>
                <Box component="form" onSubmit={handleSubmit} sx={{ mt: 1 }}>
                    <Input
                        label="Email"
                        id="email"
                        name="email"
                        type="email"
                        value={credentials.email}
                        onChange={handleChange}
                        placeholder="seuemail@exemplo.com"
                        required
                        disabled={isLoading || changePassword}
                    />
                    
                    {!changePassword ? (
                        <>
                            <Input
                                label="Senha"
                                id="password"
                                name="password"
                                type="password"
                                value={credentials.password}
                                onChange={handleChange}
                                placeholder="Sua senha"
                                required
                                disabled={isLoading}
                            />
                            <Box sx={{ textAlign: 'right', mt: 1, mb: 2 }}>
                                <Link 
                                    component={NextLink} 
                                    href="/forgot-password" 
                                    variant="body2"
                                    underline="hover"
                                >
                                    Esqueceu sua senha?
                                </Link>
                            </Box>
                        </>
                    ) : (
                        <>
                            <Input
                                label="Nova Senha"
                                id="password"
                                name="password"
                                type="password"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                placeholder="Nova senha"
                                required
                                disabled={isLoading}
                            />
                            <Input
                                label="Confirmar Nova Senha"
                                id="confirmPassword"
                                name="confirmPassword"
                                type="password"
                                value={confirmPassword}
                                onChange={(e) => setConfirmPassword(e.target.value)}
                                placeholder="Confirme sua nova senha"
                                required
                                disabled={isLoading}
                            />
                        </>
                    )}
                    
                    {!changePassword && (
                        <FormControlLabel
                            control={
                                <Checkbox
                                    name="rememberMe"
                                    checked={credentials.rememberMe}
                                    onChange={handleChange}
                                    color="primary"
                                    disabled={isLoading}
                                />
                            }
                            label="Lembrar-me"
                        />
                    )}
                    
                    {loginHookError && ( 
                        <Typography color="error" variant="body2" sx={{ mt: 2 }}>
                            {loginHookError}
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
                            changePassword ? "Alterar Senha e Entrar" : "Login"
                        )}
                    </Button>
                    
                    <Divider sx={{ my: 2 }} />
                    
                    <Typography variant="body2" align="center">
                        Não possui uma conta?{' '}
                        <NextLink href="/register" passHref>
                            Registre-se
                        </NextLink>
                    </Typography>
                </Box>
            </Paper>

            {/* Diálogo de reativação de conta */}
            <Dialog
                open={showActivateDialog}
                onClose={handleCancelReactivation}
                aria-labelledby="activate-dialog-title"
                aria-describedby="activate-dialog-description"
            >
                <DialogTitle id="activate-dialog-title">
                    Conta Desativada
                </DialogTitle>
                <DialogContent>
                    <DialogContentText id="activate-dialog-description">
                        Esta conta está desativada. Deseja reativá-la para continuar?
                    </DialogContentText>
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleCancelReactivation} color="inherit" disabled={isLoading}>
                        Cancelar
                    </Button>
                    <Button 
                        onClick={handleActivateAccount} 
                        color="primary" 
                        variant="contained"
                        disabled={isLoading}
                        autoFocus
                    >
                        {isLoading ? <CircularProgress size={24} color="inherit" /> : "Reativar Conta"}
                    </Button>
                </DialogActions>
            </Dialog>
        </>
    );
};

export default LoginForm;