import dayjs from 'dayjs/esm';
import { ILibrary } from 'app/entities/library/library.model';
import { IProduct } from 'app/entities/product/product.model';

export interface ILibraryPerProduct {
  id?: number;
  addedDate?: dayjs.Dayjs | null;
  addedManually?: boolean | null;
  hideForPublishing?: boolean | null;
  comment?: string | null;
  library?: ILibrary | null;
  product?: IProduct | null;
}

export class LibraryPerProduct implements ILibraryPerProduct {
  constructor(
    public id?: number,
    public addedDate?: dayjs.Dayjs | null,
    public addedManually?: boolean | null,
    public hideForPublishing?: boolean | null,
    public comment?: string | null,
    public library?: ILibrary | null,
    public product?: IProduct | null
  ) {
    this.addedManually = this.addedManually ?? false;
    this.hideForPublishing = this.hideForPublishing ?? false;
  }
}

export function getLibraryPerProductIdentifier(libraryPerProduct: ILibraryPerProduct): number | undefined {
  return libraryPerProduct.id;
}
