import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { RequirementService } from 'app/entities/requirement/service/requirement.service';

@Injectable({ providedIn: 'root' })
export class RequirementCustomService extends RequirementService {
  public resourceUrl = this.applicationConfigService.getEndpointFor('api/v1/requirements');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {
    super(http, applicationConfigService);
  }
}
