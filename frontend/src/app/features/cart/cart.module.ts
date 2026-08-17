import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { SharedModule } from '../../shared/shared.module';
import { CartComponent } from './cart.component';
import { AuthGuard } from '../../core/guards/auth.guard';

const routes: Routes = [
  { path: 'cart', component: CartComponent, canActivate: [AuthGuard] }
];

@NgModule({
  declarations: [CartComponent],
  imports: [SharedModule, RouterModule.forChild(routes)]
})
export class CartModule {}
