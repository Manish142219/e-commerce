import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CartService } from '../../core/services/cart.service';
import { AddressService } from '../../core/services/address.service';
import { CartSummary } from '../../core/models/cart.model';
import { Address } from '../../core/models/address.model';

@Component({
  selector: 'app-cart',
  templateUrl: './cart.component.html',
  styleUrls: ['./cart.component.css']
})
export class CartComponent implements OnInit {
  cart: CartSummary | null = null;
  loading = true;
  showAddressModal = false;
  selectedAddress: Address | null = null;
  couponCode = '';
  couponMessage = '';
  couponError = false;
  allSelected = true;

  constructor(
    private cartService: CartService,
    private addressService: AddressService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.selectedAddress = this.addressService.getSelectedAddress();
    this.loadCart();
    this.loadDefaultAddress();
  }

  loadDefaultAddress(): void {
    this.addressService.getDefaultAddress().subscribe({
      next: (res) => {
        if (res.success && res.data) {
          this.selectedAddress = res.data;
          this.addressService.setSelectedAddress(res.data);
        }
      }
    });
  }

  loadCart(): void {
    this.loading = true;
    const pincode = this.addressService.getSelectedPincode();
    this.cartService.getCart(pincode).subscribe({
      next: (res) => {
        if (res.success) {
          this.cart = res.data;
          this.allSelected = this.cart.items.length > 0 &&
            this.cart.items.every(i => i.selected);
        }
        this.loading = false;
      },
      error: () => this.loading = false
    });
  }

  updateQuantity(itemId: number, quantity: number): void {
    if (quantity < 1) return;
    this.cartService.updateQuantity(itemId, quantity).subscribe(() => this.loadCart());
  }

  toggleItemSelection(itemId: number, selected: boolean): void {
    this.cartService.toggleSelection(itemId, selected).subscribe(() => this.loadCart());
  }

  toggleAllSelection(): void {
    this.allSelected = !this.allSelected;
    this.cartService.toggleAllSelection(this.allSelected).subscribe(() => this.loadCart());
  }

  removeItem(itemId: number): void {
    this.cartService.removeFromCart(itemId).subscribe(() => this.loadCart());
  }

  applyCoupon(): void {
    if (!this.couponCode.trim()) return;
    this.cartService.applyCoupon(this.couponCode.trim()).subscribe({
      next: (res) => {
        if (res.success) {
          this.cart = res.data;
          this.couponMessage = 'Coupon applied successfully!';
          this.couponError = false;
        }
      },
      error: (err) => {
        this.couponMessage = err.error?.message || 'Invalid coupon';
        this.couponError = true;
      }
    });
  }

  removeCoupon(): void {
    this.cartService.removeCoupon().subscribe({
      next: (res) => {
        if (res.success) {
          this.cart = res.data;
          this.couponCode = '';
          this.couponMessage = 'Coupon removed';
          this.couponError = false;
        }
      }
    });
  }

  onAddressSelected(address: Address): void {
    this.selectedAddress = address;
    this.loadCart();
  }

  placeOrder(): void {
    if (!this.cart || this.cart.selectedItemCount === 0) return;
    this.router.navigate(['/checkout/address']);
  }

  get deliveryDisplay(): string {
    if (this.selectedAddress) {
      return `${this.selectedAddress.name}, ${this.selectedAddress.pincode}`;
    }
    return `Pincode ${this.addressService.getSelectedPincode()}`;
  }

  get deliveryAddressLine(): string {
    if (this.selectedAddress) {
      return `${this.selectedAddress.addressLine}, ${this.selectedAddress.city}, ${this.selectedAddress.state}`;
    }
    return 'Select or add a delivery address';
  }
}
