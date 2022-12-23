import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { GenericLicenseUrlComponent } from './list/generic-license-url.component';
import { GenericLicenseUrlDetailComponent } from './detail/generic-license-url-detail.component';
import { GenericLicenseUrlUpdateComponent } from './update/generic-license-url-update.component';
import { GenericLicenseUrlDeleteDialogComponent } from './delete/generic-license-url-delete-dialog.component';
import { GenericLicenseUrlRoutingModule } from './route/generic-license-url-routing.module';

@NgModule({
  imports: [SharedModule, GenericLicenseUrlRoutingModule],
  declarations: [
    GenericLicenseUrlComponent,
    GenericLicenseUrlDetailComponent,
    GenericLicenseUrlUpdateComponent,
    GenericLicenseUrlDeleteDialogComponent,
  ],
  entryComponents: [GenericLicenseUrlDeleteDialogComponent],
})
export class GenericLicenseUrlModule {}
