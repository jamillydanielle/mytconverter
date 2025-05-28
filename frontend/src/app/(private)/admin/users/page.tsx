// app/users/page.tsx
import AppLayout from '@/components/layout/AppLayout';
import type { Metadata } from 'next';
import { Box, Typography, Divider, Paper } from '@mui/material';

export const metadata: Metadata = {
  title: 'Gerenciar Usuários - Admin',
  description: 'Administração de usuários do sistema.',
};

export default function UsersPage() {
  // Lógica para buscar e listar usuários aqui
  // const { data: users, isLoading, error } = useFetchUsers(); // Exemplo

  return (
    <AppLayout sidebarState="dashboard"> {/* ou um sidebarState="admin" se tiver configurações diferentes */}
      <Box sx={{ mb: 3, width: '100%' }}>
        <Typography variant="h4" component="h1" gutterBottom sx={{ fontWeight: 'medium' }}>
          Gerenciar Usuários
        </Typography>
        <Divider />
      </Box>
      <Paper sx={{p: 2}}>
        <Typography variant="body1">
          Conteúdo da página de gerenciamento de usuários aqui.
          (Ex: Tabela de usuários, botões de adicionar/editar/remover, etc.)
        </Typography>
        {/* Exemplo: <UsersTable users={users} /> */}
      </Paper>
    </AppLayout>
  );
}