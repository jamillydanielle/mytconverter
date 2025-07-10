"use client";

import React, { useState } from 'react';
import { Box, Typography, Paper, Divider, Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle } from '@mui/material';
import Input from '@/components/ui/Input';
import Button from '@/components/ui/Button';
import useProfileEdit from '@/hooks/useProfileEdit';

const ProfileEditForm: React.FC = () => {
    const {
        name,
        setName,
        email,
        setEmail,
        password,
        setPassword,
        confirmPassword,
        setConfirmPassword,
        passwordError,
        loading,
        isDeactivating,
        updateProfile,
        deactivateAccount,
        validatePasswordStrength
    } = useProfileEdit();

    const [openConfirmDialog, setOpenConfirmDialog] = useState(false);
    const [passwordStrengthError, setPasswordStrengthError] = useState<string | null>(null);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        await updateProfile();
    };

    const handlePasswordChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const newPassword = e.target.value;
        setPassword(newPassword);
        
        if (newPassword) {
            const error = validatePasswordStrength(newPassword);
            setPasswordStrengthError(error);
        } else {
            setPasswordStrengthError(null);
        }
    };

    const handleDeactivateClick = () => {
        setOpenConfirmDialog(true);
    };

    const handleConfirmDeactivate = async () => {
        setOpenConfirmDialog(false);
        await deactivateAccount();
    };

    return (
        <>
            <Paper elevation={3} sx={{ maxWidth: 600, mx: 'auto', mt: 4, p: 4 }}>
                <Typography variant="h4" component="h2" align="center" gutterBottom sx={{ fontWeight: 'bold', color: 'text.primary' }}>
                    Editar Perfil
                </Typography>
                <Box component="form" onSubmit={handleSubmit} sx={{ mt: 3 }}>
                    <Input
                        label="Nome"
                        id="name"
                        name="name"
                        type="text"
                        value={name}
                        onChange={(e) => setName(e.target.value)}
                        placeholder="Seu nome completo"
                        required
                        disabled={loading}
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
                        disabled={loading}
                    />
                    
                    <Divider sx={{ my: 3 }} />
                    
                    <Typography variant="h6" gutterBottom>
                        Alterar Senha (opcional)
                    </Typography>
                    <Input
                        label="Nova Senha"
                        id="password"
                        name="password"
                        type="password"
                        value={password}
                        onChange={handlePasswordChange}
                        placeholder="Deixe em branco para manter a senha atual"
                        disabled={loading}
                        helperText="Mín. 8 caracteres, maiúscula, minúscula, número, especial."
                    />
                    {passwordStrengthError && (
                        <Typography color="error" variant="body2" sx={{ mt: -1, mb: 2 }}>
                            {passwordStrengthError}
                        </Typography>
                    )}
                    {password && (
                        <Input
                            label="Confirmar Nova Senha"
                            id="confirmPassword"
                            name="confirmPassword"
                            type="password"
                            value={confirmPassword}
                            onChange={(e) => setConfirmPassword(e.target.value)}
                            placeholder="Confirme sua nova senha"
                            disabled={loading}
                        />
                    )}
                    {passwordError && (
                        <Typography color="error" variant="body2" sx={{ mt: 1 }}>
                            {passwordError}
                        </Typography>
                    )}
                    
                    <Box sx={{ display: 'flex', justifyContent: 'space-between', mt: 4 }}>
                        <Button
                            type="button"
                            variant="outlined"
                            color="error"
                            onClick={handleDeactivateClick}
                            disabled={loading || isDeactivating}
                            sx={{ width: '48%' }}
                        >
                            Desativar Conta
                        </Button>
                        <Button
                            type="submit"
                            variant="contained"
                            disabled={loading}
                            sx={{ width: '48%' }}
                        >
                            Salvar Alterações
                        </Button>
                    </Box>
                </Box>
            </Paper>

            <Dialog
                open={openConfirmDialog}
                onClose={() => setOpenConfirmDialog(false)}
            >
                <DialogTitle>Confirmar Desativação</DialogTitle>
                <DialogContent>
                    <DialogContentText>
                        Tem certeza que deseja desativar sua conta? Esta ação não pode ser desfeita facilmente.
                    </DialogContentText>
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setOpenConfirmDialog(false)} color="primary">
                        Cancelar
                    </Button>
                    <Button onClick={handleConfirmDeactivate} color="error" variant="contained">
                        Desativar
                    </Button>
                </DialogActions>
            </Dialog>
        </>
    );
};

export default ProfileEditForm;