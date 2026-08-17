import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { CartService } from '../../../core/services/cart.service';
import { WishlistService } from '../../../core/services/wishlist.service';
import { NavService } from '../../../core/services/nav.service';
import { NavMenu, NavMenuLink } from '../../../core/models/cart.model';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit {
  navSections = ['MEN', 'WOMEN', 'KIDS', 'HOME', 'BEAUTY', 'GENZ', 'STUDIO'];
  activeSection = '';
  megaMenu: NavMenu | null = null;
  searchQuery = '';
  cartCount = 0;
  wishlistCount = 0;
  userName = '';
  showMegaMenu = false;
  showProfileMenu = false;

  constructor(
    public authService: AuthService,
    private cartService: CartService,
    private wishlistService: WishlistService,
    private navService: NavService,
    private router: Router
  ) {}

  ngOnInit(): void {
    if (this.authService.isLoggedIn()) {
      this.userName = this.authService.getCurrentUser()?.name || '';
      this.cartService.refreshCount();
      this.wishlistService.refreshCount();
    }
    this.cartService.cartCount$.subscribe(c => this.cartCount = c);
    this.wishlistService.wishlistCount$.subscribe(c => this.wishlistCount = c);
  }

  onNavHover(section: string): void {
    this.activeSection = section;
    this.showMegaMenu = true;
    this.showProfileMenu = false;
    this.navService.getMenu(section).subscribe({
      next: (res) => {
        if (res.success && this.activeSection === section) {
          this.megaMenu = res.data;
        }
      },
      error: () => {
        this.megaMenu = null;
      }
    });
  }

  onNavLeave(): void {
    this.showMegaMenu = false;
    this.activeSection = '';
    this.megaMenu = null;
  }

  onProfileEnter(): void {
    this.showProfileMenu = true;
    this.showMegaMenu = false;
  }

  onProfileLeave(): void {
    this.showProfileMenu = false;
  }

  onSearch(): void {
    if (this.searchQuery.trim()) {
      this.router.navigate(['/search'], { queryParams: { q: this.searchQuery.trim() } });
    }
  }

  openCategory(slug: string): void {
    if (!slug) return;
    this.showMegaMenu = false;
    this.activeSection = '';
    this.megaMenu = null;
    this.router.navigate(['/category', slug]);
  }

  openNavLink(link: NavMenuLink): void {
    if (!link) return;
    this.showMegaMenu = false;
    this.activeSection = '';
    this.megaMenu = null;

    if (link.linkType === 'SEARCH') {
      this.router.navigate(['/search'], { queryParams: { q: link.name } });
    } else {
      this.router.navigate(['/category', link.slug || 'casual-wear']);
    }
  }

  navigateAndClose(path: string): void {
    this.showProfileMenu = false;
    this.router.navigate([path]);
  }

  logout(): void {
    this.showProfileMenu = false;
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  getColumnKeys(): number[] {
    if (!this.megaMenu?.columns) return [];
    return Object.keys(this.megaMenu.columns).map(k => +k).sort();
  }
}
