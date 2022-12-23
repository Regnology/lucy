import { Component, Input } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';
import { HttpResponse } from '@angular/common/http';

import { ILibrary } from 'app/entities/library/library.model';
import { LibraryService } from 'app/entities/library/service/library.service';

@Component({
  selector: 'jhi-copyright-modal',
  templateUrl: './copyright-modal.component.html',
})
export class CopyrightModalComponent {
  @Input() library;
  @Input() fromParent;

  finalCopyright = '';
  isSaving = false;

  constructor(protected activeModal: NgbActiveModal, protected libraryService: LibraryService) {}

  closeModal(sendData: string): void {
    this.activeModal.close(sendData);
  }

  save(): void {
    this.isSaving = true;
    this.library.copyright = this.finalCopyright;
    this.subscribeToSaveResponse(this.libraryService.update(this.library));
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ILibrary>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    this.closeModal('saved successfully');
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }
}
