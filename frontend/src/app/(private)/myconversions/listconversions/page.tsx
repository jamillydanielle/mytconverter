"use client";

import React from 'react';
import { Container, Typography, Box, Divider } from '@mui/material';
import AppLayout from '@/components/layout/AppLayout';
import ConversionGroupsTable from '@/components/conversions/ConversionGroupsTable';
import { useConversionGroups } from '@/hooks/useConversionGroups';

const ListConversionsPage = () => {
  const {
    conversionGroups,
    loading,
    error,
    currentPage,
    setCurrentPage,
    totalPages,
    downloadFile
  } = useConversionGroups();

  return (
    <AppLayout sidebarState="dashboard">
      <Container>
        <Typography variant="h4" component="h1" gutterBottom>
          Minhas Conversões
        </Typography>
        
        <Box sx={{ mt: 4, mb: 2 }}>
          <Typography variant="h5" component="h2" gutterBottom>
            Conversões Agrupadas
          </Typography>
          <Divider />
        </Box>
        
        <ConversionGroupsTable
          conversionGroups={conversionGroups}
          loading={loading}
          error={error}
          currentPage={currentPage}
          totalPages={totalPages}
          onPageChange={setCurrentPage}
          onDownload={downloadFile}
        />
      </Container>
    </AppLayout>
  );
};

export default ListConversionsPage;