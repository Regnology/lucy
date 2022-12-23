import { ILicense } from 'app/entities/license/license.model';
import { ILibrary } from 'app/entities/library/library.model';
import { LinkType } from 'app/entities/enumerations/link-type.model';

export interface ILicensePerLibrary {
  id?: number;
  orderId?: number;
  linkType?: LinkType | null;
  license?: ILicense | null;
  library?: ILibrary | null;
}

export class LicensePerLibrary implements ILicensePerLibrary {
  constructor(
    public id?: number,
    public orderId?: number,
    public linkType?: LinkType | null,
    public license?: ILicense | null,
    public library?: ILibrary | null
  ) {}
}

export function getLicensePerLibraryIdentifier(licensePerLibrary: ILicensePerLibrary): number | undefined {
  return licensePerLibrary.id;
}
