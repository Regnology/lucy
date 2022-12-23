import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { LicensePerLibraryService } from 'app/entities/license-per-library/service/license-per-library.service';

@Injectable({ providedIn: 'root' })
export class LicensePerLibraryCustomService extends LicensePerLibraryService {
  public resourceUrl = this.applicationConfigService.getEndpointFor('api/v1/license-per-libraries');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {
    super(http, applicationConfigService);
  }
}
