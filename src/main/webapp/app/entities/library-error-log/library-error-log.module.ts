import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { LibraryErrorLogComponent } from './list/library-error-log.component';
import { LibraryErrorLogDetailComponent } from './detail/library-error-log-detail.component';
import { LibraryErrorLogUpdateComponent } from './update/library-error-log-update.component';
import { LibraryErrorLogDeleteDialogComponent } from './delete/library-error-log-delete-dialog.component';
import { LibraryErrorLogRoutingModule } from './route/library-error-log-routing.module';

@NgModule({
  imports: [SharedModule, LibraryErrorLogRoutingModule],
  declarations: [
    LibraryErrorLogComponent,
    LibraryErrorLogDetailComponent,
    LibraryErrorLogUpdateComponent,
    LibraryErrorLogDeleteDialogComponent,
  ],
  entryComponents: [LibraryErrorLogDeleteDialogComponent],
})
export class LibraryErrorLogModule {}
