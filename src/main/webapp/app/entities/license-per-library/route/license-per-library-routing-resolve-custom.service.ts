import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { LicensePerLibraryRoutingResolveService } from 'app/entities/license-per-library/route/license-per-library-routing-resolve.service';
import { LicensePerLibraryCustomService } from 'app/entities/license-per-library/service/license-per-library-custom.service';

@Injectable({ providedIn: 'root' })
export class LicensePerLibraryRoutingResolveCustomService extends LicensePerLibraryRoutingResolveService {
  constructor(protected service: LicensePerLibraryCustomService, protected router: Router) {
    super(service, router);
  }
}
