import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { SharedModule } from '../../shared/shared.module';
import { ProductDetailComponent } from './product-detail/product-detail.component';
import { AuthGuard } from '../../core/guards/auth.guard';

const routes: Routes = [
  { path: 'product/:id', component: ProductDetailComponent, canActivate: [AuthGuard] }
];

@NgModule({
  declarations: [ProductDetailComponent],
  imports: [SharedModule, RouterModule.forChild(routes)]
})
export class ProductModule {}
