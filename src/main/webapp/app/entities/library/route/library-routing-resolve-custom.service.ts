import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ILibrary, Library } from '../library.model';
import { LibraryCustomService } from 'app/entities/library/service/library-custom.service';
import { LibraryRoutingResolveService } from 'app/entities/library/route/library-routing-resolve.service';

@Injectable({ providedIn: 'root' })
export class LibraryRoutingResolveCustomService extends LibraryRoutingResolveService {
  constructor(protected service: LibraryCustomService, protected router: Router) {
    super(service, router);
  }

  resolve(route: ActivatedRouteSnapshot): Observable<ILibrary> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((library: HttpResponse<Library>) => {
          if (library.body) {
            return of(library.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Library());
  }
}
