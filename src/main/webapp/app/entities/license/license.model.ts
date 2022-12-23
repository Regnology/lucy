import dayjs from 'dayjs/esm';
import { ILicenseConflict } from 'app/entities/license-conflict/license-conflict.model';
import { IUser } from 'app/entities/user/user.model';
import { ILicenseRisk } from 'app/entities/license-risk/license-risk.model';
import { IRequirement } from 'app/entities/requirement/requirement.model';
import { ILibrary } from 'app/entities/library/library.model';

export interface ILicense {
  id?: number;
  fullName?: string;
  shortIdentifier?: string;
  spdxIdentifier?: string | null;
  url?: string | null;
  genericLicenseText?: string | null;
  other?: string | null;
  reviewed?: boolean | null;
  lastReviewedDate?: dayjs.Dayjs | null;
  licenseConflicts?: ILicenseConflict[] | null;
  lastReviewedBy?: IUser | null;
  licenseRisk?: ILicenseRisk | null;
  requirements?: IRequirement[] | null;
  libraryPublishes?: ILibrary[] | null;
  libraryFiles?: ILibrary[] | null;
}

export class License implements ILicense {
  constructor(
    public id?: number,
    public fullName?: string,
    public shortIdentifier?: string,
    public spdxIdentifier?: string | null,
    public url?: string | null,
    public genericLicenseText?: string | null,
    public other?: string | null,
    public reviewed?: boolean | null,
    public lastReviewedDate?: dayjs.Dayjs | null,
    public licenseConflicts?: ILicenseConflict[] | null,
    public lastReviewedBy?: IUser | null,
    public licenseRisk?: ILicenseRisk | null,
    public requirements?: IRequirement[] | null,
    public libraryPublishes?: ILibrary[] | null,
    public libraryFiles?: ILibrary[] | null
  ) {
    this.reviewed = this.reviewed ?? false;
  }
}

export function getLicenseIdentifier(license: ILicense): number | undefined {
  return license.id;
}
