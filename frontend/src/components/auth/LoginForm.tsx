"use client";

import React, { useEffect } from 'react';
import NextLink from 'next/link';
import { useRouter } from 'next/navigation';
import { Box, Typography, Link as MuiLink, Paper, Checkbox, FormControlLabel } from '@mui/material';
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
        confirmPassword,
    } = useLoginForm();

    useEffect(() => {
        if (loginSuccess) {
            router.push('/');
            router.refresh();
        }
    }, [loginSuccess, router]);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        await handleLogin(); // handleLogin já é async
    };

    return (
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
                    autoFocus
                    disabled={changePassword && !!credentials.email}
                />

                {!changePassword && (
                    <Input
                        label="Senha"
                        id="password"
                        name="password"
                        type="password"
                        value={credentials.password}
                        onChange={handleChange}
                        placeholder="Sua senha"
                        required
                    />
                )}

                {changePassword && (
                    <>
                        <Input
                            label="Nova Senha"
                            id="currentPassword"
                            name="currentPassword"
                            type="password"
                            value={password}
                            onChange={handleChange}
                            placeholder="Digite sua nova senha"
                            required
                            helperText="Mín. 8 caracteres, maiúscula, minúscula, número, especial."
                        />
                        <Input
                            label="Confirmar Nova Senha"
                            id="newPassword"
                            name="newPassword"
                            type="password"
                            value={confirmPassword}
                            onChange={handleChange}
                            placeholder="Confirme sua nova senha"
                            required
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
                            />
                        }
                        label="Lembrar-me"
                        sx={{ mt: 1 }}
                    />
                )}

                {loginHookError && ( 
                    <Typography color="error" variant="body2" sx={{ mt: 2 }}>
                        {loginHookError}
                    </Typography>
                )}

                <Button
                    type="submit"
                    fullWidth
                    variant="contained"
                    sx={{ mt: 3, mb: 2 }}
                >
                    {changePassword ? "Alterar Senha e Entrar" : "Login"}
                </Button>

                {!changePassword && (
                    <Typography variant="body2" align="center">
                        Não possui cadastro?{' '}
                        <MuiLink component={NextLink} href="/register" variant="body2">
                            Crie aqui
                        </MuiLink>
                    </Typography>
                )}
            </Box>
        </Paper>
    );
};

export default LoginForm;