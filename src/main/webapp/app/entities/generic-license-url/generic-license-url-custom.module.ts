import { NgModule } from '@angular/core';

import { SharedModule } from 'app/shared/shared.module';
import { GenericLicenseUrlCustomComponent } from './list/generic-license-url-custom.component';
import { GenericLicenseUrlUpdateCustomComponent } from './update/generic-license-url-update-custom.component';
import { GenericLicenseUrlDeleteDialogCustomComponent } from './delete/generic-license-url-delete-dialog-custom.component';
import { GenericLicenseUrlDetailCustomComponent } from 'app/entities/generic-license-url/detail/generic-license-url-detail-custom.component';
import { GenericLicenseUrlRoutingCustomModule } from 'app/entities/generic-license-url/route/generic-license-url-routing-custom.module';
import { GenericLicenseUrlComponent } from 'app/entities/generic-license-url/list/generic-license-url.component';
import { GenericLicenseUrlDetailComponent } from 'app/entities/generic-license-url/detail/generic-license-url-detail.component';
import { GenericLicenseUrlUpdateComponent } from 'app/entities/generic-license-url/update/generic-license-url-update.component';
import { GenericLicenseUrlDeleteDialogComponent } from 'app/entities/generic-license-url/delete/generic-license-url-delete-dialog.component';

@NgModule({
  imports: [SharedModule, GenericLicenseUrlRoutingCustomModule],
  declarations: [
    GenericLicenseUrlComponent,
    GenericLicenseUrlDetailComponent,
    GenericLicenseUrlUpdateComponent,
    GenericLicenseUrlDeleteDialogComponent,
    GenericLicenseUrlCustomComponent,
    GenericLicenseUrlDetailCustomComponent,
    GenericLicenseUrlUpdateCustomComponent,
    GenericLicenseUrlDeleteDialogCustomComponent,
  ],
  entryComponents: [GenericLicenseUrlDeleteDialogCustomComponent],
})
export class GenericLicenseUrlCustomModule {}
