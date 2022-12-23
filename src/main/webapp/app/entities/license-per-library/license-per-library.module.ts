import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { LicensePerLibraryComponent } from './list/license-per-library.component';
import { LicensePerLibraryDetailComponent } from './detail/license-per-library-detail.component';
import { LicensePerLibraryUpdateComponent } from './update/license-per-library-update.component';
import { LicensePerLibraryDeleteDialogComponent } from './delete/license-per-library-delete-dialog.component';
import { LicensePerLibraryRoutingModule } from './route/license-per-library-routing.module';

@NgModule({
  imports: [SharedModule, LicensePerLibraryRoutingModule],
  declarations: [
    LicensePerLibraryComponent,
    LicensePerLibraryDetailComponent,
    LicensePerLibraryUpdateComponent,
    LicensePerLibraryDeleteDialogComponent,
  ],
  entryComponents: [LicensePerLibraryDeleteDialogComponent],
})
export class LicensePerLibraryModule {}
