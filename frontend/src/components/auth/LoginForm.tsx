// components/auth/LoginForm.tsx
"use client";

import React, { useEffect } from 'react';
import NextLink from 'next/link';
import { useRouter } from 'next/navigation';
import { Box, Typography, Link as MuiLink, Paper, Checkbox, FormControlLabel } from '@mui/material';
import Input from '@/components/ui/Input'; // Seu componente de Input MUI
import Button from '@/components/ui/Button'; // Seu componente de Button MUI
import { useLoginForm } from '@/hooks/useLoginForm'; // Seu hook!
// useAlert será pego pelo useLoginForm se ele o utilizar internamente,
// ou você pode chamá-lo aqui se precisar de alertas adicionais.

const LoginForm: React.FC = () => {
    const router = useRouter();
    const {
        credentials,
        error: loginHookError, // Renomeado para clareza
        handleChange,
        handleLogin,
        loginSuccess,
        changePassword,
        password, // Nova senha (do usePasswordValidation, via useLoginForm)
        confirmPassword, // Confirmação da nova senha (do usePasswordValidation, via useLoginForm)
    } = useLoginForm();

    useEffect(() => {
        if (loginSuccess) {
            // O useAlert já deve ter sido chamado dentro do useLoginForm ou handleLogin
            // Redirecionamento após sucesso
            router.push('/'); // Ou deixe o useSessionIdentifier gerenciar a rota pós-login
            router.refresh(); // Garante que o useSessionIdentifier seja reavaliado com o novo token
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
                    disabled={changePassword && !!credentials.email} // Desabilitar se já tiver email e for mudar senha
                />

                {!changePassword && (
                    <Input
                        label="Senha"
                        id="password" // Este é o credentials.password
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
                        {/* No seu useLoginForm, 'currentPassword' é o campo para a NOVA senha */}
                        <Input
                            label="Nova Senha"
                            id="currentPassword"
                            name="currentPassword" // Mapeia para setPassword do usePasswordValidation
                            type="password"
                            value={password} // Estado 'password' do usePasswordValidation
                            onChange={handleChange}
                            placeholder="Digite sua nova senha"
                            required
                            helperText="Mín. 8 caracteres, maiúscula, minúscula, número, especial."
                        />
                        {/* E 'newPassword' é o campo para CONFIRMAR a nova senha */}
                        <Input
                            label="Confirmar Nova Senha"
                            id="newPassword"
                            name="newPassword" // Mapeia para setConfirmPassword do usePasswordValidation
                            type="password"
                            value={confirmPassword} // Estado 'confirmPassword' do usePasswordValidation
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
                                onChange={handleChange} // Seu hook já lida com 'checked'
                                color="primary"
                            />
                        }
                        label="Lembrar-me"
                        sx={{ mt: 1 }}
                    />
                )}

                {loginHookError && ( // Exibe o erro gerenciado pelo useLoginForm (que inclui o passwordError)
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