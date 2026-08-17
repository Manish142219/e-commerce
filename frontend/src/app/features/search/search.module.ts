import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { SharedModule } from '../../shared/shared.module';
import { SearchComponent } from './search.component';
import { AuthGuard } from '../../core/guards/auth.guard';

const routes: Routes = [
  { path: 'search', component: SearchComponent, canActivate: [AuthGuard] }
];

@NgModule({
  declarations: [SearchComponent],
  imports: [SharedModule, RouterModule.forChild(routes)]
})
export class SearchModule {}
