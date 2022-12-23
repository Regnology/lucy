import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IGenericLicenseUrl, GenericLicenseUrl } from '../generic-license-url.model';
import { GenericLicenseUrlService } from '../service/generic-license-url.service';

@Injectable({ providedIn: 'root' })
export class GenericLicenseUrlRoutingResolveService implements Resolve<IGenericLicenseUrl> {
  constructor(protected service: GenericLicenseUrlService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IGenericLicenseUrl> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((genericLicenseUrl: HttpResponse<GenericLicenseUrl>) => {
          if (genericLicenseUrl.body) {
            return of(genericLicenseUrl.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new GenericLicenseUrl());
  }
}
