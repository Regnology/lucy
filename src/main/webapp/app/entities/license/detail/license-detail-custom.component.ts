import { Component, TemplateRef } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { DataUtils } from 'app/core/util/data-util.service';
import { LicenseDetailComponent } from 'app/entities/license/detail/license-detail.component';
import { HttpResponse } from '@angular/common/http';
import { ILicenseConflict } from '../../license-conflict/license-conflict.model';
import { LicenseCustomService } from '../service/license-custom.service';
import { ILicenseRisk } from '../../license-risk/license-risk.model';
import { map } from 'rxjs/operators';
import { LicenseRiskCustomService } from '../../license-risk/service/license-risk-custom.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'jhi-license-detail-custom',
  templateUrl: './license-detail-custom.component.html',
})
export class LicenseDetailCustomComponent extends LicenseDetailComponent {
  isLoadingLicenseConflicts = false;
  finishedLoadingLicenseConflicts = false;

  licenseConflictsMap = new Map<string, ILicenseConflict[]>();
  licenseRisksSharedCollection: ILicenseRisk[] = [];

  constructor(
    protected dataUtils: DataUtils,
    protected activatedRoute: ActivatedRoute,
    protected licenseService: LicenseCustomService,
    protected licenseRiskService: LicenseRiskCustomService,
    protected modalService: NgbModal
  ) {
    super(dataUtils, activatedRoute);
  }

  navTabChange(event: any): void {
    this.isLoadingLicenseConflicts = true;

    if (event.nextId === 'License Conflicts' && !this.finishedLoadingLicenseConflicts && this.license?.id) {
      this.licenseService.fetchLicenseConflictsWithRisk(this.license.id).subscribe({
        next: (res: HttpResponse<ILicenseConflict[]>) => {
          this.licenseRiskService
            .query()
            .pipe(map((resRisk: HttpResponse<ILicenseRisk[]>) => resRisk.body ?? []))
            .subscribe((licenseRisks: ILicenseRisk[]) => {
              this.licenseRisksSharedCollection = licenseRisks;

              res.body?.forEach(value => {
                if (value.secondLicenseConflict?.licenseRisk?.name) {
                  const licenseRiskName = value.secondLicenseConflict.licenseRisk.name;
                  if (this.licenseConflictsMap.has(licenseRiskName)) {
                    this.licenseConflictsMap.get(licenseRiskName)!.push(value);
                  } else {
                    this.licenseConflictsMap.set(licenseRiskName, [value]);
                  }
                }
              });

              this.licenseConflictsMap = new Map<string, ILicenseConflict[]>(
                [...this.licenseConflictsMap].sort((a, b) => {
                  if (
                    this.licenseRisksSharedCollection.find(value => value.name === a[0])!.level! >
                    this.licenseRisksSharedCollection.find(value => value.name === b[0])!.level!
                  ) {
                    return 1;
                  } else if (
                    this.licenseRisksSharedCollection.find(value => value.name === a[0])!.level! <
                    this.licenseRisksSharedCollection.find(value => value.name === b[0])!.level!
                  ) {
                    return -1;
                  }
                  return 0;
                })
              );

              this.isLoadingLicenseConflicts = false;
              this.finishedLoadingLicenseConflicts = true;
            });
        },
        error: () => (this.isLoadingLicenseConflicts = false),
      });
    }
  }

  asIsOrder(a, b): number {
    return 0;
  }

  licenseRiskColor(name: string): string | null | undefined {
    return this.licenseRisksSharedCollection.find(value => value.name === name)?.color;
  }

  openScrollableContent(longContent: TemplateRef<any>, windowSize: string): void {
    this.modalService.open(longContent, { scrollable: true, size: windowSize });
  }
}
