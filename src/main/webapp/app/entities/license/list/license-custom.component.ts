import { Component } from '@angular/core';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';
import { combineLatest } from 'rxjs';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ILicense } from '../license.model';
import { DataUtils } from 'app/core/util/data-util.service';

import { IFile } from 'app/core/file/file.model';
import { IRequirement } from 'app/entities/requirement/requirement.model';
import { UntypedFormBuilder } from '@angular/forms';
import { LicenseDeleteDialogCustomComponent } from 'app/entities/license/delete/license-delete-dialog-custom.component';
import { LicenseCustomService } from 'app/entities/license/service/license-custom.service';
import { LicenseComponent } from 'app/entities/license/list/license.component';
import { RequirementCustomService } from 'app/entities/requirement/service/requirement-custom.service';
import { ASC, DESC, SORT } from '../../../config/pagination.constants';

@Component({
  selector: 'jhi-license-custom',
  templateUrl: './license-custom.component.html',
})
export class LicenseCustomComponent extends LicenseComponent {
  jsonExport = true;
  isExporting = false;

  requirementsSharedCollection: (string | undefined)[] = [];

  searchForm = this.fb.group({
    fullName: [],
    shortIdentifier: [],
    spdx: [],
    requirements: [],
  });

  constructor(
    protected licenseService: LicenseCustomService,
    protected requirementService: RequirementCustomService,
    protected activatedRoute: ActivatedRoute,
    protected dataUtils: DataUtils,
    protected router: Router,
    protected modalService: NgbModal,
    protected fb: UntypedFormBuilder
  ) {
    super(licenseService, activatedRoute, dataUtils, router, modalService);
  }

  loadPage(page?: number, dontNavigate?: boolean): void {
    this.isLoading = true;
    const pageToLoad: number = page ?? this.page ?? 1;

    this.licenseService
      .query({
        'fullName.contains': this.searchForm.get('fullName')?.value ?? null,
        'shortIdentifier.contains': this.searchForm.get('shortIdentifier')?.value ?? null,
        'spdxIdentifier.contains': this.searchForm.get('spdx')?.value ?? null,
        'requirementShortText.equals':
          this.searchForm.get('requirements')?.value && this.searchForm.get('requirements')?.value !== 'null'
            ? this.searchForm.get('requirements')?.value
            : null,
        page: pageToLoad - 1,
        size: this.itemsPerPage,
        sort: super.sort(),
      })
      .subscribe(
        (res: HttpResponse<ILicense[]>) => {
          this.isLoading = false;
          this.onSuccess(res.body, res.headers, pageToLoad, !dontNavigate);
        },
        () => {
          this.isLoading = false;
          super.onError();
        }
      );

    this.updateForm();
  }

  delete(license: ILicense): void {
    const modalRef = this.modalService.open(LicenseDeleteDialogCustomComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.license = license;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadPage();
      }
    });
  }

  trackRequirementById(index: number, item: IRequirement): number {
    return item.id!;
  }

  export(): void {
    this.isExporting = true;
    this.licenseService.export({ format: this.jsonExport ? 'JSON' : 'CSV' }).subscribe(
      (data: HttpResponse<IFile>) => {
        if (data.body) {
          this.dataUtils.downloadFile(data.body.fileContentType, data.body.file, data.body.fileName);
          this.isExporting = false;
        }
      },
      () => (this.isExporting = false)
    );
  }

  search(): void {
    this.licenseService
      .query({
        'fullName.contains': this.searchForm.get('fullName')?.value ?? null,
        'shortIdentifier.contains': this.searchForm.get('shortIdentifier')?.value ?? null,
        'spdxIdentifier.contains': this.searchForm.get('spdx')?.value ?? null,
        'requirementShortText.equals':
          this.searchForm.get('requirements')?.value && this.searchForm.get('requirements')?.value !== 'null'
            ? this.searchForm.get('requirements')?.value
            : null,
        sort: super.sort(),
        page: 0,
        size: this.itemsPerPage,
      })
      .subscribe({
        next: (res: HttpResponse<ILicense[]>) => {
          this.isLoading = false;
          this.onSuccess(res.body, res.headers, 1, true);
        },
        error: () => {
          this.isLoading = false;
          super.onError();
        },
      });
  }

  protected handleNavigation(): void {
    combineLatest([this.activatedRoute.data, this.activatedRoute.queryParamMap]).subscribe(([data, params]) => {
      const page = params.get('page');
      const pageNumber = +(page ?? 1);
      const sort = (params.get(SORT) ?? data['defaultSort']).split(',');
      const predicate = sort[0];
      const ascending = sort[1] === ASC;

      const fullName = params.get('fullName.contains');
      const shortIdentifier = params.get('shortIdentifier.contains');
      const spdxIdentifier = params.get('spdxIdentifier.contains');
      const requirements = params.get('requirementShortText.equals');

      this.searchForm.get('fullName')?.setValue(fullName);
      this.searchForm.get('shortIdentifier')?.setValue(shortIdentifier);
      this.searchForm.get('spdxIdentifier')?.setValue(spdxIdentifier);
      this.searchForm.get('requirements')?.setValue(requirements);

      if (pageNumber !== this.page || predicate !== this.predicate || ascending !== this.ascending) {
        this.predicate = predicate;
        this.ascending = ascending;
        this.loadPage(pageNumber, true);
      }
    });
  }

  protected onSuccess(data: ILicense[] | null, headers: HttpHeaders, page: number, navigate: boolean): void {
    this.totalItems = Number(headers.get('X-Total-Count'));
    this.page = page;
    if (navigate) {
      this.router.navigate(['/license'], {
        queryParams: {
          'fullName.contains': this.searchForm.get('fullName')?.value ?? null,
          'shortIdentifier.contains': this.searchForm.get('shortIdentifier')?.value ?? null,
          'spdxIdentifier.contains': this.searchForm.get('spdx')?.value ?? null,
          'requirementShortText.equals':
            this.searchForm.get('requirements')?.value && this.searchForm.get('requirements')?.value !== 'null'
              ? this.searchForm.get('requirements')?.value
              : null,
          page: this.page,
          size: this.itemsPerPage,
          sort: this.predicate + ',' + (this.ascending ? ASC : DESC),
        },
      });
    }
    this.licenses = data ?? [];
    this.ngbPaginationPage = this.page;
  }

  protected updateForm(): void {
    this.requirementService
      .query()
      .subscribe((res: HttpResponse<IRequirement[]>) => (this.requirementsSharedCollection = res.body?.map(e => e.shortText) ?? []));
  }
}
