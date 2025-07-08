import { Metadata } from 'next';

export const metadata: Metadata = {
  title: 'Gerenciar Usuários - Admin',
  description: 'Administração de usuários do sistema.',
};

export default function UsersLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return <>{children}</>;
}
