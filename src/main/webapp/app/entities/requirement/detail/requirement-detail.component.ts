import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IRequirement } from '../requirement.model';

@Component({
  selector: 'jhi-requirement-detail',
  templateUrl: './requirement-detail.component.html',
})
export class RequirementDetailComponent implements OnInit {
  requirement: IRequirement | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ requirement }) => {
      this.requirement = requirement;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
