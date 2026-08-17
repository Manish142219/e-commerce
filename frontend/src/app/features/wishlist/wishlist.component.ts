import { Component, OnInit } from '@angular/core';
import { WishlistService } from '../../core/services/wishlist.service';
import { WishlistItem } from '../../core/models/cart.model';

@Component({
  selector: 'app-wishlist',
  templateUrl: './wishlist.component.html',
  styleUrls: ['./wishlist.component.css']
})
export class WishlistComponent implements OnInit {
  items: WishlistItem[] = [];
  loading = true;

  constructor(private wishlistService: WishlistService) {}

  ngOnInit(): void {
    this.wishlistService.getWishlist().subscribe({
      next: (res) => {
        if (res.success) this.items = res.data;
        this.loading = false;
      },
      error: () => this.loading = false
    });
  }

  removeItem(productId: number): void {
    this.wishlistService.removeFromWishlist(productId).subscribe(() => {
      this.items = this.items.filter(i => i.productId !== productId);
    });
  }
}
