import { useState } from 'react';
import { useRouter } from 'next/navigation';
import { User } from '@/types/User';
import { createUser } from '@/services/Users.service';
import { useAlert } from '@/components/alert/AlertProvider';
import { UserType } from '@/types/UserType';

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
            // Adicionar o tipo USER explicitamente ao objeto
            const userData = {
                ...registrationData,
                type: UserType.USER // Adicionar o tipo de usuário padrão
            };
            
            await createUser(userData);
            addAlert("Usuário criado com sucesso!", "success");
            router.push('/login');
        } catch (error: any) {
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