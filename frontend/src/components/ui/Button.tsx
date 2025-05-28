// components/ui/Button.tsx
    import React from 'react';
    import MuiButton, { ButtonProps as MuiButtonProps } from '@mui/material/Button';

    interface ButtonProps extends MuiButtonProps {
      // Adicione props customizadas se necessário, mas geralmente as do MUI são suficientes
      // Ex: fullWidth já existe no MuiButtonProps
    }

    const Button: React.FC<ButtonProps> = ({ children, variant = "contained", ...props }) => {
      return (
        <MuiButton variant={variant} {...props}>
          {children}
        </MuiButton>
      );
    };

    export default Button;