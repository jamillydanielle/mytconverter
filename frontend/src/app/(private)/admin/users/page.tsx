"use client";

import AppLayout from '@/components/layout/AppLayout';
import { Box, Typography, Divider, Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow } from '@mui/material';
import { useUsers } from '@/hooks/useUsers';

export default function UsersPage() {
  const { users, loading, error } = useUsers();

  return (
    <AppLayout sidebarState="dashboard">
      <Box sx={{ mb: 3, width: '100%' }}>
        <Typography variant="h4" component="h1" gutterBottom sx={{ fontWeight: 'medium' }}>
          Gerenciar Usuários
        </Typography>
        <Divider />
      </Box>
      <Paper sx={{ p: 2 }}>
        {loading ? (
          <Typography>Carregando usuários...</Typography>
        ) : error ? (
          <Typography color="error">Erro ao carregar usuários: {error}</Typography>
        ) : (
          <TableContainer>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell>Nome</TableCell>
                  <TableCell>Email</TableCell>
                  <TableCell>Tipo de Usuário</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {users.map((user) => (
                  <TableRow key={user.id}>
                    <TableCell>{user.name}</TableCell>
                    <TableCell>{user.email}</TableCell>
                    <TableCell>{user.type}</TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
        )}
      </Paper>
    </AppLayout>
  );
}