import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { RequirementCustomService } from 'app/entities/requirement/service/requirement-custom.service';
import { RequirementRoutingResolveService } from 'app/entities/requirement/route/requirement-routing-resolve.service';

@Injectable({ providedIn: 'root' })
export class RequirementRoutingResolveCustomService extends RequirementRoutingResolveService {
  constructor(protected service: RequirementCustomService, protected router: Router) {
    super(service, router);
  }
}
