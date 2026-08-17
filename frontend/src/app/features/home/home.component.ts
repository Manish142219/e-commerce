import { Component, OnInit } from '@angular/core';
import { CategoryService } from '../../core/services/category.service';
import { Category } from '../../core/models/category.model';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  categories: Category[] = [];
  loading = true;

  constructor(private categoryService: CategoryService) {}

  ngOnInit(): void {
    this.categoryService.getAll().subscribe({
      next: (res) => {
        if (res.success) this.categories = res.data;
        this.loading = false;
      },
      error: () => this.loading = false
    });
  }
}
