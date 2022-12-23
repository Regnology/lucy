import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';

import { ILibraryErrorLog, LibraryErrorLog } from '../library-error-log.model';
import { LibraryErrorLogService } from '../service/library-error-log.service';
import { ILibrary } from 'app/entities/library/library.model';
import { LibraryService } from 'app/entities/library/service/library.service';
import { LogSeverity } from 'app/entities/enumerations/log-severity.model';
import { LogStatus } from 'app/entities/enumerations/log-status.model';

@Component({
  selector: 'jhi-library-error-log-update',
  templateUrl: './library-error-log-update.component.html',
})
export class LibraryErrorLogUpdateComponent implements OnInit {
  isSaving = false;
  logSeverityValues = Object.keys(LogSeverity);
  logStatusValues = Object.keys(LogStatus);

  librariesSharedCollection: ILibrary[] = [];

  editForm = this.fb.group({
    id: [],
    message: [null, [Validators.required, Validators.maxLength(1024)]],
    severity: [null, [Validators.required]],
    status: [null, [Validators.required]],
    timestamp: [null, [Validators.required]],
    library: [],
  });

  constructor(
    protected libraryErrorLogService: LibraryErrorLogService,
    protected libraryService: LibraryService,
    protected activatedRoute: ActivatedRoute,
    protected fb: UntypedFormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ libraryErrorLog }) => {
      if (libraryErrorLog.id === undefined) {
        const today = dayjs().startOf('day');
        libraryErrorLog.timestamp = today;
      }

      this.updateForm(libraryErrorLog);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const libraryErrorLog = this.createFromForm();
    if (libraryErrorLog.id !== undefined) {
      this.subscribeToSaveResponse(this.libraryErrorLogService.update(libraryErrorLog));
    } else {
      this.subscribeToSaveResponse(this.libraryErrorLogService.create(libraryErrorLog));
    }
  }

  trackLibraryById(index: number, item: ILibrary): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ILibraryErrorLog>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(libraryErrorLog: ILibraryErrorLog): void {
    this.editForm.patchValue({
      id: libraryErrorLog.id,
      message: libraryErrorLog.message,
      severity: libraryErrorLog.severity,
      status: libraryErrorLog.status,
      timestamp: libraryErrorLog.timestamp ? libraryErrorLog.timestamp.format(DATE_TIME_FORMAT) : null,
      library: libraryErrorLog.library,
    });

    this.librariesSharedCollection = this.libraryService.addLibraryToCollectionIfMissing(
      this.librariesSharedCollection,
      libraryErrorLog.library
    );
  }

  protected loadRelationshipsOptions(): void {
    this.libraryService
      .query()
      .pipe(map((res: HttpResponse<ILibrary[]>) => res.body ?? []))
      .pipe(
        map((libraries: ILibrary[]) => this.libraryService.addLibraryToCollectionIfMissing(libraries, this.editForm.get('library')!.value))
      )
      .subscribe((libraries: ILibrary[]) => (this.librariesSharedCollection = libraries));
  }

  protected createFromForm(): ILibraryErrorLog {
    return {
      ...new LibraryErrorLog(),
      id: this.editForm.get(['id'])!.value,
      message: this.editForm.get(['message'])!.value,
      severity: this.editForm.get(['severity'])!.value,
      status: this.editForm.get(['status'])!.value,
      timestamp: this.editForm.get(['timestamp'])!.value ? dayjs(this.editForm.get(['timestamp'])!.value, DATE_TIME_FORMAT) : undefined,
      library: this.editForm.get(['library'])!.value,
    };
  }
}
