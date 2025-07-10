import { useState, useCallback } from 'react';
import { useRouter } from 'next/navigation';
import usePasswordValidation from './usePasswordValidation';
import { setToken } from "@/utils/token";
import { changeUserPassword, loginUser, activateAccount } from "@/services/Auth.service";
import { useAlert } from "@/components/alert/AlertProvider";

export const useLoginForm = () => {
    const [credentials, setCredentials] = useState({ email: "", rememberMe: false, password: "" });
    const { password, confirmPassword, error: passwordError, setPassword, validatePasswordStrength, setConfirmPassword, validatePasswords } = usePasswordValidation();
    const [error, setError] = useState("");
    const [tempToken, setTempToken] = useState("");
    const [changePassword, setChangePassword] = useState(false);
    const [loginSuccess, setLoginSuccess] = useState(false);
    const [isLoading, setIsLoading] = useState(false);
    const [showActivateDialog, setShowActivateDialog] = useState(false);
    const [deactivatedEmail, setDeactivatedEmail] = useState("");
    const { addAlert } = useAlert();
    const router = useRouter();

    const handleChange = useCallback((e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value, checked, type } = e.target;
        console.log(`[LoginForm] Campo alterado: ${name}, valor: ${type === 'checkbox' ? checked : value}`);
        
        if (type === "checkbox") {
            setCredentials(prev => ({ ...prev, [name]: checked }));
        } else {
            setCredentials(prev => ({ ...prev, [name]: value }));
        }
        
        setError("");
    }, []);

    const validateInput = useCallback(() => {
        console.log("[LoginForm] Validando entrada:", { 
            email: credentials.email, 
            passwordProvided: !!credentials.password,
            changePassword
        });
        
        if (credentials.email.trim() === "" || (!changePassword && credentials.password.trim() === "")) {
            const errorMsg = "Informe email e senha";
            console.log("[LoginForm] Erro de validação:", errorMsg);
            addAlert(errorMsg, "error");
            setError(errorMsg);
            return false;
        }
        
        if (changePassword && !validatePasswords()) {
            console.log("[LoginForm] Erro de validação de senha:", passwordError);
            setError(passwordError);
            return false;
        }
        console.log("[LoginForm] Validação bem-sucedida");
        return true;
    }, [credentials.email, credentials.password, changePassword, validatePasswords, passwordError, addAlert]);

    const handleLogin = async () => {
        console.log("[LoginForm] Iniciando processo de login");
        if (!validateInput()) {
            console.log("[LoginForm] Validação falhou, abortando login");
            return;
        }
        
        setIsLoading(true);
        setError("");
        console.log("[LoginForm] Enviando credenciais para autenticação");
        try {
            let result;
            
            if (changePassword) {
                console.log("[LoginForm] Alterando senha antes do login");
                await changeUserPassword(credentials.email, password, tempToken);
                console.log("[LoginForm] Senha alterada com sucesso, tentando login com nova senha");
                result = await loginUser(credentials.email, password, credentials.rememberMe);
            } else {
                console.log("[LoginForm] Tentando login normal");
                result = await loginUser(credentials.email, credentials.password, credentials.rememberMe);
            }
            
            console.log("[LoginForm] Resultado do login:", result);
            
            if (result.message === "A senha precisa ser trocada") {
                console.log("[LoginForm] Senha precisa ser trocada");
                addAlert("A senha precisa ser trocada", "warning");
                setChangePassword(true);
                setTempToken(result.token);
                setError("");
            } else if (result.deactivated) {
                console.log("[LoginForm] Conta desativada");
                setDeactivatedEmail(result.email || credentials.email);
                setShowActivateDialog(true);
                setError("");
            } else {
                if (result.token) {
                    console.log("[LoginForm] Login bem-sucedido, token recebido");
                    setToken(result.token);
                    setLoginSuccess(true);
                    addAlert("Login realizado com sucesso", "success");
                } else {
                    const errorMsg = "Login realizado com sucesso, mas nenhum token foi recebido";
                    console.error("[LoginForm]", errorMsg);
                    setError(errorMsg);
                    addAlert(errorMsg, "warning");
                }
            }
        } catch (error) {
            console.error("[LoginForm] Erro durante o login:", error);
            
            if (error instanceof Error) {
                if (error.message.includes("Failed to fetch") || error.message.includes("NetworkError") || error.message.includes("Erro na conexão")) {
                    const errorMsg = "Erro de conexão com o servidor. Verifique sua conexão de internet ou tente novamente mais tarde.";
                    console.error("[LoginForm]", errorMsg);
                    addAlert(errorMsg, "error");
                    setError(errorMsg);
                } else if (error.message === "A senha precisa ser trocada") {
                    console.log("[LoginForm] Senha precisa ser trocada");
                    addAlert("A senha precisa ser trocada", "warning");
                    setChangePassword(true);
                    setError("");
                } else {
                    console.error("[LoginForm] Erro específico:", error.message);
                    addAlert(error.message, "error");
                    setError(error.message);
                }
            } else {
                const errorMsg = "Ocorreu um erro desconhecido durante o login";
                console.error("[LoginForm]", errorMsg);
                addAlert(errorMsg, "error");
                setError(errorMsg);
            }
        } finally {
            console.log("[LoginForm] Processo de login finalizado");
            setIsLoading(false);
        }
    };

    const handleActivateAccount = async () => {
        setIsLoading(true);
        try {
            // Passando a senha junto com o email para reativar a conta
            await activateAccount(deactivatedEmail, credentials.password);
            addAlert("Conta reativada com sucesso!", "success");
            setShowActivateDialog(false);
            
            // Tentar fazer login automaticamente após reativar a conta
            try {
                console.log("[LoginForm] Tentando login automático após reativação");
                const result = await loginUser(deactivatedEmail, credentials.password, credentials.rememberMe);
                
                if (result.token) {
                    console.log("[LoginForm] Login automático bem-sucedido após reativação");
                    setToken(result.token);
                    setLoginSuccess(true);
                    addAlert("Login realizado com sucesso", "success");
                } else {
                    console.warn("[LoginForm] Login automático após reativação não retornou token");
                    // Não exibimos erro aqui, apenas deixamos o usuário fazer login manualmente
                }
            } catch (loginError) {
                console.error("[LoginForm] Erro no login automático após reativação:", loginError);
                // Não exibimos erro aqui, apenas deixamos o usuário fazer login manualmente
            }
        } catch (error) {
            console.error("[LoginForm] Erro ao reativar conta:", error);
            if (error instanceof Error) {
                if (error.message.includes("Credenciais inválidas")) {
                    addAlert("Senha incorreta. Não foi possível reativar a conta.", "error");
                } else {
                    addAlert(`Erro ao reativar conta: ${error.message}`, "error");
                }
            } else {
                addAlert("Erro desconhecido ao reativar conta", "error");
            }
        } finally {
            setIsLoading(false);
        }
    };

    const handleCancelReactivation = () => {
        setShowActivateDialog(false);
        setError("Login cancelado. A conta permanece desativada.");
    };

    return {
        credentials,
        error: error || passwordError, 
        handleChange, 
        handleLogin, 
        password, 
        confirmPassword,
        setPassword,
        setConfirmPassword,
        changePassword,
        loginSuccess,
        isLoading,
        showActivateDialog,
        handleActivateAccount,
        handleCancelReactivation
    };
};