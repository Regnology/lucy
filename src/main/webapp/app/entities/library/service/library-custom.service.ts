import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import dayjs from 'dayjs/esm';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ILibrary } from '../library.model';
import { ILicense } from 'app/entities/license/license.model';
import { ICopyright } from 'app/core/copyright/copyright.model';
import { ILibraryErrorLog } from 'app/entities/library-error-log/library-error-log.model';
import { LogStatus } from 'app/entities/enumerations/log-status.model';

import { IFile } from 'app/core/file/file.model';
import { EntityArrayResponseType, EntityResponseType, LibraryService } from 'app/entities/library/service/library.service';
import { IFossology } from '../../fossology/fossology.model';

@Injectable({ providedIn: 'root' })
export class LibraryCustomService extends LibraryService {
  public resourceUrl = this.applicationConfigService.getEndpointFor('api/v1/libraries');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {
    super(http, applicationConfigService);
  }

  export(req?: any): Observable<HttpResponse<IFile>> {
    const options = createRequestOption(req);
    return this.http.get<IFile>(`${this.resourceUrl}/export`, { params: options, observe: 'response' });
  }

  analyseCopyright(id: number): Observable<HttpResponse<ICopyright>> {
    return this.http.get<ICopyright>(`${this.resourceUrl}/${id}/analyse-copyright`, { observe: 'response' });
  }

  analyseWithFossology(id: number): Observable<HttpResponse<{}>> {
    return this.http.get(`${this.resourceUrl}/${id}/analyse-with-fossology`, { observe: 'response' });
  }

  fossologyAnalysis(id: number): Observable<HttpResponse<IFossology>> {
    return this.http.get<IFossology>(`${this.resourceUrl}/${id}/fossology-analysis`, { observe: 'response' });
  }

  hasErrors(library: ILibrary): boolean {
    for (const errorLog of library.errorLogs ?? []) {
      if (errorLog.status === LogStatus.OPEN) {
        return true;
      }
    }

    return false;
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.lastReviewedDate = res.body.lastReviewedDate ? dayjs(res.body.lastReviewedDate) : undefined;
      res.body.lastReviewedDeepScanDate = res.body.lastReviewedDeepScanDate ? dayjs(res.body.lastReviewedDeepScanDate) : undefined;
      res.body.createdDate = res.body.createdDate ? dayjs(res.body.createdDate) : undefined;
      res.body.errorLogs?.forEach((libraryErrorLog: ILibraryErrorLog) => {
        libraryErrorLog.timestamp = libraryErrorLog.timestamp ? dayjs(libraryErrorLog.timestamp) : undefined;
      });
      if (res.body.fossology) {
        res.body.fossology.lastScan = res.body.fossology.lastScan ? dayjs(res.body.fossology.lastScan) : undefined;
      }
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((library: ILibrary) => {
        library.lastReviewedDate = library.lastReviewedDate ? dayjs(library.lastReviewedDate) : undefined;
        library.lastReviewedDeepScanDate = library.lastReviewedDeepScanDate ? dayjs(library.lastReviewedDeepScanDate) : undefined;
        library.createdDate = library.createdDate ? dayjs(library.createdDate) : undefined;
        library.errorLogs?.forEach((libraryErrorLog: ILibraryErrorLog) => {
          libraryErrorLog.timestamp = libraryErrorLog.timestamp ? dayjs(libraryErrorLog.timestamp) : undefined;
        });
        if (library.fossology) {
          library.fossology.lastScan = library.fossology.lastScan ? dayjs(library.fossology.lastScan) : undefined;
        }
      });
    }
    return res;
  }
}
