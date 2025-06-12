// frontend/src/app/(private)/admin/convertions/page.tsx
"use client";

import AppLayout from '@/components/layout/AppLayout';
import { Box, Typography, Divider, Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow } from '@mui/material';
import { useConvertions } from '@/hooks/useConvertions';
import { format } from 'date-fns';
import { ptBR } from 'date-fns/locale';

export default function ConvertionsPage() {
  const { convertions, loading, error } = useConvertions();

  return (
    <AppLayout sidebarState="dashboard">
      <Box sx={{ mb: 3, width: '100%' }}>
        <Typography variant="h4" component="h1" gutterBottom sx={{ fontWeight: 'medium' }}>
          Gerenciar Conversões
        </Typography>
        <Divider />
      </Box>
      <Paper sx={{ p: 2 }}>
        {loading ? (
          <Typography>Carregando conversões...</Typography>
        ) : error ? (
          <Typography color="error">Erro ao carregar conversões: {error}</Typography>
        ) : (
          <TableContainer>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell>URL</TableCell>
                  <TableCell>Tipo</TableCell>
                  <TableCell>Tamanho</TableCell>
                  <TableCell>Autor</TableCell>
                  <TableCell>Criado em</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {convertions.map((convertion) => (
                  <TableRow key={convertion.id}>
                    <TableCell>{convertion.url}</TableCell>
                    <TableCell>{convertion.Type}</TableCell>
                    <TableCell>{convertion.file_size}</TableCell>
                    <TableCell>{convertion.user_name}</TableCell>
                    <TableCell>{format(new Date(convertion.createdAt), 'dd/MM/yyyy HH:mm:ss', { locale: ptBR })}</TableCell>
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
