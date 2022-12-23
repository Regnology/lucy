import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ILibraryErrorLog } from '../library-error-log.model';
import { LibraryErrorLogDetailComponent } from 'app/entities/library-error-log/detail/library-error-log-detail.component';

@Component({
  selector: 'jhi-library-error-log-detail-custom',
  templateUrl: './library-error-log-detail-custom.component.html',
})
export class LibraryErrorLogDetailCustomComponent extends LibraryErrorLogDetailComponent {
  libraryErrorLog: ILibraryErrorLog | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {
    super(activatedRoute);
  }
}
