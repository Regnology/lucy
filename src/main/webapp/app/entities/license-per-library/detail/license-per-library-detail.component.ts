import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ILicensePerLibrary } from '../license-per-library.model';

@Component({
  selector: 'jhi-license-per-library-detail',
  templateUrl: './license-per-library-detail.component.html',
})
export class LicensePerLibraryDetailComponent implements OnInit {
  licensePerLibrary: ILicensePerLibrary | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ licensePerLibrary }) => {
      this.licensePerLibrary = licensePerLibrary;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
