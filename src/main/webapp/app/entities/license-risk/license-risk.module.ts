import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { LicenseRiskComponent } from './list/license-risk.component';
import { LicenseRiskDetailComponent } from './detail/license-risk-detail.component';
import { LicenseRiskUpdateComponent } from './update/license-risk-update.component';
import { LicenseRiskDeleteDialogComponent } from './delete/license-risk-delete-dialog.component';
import { LicenseRiskRoutingModule } from './route/license-risk-routing.module';

@NgModule({
  imports: [SharedModule, LicenseRiskRoutingModule],
  declarations: [LicenseRiskComponent, LicenseRiskDetailComponent, LicenseRiskUpdateComponent, LicenseRiskDeleteDialogComponent],
  entryComponents: [LicenseRiskDeleteDialogComponent],
})
export class LicenseRiskModule {}
