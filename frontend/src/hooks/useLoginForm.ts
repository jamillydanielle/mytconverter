import { useState, useCallback } from "react";
import usePasswordValidation from './usePasswordValidation';
import { setToken } from "@/utils/token";
import { changeUserPassword, loginUser } from "@/services/Auth.service";
import { useAlert } from "@/components/alert/AlertProvider";

export const useLoginForm = () => {
    const [credentials, setCredentials] = useState({ email: "", rememberMe: false, password: "" });
    const { password, confirmPassword, error: passwordError, setPassword, validatePasswordStrength, setConfirmPassword, validatePasswords } = usePasswordValidation();
    const [error, setError] = useState("");
    const [tempToken, setTempToken] = useState("");
    const [changePassword, setChangePassword] = useState(false);
    const [loginSuccess, setLoginSuccess] = useState(false);
    const { addAlert } = useAlert();


    const handleChange = useCallback((e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value, checked, type } = e.target;
        if (type === "checkbox") {
            setCredentials(prev => ({ ...prev, [name]: checked }));
        } else if (name === "newPassword") {
            setConfirmPassword(value);
            validatePasswordStrength(value);
        } else if (name === "currentPassword") {
            setPassword(value);
            validatePasswordStrength(value);
        } else {
            setCredentials(prev => ({ ...prev, [name]: value.trim() }));
        }
    }, [setConfirmPassword, setPassword]);

    const validateInput = useCallback(() => {
        if (credentials.email.trim() === "" || (!changePassword && credentials.password.trim() === "")) {
            addAlert("Please insert email and password", "error")
            setError("Please insert email and password");
            return false;
        }
        if (changePassword && !validatePasswords()) {
            setError(passwordError);
            return false;
        }
        return true;
    }, [credentials.email, credentials.password, changePassword, validatePasswords, passwordError]);

    const handleLogin = useCallback(async () => {
        if (!validateInput()) return;

        try {
            let result;
            if (changePassword) {
                await changeUserPassword(credentials.email, password, tempToken);
                result = await loginUser(credentials.email, password, credentials.rememberMe);
            } else {
                result = await loginUser(credentials.email, credentials.password, credentials.rememberMe);
            }
            if (result.message === "Password needs to be changed") {
                addAlert("Password needs to be changed", "warning")
                setChangePassword(true);
                setPassword(credentials.password)
                setTempToken(result.token)
                setError("");
            } else {
                if (result.token) {
                    setToken(result.token);
                    setLoginSuccess(true);
                } else {
                    setError("Login successful, but no token received");
                }
            }
        } catch (error) {
            if (error instanceof Error) {
                if (error.message === "Failed to fetch") {
                    addAlert("A network error occurred", "error")
                    setError("A network error occurred");
                } else if (error.message === "Password needs to be changed") {
                    addAlert("Password needs to be changed", "warning")
                    setChangePassword(true);
                    setError("");
                } else {
                    addAlert(error.message, "error")
                    setError(error.message);
                }
            }
        }
    }, [validateInput, changePassword, credentials, password]);

    return { 
        changePassword, 
        error: error || passwordError, 
        handleChange, 
        handleLogin, 
        credentials, 
        password, 
        confirmPassword,
        loginSuccess,
    };
};