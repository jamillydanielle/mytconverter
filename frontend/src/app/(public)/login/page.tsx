// app/(auth)/login/page.tsx
import AppLayout from '@/components/layout/AppLayout';
import LoginForm from '@/components/auth/LoginForm'; // Seu componente de formulário de login
import type { Metadata } from 'next';
import { Box, Container } from '@mui/material'; // Opcional para centralização/padding adicional

export const metadata: Metadata = {
  title: 'Login - Meu Conversor',
  description: 'Acesse sua conta no Meu Conversor de Áudio e Vídeo.',
};

export default function LoginPage() {
  return (
    <AppLayout sidebarState="login"> {/* Define o estado da sidebar para a página de login */}
      {/* Você pode usar um Container do MUI para um padding e centralização consistentes,
        mas o Paper dentro do LoginForm já pode estar fazendo isso.
        Ajuste conforme o layout desejado.
      */}
      <Container maxWidth="lg" sx={{ display: 'flex', justifyContent: 'center', alignItems: 'flex-start', pt: { xs: 2, md: 4 } }}>
        <LoginForm />
      </Container>
    </AppLayout>
  );
}