import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IUpload } from '../upload.model';
import { UploadDeleteDialogComponent } from 'app/entities/upload/delete/upload-delete-dialog.component';
import { UploadCustomService } from 'app/entities/upload/service/upload-custom.service';

@Component({
  templateUrl: './upload-delete-dialog-custom.component.html',
})
export class UploadDeleteDialogCustomComponent extends UploadDeleteDialogComponent {
  upload?: IUpload;

  constructor(protected uploadService: UploadCustomService, public activeModal: NgbActiveModal) {
    super(uploadService, activeModal);
  }
}
