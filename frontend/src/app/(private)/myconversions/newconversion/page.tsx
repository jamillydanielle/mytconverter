"use client";

import React from 'react';
import { Container, Typography } from '@mui/material';
import { DownloadForm } from '@/components/download/DownloadForm';
import { useDownload } from '@/hooks/useDownloads';
import AppLayout from '@/components/layout/AppLayout';

const NewConversionPage = () => {
  const {
    download,
    loading,
    error,
    successMessage,
    fileName,
    internalFileName,
    handleDownloadClick
  } = useDownload();

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
          loading={loading}
          error={error}
          successMessage={successMessage}
          fileName={fileName}
          internalFileName={internalFileName}
        />
      </Container>
    </AppLayout>
  );
};

export default NewConversionPage;