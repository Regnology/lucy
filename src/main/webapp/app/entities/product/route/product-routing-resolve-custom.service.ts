import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { ProductRoutingResolveService } from 'app/entities/product/route/product-routing-resolve.service';
import { ProductCustomService } from '../service/product-custom.service';

@Injectable({ providedIn: 'root' })
export class ProductRoutingResolveCustomService extends ProductRoutingResolveService {
  constructor(protected service: ProductCustomService, protected router: Router) {
    super(service, router);
  }
}
