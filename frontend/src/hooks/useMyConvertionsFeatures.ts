"use client";

import { Headphones, Activity, Clock, Layers } from 'lucide-react';
import { useCallback } from 'react';
import React from 'react';

export interface Feature { // Added export here
  icon: React.ComponentType; // Change to store the component type
  title: string;
  actionKey: string;
  onClick: () => void;
}

const useMyConversionsFeatures = () => {
  const handleFeatureClick = useCallback((featureName: string) => {
    alert(`Funcionalidade "${featureName}" clicada!`);
    // Add navigation or action logic here
  }, []);

  const features: Feature[] = [
    {
      icon: Headphones,  // Store the component type (no JSX here)
      title: "Nova Conversão",
      actionKey: "converter",
      onClick: () => handleFeatureClick("Nova Conversão"),
    },
    {
      icon: Activity,
      title: "Editar Áudio",
      actionKey: "editar",
      onClick: () => handleFeatureClick("Editar Áudio"),
    },
    {
      icon: Clock,
      title: "Meu Histórico",
      actionKey: "historico",
      onClick: () => handleFeatureClick("Meu Histórico"),
    },
    {
      icon: Layers,
      title: "Converter em Lote",
      actionKey: "lote",
      onClick: () => handleFeatureClick("Converter em Lote"),
    },
  ];

  return { features };
};

export default useMyConversionsFeatures;