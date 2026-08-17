export interface OrderItem {
  productId: number;
  brand: string;
  name: string;
  imageUrl: string;
  size: string;
  quantity: number;
  price: number;
  mrp: number;
}

export interface Order {
  id: number;
  orderNumber: string;
  status: string;
  totalAmount: number;
  deliveryName: string;
  deliveryPhone: string;
  deliveryAddress: string;
  deliveryPincode: string;
  createdAt: string;
  items: OrderItem[];
}
