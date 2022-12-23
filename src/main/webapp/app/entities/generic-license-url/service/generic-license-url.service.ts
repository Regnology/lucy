import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IGenericLicenseUrl, getGenericLicenseUrlIdentifier } from '../generic-license-url.model';

export type EntityResponseType = HttpResponse<IGenericLicenseUrl>;
export type EntityArrayResponseType = HttpResponse<IGenericLicenseUrl[]>;

@Injectable({ providedIn: 'root' })
export class GenericLicenseUrlService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/generic-license-urls');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(genericLicenseUrl: IGenericLicenseUrl): Observable<EntityResponseType> {
    return this.http.post<IGenericLicenseUrl>(this.resourceUrl, genericLicenseUrl, { observe: 'response' });
  }

  update(genericLicenseUrl: IGenericLicenseUrl): Observable<EntityResponseType> {
    return this.http.put<IGenericLicenseUrl>(
      `${this.resourceUrl}/${getGenericLicenseUrlIdentifier(genericLicenseUrl) as number}`,
      genericLicenseUrl,
      { observe: 'response' }
    );
  }

  partialUpdate(genericLicenseUrl: IGenericLicenseUrl): Observable<EntityResponseType> {
    return this.http.patch<IGenericLicenseUrl>(
      `${this.resourceUrl}/${getGenericLicenseUrlIdentifier(genericLicenseUrl) as number}`,
      genericLicenseUrl,
      { observe: 'response' }
    );
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IGenericLicenseUrl>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IGenericLicenseUrl[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addGenericLicenseUrlToCollectionIfMissing(
    genericLicenseUrlCollection: IGenericLicenseUrl[],
    ...genericLicenseUrlsToCheck: (IGenericLicenseUrl | null | undefined)[]
  ): IGenericLicenseUrl[] {
    const genericLicenseUrls: IGenericLicenseUrl[] = genericLicenseUrlsToCheck.filter(isPresent);
    if (genericLicenseUrls.length > 0) {
      const genericLicenseUrlCollectionIdentifiers = genericLicenseUrlCollection.map(
        genericLicenseUrlItem => getGenericLicenseUrlIdentifier(genericLicenseUrlItem)!
      );
      const genericLicenseUrlsToAdd = genericLicenseUrls.filter(genericLicenseUrlItem => {
        const genericLicenseUrlIdentifier = getGenericLicenseUrlIdentifier(genericLicenseUrlItem);
        if (genericLicenseUrlIdentifier == null || genericLicenseUrlCollectionIdentifiers.includes(genericLicenseUrlIdentifier)) {
          return false;
        }
        genericLicenseUrlCollectionIdentifiers.push(genericLicenseUrlIdentifier);
        return true;
      });
      return [...genericLicenseUrlsToAdd, ...genericLicenseUrlCollection];
    }
    return genericLicenseUrlCollection;
  }
}
