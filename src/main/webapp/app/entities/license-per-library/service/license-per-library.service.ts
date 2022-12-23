import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ILicensePerLibrary, getLicensePerLibraryIdentifier } from '../license-per-library.model';

export type EntityResponseType = HttpResponse<ILicensePerLibrary>;
export type EntityArrayResponseType = HttpResponse<ILicensePerLibrary[]>;

@Injectable({ providedIn: 'root' })
export class LicensePerLibraryService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/license-per-libraries');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(licensePerLibrary: ILicensePerLibrary): Observable<EntityResponseType> {
    return this.http.post<ILicensePerLibrary>(this.resourceUrl, licensePerLibrary, { observe: 'response' });
  }

  update(licensePerLibrary: ILicensePerLibrary): Observable<EntityResponseType> {
    return this.http.put<ILicensePerLibrary>(
      `${this.resourceUrl}/${getLicensePerLibraryIdentifier(licensePerLibrary) as number}`,
      licensePerLibrary,
      { observe: 'response' }
    );
  }

  partialUpdate(licensePerLibrary: ILicensePerLibrary): Observable<EntityResponseType> {
    return this.http.patch<ILicensePerLibrary>(
      `${this.resourceUrl}/${getLicensePerLibraryIdentifier(licensePerLibrary) as number}`,
      licensePerLibrary,
      { observe: 'response' }
    );
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ILicensePerLibrary>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ILicensePerLibrary[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addLicensePerLibraryToCollectionIfMissing(
    licensePerLibraryCollection: ILicensePerLibrary[],
    ...licensePerLibrariesToCheck: (ILicensePerLibrary | null | undefined)[]
  ): ILicensePerLibrary[] {
    const licensePerLibraries: ILicensePerLibrary[] = licensePerLibrariesToCheck.filter(isPresent);
    if (licensePerLibraries.length > 0) {
      const licensePerLibraryCollectionIdentifiers = licensePerLibraryCollection.map(
        licensePerLibraryItem => getLicensePerLibraryIdentifier(licensePerLibraryItem)!
      );
      const licensePerLibrariesToAdd = licensePerLibraries.filter(licensePerLibraryItem => {
        const licensePerLibraryIdentifier = getLicensePerLibraryIdentifier(licensePerLibraryItem);
        if (licensePerLibraryIdentifier == null || licensePerLibraryCollectionIdentifiers.includes(licensePerLibraryIdentifier)) {
          return false;
        }
        licensePerLibraryCollectionIdentifiers.push(licensePerLibraryIdentifier);
        return true;
      });
      return [...licensePerLibrariesToAdd, ...licensePerLibraryCollection];
    }
    return licensePerLibraryCollection;
  }
}
