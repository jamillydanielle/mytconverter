import AppLayout from '@/components/layout/AppLayout';
import PasswordResetForm from '@/components/auth/PasswordResetForm';
import type { Metadata } from 'next';
import { Container } from '@mui/material';

export const metadata: Metadata = {
  title: 'Redefinir Senha - MytConvert',
  description: 'Redefina sua senha do MytConvert.',
};

export default function ResetPasswordPage() {
  return (
    <AppLayout sidebarState="login">
      <Container maxWidth="lg" sx={{ display: 'flex', justifyContent: 'center', alignItems: 'flex-start', pt: { xs: 2, md: 4 } }}>
        <PasswordResetForm />
      </Container>
    </AppLayout>
  );
}