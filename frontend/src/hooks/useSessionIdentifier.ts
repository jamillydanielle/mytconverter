import { useState, useEffect } from "react";
import { decodeJwtToken, UserData } from "@/utils/jwtDecoder";
import { UserType } from "@/types";
import { usePathname, useRouter } from "next/navigation";

interface Tab {
    label: string;
    path: string;
}

export function useSessionIdentifier() {
    const router = useRouter();
    const pathname = usePathname();
    const [userData, setUserData] = useState<UserData | null>(null); // UserData do seu utils/jwtDecoder.ts
    const [tabs, setTabs] = useState<Tab[]>([]);
    const [selectedTabIndex, setSelectedTabIndex] = useState(0);

    useEffect(() => {
        const userDataFromToken = decodeJwtToken(); // Sua função de decodificação de token
        setUserData(userDataFromToken);

        const getTabsForUser = (): Tab[] => {
            if (!userDataFromToken || !userDataFromToken.user) {
                // Se não há dados do usuário ou token, default para login
                // O RootRedirect já deve ter tratado o redirecionamento para /login se não houver token
                return [{ label: "Login", path: "/login" }];
            }

            const userRole = userDataFromToken.user.type; // UserType.ADMIN ou UserType.USER

            if (userRole === UserType.ADMIN) {
                return [
                    { label: "Usuários", path: "/admin/users" },
                    { label: "Todas Conversões", path: "/admin/convertions" },
                    { label: "Minhas Conversões", path: "/myconvertions" } // Rota para admin ver conversões
                ];
            } else if (userRole === UserType.USER) {
                return [
                    { label: "Minhas Conversões", path: "/myconvertions" }
                ];
            } else {
                console.warn("Tipo de usuário desconhecido:", userRole);
                return [{ label: "Login", path: "/login" }]; // Fallback
            }
        };

        const newTabs = getTabsForUser();
        setTabs(newTabs);

    // Adicionando uma forma de reavaliar se o token mudar (ex: após login/logout)
    // A reavaliação também ocorrerá se o componente que usa o hook for remontado devido à navegação.
    }, [pathname]); // Reavalia tabs quando o pathname muda ou userData (se incluído)

    useEffect(() => {
        if (tabs.length > 0) {
            const currentTabIndex = tabs.findIndex((tab) => pathname === tab.path || pathname.startsWith(tab.path + '/'));
            if (currentTabIndex !== -1) {
                setSelectedTabIndex(currentTabIndex);
            } else {
                // Se nenhuma aba corresponder exatamente, pode-se definir um padrão ou -1
                // setSelectedTabIndex(0); // Ou lógica para encontrar a melhor correspondência
            }
        }
    }, [pathname, tabs]);

    const handleTabChange = (newValue: number) => {
        if (tabs[newValue]) {
            setSelectedTabIndex(newValue);
            router.push(tabs[newValue].path);
        }
    };

    return {
        userData,
        tabs,
        selectedTabIndex,
        handleTabChange,
    };
}