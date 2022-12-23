import { NgModule } from '@angular/core';

import { SharedModule } from 'app/shared/shared.module';
import { LicenseNamingMappingDeleteDialogCustomComponent } from 'app/entities/license-naming-mapping/delete/license-naming-mapping-delete-dialog-custom.component';
import { LicenseNamingMappingUpdateCustomComponent } from 'app/entities/license-naming-mapping/update/license-naming-mapping-update-custom.component';
import { LicenseNamingMappingRoutingCustomModule } from 'app/entities/license-naming-mapping/route/license-naming-mapping-routing-custom.module';
import { LicenseNamingMappingCustomComponent } from 'app/entities/license-naming-mapping/list/license-naming-mapping-custom.component';
import { LicenseNamingMappingDetailCustomComponent } from 'app/entities/license-naming-mapping/detail/license-naming-mapping-detail-custom.component';
import { LicenseNamingMappingComponent } from 'app/entities/license-naming-mapping/list/license-naming-mapping.component';
import { LicenseNamingMappingDetailComponent } from 'app/entities/license-naming-mapping/detail/license-naming-mapping-detail.component';
import { LicenseNamingMappingUpdateComponent } from 'app/entities/license-naming-mapping/update/license-naming-mapping-update.component';
import { LicenseNamingMappingDeleteDialogComponent } from 'app/entities/license-naming-mapping/delete/license-naming-mapping-delete-dialog.component';

@NgModule({
  imports: [SharedModule, LicenseNamingMappingRoutingCustomModule],
  declarations: [
    LicenseNamingMappingComponent,
    LicenseNamingMappingDetailComponent,
    LicenseNamingMappingUpdateComponent,
    LicenseNamingMappingDeleteDialogComponent,
    LicenseNamingMappingCustomComponent,
    LicenseNamingMappingDetailCustomComponent,
    LicenseNamingMappingUpdateCustomComponent,
    LicenseNamingMappingDeleteDialogCustomComponent,
  ],
  entryComponents: [LicenseNamingMappingDeleteDialogCustomComponent],
})
export class LicenseNamingMappingCustomModule {}
