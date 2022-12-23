import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { ILibraryErrorLog } from '../library-error-log.model';
import { LibraryErrorLogService } from 'app/entities/library-error-log/service/library-error-log.service';

export type EntityResponseType = HttpResponse<ILibraryErrorLog>;
export type EntityArrayResponseType = HttpResponse<ILibraryErrorLog[]>;

@Injectable({ providedIn: 'root' })
export class LibraryErrorLogCustomService extends LibraryErrorLogService {
  public resourceUrl = this.applicationConfigService.getEndpointFor('api/v1/library-error-logs');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {
    super(http, applicationConfigService);
  }
}
