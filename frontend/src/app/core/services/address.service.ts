import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse } from '../models/user.model';
import { Address, CreateAddressRequest } from '../models/address.model';

@Injectable({ providedIn: 'root' })
export class AddressService {
  private apiUrl = `${environment.apiUrl}/addresses`;
  private selectedAddressSubject = new BehaviorSubject<Address | null>(this.getStoredAddress());
  selectedAddress$ = this.selectedAddressSubject.asObservable();

  constructor(private http: HttpClient) {}

  getAddresses(): Observable<ApiResponse<Address[]>> {
    return this.http.get<ApiResponse<Address[]>>(this.apiUrl);
  }

  getDefaultAddress(): Observable<ApiResponse<Address>> {
    return this.http.get<ApiResponse<Address>>(`${this.apiUrl}/default`);
  }

  createAddress(data: CreateAddressRequest): Observable<ApiResponse<Address>> {
    return this.http.post<ApiResponse<Address>>(this.apiUrl, data);
  }

  setDefaultAddress(id: number): Observable<ApiResponse<Address>> {
    return this.http.put<ApiResponse<Address>>(`${this.apiUrl}/${id}/default`, {});
  }

  deleteAddress(id: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/${id}`);
  }

  setSelectedAddress(address: Address | null): void {
    if (address) {
      localStorage.setItem('selectedAddress', JSON.stringify(address));
    } else {
      localStorage.removeItem('selectedAddress');
    }
    this.selectedAddressSubject.next(address);
  }

  getSelectedAddress(): Address | null {
    return this.selectedAddressSubject.value;
  }

  getSelectedPincode(): string {
    return this.selectedAddressSubject.value?.pincode || localStorage.getItem('selectedPincode') || '201309';
  }

  setSelectedPincode(pincode: string): void {
    localStorage.setItem('selectedPincode', pincode);
  }

  private getStoredAddress(): Address | null {
    const stored = localStorage.getItem('selectedAddress');
    return stored ? JSON.parse(stored) : null;
  }
}
