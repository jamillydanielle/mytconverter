import React, { useState, useEffect } from 'react';
import { Box, TextField, Select, MenuItem, Button, Typography } from '@mui/material';
import DownloadIcon from '@mui/icons-material/Download';

interface DownloadFormProps {
  onSubmit: (url: string, format: 'mp3' | 'mp4') => void;
  onDownloadClick: () => void;
  loading: boolean;
  error: string;
  successMessage: string;
  fileName: string | null;
  internalFileName: string | null;
}

export const DownloadForm: React.FC<DownloadFormProps> = ({
  onSubmit,
  onDownloadClick,
  loading,
  error,
  successMessage,
  fileName,
  internalFileName
}) => {
  const [url, setUrl] = useState<string>('');
  const [format, setFormat] = useState<'mp3' | 'mp4'>('mp3');

  const handleSubmit = (event: React.FormEvent) => {
    event.preventDefault();
    onSubmit(url, format);
  };

  return (
    <Box component="form" onSubmit={handleSubmit} sx={{ display: 'flex', flexDirection: 'column', gap: 2, mt: 3 }}>
      <TextField
        label="URL do vídeo"
        variant="outlined"
        required
        value={url}
        onChange={(e) => setUrl(e.target.value)}
        placeholder="https://www.youtube.com/watch?v=exemplo"
      />
      <Select
        value={format}
        onChange={(e) => setFormat(e.target.value as 'mp3' | 'mp4')}
        required
        variant="outlined"
      >
        <MenuItem value="mp3">MP3 (Áudio)</MenuItem>
        <MenuItem value="mp4">MP4 (Vídeo)</MenuItem>
      </Select>
      <Button
        type="submit"
        variant="contained"
        color="primary"
        disabled={loading}
      >
        {loading ? "Processando..." : "Converter"}
      </Button>

      {successMessage && (
        <Typography variant="body1" color="success" mt={2}>
          ✅ {successMessage}
        </Typography>
      )}

      {internalFileName && (
        <Button
          variant="contained"
          color="secondary"
          startIcon={<DownloadIcon />}
          onClick={onDownloadClick}
          disabled={loading}
          sx={{ mt: 2 }}
        >
          Baixar {fileName || internalFileName}
        </Button>
      )}

      {error && (
        <Typography variant="body1" color="error" mt={2}>
          ❌ {error}
        </Typography>
      )}
    </Box>
  );
};