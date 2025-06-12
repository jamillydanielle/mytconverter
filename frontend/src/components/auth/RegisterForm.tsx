// src/components/auth/RegisterForm.tsx
"use client";

import React from 'react';
import NextLink from 'next/link';
import { Box, Typography, Link as MuiLink, Paper } from '@mui/material';
import Input from '@/components/ui/Input';
import Button from '@/components/ui/Button';
import usePasswordValidation from '@/hooks/usePasswordValidation';
import { useAlert } from '@/components/alert/AlertProvider';
import useRegisterForm from '../../hooks/useRegisterForm'; // Importando o hook useRegisterForm

const RegisterForm: React.FC = () => {
    const {
        name,
        setName,
        email,
        setEmail,
        register,
        loading, // Recuperando o estado de loading do hook
    } = useRegisterForm();

    const {
        password,
        confirmPassword,
        error: passwordValidationError,
        setPassword,
        setConfirmPassword,
        validatePasswords,
        validatePasswordStrength,
    } = usePasswordValidation();

    const { addAlert } = useAlert();

    const handlePasswordChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setPassword(e.target.value);
    };

    const handleConfirmPasswordChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setConfirmPassword(e.target.value);
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();

        // Validação explícita antes de tentar registrar
        const strengthError = validatePasswordStrength(password);
        if (strengthError) {
            addAlert(strengthError, "error");
            return;
        }
        if (password !== confirmPassword) {
            addAlert("As senhas não combinam.", "error");
            return;
        }

        if (!validatePasswords()) {
            addAlert(passwordValidationError || "Por favor, verifique os campos da senha.", "error");
            return;
        }

        await register({ name, email, password });
    };

    return (
        <Paper elevation={3} sx={{ maxWidth: 400, mx: 'auto', mt: 8, p: 4 }}>
            <Typography variant="h4" component="h2" align="center" gutterBottom sx={{ fontWeight: 'bold', color: 'text.primary' }}>
                Crie sua conta
            </Typography>
            <Box component="form" onSubmit={handleSubmit} sx={{ mt: 1 }}>
                <Input
                    label="Nome"
                    id="name"
                    name="name"
                    type="text"
                    value={name}
                    onChange={(e) => setName(e.target.value)}
                    placeholder="Seu nome completo"
                    required
                    autoFocus
                />
                <Input
                    label="Email"
                    id="email"
                    name="email"
                    type="email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    placeholder="seuemail@exemplo.com"
                    required
                />
                <Input
                    label="Senha"
                    id="register-password"
                    name="password"
                    type="password"
                    value={password}
                    onChange={handlePasswordChange}
                    placeholder="Crie uma senha forte"
                    required
                    helperText="Mín. 8 caracteres, maiúscula, minúscula, número, especial."
                />
                <Input
                    label="Confirmar Senha"
                    id="confirmPassword"
                    name="confirmPassword"
                    type="password"
                    value={confirmPassword}
                    onChange={handleConfirmPasswordChange}
                    placeholder="Confirme sua senha"
                    required
                />
                {passwordValidationError && (
                    <Typography color="error" variant="body2" sx={{ mt: 2 }}>
                        {passwordValidationError}
                    </Typography>
                )}
                <Button
                    type="submit"
                    fullWidth
                    variant="contained"
                    sx={{ mt: 3, mb: 2 }}
                    disabled={loading} // Desabilita o botão enquanto o loading estiver ativo
                >
                    Criar conta
                </Button>
                <Typography variant="body2" align="center">
                    Já possui cadastro?{' '}
                    <MuiLink component={NextLink} href="/login" variant="body2">
                        Faça login
                    </MuiLink>
                </Typography>
            </Box>
        </Paper>
    );
};

export default RegisterForm;