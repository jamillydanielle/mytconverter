import AppLayout from '@/components/layout/AppLayout';
import RegisterForm from '@/components/auth/RegisterForm'; 
import type { Metadata } from 'next';
import { Box, Container } from '@mui/material';

export const metadata: Metadata = {
  title: 'Criar Conta - Meu Conversor',
  description: 'Crie sua conta no Meu Conversor de Áudio e Vídeo.',
};

export default function RegisterPage() {
  return (
    <AppLayout sidebarState="register"> 
      <Container maxWidth="lg" sx={{ display: 'flex', justifyContent: 'center', alignItems: 'flex-start', pt: { xs: 2, md: 4 } }}>
        <RegisterForm />
      </Container>
    </AppLayout>
  );
}