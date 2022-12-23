import { Component } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ParseLinks } from 'app/core/util/parse-links.service';
import { RequirementComponent } from 'app/entities/requirement/list/requirement.component';
import { RequirementCustomService } from 'app/entities/requirement/service/requirement-custom.service';

@Component({
  selector: 'jhi-requirement-custom',
  templateUrl: './requirement-custom.component.html',
})
export class RequirementCustomComponent extends RequirementComponent {
  constructor(protected requirementService: RequirementCustomService, protected modalService: NgbModal, protected parseLinks: ParseLinks) {
    super(requirementService, modalService, parseLinks);
  }
}
