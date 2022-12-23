import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ILicense, License } from '../license.model';
import { LicenseService } from '../service/license.service';

@Injectable({ providedIn: 'root' })
export class LicenseRoutingResolveService implements Resolve<ILicense> {
  constructor(protected service: LicenseService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ILicense> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((license: HttpResponse<License>) => {
          if (license.body) {
            return of(license.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new License());
  }
}
