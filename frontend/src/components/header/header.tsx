"use client"
import React, { useState } from 'react';
import { AppBar, Toolbar, Typography, IconButton, MenuItem, Menu, useMediaQuery, useTheme } from "@mui/material";
import MenuIcon from "@mui/icons-material/Menu";
import LogoutIcon from '@mui/icons-material/Logout';
import { useRouter } from 'next/navigation';
import { removeToken } from '@/utils/token';

export default function Header() {
  const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);
  const router = useRouter();
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('sm'));

  const handleMenuOpen = (event: React.MouseEvent<HTMLElement>) => {
    setAnchorEl(event.currentTarget);
  };

  const handleMenuClose = () => {
    setAnchorEl(null);
  };

  const handleDisconnect = () => {
    removeToken();
    localStorage.removeItem('avatarUrl');
    localStorage.removeItem('evaluatorData');
    router.push("/login");
  };

  const navigateToHome = () => {
    router.push('/');
  };
  
  return (
    <AppBar position="static" sx={{ backgroundColor: "white", boxShadow: "none", width: '100%', padding: {xs: '0 5%', sm: '10px 7%'} }}>
      <Toolbar sx={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
        <Typography variant="h4" sx={{ cursor:"pointer" , fontWeight: "bold", color: "#9B4274", fontSize: { xs: '1.5rem', sm: '2rem' } }} onClick={navigateToHome}>
          CI&T <span style={{ fontWeight: "400" }}>JOURNEY</span>
        </Typography>

        <div style={{ display: "flex", alignItems: "center", gap: "10px" }}>
          
            <MenuItem onClick={handleDisconnect}>
              <LogoutIcon sx={{ mr: 1 }} />
              Disconnect
            </MenuItem>
        </div>
      </Toolbar>
    </AppBar>
  );
}