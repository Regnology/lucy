import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { SharedModule } from 'app/shared/shared.module';
import { LoginComponent } from './login.component';
import { LoginCustomComponent } from './login-custom.component';
import { LOGIN_CUSTOM_ROUTE } from './login-custom.route';

@NgModule({
  imports: [SharedModule, RouterModule.forChild([LOGIN_CUSTOM_ROUTE])],
  declarations: [LoginComponent, LoginCustomComponent],
})
export class LoginCustomModule {}
