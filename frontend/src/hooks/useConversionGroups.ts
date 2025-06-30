import { useState, useEffect, useCallback } from 'react';
import { ConversionDTO } from '@/types/ConversionDTO';
import { getConversions, getFile } from '@/services/Conversions.service';
import { groupConversions } from '@/utils/conversionMapper';

export const useConversionGroups = () => {
    const [conversionGroups, setConversionGroups] = useState<ConversionDTO[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [currentPage, setCurrentPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const pageSize = 10; 

    const fetchConversionGroups = useCallback(async (page: number) => {
        try {
            setLoading(true);
            const data = await getConversions(page, pageSize);
            
            // Group the conversions by URL and user
            const grouped = groupConversions(data.content);
            
            setConversionGroups(grouped);
            setTotalPages(data.totalPages);
        } catch (err) {
            setError(err instanceof Error ? err.message : 'Erro desconhecido');
        } finally {
            setLoading(false);
        }
    }, [pageSize]);

    useEffect(() => {
        fetchConversionGroups(currentPage);
    }, [fetchConversionGroups, currentPage]);

    const downloadFile = useCallback(async (group: ConversionDTO, format: 'mp3' | 'mp4') => {
        try {
            const internalFileName = format === 'mp3' ? group.mp3InternalFileName : group.mp4InternalFileName;
            
            if (!internalFileName) {
                throw new Error(`Formato ${format} não disponível para esta conversão`);
            }
            
            const blob = await getFile(internalFileName);
            
            // Create download link
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            
            // Check if youtubeVideoName already has the format extension
            let downloadName = internalFileName;
            if (group.youtubeVideoName) {
                // Remover qualquer extensão existente (.mp3 ou .mp4) antes de adicionar a nova extensão
                let baseName = group.youtubeVideoName;
                if (baseName.toLowerCase().endsWith('.mp3')) {
                    baseName = baseName.substring(0, baseName.length - 4);
                } else if (baseName.toLowerCase().endsWith('.mp4')) {
                    baseName = baseName.substring(0, baseName.length - 4);
                } else if (baseName.toLowerCase().endsWith('.webm')) {
                    baseName = baseName.substring(0, baseName.length - 5);
                }
                
                // Adicionar a extensão correta
                downloadName = `${baseName}.${format}`;
                
                // Se o nome do arquivo terminar com .webm, substitua por .mp4
                if (format === 'mp4' && downloadName.toLowerCase().endsWith('.webm')) {
                    downloadName = downloadName.substring(0, downloadName.length - 5) + '.mp4';
                }
            }
            
            a.download = downloadName;
            document.body.appendChild(a);
            a.click();
            a.remove();
            
            window.URL.revokeObjectURL(url);
            
            return true;
        } catch (error) {
            console.error(`Erro ao baixar arquivo ${format}:`, error);
            return false;
        }
    }, []);

    return { 
        conversionGroups, 
        loading, 
        error, 
        currentPage, 
        setCurrentPage, 
        totalPages, 
        pageSize,
        downloadFile,
        fetchConversionGroups // Expor a função para permitir atualizações manuais
    };
};