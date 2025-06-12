import type { Metadata } from "next";
import ThemeRegistry from "@/components/ThemeRegistry";
import { AlertProvider } from "@/components/alert/AlertProvider"; // Seu AlertProvider
import "./globals.css";

export const metadata: Metadata = {
  title: "MytConvert",
  description: "Conversor de áudio e vídeo",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="pt-BR">
      <body>
        <ThemeRegistry>
          <AlertProvider>{children}</AlertProvider>
        </ThemeRegistry>
      </body>
    </html>
  );
}