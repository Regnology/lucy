import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IUpload } from '../upload.model';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-upload-detail',
  templateUrl: './upload-detail.component.html',
})
export class UploadDetailComponent implements OnInit {
  upload: IUpload | null = null;

  constructor(protected dataUtils: DataUtils, protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ upload }) => {
      this.upload = upload;
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  previousState(): void {
    window.history.back();
  }
}
