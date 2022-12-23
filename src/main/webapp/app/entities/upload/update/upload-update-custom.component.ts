import { Component } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { EventManager } from 'app/core/util/event-manager.service';
import { DataUtils } from 'app/core/util/data-util.service';
import { UploadUpdateComponent } from 'app/entities/upload/update/upload-update.component';
import { UploadCustomService } from 'app/entities/upload/service/upload-custom.service';

@Component({
  selector: 'jhi-upload-update-custom',
  templateUrl: './upload-update-custom.component.html',
})
export class UploadUpdateCustomComponent extends UploadUpdateComponent {
  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected uploadService: UploadCustomService,
    protected activatedRoute: ActivatedRoute,
    protected fb: UntypedFormBuilder
  ) {
    super(dataUtils, eventManager, uploadService, activatedRoute, fb);
  }
}
