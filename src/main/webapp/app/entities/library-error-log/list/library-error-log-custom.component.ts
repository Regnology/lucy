import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ILibraryErrorLog } from '../library-error-log.model';
import { LibraryErrorLogComponent } from 'app/entities/library-error-log/list/library-error-log.component';
import { LibraryErrorLogCustomService } from 'app/entities/library-error-log/service/library-error-log-custom.service';
import { LibraryErrorLogDeleteDialogCustomComponent } from 'app/entities/library-error-log/delete/library-error-log-delete-dialog-custom.component';

@Component({
  selector: 'jhi-library-error-log-custom',
  templateUrl: './library-error-log-custom.component.html',
})
export class LibraryErrorLogCustomComponent extends LibraryErrorLogComponent {
  constructor(
    protected libraryErrorLogService: LibraryErrorLogCustomService,
    protected activatedRoute: ActivatedRoute,
    protected router: Router,
    protected modalService: NgbModal
  ) {
    super(libraryErrorLogService, activatedRoute, router, modalService);
  }

  delete(libraryErrorLog: ILibraryErrorLog): void {
    const modalRef = this.modalService.open(LibraryErrorLogDeleteDialogCustomComponent, {
      size: 'lg',
      backdrop: 'static',
    });
    modalRef.componentInstance.libraryErrorLog = libraryErrorLog;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadPage();
      }
    });
  }
}
