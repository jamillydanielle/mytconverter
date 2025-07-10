import { useState, useEffect } from 'react';
import { useRouter, useSearchParams } from 'next/navigation';
import { useAlert } from '@/components/alert/AlertProvider';
import { validateResetToken, resetPassword } from '@/services/PasswordReset.service';
import usePasswordValidation from './usePasswordValidation';

export const usePasswordReset = () => {
  const [token, setToken] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [isValidating, setIsValidating] = useState(true);
  const [isTokenValid, setIsTokenValid] = useState(false);
  const [resetSuccess, setResetSuccess] = useState(false);
  const { addAlert } = useAlert();
  const router = useRouter();
  const searchParams = useSearchParams();
  
  const { 
    password, 
    confirmPassword, 
    error: passwordError, 
    setPassword, 
    setConfirmPassword, 
    validatePasswords,
    validatePasswordStrength
  } = usePasswordValidation();

  // Extrair e validar o token quando o componente é montado
  useEffect(() => {
    let tokenValue = searchParams?.get('token');
    
    // Se não encontrar o token normalmente, tentar extrair da URL diretamente
    if (!tokenValue && typeof window !== 'undefined') {
      const queryString = window.location.search;
      // Procurar por token= ou token%3D seguido pelo valor do token
      const match = queryString.match(/[?&](token(?:%3D|=))([^&]*)/);
      if (match) {
        tokenValue = match[2]; // O grupo 2 contém o valor do token
      }
    }
    
    if (!tokenValue) {
      console.error("No token found in URL parameters");
      addAlert('Token de recuperação não fornecido', 'error');
      setIsValidating(false);
      return;
    }
    
    // Log the raw token from URL
    console.log("Raw token from URL:", tokenValue);
    
    // Check if token needs decoding
    const decodedToken = decodeURIComponent(tokenValue);
    console.log("Decoded token:", decodedToken);
    
    // Use the decoded token
    setToken(decodedToken);
    
    const validateToken = async () => {
      try {
        console.log("Sending token for validation:", decodedToken);
        const response = await validateResetToken(decodedToken);
        console.log("Token validation response:", response);
        setIsTokenValid(true);
      } catch (error: any) {
        console.error("Token validation error:", error);
        console.error("Error details:", error.message, error.status, error.data);
        addAlert(error.message || 'Token inválido ou expirado', 'error');
        setIsTokenValid(false);
      } finally {
        setIsValidating(false);
      }
    };
    
    validateToken();
  }, [searchParams, addAlert]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!token) {
      addAlert('Token de recuperação não fornecido', 'error');
      return;
    }
    
    // Validar senha
    const strengthError = validatePasswordStrength(password);
    if (strengthError) {
      addAlert(strengthError, 'error');
      return;
    }
    
    if (!validatePasswords()) {
      addAlert(passwordError || 'As senhas não coincidem', 'error');
      return;
    }
    
    setIsLoading(true);
    
    try {
      console.log("Resetting password with token:", token);
      await resetPassword(token, password);
      setResetSuccess(true);
      addAlert('Senha redefinida com sucesso!', 'success');
      
      // Redirecionar para a página de login após 3 segundos
      setTimeout(() => {
        router.push('/login');
      }, 3000);
    } catch (error: any) {
      console.error("Password reset failed:", error);
      addAlert(error.message || 'Erro ao redefinir senha', 'error');
    } finally {
      setIsLoading(false);
    }
  };

  const handleBackToLogin = () => {
    router.push('/login');
  };

  return {
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
  };
};