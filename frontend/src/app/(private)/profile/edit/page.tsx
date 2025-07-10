"use client";

import React from 'react';
import { Box, Container, Typography } from '@mui/material';
import ProfileEditForm from '@/components/profile/ProfileEditForm';

const ProfileEditPage = () => {
  return (
    <Container maxWidth="md">
      <Box sx={{ my: 4 }}>
        <Typography variant="h4" component="h1" gutterBottom align="center">
          Editar Perfil
        </Typography>
        <ProfileEditForm />
      </Box>
    </Container>
  );
};

export default ProfileEditPage;