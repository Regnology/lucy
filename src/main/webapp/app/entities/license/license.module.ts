import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { LicenseComponent } from './list/license.component';
import { LicenseDetailComponent } from './detail/license-detail.component';
import { LicenseUpdateComponent } from './update/license-update.component';
import { LicenseDeleteDialogComponent } from './delete/license-delete-dialog.component';
import { LicenseRoutingModule } from './route/license-routing.module';

@NgModule({
  imports: [SharedModule, LicenseRoutingModule],
  declarations: [LicenseComponent, LicenseDetailComponent, LicenseUpdateComponent, LicenseDeleteDialogComponent],
  entryComponents: [LicenseDeleteDialogComponent],
})
export class LicenseModule {}
