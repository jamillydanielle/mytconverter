"use client";

import AppLayout from '@/components/layout/AppLayout';
import { Box, Typography, Divider } from '@mui/material';
import { useUsers } from '@/hooks/useUsers';
import UsersTable from '@/components/users/UsersTable';

export default function UsersPage() {
  const { 
    users, 
    loading, 
    error, 
    currentPage, 
    setCurrentPage, 
    totalPages
  } = useUsers();

  return (
    <AppLayout sidebarState="dashboard">
      <Box sx={{ mb: 3, width: '100%' }}>
        <Typography variant="h4" component="h1" gutterBottom sx={{ fontWeight: 'medium' }}>
          Gerenciar Usuários
        </Typography>
        <Divider />
      </Box>
      
      <UsersTable 
        users={users}
        loading={loading}
        error={error}
        currentPage={currentPage}
        totalPages={totalPages}
        onPageChange={setCurrentPage}
        onDeactivate={() => {}} // Mantendo para evitar erros de tipo, mas não será usado
        onActivate={() => {}}   // Mantendo para evitar erros de tipo, mas não será usado
      />
    </AppLayout>
  );
}