import AppLayout from '@/components/layout/AppLayout';
import LoginForm from '@/components/auth/LoginForm';
import type { Metadata } from 'next';
import { Box, Container } from '@mui/material';

export const metadata: Metadata = {
  title: 'Login - Meu Conversor',
  description: 'Acesse sua conta no Meu Conversor de Áudio e Vídeo.',
};

export default function LoginPage() {
  return (
    <AppLayout sidebarState="login">
      <Container maxWidth="lg" sx={{ display: 'flex', justifyContent: 'center', alignItems: 'flex-start', pt: { xs: 2, md: 4 } }}>
        <LoginForm />
      </Container>
    </AppLayout>
  );
}