import { useState, useEffect, useCallback } from 'react';
import { User } from '@/types/User';
import { getToken } from '@/utils/token';
import { activateUser, deactivateUser, getUserById, getUsers } from '@/services/Users.service';
import { UserSession, getUserSessions } from '@/services/UserSession.service';

// Interface para usuário com informações de sessão
interface UserWithSession extends User {
  lastSession?: Date | null;
}

export const useUsers = () => {
  const [users, setUsers] = useState<UserWithSession[]>([]);
  const [evaluatedUser, setEvaluatedUser] = useState<User>();
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const pageSize = 10;

  const fetchUsers = useCallback(async (currentPage: number) => {
    try {
      setLoading(true);
      
      // Buscar usuários
      const userData = await getUsers(currentPage, pageSize);
      
      // Buscar informações de sessão
      const sessionData = await getUserSessions();
      
      // Combinar dados de usuários com informações de sessão
      const usersWithSession = userData.content.map(user => {
        const session = sessionData.find(s => s.userId === user.id);
        return {
          ...user,
          lastSession: session?.lastSession ? new Date(session.lastSession) : null
        };
      });
      
      setUsers(usersWithSession);
      setTotalPages(userData.totalPages);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Erro desconhecido');
    } finally {
      setLoading(false);
    }
  }, [pageSize]);

  const deactiveUser = useCallback(async (userId: string) => {
    try {
      await deactivateUser(userId);
      // Atualizar a lista de usuários após desativar
      fetchUsers(currentPage);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Erro ao desativar usuário');
    }
  }, [currentPage, fetchUsers]);

  const activateUserById = useCallback(async (userId: string) => {
    try {
      await activateUser(userId);
      // Atualizar a lista de usuários após ativar
      fetchUsers(currentPage);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Erro ao ativar usuário');
    }
  }, [currentPage, fetchUsers]);

  const fetchUserById = useCallback(async (userId: string) => {
    try {
      setLoading(true);
      const data = await getUserById(userId);
      setEvaluatedUser(data);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Erro desconhecido');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchUsers(currentPage);
  }, [fetchUsers, currentPage]);

  return { 
    users, 
    loading, 
    error, 
    fetchUsers, 
    deactiveUser,
    activateUserById,
    currentPage, 
    setCurrentPage, 
    totalPages, 
    pageSize, 
    fetchUserById, 
    evaluatedUser
  };
};