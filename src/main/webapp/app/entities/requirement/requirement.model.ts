import { ILicense } from 'app/entities/license/license.model';

export interface IRequirement {
  id?: number;
  shortText?: string;
  description?: string | null;
  licenses?: ILicense[] | null;
}

export class Requirement implements IRequirement {
  constructor(public id?: number, public shortText?: string, public description?: string | null, public licenses?: ILicense[] | null) {}
}

export function getRequirementIdentifier(requirement: IRequirement): number | undefined {
  return requirement.id;
}
