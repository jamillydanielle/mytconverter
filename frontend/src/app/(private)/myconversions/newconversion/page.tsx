"use client";

import React from 'react';
import { Container, Typography, Box, Divider } from '@mui/material';
import { DownloadForm } from '@/components/download/DownloadForm';
import { useDownload } from '@/hooks/useDownloads';
import AppLayout from '@/components/layout/AppLayout';
import ConversionsTable from '@/components/conversions/ConversionsTable';
import { useConversions } from '@/hooks/useConversions';

const NewConversionPage = () => {
  const {
    download,
    loading: downloadLoading,
    error: downloadError,
    successMessage,
    fileName,
    internalFileName,
    handleDownloadClick
  } = useDownload();

  const {
    conversions,
    loading: conversionsLoading,
    error: conversionsError,
    currentPage,
    setCurrentPage,
    totalPages
  } = useConversions();

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
        
        <ConversionsTable
          conversions={conversions}
          loading={conversionsLoading}
          error={conversionsError}
          currentPage={currentPage}
          totalPages={totalPages}
          onPageChange={setCurrentPage}
        />
      </Container>
    </AppLayout>
  );
};

export default NewConversionPage;