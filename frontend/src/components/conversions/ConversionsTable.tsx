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
  Button
} from '@mui/material';
import { Conversion } from '@/types/Conversion';
import { format } from 'date-fns';
import AudioFileIcon from '@mui/icons-material/AudioFile';
import VideocamIcon from '@mui/icons-material/Videocam';
import { getFile } from '@/services/Conversions.service';

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

  // Function to handle download
  const handleDownload = async (conversion: Conversion) => {
    try {
      const blob = await getFile(conversion.internalFileName);
      
      // Create download link
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      
      // Check if youtubeVideoName already has the format extension
      let downloadName = conversion.internalFileName;
      if (conversion.youtubeVideoName) {
        // Para vídeos, sempre use a extensão .mp4, mesmo que o formato real seja .webm
        const format = conversion.format.toUpperCase() === 'MP4' ? 'mp4' : conversion.format.toLowerCase();
        
        // Check if youtubeVideoName already ends with the format extension
        if (conversion.youtubeVideoName.toLowerCase().endsWith(`.${format}`)) {
          downloadName = conversion.youtubeVideoName;
        } else {
          downloadName = `${conversion.youtubeVideoName}.${format}`;
        }
        
        // Se o nome do arquivo terminar com .webm, substitua por .mp4
        if (downloadName.toLowerCase().endsWith('.webm')) {
          downloadName = downloadName.substring(0, downloadName.length - 5) + '.mp4';
        }
      }
      
      a.download = downloadName;
      document.body.appendChild(a);
      a.click();
      a.remove();
      
      window.URL.revokeObjectURL(url);
    } catch (error) {
      console.error("Erro ao baixar o arquivo:", error);
      alert("Erro ao baixar o arquivo. Por favor, tente novamente.");
    }
  };

  // Function to get the appropriate icon based on format
  const getFormatIcon = (format: string) => {
    if (format.toUpperCase() === 'MP3') {
      return <AudioFileIcon />;
    } else if (format.toUpperCase() === 'MP4') {
      return <VideocamIcon />;
    }
    return null;
  };

  // Function to get button style based on format
  const getButtonStyle = (format: string) => {
    if (format.toUpperCase() === 'MP3') {
      return {
        backgroundColor: '#1976d2', // Cor azul para MP3
        color: 'white',
        '&:hover': {
          backgroundColor: '#115293', // Cor mais escura no hover
        },
        borderRadius: '20px', // Botões mais arredondados
        padding: '4px 12px',
      };
    } else if (format.toUpperCase() === 'MP4') {
      return {
        backgroundColor: '#f44336', // Cor vermelha para MP4
        color: 'white',
        '&:hover': {
          backgroundColor: '#d32f2f', // Cor mais escura no hover
        },
        borderRadius: '20px', // Botões mais arredondados
        padding: '4px 12px',
      };
    }
    return {};
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
              <TableCell>Download</TableCell>
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
                <TableCell>
                  <Button
                    variant="contained"
                    size="small"
                    startIcon={getFormatIcon(conversion.format)}
                    onClick={() => handleDownload(conversion)}
                    sx={getButtonStyle(conversion.format)}
                  >
                    {conversion.format}
                  </Button>
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