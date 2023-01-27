import dayjs from 'dayjs/esm';
import { ILicensePerLibrary } from 'app/entities/license-per-library/license-per-library.model';
import { ILibraryErrorLog } from 'app/entities/library-error-log/library-error-log.model';
import { IUser } from 'app/entities/user/user.model';
import { ILicense } from 'app/entities/license/license.model';
import { LibraryType } from 'app/entities/enumerations/library-type.model';
import { IFossology } from 'app/entities/fossology/fossology.model';
import { ILicenseRisk } from 'app/entities/license-risk/license-risk.model';
export interface ILibrary {
  id?: number;
  groupId?: string | null;
  artifactId?: string;
  version?: string;
  type?: LibraryType | null;
  originalLicense?: string | null;
  licenseUrl?: string | null;
  licenseText?: string | null;
  sourceCodeUrl?: string | null;
  pUrl?: string | null;
  copyright?: string | null;
  compliance?: string | null;
  complianceComment?: string | null;
  comment?: string | null;
  reviewed?: boolean | null;
  reviewedDeepScan?: boolean | null;
  lastReviewedDate?: dayjs.Dayjs | null;
  lastReviewedDeepScanDate?: dayjs.Dayjs | null;
  createdDate?: dayjs.Dayjs | null;
  hideForPublishing?: boolean | null;
  md5?: string | null;
  sha1?: string | null;
  fossology?: IFossology | null;
  licenses?: ILicensePerLibrary[] | null;
  errorLogs?: ILibraryErrorLog[] | null;
  lastReviewedBy?: IUser | null;
  lastReviewedDeepScanBy?: IUser | null;
  licenseToPublishes?: ILicense[] | null;
  licenseOfFiles?: ILicense[] | null;
  libraryRisk?: ILicenseRisk | null;
}

export class Library implements ILibrary {
  constructor(
    public id?: number,
    public groupId?: string | null,
    public artifactId?: string,
    public version?: string,
    public type?: LibraryType | null,
    public originalLicense?: string | null,
    public licenseUrl?: string | null,
    public licenseText?: string | null,
    public sourceCodeUrl?: string | null,
    public pUrl?: string | null,
    public copyright?: string | null,
    public compliance?: string | null,
    public complianceComment?: string | null,
    public comment?: string | null,
    public reviewed?: boolean | null,
    public reviewedDeepScan?: boolean | null,
    public lastReviewedDate?: dayjs.Dayjs | null,
    public lastReviewedDeepScanDate?: dayjs.Dayjs | null,
    public createdDate?: dayjs.Dayjs | null,
    public hideForPublishing?: boolean | null,
    public md5?: string | null,
    public sha1?: string | null,
    public fossology?: IFossology | null,
    public licenses?: ILicensePerLibrary[] | null,
    public errorLogs?: ILibraryErrorLog[] | null,
    public lastReviewedBy?: IUser | null,
    public lastReviewedDeepScanBy?: IUser | null,
    public licenseToPublishes?: ILicense[] | null,
    public licenseOfFiles?: ILicense[] | null,
    public libraryRisk?: ILicenseRisk | null
  ) {
    this.reviewed = this.reviewed ?? false;
    this.reviewedDeepScan = this.reviewedDeepScan ?? false;
    this.hideForPublishing = this.hideForPublishing ?? false;
  }
}

export function getLibraryIdentifier(library: ILibrary): number | undefined {
  return library.id;
}
