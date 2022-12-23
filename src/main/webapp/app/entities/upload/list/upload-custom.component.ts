import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IUpload } from '../upload.model';
import { DataUtils } from 'app/core/util/data-util.service';
import { UploadComponent } from 'app/entities/upload/list/upload.component';
import { UploadCustomService } from 'app/entities/upload/service/upload-custom.service';
import { UploadDeleteDialogCustomComponent } from 'app/entities/upload/delete/upload-delete-dialog-custom.component';

@Component({
  selector: 'jhi-upload-custom',
  templateUrl: './upload-custom.component.html',
})
export class UploadCustomComponent extends UploadComponent {
  constructor(
    protected uploadService: UploadCustomService,
    protected activatedRoute: ActivatedRoute,
    protected dataUtils: DataUtils,
    protected router: Router,
    protected modalService: NgbModal
  ) {
    super(uploadService, activatedRoute, dataUtils, router, modalService);
  }

  delete(upload: IUpload): void {
    const modalRef = this.modalService.open(UploadDeleteDialogCustomComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.upload = upload;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadPage();
      }
    });
  }
}
