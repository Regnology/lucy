import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ILibraryPerProduct, LibraryPerProduct } from '../library-per-product.model';
import { LibraryPerProductService } from '../service/library-per-product.service';

@Injectable({ providedIn: 'root' })
export class LibraryPerProductRoutingResolveService implements Resolve<ILibraryPerProduct> {
  constructor(protected service: LibraryPerProductService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ILibraryPerProduct> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((libraryPerProduct: HttpResponse<LibraryPerProduct>) => {
          if (libraryPerProduct.body) {
            return of(libraryPerProduct.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new LibraryPerProduct());
  }
}
