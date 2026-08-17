import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { SharedModule } from '../../shared/shared.module';
import { WishlistComponent } from './wishlist.component';
import { AuthGuard } from '../../core/guards/auth.guard';

const routes: Routes = [
  { path: 'wishlist', component: WishlistComponent, canActivate: [AuthGuard] }
];

@NgModule({
  declarations: [WishlistComponent],
  imports: [SharedModule, RouterModule.forChild(routes)]
})
export class WishlistModule {}
