import { Metadata } from 'next';

export const metadata: Metadata = {
  title: 'Métricas de uso - Admin',
  description: 'Administração das métricas de uso do sistema.',
};

export default function ConvertionsLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return <>{children}</>;
}