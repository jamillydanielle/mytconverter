import { useState, useEffect, useCallback } from 'react';

interface UsePasswordValidationResult {
    password: string;
    confirmPassword: string;
    error: string;
    setPassword: (password: string) => void;
    setConfirmPassword: (confirmPassword: string) => void;
    validatePasswords: () => boolean;
    validatePasswordStrength : (pwd: string) => string | null;
}

const usePasswordValidation = (): UsePasswordValidationResult => {
    const [password, setPassword] = useState<string>('');
    const [confirmPassword, setConfirmPassword] = useState<string>('');
    const [error, setError] = useState<string>('');

    const validatePasswordStrength = useCallback((pwd: string): string | null => {
        if (pwd.length === 0) return null;
        if (pwd.length < 8) return "A senha precisa ter pelo menos 8 caracteres.";
        if (!/[A-Z]/.test(pwd)) return "A senha precisa ter pelo menos uma letra maiuscula.";
        if (!/[a-z]/.test(pwd)) return "A senha precisa ter pelo menos uma letra minuscula.";
        if (!/\d/.test(pwd)) return "A senha precisa ter pelo menos um numero";
        if (!/[@$!%*?&]/.test(pwd)) return "A senha precisa ter pelo menos um caractere especial (@$!%*?&).";
        return null;
    }, []);

    useEffect(() => {
        const strengthError = validatePasswordStrength(password);
        if (strengthError) {
            setError(strengthError);
        } else if (password !== confirmPassword && confirmPassword !== '') {
            setError('As senhas não combinam.');
        } else {
            setError('');
        }
    }, [password, confirmPassword, validatePasswordStrength]);

    const validatePasswords = useCallback((): boolean => {
        return !validatePasswordStrength(password) && password === confirmPassword;
    }, [password, confirmPassword, validatePasswordStrength]);

    return {
        password,
        confirmPassword,
        error,
        setPassword,
        setConfirmPassword,
        validatePasswordStrength,
        validatePasswords,
    };
};

export default usePasswordValidation;