import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse } from '@angular/common/http';

import { ILibrary } from '../library.model';
import { ILicense } from 'app/entities/license/license.model';
import { ILicenseRisk } from 'app/entities/license-risk/license-risk.model';
import { IRequirement } from 'app/entities/requirement/requirement.model';
import { ICopyright } from 'app/core/copyright/copyright.model';
import { DataUtils } from 'app/core/util/data-util.service';
import { ILibraryErrorLog } from 'app/entities/library-error-log/library-error-log.model';
import { LogStatus } from 'app/entities/enumerations/log-status.model';

import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { CopyrightModalComponent } from 'app/shared/modals/copyright-modal/copyright-modal.component';
import { LibraryDetailComponent } from 'app/entities/library/detail/library-detail.component';
import { LibraryCustomService } from 'app/entities/library/service/library-custom.service';
import { interval, Observable, Subscription } from 'rxjs';
import { finalize } from 'rxjs/operators';
import { IFossology } from '../../fossology/fossology.model';
import dayjs from 'dayjs/esm';
import { FossologyStatus } from '../../enumerations/fossology-status.model';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { FossologyService } from 'app/entities/fossology/service/fossology.service';
import { IFossologyConfig } from 'app/entities/fossology/config/fossology-config.model';

@Component({
  selector: 'jhi-library-detail-custom',
  templateUrl: './library-detail-custom.component.html',
})
export class LibraryDetailCustomComponent extends LibraryDetailComponent {
  library: ILibrary | null = null;
  isLoadingCopyright = false;
  isLoadingFossology = false;
  isSaving = false;
  isFossologyEnabled = false;
  fossologyUrl: string;

  updateSubscription?: Subscription;

  constructor(
    protected dataUtils: DataUtils,
    protected activatedRoute: ActivatedRoute,
    protected libraryService: LibraryCustomService,
    protected modalService: NgbModal,
    protected applicationConfigService: ApplicationConfigService,
    protected fossologyService: FossologyService
  ) {
    super(dataUtils, activatedRoute);

    let config = applicationConfigService.getFossologyConfig();
    if (config) {
      this.isFossologyEnabled = config.enabled;
      this.fossologyUrl = config.url;
    } else {
      fossologyService.config().subscribe({
        next: (res: HttpResponse<IFossologyConfig>) => {
          applicationConfigService.setFossologyConfig(res.body);
          config = applicationConfigService.getFossologyConfig();
          if (config) {
            this.isFossologyEnabled = config.enabled;
            this.fossologyUrl = config.url;
          }
        },
      });
    }
  }

  analyseCopyright(): void {
    this.isLoadingCopyright = true;
    if (this.library?.id) {
      this.libraryService.analyseCopyright(this.library.id).subscribe(
        (res: HttpResponse<ICopyright>) => {
          this.isLoadingCopyright = false;

          const copyright = res.body;

          const data = {
            full: copyright?.fullCopyright,
            simple: copyright?.simpleCopyright,
          };

          this.openModal(data);
        },
        () => (this.isLoadingCopyright = false)
      );
    }
  }

  analyseWithFossology(): void {
    this.isLoadingFossology = true;
    if (this.library?.id) {
      this.libraryService.analyseWithFossology(this.library.id).subscribe({
        next: () => {
          this.isLoadingFossology = false;
          this.fossologyAnalysis();
        },
        error: () => {
          this.isLoadingFossology = false;
        },
      });
    }
  }

  fossologyAnalysis(): void {
    this.isLoadingFossology = true;
    if (this.library?.id) {
      this.libraryService.fossologyAnalysis(this.library.id).subscribe({
        next: (res: HttpResponse<IFossology>) => {
          if (this.library) {
            const fossology = res.body;
            if (fossology) {
              fossology.lastScan = fossology.lastScan ? dayjs(fossology.lastScan) : undefined;
            }
            this.library.fossology = fossology;

            if (this.updateSubscription === undefined) {
              this.updateSubscription = interval(4000).subscribe(() => {
                this.fossologyAnalysis();
              });
            } else if (
              this.library.fossology?.status === FossologyStatus.SCAN_FINISHED ||
              this.library.fossology?.status === FossologyStatus.FAILURE
            ) {
              this.updateSubscription.unsubscribe();
              this.updateSubscription = undefined;
            }
          }
          this.isLoadingFossology = false;
        },
        error: () => {
          this.isLoadingFossology = false;
        },
      });
    }
  }

  getRequirements(): IRequirement[] {
    const requirements: IRequirement[] = [];

    this.library?.licenseToPublishes?.forEach(license => {
      license.requirements?.forEach(requirement => {
        if (this.uniqueRequirement(requirements, requirement)) {
          requirements.push(requirement);
        }
      });
    });

    return requirements;
  }

  openModal(data: any): void {
    const modalRef = this.modalService.open(CopyrightModalComponent, {
      scrollable: true,
      size: 'xl',
    });

    modalRef.componentInstance.library = this.library;
    modalRef.componentInstance.fromParent = data;
    modalRef.componentInstance.finalCopyright = this.library?.copyright;
  }

  changeStatus(log: ILibraryErrorLog, status: string): void {
    this.isSaving = true;
    if (this.library && log.status !== status) {
      log.status = LogStatus[status];
      this.subscribeToSaveResponse(this.libraryService.update(this.library));
    }
  }

  hasErrors(): boolean {
    if (this.library) {
      return this.libraryService.hasErrors(this.library);
    }

    return false;
  }

  protected uniqueRequirement(list: IRequirement[], requirement: IRequirement): boolean {
    let unique = true;

    list.forEach(value => {
      if (value.id === requirement.id) {
        unique = false;
      }
    });

    return unique;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ILibrary>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }
}
