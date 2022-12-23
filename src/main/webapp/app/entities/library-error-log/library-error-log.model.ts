import dayjs from 'dayjs/esm';
import { ILibrary } from 'app/entities/library/library.model';
import { LogSeverity } from 'app/entities/enumerations/log-severity.model';
import { LogStatus } from 'app/entities/enumerations/log-status.model';

export interface ILibraryErrorLog {
  id?: number;
  message?: string;
  severity?: LogSeverity;
  status?: LogStatus;
  timestamp?: dayjs.Dayjs;
  library?: ILibrary | null;
}

export class LibraryErrorLog implements ILibraryErrorLog {
  constructor(
    public id?: number,
    public message?: string,
    public severity?: LogSeverity,
    public status?: LogStatus,
    public timestamp?: dayjs.Dayjs,
    public library?: ILibrary | null
  ) {}
}

export function getLibraryErrorLogIdentifier(libraryErrorLog: ILibraryErrorLog): number | undefined {
  return libraryErrorLog.id;
}
