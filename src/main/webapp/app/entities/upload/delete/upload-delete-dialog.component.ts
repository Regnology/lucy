import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IUpload } from '../upload.model';
import { UploadService } from '../service/upload.service';

@Component({
  templateUrl: './upload-delete-dialog.component.html',
})
export class UploadDeleteDialogComponent {
  upload?: IUpload;

  constructor(protected uploadService: UploadService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.uploadService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
