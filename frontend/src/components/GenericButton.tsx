import React from 'react';
import { Button } from '@mui/material';

interface GenericButtonProps {
  startIcon?: React.ReactNode;
  disabled?: boolean;
  text: string; // O texto do botão
  onClick?: () => void; // Função de click do botão
  sx?: object; // Objeto para passar estilos adicionais ao MUI Button
  className?: string; // Para classes do Tailwind, se necessário
  type?: 'button' | 'submit' | 'reset'; // Tipo do botão
}

const GenericButton: React.FC<GenericButtonProps> = ({ 
  startIcon,
  disabled,
  text, 
  
  onClick, 
  sx, 
  className, 
  type = 'button'
}) => {
  return (
    <Button
      startIcon = {startIcon}
      disabled = {disabled}
      variant="contained"
      fullWidth
      onClick={onClick}
      className={className}
      type={type}
      sx={{
        mt: 3,
        backgroundColor: '#ffffff',
        color: '#000000',
        textTransform: 'none',
        '&:hover': {
          backgroundColor: '#f0f0f0',
          color: 'black'
        },
        ...sx,
      }}
    >
      {text}
    </Button>
  );
};

export default GenericButton;