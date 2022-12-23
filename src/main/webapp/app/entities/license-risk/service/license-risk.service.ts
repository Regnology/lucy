import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ILicenseRisk, getLicenseRiskIdentifier } from '../license-risk.model';

export type EntityResponseType = HttpResponse<ILicenseRisk>;
export type EntityArrayResponseType = HttpResponse<ILicenseRisk[]>;

@Injectable({ providedIn: 'root' })
export class LicenseRiskService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/license-risks');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(licenseRisk: ILicenseRisk): Observable<EntityResponseType> {
    return this.http.post<ILicenseRisk>(this.resourceUrl, licenseRisk, { observe: 'response' });
  }

  update(licenseRisk: ILicenseRisk): Observable<EntityResponseType> {
    return this.http.put<ILicenseRisk>(`${this.resourceUrl}/${getLicenseRiskIdentifier(licenseRisk) as number}`, licenseRisk, {
      observe: 'response',
    });
  }

  partialUpdate(licenseRisk: ILicenseRisk): Observable<EntityResponseType> {
    return this.http.patch<ILicenseRisk>(`${this.resourceUrl}/${getLicenseRiskIdentifier(licenseRisk) as number}`, licenseRisk, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ILicenseRisk>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ILicenseRisk[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addLicenseRiskToCollectionIfMissing(
    licenseRiskCollection: ILicenseRisk[],
    ...licenseRisksToCheck: (ILicenseRisk | null | undefined)[]
  ): ILicenseRisk[] {
    const licenseRisks: ILicenseRisk[] = licenseRisksToCheck.filter(isPresent);
    if (licenseRisks.length > 0) {
      const licenseRiskCollectionIdentifiers = licenseRiskCollection.map(licenseRiskItem => getLicenseRiskIdentifier(licenseRiskItem)!);
      const licenseRisksToAdd = licenseRisks.filter(licenseRiskItem => {
        const licenseRiskIdentifier = getLicenseRiskIdentifier(licenseRiskItem);
        if (licenseRiskIdentifier == null || licenseRiskCollectionIdentifiers.includes(licenseRiskIdentifier)) {
          return false;
        }
        licenseRiskCollectionIdentifiers.push(licenseRiskIdentifier);
        return true;
      });
      return [...licenseRisksToAdd, ...licenseRiskCollection];
    }
    return licenseRiskCollection;
  }
}
