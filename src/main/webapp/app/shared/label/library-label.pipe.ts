import { Pipe, PipeTransform } from '@angular/core';
import { ILibrary } from '../../entities/library/library.model';

@Pipe({
  name: 'libraryLabel',
})
export class LibraryLabelPipe implements PipeTransform {
  transform(library: ILibrary): string {
    if (library.groupId) {
      return `${library.groupId}:${library.artifactId!}:${library.version!}`;
    } else {
      return `${library.artifactId!}:${library.version!}`;
    }
  }
}
