import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ILicenseNamingMapping, LicenseNamingMapping } from '../license-naming-mapping.model';
import { LicenseNamingMappingService } from '../service/license-naming-mapping.service';

@Injectable({ providedIn: 'root' })
export class LicenseNamingMappingRoutingResolveService implements Resolve<ILicenseNamingMapping> {
  constructor(protected service: LicenseNamingMappingService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ILicenseNamingMapping> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((licenseNamingMapping: HttpResponse<LicenseNamingMapping>) => {
          if (licenseNamingMapping.body) {
            return of(licenseNamingMapping.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new LicenseNamingMapping());
  }
}
