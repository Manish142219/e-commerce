import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { SharedModule } from '../../shared/shared.module';
import { ProfileComponent } from './profile.component';
import { AuthGuard } from '../../core/guards/auth.guard';

const routes: Routes = [
  { path: 'profile', component: ProfileComponent, canActivate: [AuthGuard] }
];

@NgModule({
  declarations: [ProfileComponent],
  imports: [SharedModule, RouterModule.forChild(routes)]
})
export class ProfileModule {}
