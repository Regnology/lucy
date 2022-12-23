import { NgModule } from '@angular/core';

import { SharedModule } from 'app/shared/shared.module';
import { RequirementDeleteDialogCustomComponent } from 'app/entities/requirement/delete/requirement-delete-dialog-custom.component';
import { RequirementUpdateCustomComponent } from 'app/entities/requirement/update/requirement-update-custom.component';
import { RequirementDetailCustomComponent } from 'app/entities/requirement/detail/requirement-detail-custom.component';
import { RequirementCustomComponent } from 'app/entities/requirement/list/requirement-custom.component';
import { RequirementRoutingCustomModule } from 'app/entities/requirement/route/requirement-routing-custom.module';
import { RequirementComponent } from 'app/entities/requirement/list/requirement.component';
import { RequirementDetailComponent } from 'app/entities/requirement/detail/requirement-detail.component';
import { RequirementUpdateComponent } from 'app/entities/requirement/update/requirement-update.component';
import { RequirementDeleteDialogComponent } from 'app/entities/requirement/delete/requirement-delete-dialog.component';

@NgModule({
  imports: [SharedModule, RequirementRoutingCustomModule],
  declarations: [
    RequirementComponent,
    RequirementDetailComponent,
    RequirementUpdateComponent,
    RequirementDeleteDialogComponent,
    RequirementCustomComponent,
    RequirementDetailCustomComponent,
    RequirementUpdateCustomComponent,
    RequirementDeleteDialogCustomComponent,
  ],
  entryComponents: [RequirementDeleteDialogCustomComponent],
})
export class RequirementCustomModule {}
