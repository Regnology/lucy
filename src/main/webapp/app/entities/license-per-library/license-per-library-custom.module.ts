import { NgModule } from '@angular/core';

import { SharedModule } from 'app/shared/shared.module';
import { LicensePerLibraryCustomComponent } from 'app/entities/license-per-library/list/license-per-library-custom.component';
import { LicensePerLibraryUpdateCustomComponent } from 'app/entities/license-per-library/update/license-per-library-update-custom.component';
import { LicensePerLibraryDeleteDialogCustomComponent } from 'app/entities/license-per-library/delete/license-per-library-delete-dialog-custom.component';
import { LicensePerLibraryRoutingCustomModule } from 'app/entities/license-per-library/route/license-per-library-routing-custom.module';
import { LicensePerLibraryDetailCustomComponent } from 'app/entities/license-per-library/detail/license-per-library-detail-custom.component';
import { LicensePerLibraryComponent } from 'app/entities/license-per-library/list/license-per-library.component';
import { LicensePerLibraryDetailComponent } from 'app/entities/license-per-library/detail/license-per-library-detail.component';
import { LicensePerLibraryUpdateComponent } from 'app/entities/license-per-library/update/license-per-library-update.component';
import { LicensePerLibraryDeleteDialogComponent } from 'app/entities/license-per-library/delete/license-per-library-delete-dialog.component';

@NgModule({
  imports: [SharedModule, LicensePerLibraryRoutingCustomModule],
  declarations: [
    LicensePerLibraryComponent,
    LicensePerLibraryDetailComponent,
    LicensePerLibraryUpdateComponent,
    LicensePerLibraryDeleteDialogComponent,
    LicensePerLibraryCustomComponent,
    LicensePerLibraryDetailCustomComponent,
    LicensePerLibraryUpdateCustomComponent,
    LicensePerLibraryDeleteDialogCustomComponent,
  ],
  entryComponents: [LicensePerLibraryDeleteDialogCustomComponent],
})
export class LicensePerLibraryCustomModule {}
