import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ILibrary, Library } from '../library.model';
import { LibraryService } from '../service/library.service';

@Injectable({ providedIn: 'root' })
export class LibraryRoutingResolveService implements Resolve<ILibrary> {
  constructor(protected service: LibraryService, protected router: Router) {}

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
