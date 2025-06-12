"use client";

import React from 'react';
import AppLayout from '@/components/layout/AppLayout';
import FeatureCard from '@/components/dashboard/FeatureCard';
import useMyConvertionsFeatures, { Feature } from '@/hooks/useMyConvertionsFeatures'; // Corrected import
import { Grid, Box, Divider, Typography } from '@mui/material';

const MyConvertionsPage = () => {
  const { features } = useMyConvertionsFeatures();

  return (
    <AppLayout sidebarState="dashboard">
      <Box sx={{ mb: 3, width: '100%' }}>
        <Typography variant="h4" component="h1" gutterBottom sx={{ fontWeight: 'medium' }}>
          Minhas Conversões
        </Typography>
        <Divider />
      </Box>

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

export default MyConvertionsPage;