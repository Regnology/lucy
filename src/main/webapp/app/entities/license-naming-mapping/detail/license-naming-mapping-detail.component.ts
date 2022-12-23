import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ILicenseNamingMapping } from '../license-naming-mapping.model';

@Component({
  selector: 'jhi-license-naming-mapping-detail',
  templateUrl: './license-naming-mapping-detail.component.html',
})
export class LicenseNamingMappingDetailComponent implements OnInit {
  licenseNamingMapping: ILicenseNamingMapping | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ licenseNamingMapping }) => {
      this.licenseNamingMapping = licenseNamingMapping;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
