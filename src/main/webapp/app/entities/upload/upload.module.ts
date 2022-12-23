import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { UploadComponent } from './list/upload.component';
import { UploadDetailComponent } from './detail/upload-detail.component';
import { UploadUpdateComponent } from './update/upload-update.component';
import { UploadDeleteDialogComponent } from './delete/upload-delete-dialog.component';
import { UploadRoutingModule } from './route/upload-routing.module';

@NgModule({
  imports: [SharedModule, UploadRoutingModule],
  declarations: [UploadComponent, UploadDetailComponent, UploadUpdateComponent, UploadDeleteDialogComponent],
  entryComponents: [UploadDeleteDialogComponent],
})
export class UploadModule {}
