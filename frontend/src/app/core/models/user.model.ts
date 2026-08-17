export interface User {
  userId: number;
  name: string;
  email: string;
  token: string;
}

export interface AuthResponse {
  success: boolean;
  message: string;
  data: {
    token: string;
    userId: number;
    name: string;
    email: string;
    message: string;
  };
}

export interface ApiResponse<T> {
  success: boolean;
  message?: string;
  data: T;
}
