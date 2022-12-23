import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { SharedModule } from 'app/shared/shared.module';
import { HomeCustomComponent } from 'app/home/home-custom.component';
import { HomeComponent } from 'app/home/home.component';
import { HOME_CUSTOM_ROUTE } from 'app/home/home-custom.route';

@NgModule({
  imports: [SharedModule, RouterModule.forChild([HOME_CUSTOM_ROUTE])],
  declarations: [HomeComponent, HomeCustomComponent],
})
export class HomeCustomModule {}
