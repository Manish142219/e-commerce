import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AddressService } from '../../../core/services/address.service';
import { CartService } from '../../../core/services/cart.service';
import { OrderService } from '../../../core/services/order.service';
import { Address } from '../../../core/models/address.model';
import { CartSummary } from '../../../core/models/cart.model';

@Component({
  selector: 'app-checkout-address',
  templateUrl: './checkout-address.component.html',
  styleUrls: ['./checkout-address.component.css']
})
export class CheckoutAddressComponent implements OnInit {
  addresses: Address[] = [];
  selectedAddressId: number | null = null;
  cart: CartSummary | null = null;
  loading = true;
  placing = false;
  showAddressModal = false;
  showAddForm = false;
  errorMessage = '';
  successMessage = '';

  newAddress = {
    name: '', phone: '', pincode: '', addressLine: '', city: '', state: '', label: 'HOME'
  };

  constructor(
    private addressService: AddressService,
    private cartService: CartService,
    private orderService: OrderService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadAddresses();
    this.loadCart();
  }

  loadAddresses(): void {
    this.addressService.getAddresses().subscribe({
      next: (res) => {
        if (res.success) {
          this.addresses = res.data || [];
          const defaultAddr = this.addresses.find(a => a.isDefault);
          if (defaultAddr) this.selectedAddressId = defaultAddr.id;
        }
        this.loading = false;
      },
      error: () => this.loading = false
    });
  }

  loadCart(): void {
    const pincode = this.addressService.getSelectedPincode();
    this.cartService.getCart(pincode).subscribe({
      next: (res) => { if (res.success) this.cart = res.data; }
    });
  }

  selectAddress(id: number): void {
    this.selectedAddressId = id;
    this.errorMessage = '';
  }

  continueToPayment(): void {
    if (!this.selectedAddressId) {
      this.errorMessage = 'Please choose a delivery address to continue';
      return;
    }

    this.placing = true;
    this.errorMessage = '';

    this.orderService.placeOrder(this.selectedAddressId).subscribe({
      next: (res) => {
        this.placing = false;
        if (res.success) {
          this.cartService.refreshCount();
          this.successMessage = `Order ${res.data.orderNumber} placed! Payment comes in next phase.`;
          setTimeout(() => this.router.navigate(['/orders']), 1200);
        } else {
          this.errorMessage = res.message || 'Failed to place order';
        }
      },
      error: (err) => {
        this.placing = false;
        this.errorMessage = err.error?.message || 'Failed to place order';
      }
    });
  }

  onAddressSelected(address: Address): void {
    this.addresses.push(address);
    this.selectedAddressId = address.id;
    this.loadCart();
  }

  saveNewAddress(): void {
    this.addressService.createAddress({
      ...this.newAddress,
      isDefault: this.addresses.length === 0
    }).subscribe({
      next: (res) => {
        if (res.success) {
          this.addresses.push(res.data);
          this.selectedAddressId = res.data.id;
          this.showAddForm = false;
        }
      }
    });
  }

  get defaultAddresses(): Address[] {
    return this.addresses.filter(a => a.isDefault);
  }

  get otherAddresses(): Address[] {
    return this.addresses.filter(a => !a.isDefault);
  }

  goBack(): void {
    this.router.navigate(['/cart']);
  }
}
