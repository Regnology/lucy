import { HttpClient, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { IFossologyConfig } from '../config/fossology-config.model';

@Injectable({ providedIn: 'root' })
export class FossologyService {
  private resourceUrl = this.applicationConfigService.getEndpointFor('api/v1/fossology');

  constructor(protected http: HttpClient, private applicationConfigService: ApplicationConfigService) {}

  config(): Observable<HttpResponse<IFossologyConfig>> {
    return this.http.get<IFossologyConfig>(`${this.resourceUrl}/config`, { observe: 'response' });
  }
}
