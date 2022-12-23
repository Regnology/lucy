import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IGenericLicenseUrl } from '../generic-license-url.model';
import { GenericLicenseUrlDetailComponent } from 'app/entities/generic-license-url/detail/generic-license-url-detail.component';

@Component({
  selector: 'jhi-generic-license-url-detail-custom',
  templateUrl: './generic-license-url-detail-custom.component.html',
})
export class GenericLicenseUrlDetailCustomComponent extends GenericLicenseUrlDetailComponent {
  genericLicenseUrl: IGenericLicenseUrl | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {
    super(activatedRoute);
  }
}
