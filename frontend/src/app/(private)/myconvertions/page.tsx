// src/app/(private)/myconvertions/page.tsx
"use client";

import React from 'react';
import AppLayout from '@/components/layout/AppLayout';
import FeatureCard from '@/components/dashboard/FeatureCard';
import useMyConversionsFeatures, { Feature } from '@/hooks/useMyConvertionsFeatures'; // Corrected import
import { Grid, Box, Divider, Typography } from '@mui/material';

// Metadata não pode ser exportada de Client Components.
// Deve ser definida no layout.tsx pai ou usando generateMetadata se esta fosse uma Server Page.

const MyConversionsPage = () => {
  const { features } = useMyConversionsFeatures();

  return (
    <AppLayout sidebarState="dashboard">
      {/* Título da Página e Divisor */}
      <Box sx={{ mb: 3, width: '100%' }}>
        <Typography variant="h4" component="h1" gutterBottom sx={{ fontWeight: 'medium' }}>
          Minhas Conversões
        </Typography>
        <Divider />
      </Box>

      {/* 2. Usar Grid para layout 2x2 */}
      <Grid container spacing={3}>
        {features.map((feature: Feature) => (
          <Grid item xs={12} sm={6} key={feature.actionKey}>
            <FeatureCard
              icon={feature.icon}
              title={feature.title}
              onClick={feature.onClick}
            />
          </Grid>
        ))}
      </Grid>
    </AppLayout>
  );
};

export default MyConversionsPage;