import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ILibraryErrorLog } from '../library-error-log.model';
import { LibraryErrorLogDeleteDialogComponent } from 'app/entities/library-error-log/delete/library-error-log-delete-dialog.component';
import { LibraryErrorLogCustomService } from 'app/entities/library-error-log/service/library-error-log-custom.service';

@Component({
  templateUrl: './library-error-log-delete-dialog-custom.component.html',
})
export class LibraryErrorLogDeleteDialogCustomComponent extends LibraryErrorLogDeleteDialogComponent {
  libraryErrorLog?: ILibraryErrorLog;

  constructor(protected libraryErrorLogService: LibraryErrorLogCustomService, public activeModal: NgbActiveModal) {
    super(libraryErrorLogService, activeModal);
  }
}
