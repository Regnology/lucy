import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { UploadCustomService } from 'app/entities/upload/service/upload-custom.service';
import { UploadRoutingResolveService } from 'app/entities/upload/route/upload-routing-resolve.service';

@Injectable({ providedIn: 'root' })
export class UploadRoutingResolveCustomService extends UploadRoutingResolveService {
  constructor(protected service: UploadCustomService, protected router: Router) {
    super(service, router);
  }
}
