export interface ILicenseRisk {
  id?: number;
  name?: string;
  level?: number;
  description?: string | null;
  color?: string | null;
}

export class LicenseRisk implements ILicenseRisk {
  constructor(
    public id?: number,
    public name?: string,
    public level?: number,
    public description?: string | null,
    public color?: string | null
  ) {}
}

export function getLicenseRiskIdentifier(licenseRisk: ILicenseRisk): number | undefined {
  return licenseRisk.id;
}
