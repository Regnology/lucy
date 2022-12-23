import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { EntityResponseType, LibraryPerProductService } from 'app/entities/library-per-product/service/library-per-product.service';
import { getLibraryPerProductIdentifier, ILibraryPerProduct } from '../library-per-product.model';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable({ providedIn: 'root' })
export class LibraryPerProductCustomService extends LibraryPerProductService {
  public resourceUrl = this.applicationConfigService.getEndpointFor('api/v1/library-per-products');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {
    super(http, applicationConfigService);
  }

  partialUpdate(libraryPerProduct: ILibraryPerProduct): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(libraryPerProduct);
    return this.http
      .patch<ILibraryPerProduct>(`${this.resourceUrl}/${getLibraryPerProductIdentifier(libraryPerProduct) as number}`, copy, {
        observe: 'response',
        headers: new HttpHeaders({ 'Content-Type': 'application/merge-patch+json' }),
      })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }
}
