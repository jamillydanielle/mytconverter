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
  Link,
  Button,
  Stack
} from '@mui/material';
import { ConversionDTO } from '@/types/ConversionDTO';
import { format } from 'date-fns';
import AudioFileIcon from '@mui/icons-material/AudioFile';
import VideocamIcon from '@mui/icons-material/Videocam';

interface ConversionGroupsTableProps {
  conversionGroups: ConversionDTO[];
  loading: boolean;
  error: string | null;
  currentPage: number;
  totalPages: number;
  onPageChange: (page: number) => void;
  onDownload: (group: ConversionDTO, format: 'mp3' | 'mp4') => Promise<boolean>;
}

const ConversionGroupsTable: React.FC<ConversionGroupsTableProps> = ({
  conversionGroups,
  loading,
  error,
  currentPage,
  totalPages,
  onPageChange,
  onDownload
}) => {
  if (loading) {
    return <Typography>Carregando conversões...</Typography>;
  }

  if (error) {
    return <Typography color="error">Erro ao carregar conversões: {error}</Typography>;
  }

  if (conversionGroups.length === 0) {
    return <Typography>Nenhuma conversão encontrada.</Typography>;
  }

  const handlePageChange = (event: React.ChangeEvent<unknown>, page: number) => {
    onPageChange(page - 1); // API uses 0-based indexing
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
              <TableCell>Duração</TableCell>
              <TableCell>Data de Criação</TableCell>
              <TableCell>Downloads disponíveis</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {conversionGroups.map((group) => (
              <TableRow key={group.id}>
                <TableCell>
                  <Tooltip title={group.youtubeVideoName || "Nome não disponível"}>
                    <Typography noWrap sx={{ maxWidth: 200 }}>
                      {truncateText(group.youtubeVideoName || "Nome não disponível", 30)}
                    </Typography>
                  </Tooltip>
                </TableCell>
                <TableCell>
                  {group.youtubeUrl ? (
                    <Tooltip title={group.youtubeUrl}>
                      <Link 
                        href={group.youtubeUrl} 
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
                        {truncateText(group.youtubeUrl, 30)}
                      </Link>
                    </Tooltip>
                  ) : (
                    "URL não disponível"
                  )}
                </TableCell>
                <TableCell>{`${group.length} segundos`}</TableCell>
                <TableCell>
                  {format(new Date(group.createdAt), 'dd/MM/yyyy HH:mm')}
                </TableCell>
                <TableCell>
                  <Stack direction="row" spacing={1}>
                    {group.hasMP3 && (
                      <Button 
                        variant="contained"
                        size="small"
                        startIcon={<AudioFileIcon />}
                        onClick={() => onDownload(group, 'mp3')}
                        sx={{
                          backgroundColor: '#1976d2', // Cor azul para MP3
                          color: 'white',
                          '&:hover': {
                            backgroundColor: '#115293', // Cor mais escura no hover
                          },
                          borderRadius: '20px', // Botões mais arredondados
                          padding: '4px 12px',
                        }}
                      >
                        MP3
                      </Button>
                    )}
                    {group.hasMP4 && (
                      <Button 
                        variant="contained"
                        size="small"
                        startIcon={<VideocamIcon />}
                        onClick={() => onDownload(group, 'mp4')}
                        sx={{
                          backgroundColor: '#f44336', // Cor vermelha para MP4
                          color: 'white',
                          '&:hover': {
                            backgroundColor: '#d32f2f', // Cor mais escura no hover
                          },
                          borderRadius: '20px', // Botões mais arredondados
                          padding: '4px 12px',
                        }}
                      >
                        MP4
                      </Button>
                    )}
                  </Stack>
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

export default ConversionGroupsTable;