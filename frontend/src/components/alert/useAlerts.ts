"use client";
import { useCallback, useState, useEffect } from "react";

interface Alert {
  id: number;
  message: string;
  type: "error" | "warning" | "info" | "success";
  createdAt: number;
}

const MAX_ALERTS = 5;
const ALERT_DURATION = 5000; // 5 segundos

export const useAlerts = () => {
  const [alerts, setAlerts] = useState<Alert[]>([]);

  // Carregar alertas do localStorage ao montar o componente
  useEffect(() => {
    const storedAlerts = localStorage.getItem("alerts");
    if (storedAlerts) {
      const parsedAlerts = JSON.parse(storedAlerts);
      const now = Date.now();
      const validAlerts = parsedAlerts
        .filter((alert: Alert) => now - alert.createdAt < ALERT_DURATION) // Apenas alertas válidos
        .slice(-MAX_ALERTS); // Limita ao máximo de alertas
      setAlerts(validAlerts);
      localStorage.setItem("alerts", JSON.stringify(validAlerts)); // Atualiza o localStorage
    }
  }, []);

  // Adicionar alerta ao estado e ao localStorage
  const addAlert = useCallback((message: string, type: "error" | "warning" | "info" | "success") => {
    setAlerts((prevAlerts) => {
      // Verifica se já existe um alerta com a mesma mensagem e tipo
      const exists = prevAlerts.some((alert) => alert.message === message && alert.type === type);
      if (exists) return prevAlerts; // Não adiciona duplicatas

      const newAlert: Alert = {
        id: Date.now(), // ID baseado no timestamp
        message,
        type,
        createdAt: Date.now(),
      };

      const updatedAlerts = [...prevAlerts, newAlert].slice(-MAX_ALERTS); // Limita ao máximo de alertas
      localStorage.setItem("alerts", JSON.stringify(updatedAlerts)); // Atualiza o localStorage
      return updatedAlerts;
    });
  }, []);

  // Remover alerta do estado e do localStorage
  const removeAlert = useCallback((id: number) => {
    setAlerts((prevAlerts) => {
      const updatedAlerts = prevAlerts.filter((alert) => alert.id !== id);
      localStorage.setItem("alerts", JSON.stringify(updatedAlerts)); // Atualiza o localStorage
      return updatedAlerts;
    });
  }, []);

  return { alerts, addAlert, removeAlert };
};