export interface ILicenseNamingMapping {
  id?: number;
  regex?: string;
  uniformShortIdentifier?: string | null;
}

export class LicenseNamingMapping implements ILicenseNamingMapping {
  constructor(public id?: number, public regex?: string, public uniformShortIdentifier?: string | null) {}
}

export function getLicenseNamingMappingIdentifier(licenseNamingMapping: ILicenseNamingMapping): number | undefined {
  return licenseNamingMapping.id;
}
