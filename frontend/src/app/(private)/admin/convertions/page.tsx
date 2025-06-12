"use client";

import AppLayout from '@/components/layout/AppLayout';
import { Box, Typography, Divider, Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow } from '@mui/material';


export default function ConvertionsPage() {
 

  return (
    <AppLayout sidebarState="dashboard">
      <Box sx={{ mb: 3, width: '100%' }}>
        <Typography variant="h4" component="h1" gutterBottom sx={{ fontWeight: 'medium' }}>
          Listar Todas as conversoes e metricas
        </Typography>
        <Divider />
      </Box>
    </AppLayout>
  );
}
