import { Component } from '@angular/core';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { ILibrary } from 'app/entities/library/library.model';
import { LibraryErrorLogUpdateComponent } from 'app/entities/library-error-log/update/library-error-log-update.component';
import { LibraryCustomService } from 'app/entities/library/service/library-custom.service';
import { LibraryErrorLogCustomService } from 'app/entities/library-error-log/service/library-error-log-custom.service';

@Component({
  selector: 'jhi-library-error-log-update-custom',
  templateUrl: './library-error-log-update-custom.component.html',
})
export class LibraryErrorLogUpdateCustomComponent extends LibraryErrorLogUpdateComponent {
  isSaving = false;

  librariesSharedCollection: ILibrary[] = [];

  editForm = this.fb.group({
    id: [],
    message: [null, [Validators.required]],
    severity: [null, [Validators.required]],
    status: [null, [Validators.required]],
    timestamp: [null, [Validators.required]],
    library: [],
  });

  constructor(
    protected libraryErrorLogService: LibraryErrorLogCustomService,
    protected libraryService: LibraryCustomService,
    protected activatedRoute: ActivatedRoute,
    protected fb: UntypedFormBuilder
  ) {
    super(libraryErrorLogService, libraryService, activatedRoute, fb);
  }
}
