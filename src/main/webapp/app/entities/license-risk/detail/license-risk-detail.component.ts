import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ILicenseRisk } from '../license-risk.model';

@Component({
  selector: 'jhi-license-risk-detail',
  templateUrl: './license-risk-detail.component.html',
})
export class LicenseRiskDetailComponent implements OnInit {
  licenseRisk: ILicenseRisk | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ licenseRisk }) => {
      this.licenseRisk = licenseRisk;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
