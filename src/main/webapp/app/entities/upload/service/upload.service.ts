import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IUpload, getUploadIdentifier } from '../upload.model';

export type EntityResponseType = HttpResponse<IUpload>;
export type EntityArrayResponseType = HttpResponse<IUpload[]>;

@Injectable({ providedIn: 'root' })
export class UploadService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/uploads');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(upload: IUpload): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(upload);
    return this.http
      .post<IUpload>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(upload: IUpload): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(upload);
    return this.http
      .put<IUpload>(`${this.resourceUrl}/${getUploadIdentifier(upload) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(upload: IUpload): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(upload);
    return this.http
      .patch<IUpload>(`${this.resourceUrl}/${getUploadIdentifier(upload) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IUpload>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IUpload[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addUploadToCollectionIfMissing(uploadCollection: IUpload[], ...uploadsToCheck: (IUpload | null | undefined)[]): IUpload[] {
    const uploads: IUpload[] = uploadsToCheck.filter(isPresent);
    if (uploads.length > 0) {
      const uploadCollectionIdentifiers = uploadCollection.map(uploadItem => getUploadIdentifier(uploadItem)!);
      const uploadsToAdd = uploads.filter(uploadItem => {
        const uploadIdentifier = getUploadIdentifier(uploadItem);
        if (uploadIdentifier == null || uploadCollectionIdentifiers.includes(uploadIdentifier)) {
          return false;
        }
        uploadCollectionIdentifiers.push(uploadIdentifier);
        return true;
      });
      return [...uploadsToAdd, ...uploadCollection];
    }
    return uploadCollection;
  }

  protected convertDateFromClient(upload: IUpload): IUpload {
    return Object.assign({}, upload, {
      uploadedDate: upload.uploadedDate?.isValid() ? upload.uploadedDate.format(DATE_FORMAT) : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.uploadedDate = res.body.uploadedDate ? dayjs(res.body.uploadedDate) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((upload: IUpload) => {
        upload.uploadedDate = upload.uploadedDate ? dayjs(upload.uploadedDate) : undefined;
      });
    }
    return res;
  }
}
