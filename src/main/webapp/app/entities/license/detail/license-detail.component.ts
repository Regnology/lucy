import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ILicense } from '../license.model';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-license-detail',
  templateUrl: './license-detail.component.html',
})
export class LicenseDetailComponent implements OnInit {
  license: ILicense | null = null;

  constructor(protected dataUtils: DataUtils, protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ license }) => {
      this.license = license;
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
