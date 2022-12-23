import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ILibraryPerProduct, getLibraryPerProductIdentifier } from '../library-per-product.model';

export type EntityResponseType = HttpResponse<ILibraryPerProduct>;
export type EntityArrayResponseType = HttpResponse<ILibraryPerProduct[]>;

@Injectable({ providedIn: 'root' })
export class LibraryPerProductService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/library-per-products');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(libraryPerProduct: ILibraryPerProduct): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(libraryPerProduct);
    return this.http
      .post<ILibraryPerProduct>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(libraryPerProduct: ILibraryPerProduct): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(libraryPerProduct);
    return this.http
      .put<ILibraryPerProduct>(`${this.resourceUrl}/${getLibraryPerProductIdentifier(libraryPerProduct) as number}`, copy, {
        observe: 'response',
      })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(libraryPerProduct: ILibraryPerProduct): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(libraryPerProduct);
    return this.http
      .patch<ILibraryPerProduct>(`${this.resourceUrl}/${getLibraryPerProductIdentifier(libraryPerProduct) as number}`, copy, {
        observe: 'response',
      })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<ILibraryPerProduct>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<ILibraryPerProduct[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addLibraryPerProductToCollectionIfMissing(
    libraryPerProductCollection: ILibraryPerProduct[],
    ...libraryPerProductsToCheck: (ILibraryPerProduct | null | undefined)[]
  ): ILibraryPerProduct[] {
    const libraryPerProducts: ILibraryPerProduct[] = libraryPerProductsToCheck.filter(isPresent);
    if (libraryPerProducts.length > 0) {
      const libraryPerProductCollectionIdentifiers = libraryPerProductCollection.map(
        libraryPerProductItem => getLibraryPerProductIdentifier(libraryPerProductItem)!
      );
      const libraryPerProductsToAdd = libraryPerProducts.filter(libraryPerProductItem => {
        const libraryPerProductIdentifier = getLibraryPerProductIdentifier(libraryPerProductItem);
        if (libraryPerProductIdentifier == null || libraryPerProductCollectionIdentifiers.includes(libraryPerProductIdentifier)) {
          return false;
        }
        libraryPerProductCollectionIdentifiers.push(libraryPerProductIdentifier);
        return true;
      });
      return [...libraryPerProductsToAdd, ...libraryPerProductCollection];
    }
    return libraryPerProductCollection;
  }

  protected convertDateFromClient(libraryPerProduct: ILibraryPerProduct): ILibraryPerProduct {
    return Object.assign({}, libraryPerProduct, {
      addedDate: libraryPerProduct.addedDate?.isValid() ? libraryPerProduct.addedDate.format(DATE_FORMAT) : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.addedDate = res.body.addedDate ? dayjs(res.body.addedDate) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((libraryPerProduct: ILibraryPerProduct) => {
        libraryPerProduct.addedDate = libraryPerProduct.addedDate ? dayjs(libraryPerProduct.addedDate) : undefined;
      });
    }
    return res;
  }
}
