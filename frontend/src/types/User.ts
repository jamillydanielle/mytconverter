import { UserType} from "@/types/index";

export interface User {
  id?: number | string;
  name: string;
  email: string;
  password: string;
  type: UserType;
  createdAt: Date;
  updatedAt: Date;
  deactivatedAt?: Date | null;
}