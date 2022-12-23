import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { LicenseRiskService } from 'app/entities/license-risk/service/license-risk.service';

@Injectable({ providedIn: 'root' })
export class LicenseRiskCustomService extends LicenseRiskService {
  public resourceUrl = this.applicationConfigService.getEndpointFor('api/v1/license-risks');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {
    super(http, applicationConfigService);
  }
}
