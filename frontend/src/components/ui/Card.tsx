// components/ui/Card.tsx
    import React from 'react';
    import MuiCard, { CardProps as MuiCardProps } from '@mui/material/Card';
    import CardActionArea from '@mui/material/CardActionArea';

    interface CardProps extends MuiCardProps {
      children: React.ReactNode;
      onClick?: () => void;
    }

    const Card: React.FC<CardProps> = ({ children, onClick, sx, ...props }) => {
      if (onClick) {
        return (
          <MuiCard sx={{ height: '100%', ...sx }} {...props}>
            <CardActionArea onClick={onClick} sx={{ height: '100%', display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', p: 2 }}>
              {children}
            </CardActionArea>
          </MuiCard>
        );
      }
      return (
        <MuiCard sx={{ p: 2, ...sx }} {...props}>
          {children}
        </MuiCard>
      );
    };

    export default Card;