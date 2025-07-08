import React from 'react';
import { Box, BoxProps } from '@mui/material';
import { SxProps, Theme } from '@mui/system';

interface CustomAlertProps {
  message: string;
  sx?: SxProps<Theme>;
  className?: string;
}

const CustomAlert: React.FC<CustomAlertProps & Omit<BoxProps, 'sx'>> = ({ 
  message, 
  sx, 
  className,
  ...otherProps 
}) => {
  return (
    <Box 
      className={className}
      sx={{
        textAlign: 'center',
        py: 2,
        px: 4, 
        bgcolor: '#690037',
        color: '#FA5A50',
        fontSize: 'medium',
        borderRadius: '4px',
        mt: 2,
        mb: 2,
        ...sx
      }}
      {...otherProps}
    >
      {message}
    </Box>
  );
};

export default CustomAlert;