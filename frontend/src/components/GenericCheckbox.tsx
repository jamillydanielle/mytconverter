import React from 'react';
import { Box, Checkbox, Typography } from '@mui/material';

interface GenericCheckboxProps {
  name: string;
  labelText: string; // Texto do checkbox
  textSx?: object;
  checkboxSx?: object;
  className?: string; // Para classes do Tailwind no container se desejado
  onChange?: (e: React.ChangeEvent<HTMLInputElement>) => void; // Função para lidar com mudanças
}

const GenericCheckbox: React.FC<GenericCheckboxProps> = ({
  labelText,
  textSx,
  checkboxSx,
  className,
  name,
  onChange
}) => {
  return (
    <Box
      display="flex"
      alignItems="center"
      className={className}
    >
      <Checkbox
        name={name}
        onChange={onChange}
        sx={{
          color: '#ffffff',
          ...checkboxSx,
          '&.Mui-checked': {
            color: '#ffffff',
          },
        }}
      />
      <Typography
        sx={{
          color: '#ffffff',
          fontWeight: 500,
          fontFamily: 'var(--font-dm-sans)',
          fontSize: '16px',
          ...textSx,
        }}
      >
        {labelText}
      </Typography>
    </Box>
  );
};

export default GenericCheckbox;
