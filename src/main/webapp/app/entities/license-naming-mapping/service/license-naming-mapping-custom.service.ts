import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { ILicenseNamingMapping } from '../license-naming-mapping.model';
import { LicenseNamingMappingService } from 'app/entities/license-naming-mapping/service/license-naming-mapping.service';

export type EntityResponseType = HttpResponse<ILicenseNamingMapping>;
export type EntityArrayResponseType = HttpResponse<ILicenseNamingMapping[]>;

@Injectable({ providedIn: 'root' })
export class LicenseNamingMappingCustomService extends LicenseNamingMappingService {
  public resourceUrl = this.applicationConfigService.getEndpointFor('api/v1/license-naming-mappings');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {
    super(http, applicationConfigService);
  }
}
