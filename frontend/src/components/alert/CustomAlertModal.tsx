"use client"
import React from 'react';
import { Alert, AlertTitle, LinearProgress } from '@mui/material';

interface CustomAlertModalProps {
  message: string;
  type: 'error' | 'warning' | 'info' | 'success';
  onClose: () => void;
  progress: number;
}

const CustomAlertModal: React.FC<CustomAlertModalProps> = ({ message, type, onClose, progress }) => {
  return (
    <div className="mb-6 w-full">
      <Alert 
        severity={type} 
        onClose={onClose}
        className="shadow-lg w-full"
      >
        <AlertTitle className="font-bold">{type.charAt(0).toUpperCase() + type.slice(1)}</AlertTitle>
        {message}
      </Alert>
      <LinearProgress 
        variant="determinate" 
        value={progress} 
        className="mt-1"
      />
    </div>
  );
};

export default CustomAlertModal;