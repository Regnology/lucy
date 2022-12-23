import { Component } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { LicenseRiskUpdateComponent } from 'app/entities/license-risk/update/license-risk-update.component';
import { LicenseRiskCustomService } from 'app/entities/license-risk/service/license-risk-custom.service';

@Component({
  selector: 'jhi-license-risk-update-custom',
  templateUrl: './license-risk-update-custom.component.html',
})
export class LicenseRiskUpdateCustomComponent extends LicenseRiskUpdateComponent {
  constructor(
    protected licenseRiskService: LicenseRiskCustomService,
    protected activatedRoute: ActivatedRoute,
    protected fb: UntypedFormBuilder
  ) {
    super(licenseRiskService, activatedRoute, fb);
  }
}
