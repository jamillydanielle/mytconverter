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
    const [userData, setUserData] = useState<UserData | null>(null);
    const [tabs, setTabs] = useState<Tab[]>([]);
    const [selectedTabIndex, setSelectedTabIndex] = useState(0);

    useEffect(() => {
        const userDataFromToken = decodeJwtToken();
        setUserData(userDataFromToken);

        const getTabs = (): Tab[] => {
            //Caso o token seja inválido, ou não tenha token, exibe o SessionIdentifier do Cliente
            // TO-DO: Fazer o cliente ter um token. Esse return será removido
            if (!userDataFromToken) {
                return [{ label: "login", path: "/login" }];
                //throw new Error ("UNAUTHORIZED: invalid or non-existent token");
            }

            if (userDataFromToken.user) {
                switch (userDataFromToken.user.type) {
                    case UserType.USER:
                        return [{ label: "Minhas convercoes", path: "/convertion" }];
                    case UserType.ADMIN:
                        return [{ label: "Admin", path: "/users" }];
                    default:
                        return [{ label: "Erro", path: "/erro" }];
                }
            }
            return [{ label: "Login", path: "/login" }];
        };

        setTabs(getTabs());
    }, [router]);

    useEffect(() => {
        if (tabs.length > 0) {
            const currentTabIndex = tabs.findIndex((tab) => pathname.includes(tab.path));
            if (currentTabIndex !== -1) {
                setSelectedTabIndex(currentTabIndex);
            }
        }
    }, [pathname, tabs]);

    const handleTabChange = (newValue: number) => {
        setSelectedTabIndex(newValue);
        router.push(tabs[newValue].path);
    };

    return {
        userData,
        tabs,
        selectedTabIndex,
        handleTabChange,
    };
}