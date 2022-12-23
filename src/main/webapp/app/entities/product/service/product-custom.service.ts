import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IProduct } from '../product.model';

import { IFile } from 'app/core/file/file.model';
import { ICountOccurrences } from 'app/shared/statistics/count-occurrences.model';
import { IProductOverview } from 'app/shared/statistics/product-overview/product-overview.model';
import { ILibrary } from 'app/entities/library/library.model';
import { IUpload } from 'app/core/upload/upload.model';
import { EntityResponseType, ProductService } from 'app/entities/product/service/product.service';
import { IDifferenceView } from '../../../shared/modals/difference-view-modal/difference-view.model';
import { IProductStatistic } from '../../../shared/statistics/product-overview/product-statistic.model';
import { IBasicAuth } from 'app/shared/auth/basic-auth.model';

@Injectable({ providedIn: 'root' })
export class ProductCustomService extends ProductService {
  public resourceUrl = this.applicationConfigService.getEndpointFor('api/v1/products');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {
    super(http, applicationConfigService);
  }

  count(req?: any): Observable<HttpResponse<number>> {
    const options = createRequestOption(req);
    return this.http.get<number>(`${this.resourceUrl}/count`, { params: options, observe: 'response' });
  }

  oss(id: number, req?: any): Observable<HttpResponse<IFile>> {
    const options = createRequestOption(req);
    return this.http.get<IFile>(`${this.resourceUrl}/${id}/oss`, { params: options, observe: 'response' });
  }

  archive(id: number, req?: any): Observable<HttpResponse<IFile>> {
    const options = createRequestOption(req);
    return this.http.get<IFile>(`${this.resourceUrl}/${id}/create-archive`, { params: options, observe: 'response' });
  }

  zip(id: number): Observable<HttpResponse<IFile>> {
    return this.http.get<IFile>(`${this.resourceUrl}/${id}/zip`, { observe: 'response' });
  }

  transferToTarget(id: number): Observable<any> {
    return this.http.put<any>(`${this.resourceUrl}/${id}/transfer`, { observe: 'response' });
  }

  overview(id: number): Observable<HttpResponse<IProductOverview>> {
    return this.http.get<IProductOverview>(`${this.resourceUrl}/${id}/overview`, { observe: 'response' });
  }

  statistic(id: number): Observable<HttpResponse<IProductStatistic>> {
    return this.http.get<IProductStatistic>(`${this.resourceUrl}/${id}/statistic`, { observe: 'response' });
  }

  risk(id: number): Observable<HttpResponse<ICountOccurrences[]>> {
    return this.http.get<ICountOccurrences[]>(`${this.resourceUrl}/${id}/risk`, { observe: 'response' });
  }

  licenses(id: number): Observable<HttpResponse<ICountOccurrences[]>> {
    return this.http.get<ICountOccurrences[]>(`${this.resourceUrl}/${id}/licenses`, { observe: 'response' });
  }

  createNextVersion(id: number, req?: any): Observable<EntityResponseType> {
    const options = createRequestOption(req);
    return this.http
      .post<IProduct>(`${this.resourceUrl}/${id}/create-next-version`, null, { params: options, observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  upload(id: number, upload: IUpload, req?: any): Observable<HttpResponse<{}>> {
    const options = createRequestOption(req);
    return this.http.post<IUpload>(`${this.resourceUrl}/${id}/upload`, upload, { params: options, observe: 'response' });
  }

  uploadByUrl(id: number, credentials: IBasicAuth, req?: any): Observable<HttpResponse<{}>> {
    const options = createRequestOption(req);
    return this.http.post(`${this.resourceUrl}/${id}/upload-by-url`, credentials, { params: options, observe: 'response' });
  }

  addLibrary(id: number, libraries: ILibrary[]): Observable<HttpResponse<{}>> {
    return this.http.post<IProduct>(`${this.resourceUrl}/${id}/add-libraries`, libraries, { observe: 'response' });
  }

  compareProducts(req?: any): Observable<HttpResponse<IDifferenceView>> {
    const options = createRequestOption(req);
    return this.http.get<IDifferenceView>(`${this.resourceUrl}/compare`, { params: options, observe: 'response' });
  }
}
