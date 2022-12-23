import { ILibrary } from '../../../entities/library/library.model';

export interface IDifferenceView {
  sameLibraries?: ILibrary[] | null;
  addedLibraries?: ILibrary[] | null;
  removedLibraries?: ILibrary[] | null;
  firstProductNewLibraries?: ILibrary[] | null;
  secondProductNewLibraries?: ILibrary[] | null;
}

export class DifferenceView implements IDifferenceView {
  constructor(
    sameLibraries?: ILibrary[] | null,
    addedLibraries?: ILibrary[] | null,
    removedLibraries?: ILibrary[] | null,
    firstProductLibraries?: ILibrary[] | null,
    secondProductLibraries?: ILibrary[] | null
  ) {}
}
