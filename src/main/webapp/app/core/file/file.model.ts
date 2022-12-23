export interface IFile {
  fileName: string;
  fileContentType: string;
  file: any;
}

export class File implements IFile {
  constructor(public fileName: string, public fileContentType: string, public file: any) {}
}
