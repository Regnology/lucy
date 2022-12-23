import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { LibraryPerProductRoutingResolveService } from 'app/entities/library-per-product/route/library-per-product-routing-resolve.service';
import { LibraryPerProductCustomService } from 'app/entities/library-per-product/service/library-per-product-custom.service';

@Injectable({ providedIn: 'root' })
export class LibraryPerProductRoutingResolveCustomService extends LibraryPerProductRoutingResolveService {
  constructor(protected service: LibraryPerProductCustomService, protected router: Router) {
    super(service, router);
  }
}
