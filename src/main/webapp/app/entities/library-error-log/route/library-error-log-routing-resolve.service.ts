import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ILibraryErrorLog, LibraryErrorLog } from '../library-error-log.model';
import { LibraryErrorLogService } from '../service/library-error-log.service';

@Injectable({ providedIn: 'root' })
export class LibraryErrorLogRoutingResolveService implements Resolve<ILibraryErrorLog> {
  constructor(protected service: LibraryErrorLogService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ILibraryErrorLog> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((libraryErrorLog: HttpResponse<LibraryErrorLog>) => {
          if (libraryErrorLog.body) {
            return of(libraryErrorLog.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new LibraryErrorLog());
  }
}
