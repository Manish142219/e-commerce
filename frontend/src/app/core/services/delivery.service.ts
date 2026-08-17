import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse } from '../models/user.model';
import { DeliveryCheck } from '../models/address.model';

@Injectable({ providedIn: 'root' })
export class DeliveryService {
  private apiUrl = `${environment.apiUrl}/delivery`;

  constructor(private http: HttpClient) {}

  checkPincode(pincode: string): Observable<ApiResponse<DeliveryCheck>> {
    return this.http.get<ApiResponse<DeliveryCheck>>(`${this.apiUrl}/check`, {
      params: { pincode }
    });
  }
}
