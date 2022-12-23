import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { LicenseNamingMappingComponent } from './list/license-naming-mapping.component';
import { LicenseNamingMappingDetailComponent } from './detail/license-naming-mapping-detail.component';
import { LicenseNamingMappingUpdateComponent } from './update/license-naming-mapping-update.component';
import { LicenseNamingMappingDeleteDialogComponent } from './delete/license-naming-mapping-delete-dialog.component';
import { LicenseNamingMappingRoutingModule } from './route/license-naming-mapping-routing.module';

@NgModule({
  imports: [SharedModule, LicenseNamingMappingRoutingModule],
  declarations: [
    LicenseNamingMappingComponent,
    LicenseNamingMappingDetailComponent,
    LicenseNamingMappingUpdateComponent,
    LicenseNamingMappingDeleteDialogComponent,
  ],
  entryComponents: [LicenseNamingMappingDeleteDialogComponent],
})
export class LicenseNamingMappingModule {}
