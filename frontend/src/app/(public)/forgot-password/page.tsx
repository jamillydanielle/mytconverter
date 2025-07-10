import AppLayout from '@/components/layout/AppLayout';
import PasswordResetRequestForm from '@/components/auth/PasswordResetRequestForm';
import type { Metadata } from 'next';
import { Container } from '@mui/material';

export const metadata: Metadata = {
  title: 'Recuperar Senha - MytConvert',
  description: 'Recupere sua senha no MytConvert.',
};

export default function ForgotPasswordPage() {
  return (
    <AppLayout sidebarState="login">
      <Container maxWidth="lg" sx={{ display: 'flex', justifyContent: 'center', alignItems: 'flex-start', pt: { xs: 2, md: 4 } }}>
        <PasswordResetRequestForm />
      </Container>
    </AppLayout>
  );
}