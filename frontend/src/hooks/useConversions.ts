import { useState, useEffect, useCallback } from 'react';
import { Conversion } from '@/types/Conversion';
import { getConversions } from '@/services/Conversions.service';

export const useConversions = () => {
    const [conversions, setConversions] = useState<Conversion[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [currentPage, setCurrentPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const pageSize = 10; 

    const fetchConversions = useCallback(async (page: number) => {
        try {
            setLoading(true);
            const data = await getConversions(page, pageSize);
            setConversions(data.content);
            setTotalPages(data.totalPages);
        } catch (err) {
            setError(err instanceof Error ? err.message : 'Erro desconhecido');
        } finally {
            setLoading(false);
        }
    }, [pageSize]);

    useEffect(() => {
        fetchConversions(currentPage);
    }, [fetchConversions, currentPage]);

    return { conversions, loading, error, currentPage, setCurrentPage, totalPages, pageSize };
};