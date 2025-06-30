"use client";

import React from 'react';
import { Container, Typography, Box, Divider } from '@mui/material';
import { DownloadForm } from '@/components/download/DownloadForm';
import { useDownload } from '@/hooks/useDownloads';
import AppLayout from '@/components/layout/AppLayout';
import ConversionGroupsTable from '@/components/conversions/ConversionGroupsTable';
import { useConversionGroups } from '@/hooks/useConversionGroups';

const NewConversionPage = () => {
  const {
    conversionGroups,
    loading: conversionsLoading,
    error: conversionsError,
    currentPage,
    setCurrentPage,
    totalPages,
    downloadFile,
    fetchConversionGroups
  } = useConversionGroups();

  const {
    download,
    loading: downloadLoading,
    error: downloadError,
    successMessage,
    fileName,
    internalFileName,
    handleDownloadClick
  } = useDownload({
    onDownloadSuccess: () => {
      // Atualizar a listagem de conversões quando o download for concluído com sucesso
      fetchConversionGroups(currentPage);
    }
  });

  const handleSubmit = (url: string, format: 'mp3' | 'mp4') => {
    download(url, format);
  };

  return (
    <AppLayout sidebarState="dashboard">
      <Container>
        <Typography variant="h4" component="h1" gutterBottom>
          Nova Conversão
        </Typography>
        <DownloadForm
          onSubmit={handleSubmit}
          onDownloadClick={handleDownloadClick}
          loading={downloadLoading}
          error={downloadError}
          successMessage={successMessage}
          fileName={fileName}
          internalFileName={internalFileName}
        />
        
        <Box sx={{ mt: 4, mb: 2 }}>
          <Typography variant="h5" component="h2" gutterBottom>
            Minhas Conversões
          </Typography>
          <Divider />
        </Box>
        
        <ConversionGroupsTable
          conversionGroups={conversionGroups}
          loading={conversionsLoading}
          error={conversionsError}
          currentPage={currentPage}
          totalPages={totalPages}
          onPageChange={setCurrentPage}
          onDownload={downloadFile}
        />
      </Container>
    </AppLayout>
  );
};

export default NewConversionPage;