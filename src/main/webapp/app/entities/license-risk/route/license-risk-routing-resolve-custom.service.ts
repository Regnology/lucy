import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { LicenseRiskRoutingResolveService } from 'app/entities/license-risk/route/license-risk-routing-resolve.service';
import { LicenseRiskCustomService } from 'app/entities/license-risk/service/license-risk-custom.service';

@Injectable({ providedIn: 'root' })
export class LicenseRiskRoutingResolveCustomService extends LicenseRiskRoutingResolveService {
  constructor(protected service: LicenseRiskCustomService, protected router: Router) {
    super(service, router);
  }
}
