import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ProductService } from '../../../core/services/product.service';
import { CartService } from '../../../core/services/cart.service';
import { WishlistService } from '../../../core/services/wishlist.service';
import { AddressService } from '../../../core/services/address.service';
import { DeliveryService } from '../../../core/services/delivery.service';
import { Product } from '../../../core/models/product.model';
import { AuthService } from '../../../core/services/auth.service';
import { Address, DeliveryCheck } from '../../../core/models/address.model';

@Component({
  selector: 'app-product-detail',
  templateUrl: './product-detail.component.html',
  styleUrls: ['./product-detail.component.css']
})
export class ProductDetailComponent implements OnInit {
  product: Product | null = null;
  selectedSize = '';
  selectedImage = '';
  isWishlisted = false;
  loading = true;
  addingToCart = false;
  message = '';
  messageIsError = false;

  showAddressModal = false;
  selectedAddress: Address | null = null;
  deliveryInfo: DeliveryCheck | null = null;
  pincode = '201309';

  breadcrumbItems: { label: string; link?: string }[] = [];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private productService: ProductService,
    private cartService: CartService,
    private wishlistService: WishlistService,
    private addressService: AddressService,
    private deliveryService: DeliveryService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.selectedAddress = this.addressService.getSelectedAddress();
    this.pincode = this.addressService.getSelectedPincode();
    this.loadDeliveryInfo();

    this.route.params.subscribe(params => {
      this.productService.getById(+params['id']).subscribe({
        next: (res) => {
          if (res.success) {
            this.product = res.data;
            this.selectedImage = this.product.imageUrl;
            this.selectedSize = '';
            this.autoSelectIfSingleOption();
            this.breadcrumbItems = [
              { label: 'Home', link: '/home' },
              { label: this.product.categoryName || 'Products', link: this.product.categorySlug ? `/category/${this.product.categorySlug}` : '/home' },
              { label: `${this.product.brand} ${this.product.name}` }
            ];
            this.checkWishlist();
          }
          this.loading = false;
        },
        error: () => this.loading = false
      });
    });
  }

  /** Free Size / single pack auto-select for convenience */
  private autoSelectIfSingleOption(): void {
    if (this.product?.sizes?.length === 1) {
      this.selectedSize = this.product.sizes[0];
    }
  }

  get variantType(): string {
    return this.product?.variantType || 'CLOTHING';
  }

  get variantLabel(): string {
    return this.product?.variantLabel || 'SELECT SIZE';
  }

  get chartLabel(): string {
    switch (this.variantType) {
      case 'FOOTWEAR': return 'SIZE CHART >';
      case 'BEAUTY': return 'VOLUME GUIDE >';
      case 'ACCESSORY': return 'DETAILS >';
      default: return 'SIZE CHART >';
    }
  }

  get requiredMessage(): string {
    switch (this.variantType) {
      case 'FOOTWEAR': return 'Please select a shoe size';
      case 'BEAUTY': return 'Please select quantity / volume';
      case 'ACCESSORY': return 'Please select an option';
      default: return 'Please select a size';
    }
  }

  get isPillVariant(): boolean {
    return this.variantType === 'BEAUTY' || this.variantType === 'ACCESSORY';
  }

  loadDeliveryInfo(): void {
    this.deliveryService.checkPincode(this.pincode).subscribe({
      next: (res) => {
        if (res.success) this.deliveryInfo = res.data;
      }
    });
  }

  checkWishlist(): void {
    if (!this.product) return;
    this.wishlistService.checkWishlist(this.product.id).subscribe(res => {
      if (res.success) this.isWishlisted = res.data;
    });
  }

  selectSize(size: string): void {
    this.selectedSize = size;
    this.message = '';
  }

  addToBag(): void {
    if (!this.product) return;

    // If product has options, one must be selected
    if (this.product.sizes?.length && !this.selectedSize) {
      this.message = this.requiredMessage;
      this.messageIsError = true;
      return;
    }

    // Fallback when product has no size list
    const variant = this.selectedSize || 'One Size';

    if (!this.authService.isLoggedIn()) {
      this.message = 'Please login to add items to bag';
      this.messageIsError = true;
      this.router.navigate(['/login']);
      return;
    }

    this.addingToCart = true;
    this.message = '';

    this.cartService.addToCart(this.product.id, variant).subscribe({
      next: (res) => {
        if (res.success) {
          this.message = 'Added to bag successfully!';
          this.messageIsError = false;
          this.cartService.refreshCount();
        } else {
          this.message = res.message || 'Failed to add to bag';
          this.messageIsError = true;
        }
        this.addingToCart = false;
      },
      error: (err) => {
        const status = err?.status;
        if (status === 401 || status === 403) {
          this.message = 'Session expired. Please login again.';
          this.router.navigate(['/login']);
        } else {
          this.message = err.error?.message || 'Failed to add to bag. Is backend running?';
        }
        this.messageIsError = true;
        this.addingToCart = false;
      }
    });
  }

  toggleWishlist(): void {
    if (!this.product) return;
    if (this.isWishlisted) {
      this.wishlistService.removeFromWishlist(this.product.id).subscribe(() => {
        this.isWishlisted = false;
        this.message = 'Removed from wishlist';
        this.messageIsError = false;
      });
    } else {
      this.wishlistService.addToWishlist(this.product.id).subscribe({
        next: () => {
          this.isWishlisted = true;
          this.message = 'Added to wishlist';
          this.messageIsError = false;
        },
        error: () => {
          this.message = 'Failed to add to wishlist';
          this.messageIsError = true;
        }
      });
    }
  }

  openAddressModal(): void {
    this.showAddressModal = true;
  }

  onAddressSelected(address: Address): void {
    this.selectedAddress = address;
    this.pincode = address.pincode;
    this.loadDeliveryInfo();
  }

  onDeliveryChecked(info: DeliveryCheck): void {
    this.deliveryInfo = info;
    this.pincode = info.pincode;
  }

  goToBag(): void {
    this.router.navigate(['/cart']);
  }
}
