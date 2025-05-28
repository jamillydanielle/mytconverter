// app/admin/conversions/page.tsx
import AppLayout from '@/components/layout/AppLayout';
import type { Metadata } from 'next';
import { Box, Typography, Divider, Paper } from '@mui/material';

export const metadata: Metadata = {
  title: 'Todas Conversões - Admin',
  description: 'Visualização de todas as conversões do sistema.',
};

export default function AdminConversionsPage() {
  // Lógica para buscar e listar todas as conversões
  return (
    <AppLayout sidebarState="dashboard"> {/* ou sidebarState="admin" */}
      <Box sx={{ mb: 3, width: '100%' }}>
        <Typography variant="h4" component="h1" gutterBottom sx={{ fontWeight: 'medium' }}>
          Todas as Conversões (Admin)
        </Typography>
        <Divider />
      </Box>
      <Paper sx={{p: 2}}>
        <Typography variant="body1">
          Conteúdo da página de visualização de todas as conversões.
          (Ex: Tabela de conversões com filtros, etc.)
        </Typography>
      </Paper>
    </AppLayout>
  );
}