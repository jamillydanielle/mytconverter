"use client";

import { Headphones, Activity, Clock, Layers } from 'lucide-react';
import { useCallback } from 'react';
import React from 'react';
import { useRouter } from 'next/navigation';

export interface Feature { 
  icon: React.ComponentType; 
  title: string;
  actionKey: string;
  onClick: () => void;
}

const useMyConvertionsFeatures = () => {
  const router = useRouter();

  const handleFeatureClick = useCallback((featureName: string) => {
  }, []);

  const features: Feature[] = [
    {
      icon: Headphones,  
      title: "Nova Conversão",
      actionKey: "converter",
      onClick: () => router.push('/myconvertions/newconvertion'),
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

export default useMyConvertionsFeatures;