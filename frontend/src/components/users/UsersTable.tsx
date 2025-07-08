import React from 'react';
import { 
  Table, 
  TableBody, 
  TableCell, 
  TableContainer, 
  TableHead, 
  TableRow,
  Paper,
  Typography,
  Pagination,
  Box,
  Chip
} from '@mui/material';
import { User } from '@/types/User';
import { format } from 'date-fns';

interface UserWithSession extends User {
  lastSession?: Date | null;
}

interface UsersTableProps {
  users: UserWithSession[];
  loading: boolean;
  error: string | null;
  currentPage: number;
  totalPages: number;
  onPageChange: (page: number) => void;
  onDeactivate: (userId: string) => void;
  onActivate: (userId: string) => void;
}

const UsersTable: React.FC<UsersTableProps> = ({
  users,
  loading,
  error,
  currentPage,
  totalPages,
  onPageChange
}) => {
  if (loading) {
    return <Typography>Carregando usuários...</Typography>;
  }

  if (error) {
    return <Typography color="error">Erro ao carregar usuários: {error}</Typography>;
  }

  if (users.length === 0) {
    return <Typography>Nenhum usuário encontrado.</Typography>;
  }

  const handlePageChange = (event: React.ChangeEvent<unknown>, page: number) => {
    onPageChange(page - 1); // API uses 0-based indexing
  };

  const formatDate = (date: Date | null | undefined) => {
    if (!date) return 'Nunca';
    return format(new Date(date), 'dd/MM/yyyy HH:mm');
  };

  // Função para verificar se um usuário está ativo
  const isUserActive = (user: UserWithSession): boolean => {
    // Um usuário está ativo se deactivatedAt for null ou undefined
    return user.deactivatedAt === null || user.deactivatedAt === undefined;
  };

  return (
    <Paper sx={{ p: 2, width: '100%' }}>
      <TableContainer>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Nome</TableCell>
              <TableCell>Email</TableCell>
              <TableCell>Tipo de Usuário</TableCell>
              <TableCell>Status</TableCell>
              <TableCell>Última Sessão</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {users.map((user) => {
              const active = isUserActive(user);
              
              return (
                <TableRow 
                  key={user.id}
                  sx={{ 
                    backgroundColor: active ? 'inherit' : 'rgba(0, 0, 0, 0.04)'
                  }}
                >
                  <TableCell>{user.name}</TableCell>
                  <TableCell>{user.email}</TableCell>
                  <TableCell>{user.type}</TableCell>
                  <TableCell>
                    <Chip 
                      label={active ? "Ativo" : "Inativo"} 
                      color={active ? "success" : "error"} 
                      variant="outlined"
                      size="small"
                    />
                  </TableCell>
                  <TableCell>
                    <Typography>
                      {formatDate(user.lastSession)}
                    </Typography>
                  </TableCell>
                </TableRow>
              );
            })}
          </TableBody>
        </Table>
      </TableContainer>
      <Box sx={{ display: 'flex', justifyContent: 'center', mt: 2 }}>
        <Pagination 
          count={totalPages} 
          page={currentPage + 1} // API uses 0-based indexing
          onChange={handlePageChange} 
          color="primary" 
        />
      </Box>
    </Paper>
  );
};

export default UsersTable;