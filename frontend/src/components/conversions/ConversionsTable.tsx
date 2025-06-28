import React from 'react';
import { 
  Table, 
  TableBody, 
  TableCell, 
  TableContainer, 
  TableHead, 
  TableRow,
  Paper,
  Typography,
  Pagination,
  Box,
  Tooltip,
  Link
} from '@mui/material';
import { Conversion } from '@/types/Conversion';
import { format } from 'date-fns';

interface ConversionsTableProps {
  conversions: Conversion[];
  loading: boolean;
  error: string | null;
  currentPage: number;
  totalPages: number;
  onPageChange: (page: number) => void;
}

const ConversionsTable: React.FC<ConversionsTableProps> = ({
  conversions,
  loading,
  error,
  currentPage,
  totalPages,
  onPageChange
}) => {
  if (loading) {
    return <Typography>Carregando conversões...</Typography>;
  }

  if (error) {
    return <Typography color="error">Erro ao carregar conversões: {error}</Typography>;
  }

  if (conversions.length === 0) {
    return <Typography>Nenhuma conversão encontrada.</Typography>;
  }

  const handlePageChange = (event: React.ChangeEvent<unknown>, page: number) => {
    onPageChange(page - 1); // API uses 0-based indexing
  };

  const formatFileSize = (bytes?: number) => {
    if (!bytes) return 'N/A';
    const mb = bytes / (1024 * 1024);
    return `${mb.toFixed(2)} MB`;
  };

  // Function to truncate long text with ellipsis
  const truncateText = (text: string, maxLength: number) => {
    if (!text) return '';
    return text.length > maxLength ? `${text.substring(0, maxLength)}...` : text;
  };

  return (
    <Paper sx={{ p: 2, width: '100%' }}>
      <TableContainer>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Nome do Vídeo</TableCell>
              <TableCell>URL do YouTube</TableCell>
              <TableCell>Formato</TableCell>
              <TableCell>Duração</TableCell>
              <TableCell>Data de Criação</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {conversions.map((conversion) => (
              <TableRow key={conversion.id}>
                <TableCell>
                  <Tooltip title={conversion.youtubeVideoName || "Nome não disponível"}>
                    <Typography noWrap sx={{ maxWidth: 200 }}>
                      {truncateText(conversion.youtubeVideoName || "Nome não disponível", 30)}
                    </Typography>
                  </Tooltip>
                </TableCell>
                <TableCell>
                  {conversion.youtubeUrl ? (
                    <Tooltip title={conversion.youtubeUrl}>
                      <Link 
                        href={conversion.youtubeUrl} 
                        target="_blank" 
                        rel="noopener noreferrer"
                        sx={{ 
                          display: 'block',
                          maxWidth: 200,
                          overflow: 'hidden',
                          textOverflow: 'ellipsis',
                          whiteSpace: 'nowrap'
                        }}
                      >
                        {truncateText(conversion.youtubeUrl, 30)}
                      </Link>
                    </Tooltip>
                  ) : (
                    "URL não disponível"
                  )}
                </TableCell>
                <TableCell>{conversion.format}</TableCell>
                <TableCell>{`${conversion.length} segundos`}</TableCell>
                <TableCell>
                  {format(new Date(conversion.createdAt), 'dd/MM/yyyy HH:mm')}
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
      <Box sx={{ display: 'flex', justifyContent: 'center', mt: 2 }}>
        <Pagination 
          count={totalPages} 
          page={currentPage + 1} // API uses 0-based indexing
          onChange={handlePageChange} 
          color="primary" 
        />
      </Box>
    </Paper>
  );
};

export default ConversionsTable;