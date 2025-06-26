import { Metadata } from 'next';

export const metadata: Metadata = {
  title: 'Gerenciar Conversões - Admin',
  description: 'Administração de conversões do sistema.',
};

export default function ConversionsLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return <>{children}</>;
}