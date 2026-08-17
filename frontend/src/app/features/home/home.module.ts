import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { SharedModule } from '../../shared/shared.module';
import { HomeComponent } from './home.component';
import { AuthGuard } from '../../core/guards/auth.guard';

const routes: Routes = [
  { path: 'home', component: HomeComponent, canActivate: [AuthGuard] }
];

@NgModule({
  declarations: [HomeComponent],
  imports: [SharedModule, RouterModule.forChild(routes)]
})
export class HomeModule {}
