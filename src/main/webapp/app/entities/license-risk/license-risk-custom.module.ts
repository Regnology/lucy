import { NgModule } from '@angular/core';

import { SharedModule } from 'app/shared/shared.module';
import { LicenseRiskDeleteDialogCustomComponent } from 'app/entities/license-risk/delete/license-risk-delete-dialog-custom.component';
import { LicenseRiskRoutingCustomModule } from 'app/entities/license-risk/route/license-risk-routing-custom.module';
import { LicenseRiskDetailCustomComponent } from 'app/entities/license-risk/detail/license-risk-detail-custom.component';
import { LicenseRiskUpdateCustomComponent } from 'app/entities/license-risk/update/license-risk-update-custom.component';
import { LicenseRiskCustomComponent } from 'app/entities/license-risk/list/license-risk-custom.component';
import { LicenseRiskComponent } from 'app/entities/license-risk/list/license-risk.component';
import { LicenseRiskDetailComponent } from 'app/entities/license-risk/detail/license-risk-detail.component';
import { LicenseRiskUpdateComponent } from 'app/entities/license-risk/update/license-risk-update.component';
import { LicenseRiskDeleteDialogComponent } from 'app/entities/license-risk/delete/license-risk-delete-dialog.component';

@NgModule({
  imports: [SharedModule, LicenseRiskRoutingCustomModule],
  declarations: [
    LicenseRiskComponent,
    LicenseRiskDetailComponent,
    LicenseRiskUpdateComponent,
    LicenseRiskDeleteDialogComponent,
    LicenseRiskCustomComponent,
    LicenseRiskDetailCustomComponent,
    LicenseRiskUpdateCustomComponent,
    LicenseRiskDeleteDialogCustomComponent,
  ],
  entryComponents: [LicenseRiskDeleteDialogCustomComponent],
})
export class LicenseRiskCustomModule {}
