import { NgModule } from '@angular/core';

import { SharedModule } from 'app/shared/shared.module';
import { LibraryErrorLogDeleteDialogCustomComponent } from 'app/entities/library-error-log/delete/library-error-log-delete-dialog-custom.component';
import { LibraryErrorLogRoutingCustomModule } from 'app/entities/library-error-log/route/library-error-log-routing-custom.module';
import { LibraryErrorLogUpdateCustomComponent } from 'app/entities/library-error-log/update/library-error-log-update-custom.component';
import { LibraryErrorLogDetailCustomComponent } from 'app/entities/library-error-log/detail/library-error-log-detail-custom.component';
import { LibraryErrorLogCustomComponent } from 'app/entities/library-error-log/list/library-error-log-custom.component';
import { LibraryErrorLogComponent } from 'app/entities/library-error-log/list/library-error-log.component';
import { LibraryErrorLogDetailComponent } from 'app/entities/library-error-log/detail/library-error-log-detail.component';
import { LibraryErrorLogUpdateComponent } from 'app/entities/library-error-log/update/library-error-log-update.component';
import { LibraryErrorLogDeleteDialogComponent } from 'app/entities/library-error-log/delete/library-error-log-delete-dialog.component';

@NgModule({
  imports: [SharedModule, LibraryErrorLogRoutingCustomModule],
  declarations: [
    LibraryErrorLogComponent,
    LibraryErrorLogDetailComponent,
    LibraryErrorLogUpdateComponent,
    LibraryErrorLogDeleteDialogComponent,
    LibraryErrorLogCustomComponent,
    LibraryErrorLogDetailCustomComponent,
    LibraryErrorLogUpdateCustomComponent,
    LibraryErrorLogDeleteDialogCustomComponent,
  ],
  entryComponents: [LibraryErrorLogDeleteDialogCustomComponent],
})
export class LibraryErrorLogCustomModule {}
