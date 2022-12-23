import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { LicenseNamingMappingRoutingResolveService } from 'app/entities/license-naming-mapping/route/license-naming-mapping-routing-resolve.service';
import { LicenseNamingMappingCustomService } from 'app/entities/license-naming-mapping/service/license-naming-mapping-custom.service';

@Injectable({ providedIn: 'root' })
export class LicenseNamingMappingRoutingResolveCustomService extends LicenseNamingMappingRoutingResolveService {
  constructor(protected service: LicenseNamingMappingCustomService, protected router: Router) {
    super(service, router);
  }
}
