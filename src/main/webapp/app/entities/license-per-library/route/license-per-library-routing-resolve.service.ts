import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ILicensePerLibrary, LicensePerLibrary } from '../license-per-library.model';
import { LicensePerLibraryService } from '../service/license-per-library.service';

@Injectable({ providedIn: 'root' })
export class LicensePerLibraryRoutingResolveService implements Resolve<ILicensePerLibrary> {
  constructor(protected service: LicensePerLibraryService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ILicensePerLibrary> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((licensePerLibrary: HttpResponse<LicensePerLibrary>) => {
          if (licensePerLibrary.body) {
            return of(licensePerLibrary.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new LicensePerLibrary());
  }
}
