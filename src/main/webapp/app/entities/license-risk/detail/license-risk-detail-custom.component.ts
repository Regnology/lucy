import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { LicenseRiskDetailComponent } from 'app/entities/license-risk/detail/license-risk-detail.component';

@Component({
  selector: 'jhi-license-risk-detail-custom',
  templateUrl: './license-risk-detail-custom.component.html',
})
export class LicenseRiskDetailCustomComponent extends LicenseRiskDetailComponent {
  constructor(protected activatedRoute: ActivatedRoute) {
    super(activatedRoute);
  }
}
