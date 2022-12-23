import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ILibrary } from '../library.model';
import { LibraryService } from '../service/library.service';

@Component({
  templateUrl: './library-delete-dialog.component.html',
})
export class LibraryDeleteDialogComponent {
  library?: ILibrary;

  constructor(protected libraryService: LibraryService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.libraryService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
