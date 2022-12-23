import {IFile} from "app/core/file/file.model";

export interface IUpload {
  file: IFile;
  additionalLibraries: IFile;
}

export class Upload implements IUpload {
  constructor(public file: IFile, public additionalLibraries: IFile) {}
}
