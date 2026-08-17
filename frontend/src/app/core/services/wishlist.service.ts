import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse } from '../models/user.model';
import { WishlistItem } from '../models/cart.model';

@Injectable({ providedIn: 'root' })
export class WishlistService {
  private apiUrl = `${environment.apiUrl}/wishlist`;
  private wishlistCountSubject = new BehaviorSubject<number>(0);
  wishlistCount$ = this.wishlistCountSubject.asObservable();

  constructor(private http: HttpClient) {}

  getWishlist(): Observable<ApiResponse<WishlistItem[]>> {
    return this.http.get<ApiResponse<WishlistItem[]>>(this.apiUrl);
  }

  addToWishlist(productId: number): Observable<ApiResponse<WishlistItem>> {
    return this.http.post<ApiResponse<WishlistItem>>(`${this.apiUrl}/${productId}`, {}).pipe(
      tap(() => this.refreshCount())
    );
  }

  removeFromWishlist(productId: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/${productId}`).pipe(
      tap(() => this.refreshCount())
    );
  }

  checkWishlist(productId: number): Observable<ApiResponse<boolean>> {
    return this.http.get<ApiResponse<boolean>>(`${this.apiUrl}/check/${productId}`);
  }

  refreshCount(): void {
    this.http.get<ApiResponse<number>>(`${this.apiUrl}/count`).subscribe(res => {
      if (res.success) this.wishlistCountSubject.next(res.data);
    });
  }
}
