    import React from 'react';
    import Sidebar from './Sidebar';
    import Box from '@mui/material/Box';

    interface AppLayoutProps {
      children: React.ReactNode;
      sidebarState?: 'dashboard' | 'login' | 'register';
    }

    const AppLayout: React.FC<AppLayoutProps> = ({ children, sidebarState }) => {
      return (
        <Box sx={{ display: 'flex', minHeight: '100vh' }}>
          <Sidebar currentPage={sidebarState} />
          <Box
            component="main"
            sx={{
              flexGrow: 1,
              bgcolor: 'background.default', // Usando a cor de fundo do tema
              p: 3, // Padding
            }}
          >
            {children}
          </Box>
        </Box>
      );
    };

    export default AppLayout;
