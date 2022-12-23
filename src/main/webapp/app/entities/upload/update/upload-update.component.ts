import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { IUpload, Upload } from '../upload.model';
import { UploadService } from '../service/upload.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { EntityUploadChoice } from 'app/entities/enumerations/entity-upload-choice.model';

@Component({
  selector: 'jhi-upload-update',
  templateUrl: './upload-update.component.html',
})
export class UploadUpdateComponent implements OnInit {
  isSaving = false;
  entityUploadChoiceValues = Object.keys(EntityUploadChoice);

  editForm = this.fb.group({
    id: [],
    file: [null, [Validators.required]],
    fileContentType: [],
    entityToUpload: [],
    record: [],
    overwriteData: [],
    uploadedDate: [],
  });

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected uploadService: UploadService,
    protected activatedRoute: ActivatedRoute,
    protected fb: UntypedFormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ upload }) => {
      this.updateForm(upload);
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  setFileData(event: Event, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe({
      error: (err: FileLoadError) =>
        this.eventManager.broadcast(new EventWithContent<AlertError>('lucyApp.error', { message: err.message })),
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const upload = this.createFromForm();
    if (upload.id !== undefined) {
      this.subscribeToSaveResponse(this.uploadService.update(upload));
    } else {
      this.subscribeToSaveResponse(this.uploadService.create(upload));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IUpload>>): void {
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

  protected updateForm(upload: IUpload): void {
    this.editForm.patchValue({
      id: upload.id,
      file: upload.file,
      fileContentType: upload.fileContentType,
      entityToUpload: upload.entityToUpload,
      record: upload.record,
      overwriteData: upload.overwriteData,
      uploadedDate: upload.uploadedDate,
    });
  }

  protected createFromForm(): IUpload {
    return {
      ...new Upload(),
      id: this.editForm.get(['id'])!.value,
      fileContentType: this.editForm.get(['fileContentType'])!.value,
      file: this.editForm.get(['file'])!.value,
      entityToUpload: this.editForm.get(['entityToUpload'])!.value,
      record: this.editForm.get(['record'])!.value,
      overwriteData: this.editForm.get(['overwriteData'])!.value,
      uploadedDate: this.editForm.get(['uploadedDate'])!.value,
    };
  }
}
