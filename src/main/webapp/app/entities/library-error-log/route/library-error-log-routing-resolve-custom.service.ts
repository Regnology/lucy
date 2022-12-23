import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { LibraryErrorLogRoutingResolveService } from 'app/entities/library-error-log/route/library-error-log-routing-resolve.service';
import { LibraryErrorLogCustomService } from 'app/entities/library-error-log/service/library-error-log-custom.service';

@Injectable({ providedIn: 'root' })
export class LibraryErrorLogRoutingResolveCustomService extends LibraryErrorLogRoutingResolveService {
  constructor(protected service: LibraryErrorLogCustomService, protected router: Router) {
    super(service, router);
  }
}
