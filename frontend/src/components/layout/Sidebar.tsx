// components/layout/Sidebar.tsx
"use client";
import React from 'react';
import NextLink from 'next/link'; // Renomeado para evitar conflito com MUI Link
import { Box, Typography, List, ListItem, ListItemButton, ListItemIcon, ListItemText, Divider } from '@mui/material';
import Button from '@/components/ui/Button';
import { Home, Music, Settings, Video, Shield, LogIn, LogOut as LogOutIcon } from 'lucide-react';
import { useSessionIdentifier } from '@/hooks/useSessionIdentifier'; // Seu hook!
import { removeToken } from '@/utils/token'; // Sua função de remover token
import { useRouter } from 'next/navigation';
import { UserType } from '@/types'; // Seu enum UserType

interface SidebarProps {
  currentPage?: 'dashboard' | 'login' | 'register';
}

const Sidebar: React.FC<SidebarProps> = ({ currentPage }) => {
  const router = useRouter();
  const { userData, tabs, selectedTabIndex, handleTabChange } = useSessionIdentifier();

  const sidebarBackgroundColor = '#1f2937';
  const textColor = '#ffffff';
  const hoverBgColor = 'rgba(255, 255, 255, 0.08)';

  const handleLogout = () => {
    removeToken();
    router.push('/login'); // Redireciona para login
    router.refresh();    // Força a reavaliação do useSessionIdentifier
  };

  // Itens default do dashboard, podem ser condicionalmente exibidos ou mesclados com 'tabs'
  const dashboardNavItems = [
    { text: 'Converter', icon: <Music size={20} />, href: '#dummy-converter', requiredType: UserType.USER },
    { text: 'Editar', icon: <Settings size={20} />, href: '#dummy-editar', requiredType: UserType.USER },
    { text: 'Gravar', icon: <Video size={20} />, href: '#dummy-gravar', requiredType: UserType.USER },
    { text: 'Planos', icon: <Shield size={20} />, href: '#dummy-planos', requiredType: UserType.USER },
    // Exemplo de item específico para Admin, se não vier dos 'tabs'
    // { text: 'Gerenciar Usuários', icon: <Users size={20} />, href: '/admin/users', requiredType: UserType.ADMIN },
  ];

  const userDisplayName = userData?.user?.name || "myAccount";

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
        <Typography variant="h5" component={NextLink} href={tabs[0]?.path || "/"} sx={{ color: textColor, textDecoration: 'none', fontWeight: 'bold' }}>
          {userDisplayName}
        </Typography>
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
            sx={{ color: textColor, borderColor: 'rgba(255,255,255,0.5)', '&:hover': { borderColor: textColor, bgcolor: hoverBgColor } }}
            startIcon={<LogOutIcon size={18}/>}
          >
            Logout
          </Button>
        )}
      </Box>
      {userData?.user?.type && (
        <Typography variant="caption" sx={{color: 'rgba(255,255,255,0.5)', mb: 2}}>
          Perfil: {userData.user.type.toString()} {/* Garante que seja string */}
        </Typography>
      )}

      {/* Navegação principal baseada nos tabs do useSessionIdentifier */}
      {tabs.length > 0 && userData && (
        <>
          <Typography variant="overline" sx={{color: 'rgba(255,255,255,0.7)', mt:1}}>Navegação Principal</Typography>
          <List component="nav" sx={{mb: 2}}>
            {tabs.map((tab, index) => (
              <ListItem key={tab.path} disablePadding> {/* Usar tab.path como key é mais seguro se label não for único */}
                <ListItemButton
                  selected={index === selectedTabIndex}
                  onClick={() => handleTabChange(index)}
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

      {/* Conteúdo específico do Dashboard (conforme wireframe original) */}
      {currentPage === 'dashboard' && userData && (
        <>
          <Typography variant="h6" component="h2" sx={{ mt:2, mb: 0.5, fontWeight: 'semibold' }}>
            Conversor de áudio e vídeo
          </Typography>
          <Typography variant="body2" sx={{ color: 'rgba(255,255,255,0.7)', mb: 2 }}>
            Converta seus arquivos em MP3 ou MP4 rapidamente.
          </Typography>
          <Box component="nav" sx={{ mt: 'auto' }}>
            <List>
              {dashboardNavItems.filter(item => !item.requiredType || item.requiredType === userData?.user?.type).map((item) => (
                <ListItem key={item.text} disablePadding>
                  <ListItemButton component={NextLink} href={item.href} sx={{ borderRadius: 1, '&:hover': { bgcolor: hoverBgColor } }}>
                    <ListItemIcon sx={{ color: textColor, minWidth: 'auto', mr: 1.5 }}>{item.icon}</ListItemIcon>
                    <ListItemText primary={item.text} />
                  </ListItemButton>
                </ListItem>
              ))}
            </List>
          </Box>
        </>
      )}

      {/* Seção "Welcome" para login/register */}
      {(currentPage === 'login' || currentPage === 'register') && !userData && (
        <>
          {/* ... (conteúdo do Welcome para login/register como antes) ... */}
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