import { Component } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RequirementUpdateComponent } from 'app/entities/requirement/update/requirement-update.component';
import { RequirementCustomService } from 'app/entities/requirement/service/requirement-custom.service';

@Component({
  selector: 'jhi-requirement-update-custom',
  templateUrl: './requirement-update-custom.component.html',
})
export class RequirementUpdateCustomComponent extends RequirementUpdateComponent {
  constructor(
    protected requirementService: RequirementCustomService,
    protected activatedRoute: ActivatedRoute,
    protected fb: UntypedFormBuilder
  ) {
    super(requirementService, activatedRoute, fb);
  }
}
