import { Component } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { GenericLicenseUrlCustomService } from '../service/generic-license-url-custom.service';
import { GenericLicenseUrlUpdateComponent } from 'app/entities/generic-license-url/update/generic-license-url-update.component';

@Component({
  selector: 'jhi-generic-license-url-update-custom',
  templateUrl: './generic-license-url-update-custom.component.html',
})
export class GenericLicenseUrlUpdateCustomComponent extends GenericLicenseUrlUpdateComponent {
  constructor(
    protected genericLicenseUrlService: GenericLicenseUrlCustomService,
    protected activatedRoute: ActivatedRoute,
    protected fb: UntypedFormBuilder
  ) {
    super(genericLicenseUrlService, activatedRoute, fb);
  }
}
