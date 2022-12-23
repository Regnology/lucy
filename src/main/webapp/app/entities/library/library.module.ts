import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { LibraryComponent } from './list/library.component';
import { LibraryDetailComponent } from './detail/library-detail.component';
import { LibraryUpdateComponent } from './update/library-update.component';
import { LibraryDeleteDialogComponent } from './delete/library-delete-dialog.component';
import { LibraryRoutingModule } from './route/library-routing.module';

@NgModule({
  imports: [SharedModule, LibraryRoutingModule],
  declarations: [LibraryComponent, LibraryDetailComponent, LibraryUpdateComponent, LibraryDeleteDialogComponent],
  entryComponents: [LibraryDeleteDialogComponent],
})
export class LibraryModule {}
