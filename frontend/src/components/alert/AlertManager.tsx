"use client";
import React, { useState, useEffect, useCallback } from "react";
import CustomAlertModal from "./CustomAlertModal";

interface Alert {
  id: number;
  message: string;
  type: "error" | "warning" | "info" | "success";
  createdAt: number;
}

interface VisibleAlert extends Alert {
  progress: number;
}

interface AlertManagerProps {
  alerts: Alert[];
  removeAlert: (id: number) => void;
}

const ALERT_DURATION = 5000; // 5 segundos

const AlertManager: React.FC<AlertManagerProps> = ({ alerts, removeAlert }) => {
  const [visibleAlerts, setVisibleAlerts] = useState<VisibleAlert[]>([]);

  useEffect(() => {
    const now = Date.now();
    setVisibleAlerts((prevAlerts) => {
      const newAlerts = alerts
        .filter((alert) => !prevAlerts.some((prevAlert) => prevAlert.id === alert.id))
        .map((alert) => ({
          ...alert,
          progress: Math.max(0, 100 - ((now - alert.createdAt) / ALERT_DURATION) * 100),
        }));

      // Remover duplicatas garantindo unicidade
      const uniqueAlerts = [...prevAlerts, ...newAlerts].filter(
        (alert, index, self) => self.findIndex((a) => a.id === alert.id) === index
      );

      return uniqueAlerts;
    });
  }, [alerts]);

  useEffect(() => {
    const interval = setInterval(() => {
      const now = Date.now();
      setVisibleAlerts((currentAlerts) =>
        currentAlerts.map((alert) => ({
          ...alert,
          progress: Math.max(0, 100 - ((now - alert.createdAt) / ALERT_DURATION) * 100),
        }))
      );
    }, 100);

    return () => clearInterval(interval);
  }, []);

  useEffect(() => {
    visibleAlerts.forEach((alert) => {
      if (alert.progress === 0) {
        handleClose(alert.id);
      }
    });
  }, [visibleAlerts]);

  const handleClose = useCallback(
    (id: number) => {
      setVisibleAlerts((current) => current.filter((alert) => alert.id !== id));
      removeAlert(id);
    },
    [removeAlert]
  );

  return (
    <div className="fixed top-4 right-4 z-50 flex flex-col w-80">
      {visibleAlerts
        .filter((alert) => alert.progress > 0)
        .map((alert) => (
          <CustomAlertModal
            key={alert.id}
            message={alert.message}
            type={alert.type}
            onClose={() => handleClose(alert.id)}
            progress={alert.progress}
          />
        ))}
    </div>
  );
};

export default AlertManager;