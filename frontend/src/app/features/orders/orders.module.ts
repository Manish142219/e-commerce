import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { SharedModule } from '../../shared/shared.module';
import { OrdersComponent } from './orders.component';
import { AuthGuard } from '../../core/guards/auth.guard';

const routes: Routes = [
  { path: 'orders', component: OrdersComponent, canActivate: [AuthGuard] }
];

@NgModule({
  declarations: [OrdersComponent],
  imports: [SharedModule, RouterModule.forChild(routes)]
})
export class OrdersModule {}
