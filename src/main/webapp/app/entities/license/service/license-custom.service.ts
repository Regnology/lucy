import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';

import { IFile } from 'app/core/file/file.model';
import { LicenseService } from 'app/entities/license/service/license.service';
import { ILicenseConflict } from '../../license-conflict/license-conflict.model';
import { ILicense } from '../license.model';

@Injectable({ providedIn: 'root' })
export class LicenseCustomService extends LicenseService {
  public resourceUrl = this.applicationConfigService.getEndpointFor('api/v1/licenses');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {
    super(http, applicationConfigService);
  }

  count(req?: any): Observable<HttpResponse<number>> {
    const options = createRequestOption(req);
    return this.http.get<number>(`${this.resourceUrl}/count`, { params: options, observe: 'response' });
  }

  export(req?: any): Observable<HttpResponse<IFile>> {
    const options = createRequestOption(req);
    return this.http.get<IFile>(`${this.resourceUrl}/export`, { params: options, observe: 'response' });
  }

  fetchLicenseConflicts(id: number): Observable<HttpResponse<ILicenseConflict[]>> {
    return this.http.get<ILicenseConflict[]>(`${this.resourceUrl}/${id}/license-conflicts`, { observe: 'response' });
  }

  fetchLicenseConflictsWithRisk(id: number): Observable<HttpResponse<ILicenseConflict[]>> {
    return this.http.get<ILicenseConflict[]>(`${this.resourceUrl}/${id}/license-conflicts-with-risk`, { observe: 'response' });
  }

  allLicenseNames(): Observable<HttpResponse<ILicense[]>> {
    return this.http.get<ILicense[]>(`${this.resourceUrl}/license-names`, { observe: 'response' });
  }
}
