import { useState } from 'react';
import { useRouter } from 'next/navigation';
import { useAlert } from '@/components/alert/AlertProvider';
import { requestPasswordReset } from '@/services/PasswordReset.service';

export const usePasswordResetRequest = () => {
  const [email, setEmail] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [requestSent, setRequestSent] = useState(false);
  const { addAlert } = useAlert();
  const router = useRouter();

  const handleEmailChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setEmail(e.target.value);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!email || !email.trim()) {
      addAlert('Por favor, informe seu email', 'error');
      return;
    }
    
    setIsLoading(true);
    
    try {
      await requestPasswordReset(email);
      setRequestSent(true);
      addAlert('Se o email estiver cadastrado, você receberá um link para redefinir sua senha', 'success');
    } catch (error: any) {
      // Mesmo em caso de erro, não revelamos se o email existe ou não
      addAlert('Se o email estiver cadastrado, você receberá um link para redefinir sua senha', 'success');
      setRequestSent(true);
    } finally {
      setIsLoading(false);
    }
  };

  const handleBackToLogin = () => {
    router.push('/login');
  };

  return {
    email,
    isLoading,
    requestSent,
    handleEmailChange,
    handleSubmit,
    handleBackToLogin
  };
};