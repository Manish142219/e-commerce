import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { SharedModule } from '../../shared/shared.module';
import { CouponsComponent } from './coupons.component';
import { ContactComponent } from './contact.component';
import { AuthGuard } from '../../core/guards/auth.guard';

const routes: Routes = [
  { path: 'coupons', component: CouponsComponent, canActivate: [AuthGuard] },
  { path: 'contact', component: ContactComponent, canActivate: [AuthGuard] }
];

@NgModule({
  declarations: [CouponsComponent, ContactComponent],
  imports: [SharedModule, RouterModule.forChild(routes)]
})
export class AccountModule {}
