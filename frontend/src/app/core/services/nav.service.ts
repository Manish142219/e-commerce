import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse } from '../models/user.model';
import { NavMenu } from '../models/cart.model';

@Injectable({ providedIn: 'root' })
export class NavService {
  private apiUrl = `${environment.apiUrl}/nav`;

  constructor(private http: HttpClient) {}

  getSections(): Observable<ApiResponse<string[]>> {
    return this.http.get<ApiResponse<string[]>>(`${this.apiUrl}/sections`);
  }

  getMenu(section: string): Observable<ApiResponse<NavMenu>> {
    return this.http.get<ApiResponse<NavMenu>>(`${this.apiUrl}/${section}`);
  }
}
