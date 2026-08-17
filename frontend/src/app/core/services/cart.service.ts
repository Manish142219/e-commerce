import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse } from '../models/user.model';
import { CartItem, CartSummary } from '../models/cart.model';

@Injectable({ providedIn: 'root' })
export class CartService {
  private apiUrl = `${environment.apiUrl}/cart`;
  private cartCountSubject = new BehaviorSubject<number>(0);
  cartCount$ = this.cartCountSubject.asObservable();

  constructor(private http: HttpClient) {}

  getCart(pincode?: string): Observable<ApiResponse<CartSummary>> {
    const params: any = {};
    if (pincode) params.pincode = pincode;
    return this.http.get<ApiResponse<CartSummary>>(this.apiUrl, { params });
  }

  addToCart(productId: number, size: string, quantity = 1): Observable<ApiResponse<CartItem>> {
    return this.http.post<ApiResponse<CartItem>>(this.apiUrl, { productId, size, quantity }).pipe(
      tap({
        next: (res) => {
          if (res.success) this.refreshCount();
        }
      })
    );
  }

  updateQuantity(cartItemId: number, quantity: number): Observable<ApiResponse<void>> {
    return this.http.put<ApiResponse<void>>(`${this.apiUrl}/${cartItemId}/quantity`, null, {
      params: { quantity: quantity.toString() }
    });
  }

  toggleSelection(cartItemId: number, selected: boolean): Observable<ApiResponse<void>> {
    return this.http.put<ApiResponse<void>>(`${this.apiUrl}/${cartItemId}/select`, null, {
      params: { selected: selected.toString() }
    });
  }

  toggleAllSelection(selected: boolean): Observable<ApiResponse<void>> {
    return this.http.put<ApiResponse<void>>(`${this.apiUrl}/select-all`, null, {
      params: { selected: selected.toString() }
    });
  }

  applyCoupon(couponCode: string): Observable<ApiResponse<CartSummary>> {
    return this.http.post<ApiResponse<CartSummary>>(`${this.apiUrl}/coupon`, { couponCode });
  }

  removeCoupon(): Observable<ApiResponse<CartSummary>> {
    return this.http.delete<ApiResponse<CartSummary>>(`${this.apiUrl}/coupon`);
  }

  removeFromCart(cartItemId: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/${cartItemId}`).pipe(
      tap(() => this.refreshCount())
    );
  }

  refreshCount(): void {
    this.http.get<ApiResponse<number>>(`${this.apiUrl}/count`).subscribe(res => {
      if (res.success) this.cartCountSubject.next(res.data);
    });
  }
}
