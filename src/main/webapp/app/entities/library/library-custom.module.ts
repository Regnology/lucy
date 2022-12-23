import { NgModule } from '@angular/core';

import { SharedModule } from 'app/shared/shared.module';
import { LibraryUpdateCustomComponent } from 'app/entities/library/update/library-update-custom.component';
import { LibraryDeleteDialogCustomComponent } from 'app/entities/library/delete/library-delete-dialog-custom.component';
import { LibraryCustomComponent } from 'app/entities/library/list/library-custom.component';
import { LibraryDetailCustomComponent } from 'app/entities/library/detail/library-detail-custom.component';
import { LibraryRoutingCustomModule } from 'app/entities/library/route/library-routing-custom.module';
import { LibraryComponent } from 'app/entities/library/list/library.component';
import { LibraryDetailComponent } from 'app/entities/library/detail/library-detail.component';
import { LibraryUpdateComponent } from 'app/entities/library/update/library-update.component';
import { LibraryDeleteDialogComponent } from 'app/entities/library/delete/library-delete-dialog.component';

@NgModule({
  imports: [SharedModule, LibraryRoutingCustomModule],
  declarations: [
    LibraryComponent,
    LibraryDetailComponent,
    LibraryUpdateComponent,
    LibraryDeleteDialogComponent,
    LibraryCustomComponent,
    LibraryDetailCustomComponent,
    LibraryUpdateCustomComponent,
    LibraryDeleteDialogCustomComponent,
  ],
  entryComponents: [LibraryDeleteDialogCustomComponent],
})
export class LibraryCustomModule {}
