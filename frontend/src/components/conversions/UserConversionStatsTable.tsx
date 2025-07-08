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
  Chip,
  Stack
} from '@mui/material';
import { UserConversionStats } from '@/types/UserConversionStats';
import AudioFileIcon from '@mui/icons-material/AudioFile';
import VideocamIcon from '@mui/icons-material/Videocam';

interface UserConversionStatsTableProps {
  stats: UserConversionStats[];
  loading: boolean;
  error: string | null;
  currentPage: number;
  totalPages: number;
  onPageChange: (page: number) => void;
}

// Função para formatar minutos em hh:mm:ss
const formatTimeHHMMSS = (minutes: number): string => {
  // Converter minutos para segundos
  const totalSeconds = Math.floor(minutes * 60);
  
  // Calcular horas, minutos e segundos
  const hours = Math.floor(totalSeconds / 3600);
  const mins = Math.floor((totalSeconds % 3600) / 60);
  const secs = totalSeconds % 60;
  
  // Formatar com zeros à esquerda quando necessário
  const formattedHours = hours.toString().padStart(2, '0');
  const formattedMins = mins.toString().padStart(2, '0');
  const formattedSecs = secs.toString().padStart(2, '0');
  
  return `${formattedHours}:${formattedMins}:${formattedSecs}`;
};

const UserConversionStatsTable: React.FC<UserConversionStatsTableProps> = ({
  stats,
  loading,
  error,
  currentPage,
  totalPages,
  onPageChange
}) => {
  if (loading) {
    return <Typography>Carregando métricas...</Typography>;
  }

  if (error) {
    return <Typography color="error">Erro ao carregar métricas: {error}</Typography>;
  }

  if (stats.length === 0) {
    return <Typography>Nenhuma estatística encontrada.</Typography>;
  }

  const handlePageChange = (event: React.ChangeEvent<unknown>, page: number) => {
    onPageChange(page - 1); // API uses 0-based indexing
  };

  // Função para renderizar os chips de formato preferido
  const renderFormatChips = (stat: UserConversionStats) => {
    // Se o número de conversões MP3 e MP4 for igual, mostrar ambos os formatos
    if (stat.mp3Conversions === stat.mp4Conversions && stat.mp3Conversions > 0) {
      return (
        <Stack direction="row" spacing={1}>
          <Chip 
            icon={<AudioFileIcon />}
            label="MP3" 
            color="primary" 
            variant="outlined"
          />
          <Chip 
            icon={<VideocamIcon />}
            label="MP4" 
            color="error" 
            variant="outlined"
          />
        </Stack>
      );
    } else {
      // Caso contrário, mostrar apenas o formato preferido
      return (
        <Chip 
          icon={stat.preferredFormat === 'MP3' ? <AudioFileIcon /> : <VideocamIcon />}
          label={stat.preferredFormat} 
          color={stat.preferredFormat === 'MP3' ? 'primary' : 'error'} 
          variant="outlined"
        />
      );
    }
  };

  return (
    <Paper sx={{ p: 2, width: '100%' }}>
      <TableContainer>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Usuário</TableCell>
              <TableCell>Conversões MP3</TableCell>
              <TableCell>Conversões MP4</TableCell>
              <TableCell>Tempo Total Convertido</TableCell>
              <TableCell>Formato Preferido</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {stats.map((stat) => {
              // Calcular o tempo total em minutos
              const totalMinutes = stat.mp3TotalMinutes + stat.mp4TotalMinutes;
              
              return (
                <TableRow key={stat.userId}>
                  <TableCell>
                    <Typography variant="body1" fontWeight="medium">{stat.userName}</Typography>
                    {stat.userEmail && (
                      <Typography variant="body2" color="textSecondary">{stat.userEmail}</Typography>
                    )}
                  </TableCell>
                  <TableCell>
                    <Typography>{stat.mp3Conversions}</Typography>
                  </TableCell>
                  <TableCell>
                    <Typography>{stat.mp4Conversions}</Typography>
                  </TableCell>
                  <TableCell>
                    <Typography>
                      {formatTimeHHMMSS(totalMinutes)}
                    </Typography>
                  </TableCell>
                  <TableCell>
                    {renderFormatChips(stat)}
                  </TableCell>
                </TableRow>
              );
            })}
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

export default UserConversionStatsTable;