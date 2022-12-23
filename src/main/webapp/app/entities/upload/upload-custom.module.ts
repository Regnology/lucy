import { NgModule } from '@angular/core';

import { SharedModule } from 'app/shared/shared.module';
import { UploadDeleteDialogCustomComponent } from 'app/entities/upload/delete/upload-delete-dialog-custom.component';
import { UploadUpdateCustomComponent } from 'app/entities/upload/update/upload-update-custom.component';
import { UploadDetailCustomComponent } from 'app/entities/upload/detail/upload-detail-custom.component';
import { UploadCustomComponent } from 'app/entities/upload/list/upload-custom.component';
import { UploadRoutingCustomModule } from 'app/entities/upload/route/upload-routing-custom.module';
import { UploadComponent } from 'app/entities/upload/list/upload.component';
import { UploadDetailComponent } from 'app/entities/upload/detail/upload-detail.component';
import { UploadUpdateComponent } from 'app/entities/upload/update/upload-update.component';
import { UploadDeleteDialogComponent } from 'app/entities/upload/delete/upload-delete-dialog.component';

@NgModule({
  imports: [SharedModule, UploadRoutingCustomModule],
  declarations: [
    UploadComponent,
    UploadDetailComponent,
    UploadUpdateComponent,
    UploadDeleteDialogComponent,
    UploadCustomComponent,
    UploadDetailCustomComponent,
    UploadUpdateCustomComponent,
    UploadDeleteDialogCustomComponent,
  ],
  entryComponents: [UploadDeleteDialogCustomComponent],
})
export class UploadCustomModule {}
