import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { UploadService } from 'app/entities/upload/service/upload.service';

@Injectable({ providedIn: 'root' })
export class UploadCustomService extends UploadService {
  public resourceUrl = this.applicationConfigService.getEndpointFor('api/v1/uploads');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {
    super(http, applicationConfigService);
  }
}
