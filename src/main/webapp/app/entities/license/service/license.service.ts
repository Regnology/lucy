import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ILicense, getLicenseIdentifier } from '../license.model';

export type EntityResponseType = HttpResponse<ILicense>;
export type EntityArrayResponseType = HttpResponse<ILicense[]>;

@Injectable({ providedIn: 'root' })
export class LicenseService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/licenses');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(license: ILicense): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(license);
    return this.http
      .post<ILicense>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(license: ILicense): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(license);
    return this.http
      .put<ILicense>(`${this.resourceUrl}/${getLicenseIdentifier(license) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(license: ILicense): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(license);
    return this.http
      .patch<ILicense>(`${this.resourceUrl}/${getLicenseIdentifier(license) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<ILicense>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<ILicense[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addLicenseToCollectionIfMissing(licenseCollection: ILicense[], ...licensesToCheck: (ILicense | null | undefined)[]): ILicense[] {
    const licenses: ILicense[] = licensesToCheck.filter(isPresent);
    if (licenses.length > 0) {
      const licenseCollectionIdentifiers = licenseCollection.map(licenseItem => getLicenseIdentifier(licenseItem)!);
      const licensesToAdd = licenses.filter(licenseItem => {
        const licenseIdentifier = getLicenseIdentifier(licenseItem);
        if (licenseIdentifier == null || licenseCollectionIdentifiers.includes(licenseIdentifier)) {
          return false;
        }
        licenseCollectionIdentifiers.push(licenseIdentifier);
        return true;
      });
      return [...licensesToAdd, ...licenseCollection];
    }
    return licenseCollection;
  }

  protected convertDateFromClient(license: ILicense): ILicense {
    return Object.assign({}, license, {
      lastReviewedDate: license.lastReviewedDate?.isValid() ? license.lastReviewedDate.format(DATE_FORMAT) : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.lastReviewedDate = res.body.lastReviewedDate ? dayjs(res.body.lastReviewedDate) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((license: ILicense) => {
        license.lastReviewedDate = license.lastReviewedDate ? dayjs(license.lastReviewedDate) : undefined;
      });
    }
    return res;
  }
}
