"use client";

import React, { useContext, useEffect } from "react";
import { useAlerts } from "@/components/alert/useAlerts";
import AlertManager from "@/components/alert/AlertManager";

export const AlertContext = React.createContext<{
  addAlert: (message: string, type: "error" | "warning" | "info" | "success") => void;
} | undefined>(undefined);

export const AlertProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const { alerts, addAlert, removeAlert } = useAlerts();

  useEffect(() => {
    const handleStorage = () => {
      const storedAlerts = localStorage.getItem("alerts");
      if (storedAlerts) {
        const parsedAlerts = JSON.parse(storedAlerts);
        parsedAlerts.forEach((alert: any) => {
          if (!alerts.some((existingAlert) => existingAlert.id === alert.id)) {
            addAlert(alert.message, alert.type);
          }
        });
      }
    };

    window.addEventListener("storage", handleStorage);
    return () => window.removeEventListener("storage", handleStorage);
  }, [addAlert, alerts]);

  return (
    <AlertContext.Provider value={{ addAlert }}>
      <AlertManager alerts={alerts} removeAlert={removeAlert} />
      {children}
    </AlertContext.Provider>
  );
};

export const useAlert = () => {
  const context = useContext(AlertContext);
  if (context === undefined) {
    throw new Error("useAlert must be used within a AlertProvider");
  }
  return context;
};