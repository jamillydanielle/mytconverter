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

        const getTabsForUser = (): Tab[] => {
            if (!userDataFromToken || !userDataFromToken.user) {
        
                return [{ label: "Login", path: "/login" }];
            }

            const userRole = userDataFromToken.user.type; 

            if (userRole === UserType.ADMIN) {
                return [
                    { label: "Usuários", path: "/admin/users" },
                    { label: "Todas Conversões", path: "/admin/conversions" },
                    { label: "Minhas Conversões", path: "/myconversions" } 
                ];
            } else if (userRole === UserType.USER) {
                return [
                    { label: "Minhas Conversões", path: "/myconversions" }
                ];
            } else {
                console.warn("Tipo de usuário desconhecido:", userRole);
                return [{ label: "Login", path: "/login" }]; 
            }
        };

        const newTabs = getTabsForUser();
        setTabs(newTabs);

   
    }, [pathname]); 

    useEffect(() => {
        if (tabs.length > 0) {
            const currentTabIndex = tabs.findIndex((tab) => pathname === tab.path || pathname.startsWith(tab.path + '/'));
            if (currentTabIndex !== -1) {
                setSelectedTabIndex(currentTabIndex);
            } else {
   
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