// components/ui/Input.tsx
    import React from 'react';
    import TextField, { TextFieldProps } from '@mui/material/TextField';

    interface InputProps extends Omit<TextFieldProps, 'variant'> {
      // variant é fixo para 'outlined' ou pode ser uma prop
    }

    const Input: React.FC<InputProps> = ({ label, error, helperText, ...props }) => {
      return (
        <TextField
          label={label}
          variant="outlined"
          fullWidth
          error={error}
          helperText={error ? helperText : undefined} // Mostra helperText se houver erro
          margin="normal" // Adiciona um espaçamento padrão
          {...props}
        />
      );
    };

    export default Input;
