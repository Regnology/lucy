import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ILibraryErrorLog } from '../library-error-log.model';
import { LibraryErrorLogService } from '../service/library-error-log.service';

@Component({
  templateUrl: './library-error-log-delete-dialog.component.html',
})
export class LibraryErrorLogDeleteDialogComponent {
  libraryErrorLog?: ILibraryErrorLog;

  constructor(protected libraryErrorLogService: LibraryErrorLogService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.libraryErrorLogService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
