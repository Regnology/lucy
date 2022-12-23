import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ILibraryErrorLog, getLibraryErrorLogIdentifier } from '../library-error-log.model';

export type EntityResponseType = HttpResponse<ILibraryErrorLog>;
export type EntityArrayResponseType = HttpResponse<ILibraryErrorLog[]>;

@Injectable({ providedIn: 'root' })
export class LibraryErrorLogService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/library-error-logs');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(libraryErrorLog: ILibraryErrorLog): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(libraryErrorLog);
    return this.http
      .post<ILibraryErrorLog>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(libraryErrorLog: ILibraryErrorLog): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(libraryErrorLog);
    return this.http
      .put<ILibraryErrorLog>(`${this.resourceUrl}/${getLibraryErrorLogIdentifier(libraryErrorLog) as number}`, copy, {
        observe: 'response',
      })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(libraryErrorLog: ILibraryErrorLog): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(libraryErrorLog);
    return this.http
      .patch<ILibraryErrorLog>(`${this.resourceUrl}/${getLibraryErrorLogIdentifier(libraryErrorLog) as number}`, copy, {
        observe: 'response',
      })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<ILibraryErrorLog>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<ILibraryErrorLog[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addLibraryErrorLogToCollectionIfMissing(
    libraryErrorLogCollection: ILibraryErrorLog[],
    ...libraryErrorLogsToCheck: (ILibraryErrorLog | null | undefined)[]
  ): ILibraryErrorLog[] {
    const libraryErrorLogs: ILibraryErrorLog[] = libraryErrorLogsToCheck.filter(isPresent);
    if (libraryErrorLogs.length > 0) {
      const libraryErrorLogCollectionIdentifiers = libraryErrorLogCollection.map(
        libraryErrorLogItem => getLibraryErrorLogIdentifier(libraryErrorLogItem)!
      );
      const libraryErrorLogsToAdd = libraryErrorLogs.filter(libraryErrorLogItem => {
        const libraryErrorLogIdentifier = getLibraryErrorLogIdentifier(libraryErrorLogItem);
        if (libraryErrorLogIdentifier == null || libraryErrorLogCollectionIdentifiers.includes(libraryErrorLogIdentifier)) {
          return false;
        }
        libraryErrorLogCollectionIdentifiers.push(libraryErrorLogIdentifier);
        return true;
      });
      return [...libraryErrorLogsToAdd, ...libraryErrorLogCollection];
    }
    return libraryErrorLogCollection;
  }

  protected convertDateFromClient(libraryErrorLog: ILibraryErrorLog): ILibraryErrorLog {
    return Object.assign({}, libraryErrorLog, {
      timestamp: libraryErrorLog.timestamp?.isValid() ? libraryErrorLog.timestamp.toJSON() : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.timestamp = res.body.timestamp ? dayjs(res.body.timestamp) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((libraryErrorLog: ILibraryErrorLog) => {
        libraryErrorLog.timestamp = libraryErrorLog.timestamp ? dayjs(libraryErrorLog.timestamp) : undefined;
      });
    }
    return res;
  }
}
