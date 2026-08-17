export interface Address {
  id: number;
  name: string;
  phone: string;
  pincode: string;
  addressLine: string;
  city: string;
  state: string;
  isDefault: boolean;
  label: string;
}

export interface CreateAddressRequest {
  name: string;
  phone: string;
  pincode: string;
  addressLine: string;
  city: string;
  state: string;
  label?: string;
  isDefault?: boolean;
}

export interface DeliveryCheck {
  pincode: string;
  deliverable: boolean;
  estimatedDeliveryDate: string;
  codAvailable: boolean;
  returnDays: number;
  message: string;
  city: string;
}
