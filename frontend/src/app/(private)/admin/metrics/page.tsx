"use client";

import React, { useState, useEffect } from 'react';
import AppLayout from '@/components/layout/AppLayout';
import { Box, Typography, Divider } from '@mui/material';
import { getUserConversionStats } from '@/services/Conversions.service';
import UserConversionStatsTable from '@/components/conversions/UserConversionStatsTable';
import { UserConversionStats } from '@/types/UserConversionStats';

export default function ConvertionsPage() {
  const [stats, setStats] = useState<UserConversionStats[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const pageSize = 10;

  useEffect(() => {
    const fetchUserConversionStats = async () => {
      try {
        setLoading(true);
        const data = await getUserConversionStats(currentPage, pageSize);
        setStats(data.content);
        setTotalPages(data.totalPages);
      } catch (err) {
        setError(err instanceof Error ? err.message : 'Erro desconhecido');
      } finally {
        setLoading(false);
      }
    };

    fetchUserConversionStats();
  }, [currentPage, pageSize]);

  return (
    <AppLayout sidebarState="dashboard">
      <Box sx={{ mb: 3, width: '100%' }}>
        <Typography variant="h4" component="h1" gutterBottom sx={{ fontWeight: 'medium' }}>
          Métricas de uso do sistema
        </Typography>
        <Typography variant="body1" color="textSecondary" sx={{ mb: 2 }}>
          Visualização das métricas de uso do sistema
        </Typography>
        <Divider />
      </Box>
      
      <UserConversionStatsTable
        stats={stats}
        loading={loading}
        error={error}
        currentPage={currentPage}
        totalPages={totalPages}
        onPageChange={setCurrentPage}
      />
    </AppLayout>
  );
}