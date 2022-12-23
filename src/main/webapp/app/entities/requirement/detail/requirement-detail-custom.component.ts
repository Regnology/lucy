import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { RequirementDetailComponent } from 'app/entities/requirement/detail/requirement-detail.component';

@Component({
  selector: 'jhi-requirement-detail-custom',
  templateUrl: './requirement-detail-custom.component.html',
})
export class RequirementDetailCustomComponent extends RequirementDetailComponent {
  constructor(protected activatedRoute: ActivatedRoute) {
    super(activatedRoute);
  }
}
