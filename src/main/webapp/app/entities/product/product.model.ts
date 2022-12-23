import dayjs from 'dayjs/esm';
import { ILibraryPerProduct } from 'app/entities/library-per-product/library-per-product.model';
import { UploadState } from 'app/entities/enumerations/upload-state.model';

export interface IProduct {
  id?: number;
  name?: string;
  identifier?: string;
  version?: string;
  createdDate?: dayjs.Dayjs | null;
  lastUpdatedDate?: dayjs.Dayjs | null;
  targetUrl?: string | null;
  uploadState?: UploadState | null;
  disclaimer?: string | null;
  delivered?: boolean | null;
  deliveredDate?: dayjs.Dayjs | null;
  contact?: string | null;
  comment?: string | null;
  previousProductId?: number | null;
  uploadFilter?: string | null;
  libraries?: ILibraryPerProduct[] | null;
}

export class Product implements IProduct {
  constructor(
    public id?: number,
    public name?: string,
    public identifier?: string,
    public version?: string,
    public createdDate?: dayjs.Dayjs | null,
    public lastUpdatedDate?: dayjs.Dayjs | null,
    public targetUrl?: string | null,
    public uploadState?: UploadState | null,
    public disclaimer?: string | null,
    public delivered?: boolean | null,
    public deliveredDate?: dayjs.Dayjs | null,
    public contact?: string | null,
    public comment?: string | null,
    public previousProductId?: number | null,
    public uploadFilter?: string | null,
    public libraries?: ILibraryPerProduct[] | null
  ) {
    this.delivered = this.delivered ?? false;
  }
}

export function getProductIdentifier(product: IProduct): number | undefined {
  return product.id;
}
