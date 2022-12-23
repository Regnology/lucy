import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IRequirement } from '../requirement.model';
import { RequirementService } from '../service/requirement.service';

@Component({
  templateUrl: './requirement-delete-dialog.component.html',
})
export class RequirementDeleteDialogComponent {
  requirement?: IRequirement;

  constructor(protected requirementService: RequirementService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.requirementService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
