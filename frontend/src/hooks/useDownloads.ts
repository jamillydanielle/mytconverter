import { useState, useCallback } from 'react';
import { downloadMedia, getFile } from '@/services/Conversions.service';
import { useSessionIdentifier } from '@/hooks/useSessionIdentifier';
import { FileInput } from 'lucide-react';

interface DownloadResult {
  successMessage: string;
  error: string;
  loading: boolean;
  fileName: string | null;
  internalFileName: string | null;
  download: (url: string, format: 'mp3' | 'mp4') => Promise<void>;
  handleDownloadClick: () => void;
}

interface UseDownloadOptions {
  onDownloadSuccess?: () => void; // Callback para quando o download for concluído com sucesso
}

export const useDownload = (options?: UseDownloadOptions): DownloadResult => {
  const { onDownloadSuccess } = options || {};
  const [successMessage, setSuccessMessage] = useState<string>('');
  const [error, setError] = useState<string>('');
  const [loading, setLoading] = useState<boolean>(false);
  const [fileName, setFileName] = useState<string | null>(null);
  const [internalFileName, setInternalFileName] = useState<string | null>(null);
  const [format, setFormat] = useState<'mp3' | 'mp4' | null>(null);
  const { userData } = useSessionIdentifier();


  const handleDownloadClick = useCallback(async () => {
    if (internalFileName) {
      try {
        setLoading(true);
        const blob = await getFile(internalFileName);

        // Cria um link para download
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        
        // Check if fileName already has the format extension
        let downloadName = internalFileName;
        if (fileName) {
          let fileFormat = fileName.split('.').pop()?.toLowerCase() || '';
          
          // Se a extensão for webm e o formato solicitado for mp4, substitua por mp4
          if (fileFormat === 'webm' && format === 'mp4') {
            fileFormat = 'mp4';
          }
          
          // Check if fileName already ends with the format extension
          if (fileFormat && fileName.toLowerCase().endsWith(`.${fileFormat}`)) {
            // Se a extensão for webm, substitua por mp4
            if (fileName.toLowerCase().endsWith('.webm') && format === 'mp4') {
              downloadName = fileName.substring(0, fileName.length - 5) + '.mp4';
            } else {
              downloadName = fileName;
            }
          } else if (fileFormat) {
            downloadName = `${fileName}.${fileFormat}`;
          } else {
            downloadName = fileName;
          }
        }
        
        a.download = downloadName;
        document.body.appendChild(a); 
        a.click();
        a.remove();

        window.URL.revokeObjectURL(url);

      } catch (err: any) {
        console.error("Erro ao baixar o arquivo:", err);
        setError('Erro ao baixar o arquivo.');
      } finally {
        setLoading(false);
      }
    } else {
      console.error("Tentativa de download sem nome de arquivo", { internalFileName });
      setError("Nome do arquivo não disponível para download.");
    }
  }, [internalFileName, fileName, format]);


  const download = useCallback(async (url: string, format: 'mp3' | 'mp4') => {
    setError('');
    setSuccessMessage('');
    setFileName(null);
    setInternalFileName(null);
    setFormat(format);
    setLoading(true);

    try {
      // Obter o link de download e o nome do arquivo da API
      // The downloadMedia function now handles extracting the YouTube video name
      const response = await downloadMedia(url, format);

      // Importante: definir todos os estados em uma única atualização de renderização
      const fileNameValue = response.data.file_name;
      const InternalFileNameValue = response.data.internal_filename;
      // Atualizar os estados com os valores obtidos
      setFileName(fileNameValue);
      setInternalFileName(InternalFileNameValue);
      setSuccessMessage('Seu download está pronto!');

      // Chamar o callback de sucesso, se fornecido
      if (onDownloadSuccess) {
        onDownloadSuccess();
      }

    } catch (err: any) {
      setError('Erro ao tentar baixar o vídeo. Verifique a URL ou tente novamente.');
    } finally {
      setLoading(false);
    }
  }, [onDownloadSuccess]);

  return {
    successMessage,
    error,
    loading,
    fileName,
    internalFileName,
    download,
    handleDownloadClick
  };
};