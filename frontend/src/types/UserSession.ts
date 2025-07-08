export interface UserSession {
  userId: number | string;
  isLoggedIn: boolean;
  lastSession: Date | null;
}