import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { SharedModule } from '../../shared/shared.module';
import { CheckoutAddressComponent } from './checkout-address/checkout-address.component';
import { AuthGuard } from '../../core/guards/auth.guard';

const routes: Routes = [
  { path: 'checkout/address', component: CheckoutAddressComponent, canActivate: [AuthGuard] }
];

@NgModule({
  declarations: [CheckoutAddressComponent],
  imports: [SharedModule, RouterModule.forChild(routes)]
})
export class CheckoutModule {}
