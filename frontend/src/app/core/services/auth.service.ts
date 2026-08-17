import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse, AuthResponse, User } from '../models/user.model';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private apiUrl = `${environment.apiUrl}/auth`;
  private currentUserSubject = new BehaviorSubject<User | null>(this.getStoredUser());
  currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient) {}

  register(data: { name: string; email: string; password: string; phone?: string }): Observable<ApiResponse<AuthResponse['data']>> {
    return this.http.post<ApiResponse<AuthResponse['data']>>(`${this.apiUrl}/register`, data).pipe(
      tap(res => { if (res.success) this.setSession(res.data); })
    );
  }

  login(data: { email: string; password: string }): Observable<ApiResponse<AuthResponse['data']>> {
    return this.http.post<ApiResponse<AuthResponse['data']>>(`${this.apiUrl}/login`, data).pipe(
      tap(res => { if (res.success) this.setSession(res.data); })
    );
  }

  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    this.currentUserSubject.next(null);
  }

  isLoggedIn(): boolean {
    return !!localStorage.getItem('token');
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  getCurrentUser(): User | null {
    return this.currentUserSubject.value;
  }

  private setSession(data: AuthResponse['data']): void {
    const user: User = {
      token: data.token,
      userId: data.userId,
      name: data.name,
      email: data.email
    };
    localStorage.setItem('token', data.token);
    localStorage.setItem('user', JSON.stringify(user));
    this.currentUserSubject.next(user);
  }

  private getStoredUser(): User | null {
    const user = localStorage.getItem('user');
    return user ? JSON.parse(user) : null;
  }
}
