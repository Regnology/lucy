import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { GenericLicenseUrlService } from 'app/entities/generic-license-url/service/generic-license-url.service';

@Injectable({ providedIn: 'root' })
export class GenericLicenseUrlCustomService extends GenericLicenseUrlService {
  public resourceUrl = this.applicationConfigService.getEndpointFor('api/v1/generic-license-urls');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {
    super(http, applicationConfigService);
  }
}
