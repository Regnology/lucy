import dayjs from 'dayjs/esm';
import { FossologyStatus } from 'app/entities/enumerations/fossology-status.model';

export interface IFossology {
  id?: number;
  status?: FossologyStatus;
  uploadId?: string | null;
  jobId?: string | null;
  lastScan?: dayjs.Dayjs | null;
}

export class Fossology implements IFossology {
  constructor(
    public id?: number,
    public status?: FossologyStatus,
    public uploadId?: string | null,
    public jobId?: string | null,
    public lastScan?: dayjs.Dayjs | null
  ) {}
}

export function getFossologyIdentifier(fossology: IFossology): number | undefined {
  return fossology.id;
}
