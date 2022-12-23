import { NgModule } from '@angular/core';

import { SharedModule } from 'app/shared/shared.module';
import { LicenseDeleteDialogCustomComponent } from 'app/entities/license/delete/license-delete-dialog-custom.component';
import { LicenseUpdateCustomComponent } from 'app/entities/license/update/license-update-custom.component';
import { LicenseDetailCustomComponent } from 'app/entities/license/detail/license-detail-custom.component';
import { LicenseCustomComponent } from 'app/entities/license/list/license-custom.component';
import { LicenseRoutingCustomModule } from 'app/entities/license/route/license-routing-custom.module';
import { LicenseComponent } from 'app/entities/license/list/license.component';
import { LicenseDetailComponent } from 'app/entities/license/detail/license-detail.component';
import { LicenseUpdateComponent } from 'app/entities/license/update/license-update.component';
import { LicenseDeleteDialogComponent } from 'app/entities/license/delete/license-delete-dialog.component';

@NgModule({
  imports: [SharedModule, LicenseRoutingCustomModule],
  declarations: [
    LicenseComponent,
    LicenseDetailComponent,
    LicenseUpdateComponent,
    LicenseDeleteDialogComponent,
    LicenseCustomComponent,
    LicenseDetailCustomComponent,
    LicenseUpdateCustomComponent,
    LicenseDeleteDialogCustomComponent,
  ],
  entryComponents: [LicenseDeleteDialogCustomComponent],
})
export class LicenseCustomModule {}
