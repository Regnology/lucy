import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ILicenseRisk, LicenseRisk } from '../license-risk.model';
import { LicenseRiskService } from '../service/license-risk.service';

@Injectable({ providedIn: 'root' })
export class LicenseRiskRoutingResolveService implements Resolve<ILicenseRisk> {
  constructor(protected service: LicenseRiskService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ILicenseRisk> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((licenseRisk: HttpResponse<LicenseRisk>) => {
          if (licenseRisk.body) {
            return of(licenseRisk.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new LicenseRisk());
  }
}
