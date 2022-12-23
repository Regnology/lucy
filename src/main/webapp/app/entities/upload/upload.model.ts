import dayjs from 'dayjs/esm';
import { EntityUploadChoice } from 'app/entities/enumerations/entity-upload-choice.model';

export interface IUpload {
  id?: number;
  fileContentType?: string;
  file?: string;
  entityToUpload?: EntityUploadChoice | null;
  record?: number | null;
  overwriteData?: boolean | null;
  uploadedDate?: dayjs.Dayjs | null;
}

export class Upload implements IUpload {
  constructor(
    public id?: number,
    public fileContentType?: string,
    public file?: string,
    public entityToUpload?: EntityUploadChoice | null,
    public record?: number | null,
    public overwriteData?: boolean | null,
    public uploadedDate?: dayjs.Dayjs | null
  ) {
    this.overwriteData = this.overwriteData ?? false;
  }
}

export function getUploadIdentifier(upload: IUpload): number | undefined {
  return upload.id;
}
