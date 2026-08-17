import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { SharedModule } from '../../shared/shared.module';
import { CategoryListComponent } from './category-list/category-list.component';
import { AuthGuard } from '../../core/guards/auth.guard';

const routes: Routes = [
  { path: 'category/:slug', component: CategoryListComponent, canActivate: [AuthGuard] }
];

@NgModule({
  declarations: [CategoryListComponent],
  imports: [SharedModule, RouterModule.forChild(routes)]
})
export class CategoryModule {}
