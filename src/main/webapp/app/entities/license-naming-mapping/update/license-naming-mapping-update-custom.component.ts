import { Component } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { LicenseNamingMappingUpdateComponent } from 'app/entities/license-naming-mapping/update/license-naming-mapping-update.component';
import { LicenseNamingMappingCustomService } from 'app/entities/license-naming-mapping/service/license-naming-mapping-custom.service';

@Component({
  selector: 'jhi-license-naming-mapping-update-custom',
  templateUrl: './license-naming-mapping-update-custom.component.html',
})
export class LicenseNamingMappingUpdateCustomComponent extends LicenseNamingMappingUpdateComponent {
  constructor(
    protected licenseNamingMappingService: LicenseNamingMappingCustomService,
    protected activatedRoute: ActivatedRoute,
    protected fb: UntypedFormBuilder
  ) {
    super(licenseNamingMappingService, activatedRoute, fb);
  }
}
