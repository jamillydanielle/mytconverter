"use client";

import React, { useState, useEffect } from 'react';
import AppLayout from '@/components/layout/AppLayout';
import { Box, Typography, Divider, Paper, Pagination } from '@mui/material';
import { getConversions } from '@/services/Conversions.service';
import ConversionsTable from '@/components/conversions/ConversionsTable';
import { Conversion } from '@/types/Conversion';

export default function ConversionsPage() {
  const [conversions, setConversions] = useState<Conversion[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const pageSize = 10;

  useEffect(() => {
    const fetchConversions = async () => {
      try {
        setLoading(true);
        const data = await getConversions(currentPage, pageSize);
        setConversions(data.content);
        setTotalPages(data.totalPages);
      } catch (err) {
        setError(err instanceof Error ? err.message : 'Erro desconhecido');
      } finally {
        setLoading(false);
      }
    };

    fetchConversions();
  }, [currentPage, pageSize]);

  return (
    <AppLayout sidebarState="dashboard">
      <Box sx={{ mb: 3, width: '100%' }}>
        <Typography variant="h4" component="h1" gutterBottom sx={{ fontWeight: 'medium' }}>
          Listar Todas as conversões e métricas
        </Typography>
        <Divider />
      </Box>
      
      <ConversionsTable
        conversions={conversions}
        loading={loading}
        error={error}
        currentPage={currentPage}
        totalPages={totalPages}
        onPageChange={setCurrentPage}
      />
    </AppLayout>
  );
}