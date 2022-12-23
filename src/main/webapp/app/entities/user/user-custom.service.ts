import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { UserService } from 'app/entities/user/user.service';

@Injectable({ providedIn: 'root' })
export class UserCustomService extends UserService {
  public resourceUrl = this.applicationConfigService.getEndpointFor('api/v1/users');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {
    super(http, applicationConfigService);
  }
}
