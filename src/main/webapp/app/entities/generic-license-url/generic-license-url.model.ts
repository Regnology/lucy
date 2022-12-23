export interface IGenericLicenseUrl {
  id?: number;
  url?: string;
}

export class GenericLicenseUrl implements IGenericLicenseUrl {
  constructor(public id?: number, public url?: string) {}
}

export function getGenericLicenseUrlIdentifier(genericLicenseUrl: IGenericLicenseUrl): number | undefined {
  return genericLicenseUrl.id;
}
