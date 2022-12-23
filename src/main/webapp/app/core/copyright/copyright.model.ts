export interface ICopyright {
  fullCopyright: string[];
  simpleCopyright: string[];
}

export class Copyright implements ICopyright {
  constructor(public fullCopyright: string[], public simpleCopyright: string[]) {}
}
