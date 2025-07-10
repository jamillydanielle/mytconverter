"use client";
import React, { useState, useEffect } from 'react';
import NextLink from 'next/link';
import { Box, Typography, List, ListItem, ListItemButton, ListItemIcon, ListItemText, Divider, CircularProgress } from '@mui/material';
import Button from '@/components/ui/Button';
import { Home, Music, Settings, Video, Shield, LogIn, LogOut as LogOutIcon, Edit } from 'lucide-react';
import { useSessionIdentifier } from '@/hooks/useSessionIdentifier';
import { removeToken, getToken } from '@/utils/token';
import { useRouter } from 'next/navigation';
import { UserType } from '@/types'; 
import { logoutUser } from '@/services/Auth.service';
import { useAlert } from '@/components/alert/AlertProvider';
import { getCurrentUserData } from '@/services/Users.service';

interface SidebarProps {
  currentPage?: 'dashboard' | 'login' | 'register';
}

const Sidebar: React.FC<SidebarProps> = ({ currentPage }) => {
  const router = useRouter();
  const { userData, tabs, selectedTabIndex, handleTabChange } = useSessionIdentifier();
  const [isLoggingOut, setIsLoggingOut] = useState(false);
  const [userName, setUserName] = useState<string>("myAccount");
  const { addAlert } = useAlert();

  const sidebarBackgroundColor = '#1f2937';
  const textColor = '#ffffff';
  const hoverBgColor = 'rgba(255, 255, 255, 0.08)';

  // Efeito para carregar o nome do usuário atual do backend
  useEffect(() => {
    const fetchUserName = async () => {
      // Se temos dados do usuário do token, definimos o nome inicialmente
      if (userData?.user?.name) {
        setUserName(userData.user.name);
      }
      
      // Se o usuário estiver logado, buscamos os dados atualizados do backend
      if (userData?.user?.id) {
        try {
          const currentUserData = await getCurrentUserData();
          if (currentUserData && currentUserData.name) {
            setUserName(currentUserData.name);
          }
        } catch (error) {
          console.error("Erro ao buscar dados atualizados do usuário:", error);
          // Mantemos o nome do token se houver erro
        }
      }
    };

    fetchUserName();
  }, [userData]); // Dependência em userData para recarregar quando o token mudar

  const handleLogout = async () => {
    setIsLoggingOut(true);
    try {
      const token = getToken();
      if (token) {
        await logoutUser(token);
        addAlert("Logout realizado com sucesso", "success");
      }
      removeToken();
      router.push('/login'); 
      router.refresh();
    } catch (error) {
      console.error("Error during logout:", error);
      addAlert("Erro ao realizar logout", "error");
    } finally {
      setIsLoggingOut(false);
    }
  };

  // Usamos o nome do estado em vez do nome do token
  const userDisplayName = userName || "myAccount";

  return (
    <Box
      sx={{
        width: 288,
        minHeight: '100vh',
        bgcolor: sidebarBackgroundColor,
        color: textColor,
        display: 'flex',
        flexDirection: 'column',
        p: 3,
      }}
    >
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
        <Box sx={{ display: 'flex', alignItems: 'center' }}>
          <Typography variant="h5" component={NextLink} href={tabs[0]?.path || "/"} sx={{ color: textColor, textDecoration: 'none', fontWeight: 'bold' }}>
            {userDisplayName}
          </Typography>
          {userData && (
            <Button
              variant="text"
              component={NextLink}
              href="/profile/edit"
              size="small"
              sx={{ color: textColor, ml: 1, minWidth: 'auto', p: 0.5 }}
              disabled={isLoggingOut}
            >
              <Edit size={16} />
            </Button>
          )}
        </Box>
        {!userData && (currentPage !== 'login' && currentPage !== 'register') && (
          <Button
            variant="outlined"
            component={NextLink}
            href="/login"
            size="small"
            sx={{ color: textColor, borderColor: 'rgba(255,255,255,0.5)', '&:hover': { borderColor: textColor, bgcolor: hoverBgColor } }}
            startIcon={<LogIn size={18}/>}
          >
            Login
          </Button>
        )}
        {userData && (
           <Button
            variant="outlined"
            onClick={handleLogout}
            size="small"
            disabled={isLoggingOut}
            sx={{ color: textColor, borderColor: 'rgba(255,255,255,0.5)', '&:hover': { borderColor: textColor, bgcolor: hoverBgColor } }}
            startIcon={isLoggingOut ? <CircularProgress size={16} color="inherit" /> : <LogOutIcon size={18}/>}
          >
            {isLoggingOut ? "Saindo..." : "Logout"}
          </Button>
        )}
      </Box>
      {userData?.user?.type && (
        <Typography variant="caption" sx={{color: 'rgba(255,255,255,0.5)', mb: 2}}>
          Perfil: {userData.user.type.toString()}
        </Typography>
      )}

      {tabs.length > 0 && userData && (
        <>
          <Typography variant="overline" sx={{color: 'rgba(255,255,255,0.7)', mt:1}}>Navegação Principal</Typography>
          <List component="nav" sx={{mb: 2}}>
            {tabs.map((tab, index) => (
              <ListItem key={tab.path} disablePadding> 
                <ListItemButton
                  selected={index === selectedTabIndex}
                  onClick={() => handleTabChange(index)}
                  disabled={isLoggingOut}
                  sx={{
                    borderRadius: 1,
                    '&:hover': { bgcolor: hoverBgColor },
                    '&.Mui-selected': {
                      bgcolor: 'primary.main',
                      '&:hover': { bgcolor: 'primary.dark' },
                    },
                  }}
                >
                  <ListItemText primary={tab.label.charAt(0).toUpperCase() + tab.label.slice(1)} />
                </ListItemButton>
              </ListItem>
            ))}
          </List>
          <Divider sx={{ my: 1, borderColor: 'rgba(255,255,255,0.2)' }} />
        </>
      )}

      {currentPage === 'dashboard' && userData && (
        <>
          <Typography variant="h6" component="h2" sx={{ mt:2, mb: 0.5, fontWeight: 'semibold' }}>
            Conversor de áudio e vídeo
          </Typography>
          <Typography variant="body2" sx={{ color: 'rgba(255,255,255,0.7)', mb: 2 }}>
            Converta seus arquivos em MP3 ou MP4 rapidamente.
          </Typography>
        </>
      )}

      {(currentPage === 'login' || currentPage === 'register') && !userData && (
        <>
          <Typography variant="h4" component="h2" sx={{ fontWeight: 'semibold', mb: 3, mt: 2 }}> Welcome! </Typography>
          <Box sx={{ width: '100%', height: 192, bgcolor: 'rgba(255,255,255,0.1)', borderRadius: 1, mb: 3, display: 'flex', alignItems: 'center', justifyContent: 'center', }} >
            <Typography sx={{ color: 'rgba(255,255,255,0.5)' }}>Placeholder Imagem</Typography>
          </Box>
          <Divider sx={{ my: 2, borderColor: 'rgba(255,255,255,0.2)' }}/>
          <ListItemButton component={NextLink} href="/" sx={{ borderRadius: 1, '&:hover': { bgcolor: hoverBgColor } }}>
            <ListItemIcon sx={{ color: 'rgba(255,255,255,0.7)', minWidth: 'auto', mr: 1 }}> <Home size={18} /> </ListItemIcon>
            <ListItemText primary="Página Inicial" sx={{ color: 'rgba(255,255,255,0.7)' }} />
          </ListItemButton>
        </>
      )}
    </Box>
  );
};

export default Sidebar;