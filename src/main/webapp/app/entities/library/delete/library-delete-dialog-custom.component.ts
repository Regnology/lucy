import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ILibrary } from '../library.model';
import { LibraryDeleteDialogComponent } from 'app/entities/library/delete/library-delete-dialog.component';
import { LibraryCustomService } from 'app/entities/library/service/library-custom.service';

@Component({
  templateUrl: './library-delete-dialog-custom.component.html',
})
export class LibraryDeleteDialogCustomComponent extends LibraryDeleteDialogComponent {
  library?: ILibrary;

  constructor(protected libraryService: LibraryCustomService, public activeModal: NgbActiveModal) {
    super(libraryService, activeModal);
  }
}
