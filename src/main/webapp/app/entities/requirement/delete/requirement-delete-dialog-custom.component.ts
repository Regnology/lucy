import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IRequirement } from '../requirement.model';
import { RequirementService } from '../service/requirement.service';
import { RequirementDeleteDialogComponent } from 'app/entities/requirement/delete/requirement-delete-dialog.component';

@Component({
  templateUrl: './requirement-delete-dialog-custom.component.html',
})
export class RequirementDeleteDialogCustomComponent extends RequirementDeleteDialogComponent {
  requirement?: IRequirement;

  constructor(protected requirementService: RequirementService, public activeModal: NgbActiveModal) {
    super(requirementService, activeModal);
  }
}
