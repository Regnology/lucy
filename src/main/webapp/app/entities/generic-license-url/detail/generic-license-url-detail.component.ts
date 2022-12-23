import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IGenericLicenseUrl } from '../generic-license-url.model';

@Component({
  selector: 'jhi-generic-license-url-detail',
  templateUrl: './generic-license-url-detail.component.html',
})
export class GenericLicenseUrlDetailComponent implements OnInit {
  genericLicenseUrl: IGenericLicenseUrl | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ genericLicenseUrl }) => {
      this.genericLicenseUrl = genericLicenseUrl;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
