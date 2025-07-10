import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { User } from '@/types/User';
import { updateUser, deactivateUser, getCurrentUserData } from '@/services/Users.service';
import { useAlert } from '@/components/alert/AlertProvider';
import { decodeJwtToken } from '@/utils/jwtDecoder';
import usePasswordValidation from './usePasswordValidation';
import { removeToken, getToken } from '@/utils/token';
import { useSessionIdentifier } from '@/hooks/useSessionIdentifier';
import React from 'react';

const useProfileEdit = () => {
    const [name, setName] = useState('');
    const [email, setEmail] = useState('');
    const [loading, setLoading] = useState(false);
    const [userId, setUserId] = useState<string | null>(null);
    const [isDeactivating, setIsDeactivating] = useState(false);
    const router = useRouter();
    const { addAlert } = useAlert();
    const { 
        password, 
        confirmPassword, 
        error: passwordError, 
        setPassword, 
        setConfirmPassword, 
        validatePasswords,
        validatePasswordStrength
    } = usePasswordValidation();
    useSessionIdentifier();

    // Carregar dados do usuário atual
    useEffect(() => {
        const loadUserData = async () => {
            const userData = decodeJwtToken();
            if (userData?.user?.id) {
                setUserId(String(userData.user.id));
                setLoading(true);
                try {
                    const user = await getCurrentUserData();
                    setName(user.name);
                    setEmail(user.email);
                } catch (error: any) {
                    addAlert(`Erro ao carregar dados do usuário: ${error.message || 'Erro desconhecido'}`, "error");
                } finally {
                    setLoading(false);
                }
            } else {
                addAlert("Usuário não autenticado", "error");
                router.push('/login');
            }
        };

        loadUserData();
    }, [addAlert, router]);

    const updateProfile = async () => {
        if (!userId) {
            addAlert("ID de usuário não encontrado", "error");
            return;
        }

        setLoading(true);
        try {
            const updateData: Partial<User> = { name, email };
            const token = getToken();
            
            // Se uma senha foi fornecida, valide-a e inclua-a na atualização
            if (password) {
                const strengthError = validatePasswordStrength(password);
                if (strengthError) {
                    addAlert(strengthError, "error");
                    setLoading(false);
                    return;
                }
                
                if (!validatePasswords()) {
                    addAlert(passwordError || "As senhas não combinam", "error");
                    setLoading(false);
                    return;
                }
                
                updateData.password = password;
            }

            // Chamar a API para atualizar o usuário
            const response = await updateUser(updateData);
            
            addAlert("Perfil atualizado com sucesso!", "success");

            if(!password){
                router.push('/myconversions');
            }

            // Se a senha foi alterada, invalidar o token atual
            if (password && token) {
                removeToken();
                router.push('/login');
                router.refresh();
            }
        } catch (error: any) {
            addAlert(`Erro ao atualizar perfil: ${error.message || 'Erro desconhecido'}`, "error");
        } finally {
            setLoading(false);
        }
    };

    const deactivateAccount = async () => {
        setIsDeactivating(true);
        try {
            const token = getToken();
            await deactivateUser();
            
            // Invalidar o token após desativar a conta
            if (token) {
                //await invalidateCurrentToken(token, "account_deactivation");
            }
            
            addAlert("Conta desativada com sucesso", "success");
            removeToken();
            router.push('/login');
            router.refresh();
        } catch (error: any) {
            addAlert(`Erro ao desativar conta: ${error.message || 'Erro desconhecido'}`, "error");
        } finally {
            setIsDeactivating(false);
        }
    };

    return {
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
    };
};

export default useProfileEdit;