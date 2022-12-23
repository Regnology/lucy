import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ILicenseNamingMapping, getLicenseNamingMappingIdentifier } from '../license-naming-mapping.model';

export type EntityResponseType = HttpResponse<ILicenseNamingMapping>;
export type EntityArrayResponseType = HttpResponse<ILicenseNamingMapping[]>;

@Injectable({ providedIn: 'root' })
export class LicenseNamingMappingService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/license-naming-mappings');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(licenseNamingMapping: ILicenseNamingMapping): Observable<EntityResponseType> {
    return this.http.post<ILicenseNamingMapping>(this.resourceUrl, licenseNamingMapping, { observe: 'response' });
  }

  update(licenseNamingMapping: ILicenseNamingMapping): Observable<EntityResponseType> {
    return this.http.put<ILicenseNamingMapping>(
      `${this.resourceUrl}/${getLicenseNamingMappingIdentifier(licenseNamingMapping) as number}`,
      licenseNamingMapping,
      { observe: 'response' }
    );
  }

  partialUpdate(licenseNamingMapping: ILicenseNamingMapping): Observable<EntityResponseType> {
    return this.http.patch<ILicenseNamingMapping>(
      `${this.resourceUrl}/${getLicenseNamingMappingIdentifier(licenseNamingMapping) as number}`,
      licenseNamingMapping,
      { observe: 'response' }
    );
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ILicenseNamingMapping>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ILicenseNamingMapping[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addLicenseNamingMappingToCollectionIfMissing(
    licenseNamingMappingCollection: ILicenseNamingMapping[],
    ...licenseNamingMappingsToCheck: (ILicenseNamingMapping | null | undefined)[]
  ): ILicenseNamingMapping[] {
    const licenseNamingMappings: ILicenseNamingMapping[] = licenseNamingMappingsToCheck.filter(isPresent);
    if (licenseNamingMappings.length > 0) {
      const licenseNamingMappingCollectionIdentifiers = licenseNamingMappingCollection.map(
        licenseNamingMappingItem => getLicenseNamingMappingIdentifier(licenseNamingMappingItem)!
      );
      const licenseNamingMappingsToAdd = licenseNamingMappings.filter(licenseNamingMappingItem => {
        const licenseNamingMappingIdentifier = getLicenseNamingMappingIdentifier(licenseNamingMappingItem);
        if (licenseNamingMappingIdentifier == null || licenseNamingMappingCollectionIdentifiers.includes(licenseNamingMappingIdentifier)) {
          return false;
        }
        licenseNamingMappingCollectionIdentifiers.push(licenseNamingMappingIdentifier);
        return true;
      });
      return [...licenseNamingMappingsToAdd, ...licenseNamingMappingCollection];
    }
    return licenseNamingMappingCollection;
  }
}
