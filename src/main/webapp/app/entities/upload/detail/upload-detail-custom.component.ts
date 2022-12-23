import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IUpload } from '../upload.model';
import { DataUtils } from 'app/core/util/data-util.service';
import { UploadDetailComponent } from 'app/entities/upload/detail/upload-detail.component';

@Component({
  selector: 'jhi-upload-detail-custom',
  templateUrl: './upload-detail-custom.component.html',
})
export class UploadDetailCustomComponent extends UploadDetailComponent {
  upload: IUpload | null = null;

  constructor(protected dataUtils: DataUtils, protected activatedRoute: ActivatedRoute) {
    super(dataUtils, activatedRoute);
  }
}
