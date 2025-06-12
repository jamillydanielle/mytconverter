// src/hooks/useRegister.ts
import { useState } from 'react';
import { useRouter } from 'next/navigation';
import { User } from '../types/User';
import { createUser } from '../services/Users.service';
import { useAlert } from '../components/alert/AlertProvider';

interface RegistrationData {
    name: string;
    email: string;
    password: string;
}

const useRegisterForm = () => {
    const [name, setName] = useState('');
    const [email, setEmail] = useState('');
    const [loading, setLoading] = useState(false); // Adicionando o estado de loading
    const router = useRouter();
    const { addAlert } = useAlert();

    const register = async (registrationData: RegistrationData) => {
        setLoading(true); // Iniciando o loading

        try {
            const createdUser: User = await createUser(registrationData as Omit<User, 'id' | 'createdAt' | 'updatedAt'>);
            console.log('User created:', createdUser);
            addAlert("Usuário criado com sucesso!", "success");
            router.push('/login');
        } catch (error: any) {
            console.error('Error creating user:', error);
            addAlert(`Erro ao criar usuário: ${error.message || 'Erro desconhecido'}`, "error");
        } finally {
            setLoading(false); // Finalizando o loading, independente do resultado
        }
    };

    return {
        name,
        setName,
        email,
        setEmail,
        register,
        loading, // Expondo o estado de loading
    };
};

export default useRegisterForm;