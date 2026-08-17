import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CategoryService } from '../../../core/services/category.service';
import { ProductService } from '../../../core/services/product.service';
import { Category } from '../../../core/models/category.model';
import { Product } from '../../../core/models/product.model';

@Component({
  selector: 'app-category-list',
  templateUrl: './category-list.component.html',
  styleUrls: ['./category-list.component.css']
})
export class CategoryListComponent implements OnInit {
  category: Category | null = null;
  products: Product[] = [];
  brands: string[] = [];
  loading = true;

  selectedBrand = '';
  selectedColor = '';
  minPrice = 0;
  maxPrice = 5000;
  minDiscount = 0;
  sortBy = 'recommended';

  colors = ['Blue', 'Brown', 'Pink', 'Beige', 'White', 'Grey', 'Black', 'Red', 'Peach', 'Navy Blue', 'Green'];
  discountOptions = [10, 30, 40, 50, 60, 70];

  breadcrumbItems: { label: string; link?: string }[] = [];

  constructor(
    private route: ActivatedRoute,
    private categoryService: CategoryService,
    private productService: ProductService
  ) {}

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      const slug = params['slug'];
      this.categoryService.getBySlug(slug).subscribe(res => {
        if (res.success) {
          this.category = res.data;
          this.breadcrumbItems = [
            { label: 'Home', link: '/home' },
            { label: 'Clothing', link: '/home' },
            { label: this.category!.name }
          ];
          this.loadProducts();
          this.loadBrands();
        }
      });
    });
  }

  loadProducts(): void {
    if (!this.category) return;
    this.loading = true;
    this.productService.getByCategory(this.category.id, {
      brand: this.selectedBrand || undefined,
      minPrice: this.minPrice || undefined,
      maxPrice: this.maxPrice || undefined,
      color: this.selectedColor || undefined,
      minDiscount: this.minDiscount || undefined
    }).subscribe({
      next: (res) => {
        if (res.success) {
          this.products = this.sortProducts(res.data);
        }
        this.loading = false;
      },
      error: () => this.loading = false
    });
  }

  loadBrands(): void {
    if (!this.category) return;
    this.productService.getBrands(this.category.id).subscribe(res => {
      if (res.success) this.brands = res.data;
    });
  }

  applyFilters(): void {
    this.loadProducts();
  }

  sortProducts(products: Product[]): Product[] {
    const sorted = [...products];
    switch (this.sortBy) {
      case 'price-low': return sorted.sort((a, b) => a.price - b.price);
      case 'price-high': return sorted.sort((a, b) => b.price - a.price);
      case 'discount': return sorted.sort((a, b) => b.discountPercent - a.discountPercent);
      default: return sorted;
    }
  }

  onSortChange(): void {
    this.products = this.sortProducts(this.products);
  }
}
