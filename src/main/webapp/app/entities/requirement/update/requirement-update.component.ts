import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { IRequirement, Requirement } from '../requirement.model';
import { RequirementService } from '../service/requirement.service';

@Component({
  selector: 'jhi-requirement-update',
  templateUrl: './requirement-update.component.html',
})
export class RequirementUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    shortText: [null, [Validators.required]],
    description: [null, [Validators.maxLength(2048)]],
  });

  constructor(
    protected requirementService: RequirementService,
    protected activatedRoute: ActivatedRoute,
    protected fb: UntypedFormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ requirement }) => {
      this.updateForm(requirement);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const requirement = this.createFromForm();
    if (requirement.id !== undefined) {
      this.subscribeToSaveResponse(this.requirementService.update(requirement));
    } else {
      this.subscribeToSaveResponse(this.requirementService.create(requirement));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IRequirement>>): void {
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

  protected updateForm(requirement: IRequirement): void {
    this.editForm.patchValue({
      id: requirement.id,
      shortText: requirement.shortText,
      description: requirement.description,
    });
  }

  protected createFromForm(): IRequirement {
    return {
      ...new Requirement(),
      id: this.editForm.get(['id'])!.value,
      shortText: this.editForm.get(['shortText'])!.value,
      description: this.editForm.get(['description'])!.value,
    };
  }
}
