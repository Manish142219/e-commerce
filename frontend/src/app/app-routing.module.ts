import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  {
    path: '',
    loadChildren: () => import('./features/auth/auth.module').then(m => m.AuthModule)
  },
  {
    path: '',
    loadChildren: () => import('./features/home/home.module').then(m => m.HomeModule)
  },
  {
    path: '',
    loadChildren: () => import('./features/category/category.module').then(m => m.CategoryModule)
  },
  {
    path: '',
    loadChildren: () => import('./features/product/product.module').then(m => m.ProductModule)
  },
  {
    path: '',
    loadChildren: () => import('./features/cart/cart.module').then(m => m.CartModule)
  },
  {
    path: '',
    loadChildren: () => import('./features/wishlist/wishlist.module').then(m => m.WishlistModule)
  },
  {
    path: '',
    loadChildren: () => import('./features/profile/profile.module').then(m => m.ProfileModule)
  },
  {
    path: '',
    loadChildren: () => import('./features/search/search.module').then(m => m.SearchModule)
  },
  {
    path: '',
    loadChildren: () => import('./features/checkout/checkout.module').then(m => m.CheckoutModule)
  },
  {
    path: '',
    loadChildren: () => import('./features/orders/orders.module').then(m => m.OrdersModule)
  },
  {
    path: '',
    loadChildren: () => import('./features/addresses/addresses.module').then(m => m.AddressesModule)
  },
  {
    path: '',
    loadChildren: () => import('./features/account/account.module').then(m => m.AccountModule)
  },
  { path: '**', redirectTo: 'login' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {}
