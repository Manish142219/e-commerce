import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { SharedModule } from '../../shared/shared.module';
import { AddressesComponent } from './addresses.component';
import { AuthGuard } from '../../core/guards/auth.guard';

const routes: Routes = [
  { path: 'addresses', component: AddressesComponent, canActivate: [AuthGuard] }
];

@NgModule({
  declarations: [AddressesComponent],
  imports: [SharedModule, RouterModule.forChild(routes)]
})
export class AddressesModule {}
