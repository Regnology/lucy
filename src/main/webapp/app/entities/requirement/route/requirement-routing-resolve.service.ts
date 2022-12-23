import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IRequirement, Requirement } from '../requirement.model';
import { RequirementService } from '../service/requirement.service';

@Injectable({ providedIn: 'root' })
export class RequirementRoutingResolveService implements Resolve<IRequirement> {
  constructor(protected service: RequirementService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IRequirement> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((requirement: HttpResponse<Requirement>) => {
          if (requirement.body) {
            return of(requirement.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Requirement());
  }
}
