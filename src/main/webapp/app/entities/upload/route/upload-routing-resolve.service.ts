import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IUpload, Upload } from '../upload.model';
import { UploadService } from '../service/upload.service';

@Injectable({ providedIn: 'root' })
export class UploadRoutingResolveService implements Resolve<IUpload> {
  constructor(protected service: UploadService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IUpload> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((upload: HttpResponse<Upload>) => {
          if (upload.body) {
            return of(upload.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Upload());
  }
}
