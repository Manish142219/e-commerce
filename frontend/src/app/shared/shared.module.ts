import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { HeaderComponent } from './components/header/header.component';
import { CategoryCardComponent } from './components/category-card/category-card.component';
import { ProductCardComponent } from './components/product-card/product-card.component';
import { BreadcrumbComponent } from './components/breadcrumb/breadcrumb.component';

import { AddressModalComponent } from './components/address-modal/address-modal.component';

@NgModule({
  declarations: [
    HeaderComponent,
    CategoryCardComponent,
    ProductCardComponent,
    BreadcrumbComponent,
    AddressModalComponent
  ],
  imports: [
    CommonModule,
    RouterModule,
    FormsModule
  ],
  exports: [
    HeaderComponent,
    CategoryCardComponent,
    ProductCardComponent,
    BreadcrumbComponent,
    AddressModalComponent,
    CommonModule,
    FormsModule,
    RouterModule
  ]
})
export class SharedModule {}
