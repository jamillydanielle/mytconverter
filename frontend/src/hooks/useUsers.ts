import { useState, useEffect, useCallback } from 'react';
import { User } from '@/types/User';
import { getToken } from '@/utils/token';
import { deactivateUser, getUserById, getUsers } from '@/services/Users.service';

export const useUsers = () => {
  const [users, setUsers] = useState<User[]>([]);
  const [evaluatedUser, setEvaluatedUser] = useState<User>();
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const pageSize = 10;

  const fetchUsers = useCallback(async (currentPage : number) => {
    try {
      setLoading(true);
      const data = await getUsers(currentPage, pageSize);
      setUsers(data.content);
      setTotalPages(data.totalPages);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Erro desconhecido');
    } finally {
      setLoading(false);
    }
  }, []);

  const deactiveUsers = useCallback(async (userId: string) => {
      const response = await deactivateUser(userId);
  }, []);

  const fetchUserById = useCallback(async (userId: string) => {
    try{
      setLoading(true);
      const data = await getUserById(userId);
      setEvaluatedUser(data);
    }catch(err){
      setError(err instanceof Error? err.message : 'Erro desconhecido');
    }finally{
      setLoading(false);
    }
    
  }, []);

  useEffect(() => {
    fetchUsers(currentPage);
  }, [fetchUsers]);

  return { users, loading, error, fetchUsers, deactiveUsers, currentPage, setCurrentPage, totalPages, pageSize, fetchUserById, evaluatedUser};
};