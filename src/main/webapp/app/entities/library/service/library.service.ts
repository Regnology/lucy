import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ILibrary, getLibraryIdentifier } from '../library.model';

export type EntityResponseType = HttpResponse<ILibrary>;
export type EntityArrayResponseType = HttpResponse<ILibrary[]>;

@Injectable({ providedIn: 'root' })
export class LibraryService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/libraries');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(library: ILibrary): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(library);
    return this.http
      .post<ILibrary>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(library: ILibrary): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(library);
    return this.http
      .put<ILibrary>(`${this.resourceUrl}/${getLibraryIdentifier(library) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(library: ILibrary): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(library);
    return this.http
      .patch<ILibrary>(`${this.resourceUrl}/${getLibraryIdentifier(library) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<ILibrary>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<ILibrary[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addLibraryToCollectionIfMissing(libraryCollection: ILibrary[], ...librariesToCheck: (ILibrary | null | undefined)[]): ILibrary[] {
    const libraries: ILibrary[] = librariesToCheck.filter(isPresent);
    if (libraries.length > 0) {
      const libraryCollectionIdentifiers = libraryCollection.map(libraryItem => getLibraryIdentifier(libraryItem)!);
      const librariesToAdd = libraries.filter(libraryItem => {
        const libraryIdentifier = getLibraryIdentifier(libraryItem);
        if (libraryIdentifier == null || libraryCollectionIdentifiers.includes(libraryIdentifier)) {
          return false;
        }
        libraryCollectionIdentifiers.push(libraryIdentifier);
        return true;
      });
      return [...librariesToAdd, ...libraryCollection];
    }
    return libraryCollection;
  }

  protected convertDateFromClient(library: ILibrary): ILibrary {
    return Object.assign({}, library, {
      lastReviewedDate: library.lastReviewedDate?.isValid() ? library.lastReviewedDate.format(DATE_FORMAT) : undefined,
      lastReviewedDeepScanDate: library.lastReviewedDeepScanDate?.isValid()
        ? library.lastReviewedDeepScanDate.format(DATE_FORMAT)
        : undefined,
      createdDate: library.createdDate?.isValid() ? library.createdDate.format(DATE_FORMAT) : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.lastReviewedDate = res.body.lastReviewedDate ? dayjs(res.body.lastReviewedDate) : undefined;
      res.body.lastReviewedDeepScanDate = res.body.lastReviewedDeepScanDate ? dayjs(res.body.lastReviewedDeepScanDate) : undefined;
      res.body.createdDate = res.body.createdDate ? dayjs(res.body.createdDate) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((library: ILibrary) => {
        library.lastReviewedDate = library.lastReviewedDate ? dayjs(library.lastReviewedDate) : undefined;
        library.lastReviewedDeepScanDate = library.lastReviewedDeepScanDate ? dayjs(library.lastReviewedDeepScanDate) : undefined;
        library.createdDate = library.createdDate ? dayjs(library.createdDate) : undefined;
      });
    }
    return res;
  }
}
