import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { RequirementComponent } from './list/requirement.component';
import { RequirementDetailComponent } from './detail/requirement-detail.component';
import { RequirementUpdateComponent } from './update/requirement-update.component';
import { RequirementDeleteDialogComponent } from './delete/requirement-delete-dialog.component';
import { RequirementRoutingModule } from './route/requirement-routing.module';

@NgModule({
  imports: [SharedModule, RequirementRoutingModule],
  declarations: [RequirementComponent, RequirementDetailComponent, RequirementUpdateComponent, RequirementDeleteDialogComponent],
  entryComponents: [RequirementDeleteDialogComponent],
})
export class RequirementModule {}
