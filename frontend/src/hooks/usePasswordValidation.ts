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
        console.log(pwd)
        if (pwd.length === 0) return null;
        if (pwd.length < 8) return "Password must be at least 8 characters long.";
        if (!/[A-Z]/.test(pwd)) return "Password must contain at least one uppercase letter.";
        if (!/[a-z]/.test(pwd)) return "Password must contain at least one lowercase letter.";
        if (!/\d/.test(pwd)) return "Password must contain at least one number.";
        if (!/[@$!%*?&]/.test(pwd)) return "Password must contain at least one special character (@$!%*?&).";
        return null;
    }, []);

    useEffect(() => {
        const strengthError = validatePasswordStrength(password);
        if (strengthError) {
            setError(strengthError);
        } else if (password !== confirmPassword && confirmPassword !== '') {
            setError('Passwords do not match.');
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