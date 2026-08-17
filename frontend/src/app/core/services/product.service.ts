import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse } from '../models/user.model';
import { Product } from '../models/product.model';

@Injectable({ providedIn: 'root' })
export class ProductService {
  private apiUrl = `${environment.apiUrl}/products`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<ApiResponse<Product[]>> {
    return this.http.get<ApiResponse<Product[]>>(this.apiUrl);
  }

  getById(id: number): Observable<ApiResponse<Product>> {
    return this.http.get<ApiResponse<Product>>(`${this.apiUrl}/${id}`);
  }

  getByCategory(categoryId: number, filters?: {
    brand?: string; minPrice?: number; maxPrice?: number;
    color?: string; minDiscount?: number;
  }): Observable<ApiResponse<Product[]>> {
    let params: any = {};
    if (filters) {
      Object.entries(filters).forEach(([k, v]) => { if (v !== undefined && v !== null && v !== '') params[k] = v; });
    }
    return this.http.get<ApiResponse<Product[]>>(`${this.apiUrl}/category/${categoryId}`, { params });
  }

  getBrands(categoryId: number): Observable<ApiResponse<string[]>> {
    return this.http.get<ApiResponse<string[]>>(`${this.apiUrl}/category/${categoryId}/brands`);
  }

  search(query: string): Observable<ApiResponse<Product[]>> {
    return this.http.get<ApiResponse<Product[]>>(`${this.apiUrl}/search`, { params: { q: query } });
  }
}
