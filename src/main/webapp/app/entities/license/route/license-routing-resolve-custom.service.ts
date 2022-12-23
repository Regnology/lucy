import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { LicenseRoutingResolveService } from 'app/entities/license/route/license-routing-resolve.service';
import { LicenseCustomService } from 'app/entities/license/service/license-custom.service';

@Injectable({ providedIn: 'root' })
export class LicenseRoutingResolveCustomService extends LicenseRoutingResolveService {
  constructor(protected service: LicenseCustomService, protected router: Router) {
    super(service, router);
  }
}
