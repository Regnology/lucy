import { ILicense } from 'app/entities/license/license.model';
import { CompatibilityState } from 'app/entities/enumerations/compatibility-state.model';

export interface ILicenseConflict {
  id?: number;
  compatibility?: CompatibilityState | null;
  comment?: string | null;
  firstLicenseConflict?: ILicense | null;
  secondLicenseConflict?: ILicense | null;
}

export class LicenseConflict implements ILicenseConflict {
  constructor(
    public id?: number,
    public compatibility?: CompatibilityState | null,
    public comment?: string | null,
    public firstLicenseConflict?: ILicense | null,
    public secondLicenseConflict?: ILicense | null
  ) {}
}

export function getLicenseConflictIdentifier(licenseConflict: ILicenseConflict): number | undefined {
  return licenseConflict.id;
}
