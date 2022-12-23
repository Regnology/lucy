import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { GenericLicenseUrlCustomService } from '../service/generic-license-url-custom.service';
import { GenericLicenseUrlRoutingResolveService } from 'app/entities/generic-license-url/route/generic-license-url-routing-resolve.service';

@Injectable({ providedIn: 'root' })
export class GenericLicenseUrlRoutingResolveCustomService extends GenericLicenseUrlRoutingResolveService {
  constructor(protected service: GenericLicenseUrlCustomService, protected router: Router) {
    super(service, router);
  }
}
