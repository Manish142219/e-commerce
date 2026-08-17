import { Address, CreateAddressRequest, DeliveryCheck } from './address.model';

export interface CartItem {
  id: number;
  productId: number;
  brand: string;
  name: string;
  imageUrl: string;
  size: string;
  quantity: number;
  price: number;
  mrp: number;
  discountPercent: number;
  stockQuantity: number;
  selected: boolean;
  estimatedDeliveryDate?: string;
}

export interface CartSummary {
  items: CartItem[];
  totalMrp: number;
  totalDiscount: number;
  couponDiscount: number;
  platformFee: number;
  totalAmount: number;
  itemCount: number;
  selectedItemCount: number;
  appliedCouponCode?: string;
  deliveryAddress?: Address;
}

export interface WishlistItem {
  id: number;
  productId: number;
  brand: string;
  name: string;
  imageUrl: string;
  price: number;
  mrp: number;
  discountPercent: number;
}

export interface NavMenuLink {
  name: string;
  slug: string;
  linkType?: string;
}

export interface NavMenuGroup {
  title: string;
  links: NavMenuLink[];
}

export interface NavMenuCategory {
  id: number;
  name: string;
  slug: string;
  imageUrl?: string;
  discountText?: string;
}

export interface NavMenu {
  section: string;
  categories?: NavMenuCategory[];
  columns: { [key: number]: NavMenuGroup[] };
}

export type { Address, CreateAddressRequest, DeliveryCheck };
