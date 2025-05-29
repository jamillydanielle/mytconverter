// frontend/src/hooks/useConvertions.ts
import { useState, useEffect, useCallback } from 'react';
import { Convertion } from '@/types/Convertion';
import { getConvertions } from '@/services/Convertions.service';

export const useConvertions = () => {
    const [convertions, setConvertions] = useState<Convertion[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [currentPage, setCurrentPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const pageSize = 10; // Or make this configurable

    const fetchConvertions = useCallback(async (page: number) => {
        try {
            setLoading(true);
            const data = await getConvertions(page, pageSize);
            setConvertions(data.content);
            setTotalPages(data.totalPages);
        } catch (err) {
            setError(err instanceof Error ? err.message : 'Erro desconhecido');
        } finally {
            setLoading(false);
        }
    }, [pageSize]); // pageSize as a dependency

    useEffect(() => {
        fetchConvertions(currentPage);
    }, [fetchConvertions, currentPage]);

    return { convertions, loading, error, currentPage, setCurrentPage, totalPages, pageSize };
};