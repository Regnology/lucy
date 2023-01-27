import { Component } from '@angular/core';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';
import { combineLatest } from 'rxjs';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ILibrary } from '../library.model';

import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/config/pagination.constants';
import { DataUtils } from 'app/core/util/data-util.service';

import { IFile } from 'app/core/file/file.model';
import { UntypedFormBuilder } from '@angular/forms';
import { LibraryDeleteDialogCustomComponent } from 'app/entities/library/delete/library-delete-dialog-custom.component';
import { LibraryComponent } from 'app/entities/library/list/library.component';
import { LibraryCustomService } from 'app/entities/library/service/library-custom.service';

@Component({
  selector: 'jhi-library-custom',
  templateUrl: './library-custom.component.html',
})
export class LibraryCustomComponent extends LibraryComponent {
  libraries?: ILibrary[];
  isLoading = false;
  totalItems = 0;
  itemsPerPage = ITEMS_PER_PAGE;
  page?: number;
  predicate!: string;
  ascending!: boolean;
  ngbPaginationPage = 1;
  jsonExport = true;
  isExporting = false;

  searchForm = this.fb.group({
    groupId: [],
    artifactId: [],
    version: [],
    license: [],
  });

  constructor(
    protected libraryService: LibraryCustomService,
    protected activatedRoute: ActivatedRoute,
    protected dataUtils: DataUtils,
    protected router: Router,
    protected modalService: NgbModal,
    protected fb: UntypedFormBuilder
  ) {
    super(libraryService, activatedRoute, dataUtils, router, modalService);
  }

  loadPage(page?: number, dontNavigate?: boolean): void {
    this.isLoading = true;
    const pageToLoad: number = page ?? this.page ?? 1;

    this.libraryService
      .query({
        'groupId.contains': this.searchForm.get('groupId')?.value ?? null,
        'artifactId.contains': this.searchForm.get('artifactId')?.value ?? null,
        'version.contains': this.searchForm.get('version')?.value ?? null,
        'linkedLicenseShortIdentifier.contains': this.searchForm.get('license')?.value ?? null,
        page: pageToLoad - 1,
        size: this.itemsPerPage,
        sort: this.sort(),
      })
      .subscribe(
        (res: HttpResponse<ILibrary[]>) => {
          this.isLoading = false;
          this.onSuccess(res.body, res.headers, pageToLoad, !dontNavigate);
        },
        () => {
          this.isLoading = false;
          super.onError();
        }
      );
  }

  delete(library: ILibrary): void {
    const modalRef = this.modalService.open(LibraryDeleteDialogCustomComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.library = library;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadPage();
      }
    });
  }

  export(): void {
    this.isExporting = true;
    this.libraryService.export({ format: this.jsonExport ? 'JSON' : 'CSV' }).subscribe(
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
    this.isLoading = true;

    this.libraryService
      .query({
        'groupId.contains': this.searchForm.get('groupId')?.value ?? null,
        'artifactId.contains': this.searchForm.get('artifactId')?.value ?? null,
        'version.contains': this.searchForm.get('version')?.value ?? null,
        'linkedLicenseShortIdentifier.contains': this.searchForm.get('license')?.value ?? null,
        sort: super.sort(),
        page: 0,
        size: this.itemsPerPage,
      })
      .subscribe(
        (res: HttpResponse<ILibrary[]>) => {
          this.isLoading = false;
          this.onSuccess(res.body, res.headers, 1, true);
        },
        () => {
          this.isLoading = false;
          super.onError();
        }
      );
  }

  hasErrors(library: ILibrary): boolean {
    return this.libraryService.hasErrors(library);
  }

  protected handleNavigation(): void {
    combineLatest([this.activatedRoute.data, this.activatedRoute.queryParamMap]).subscribe(([data, params]) => {
      const page = params.get('page');
      const pageNumber = +(page ?? 1);
      const sort = (params.get(SORT) ?? data['defaultSort']).split(',');
      const predicate = sort[0];
      const ascending = sort[1] === ASC;

      const groupId = params.get('groupId.contains');
      const artifactId = params.get('artifactId.contains');
      const version = params.get('version.contains');
      const linkedLicenseShortIdentifier = params.get('linkedLicenseShortIdentifier.contains');

      this.searchForm.get('groupId')?.setValue(groupId);
      this.searchForm.get('artifactId')?.setValue(artifactId);
      this.searchForm.get('version')?.setValue(version);
      this.searchForm.get('license')?.setValue(linkedLicenseShortIdentifier);

      if (pageNumber !== this.page || predicate !== this.predicate || ascending !== this.ascending) {
        this.predicate = predicate;
        this.ascending = ascending;
        this.loadPage(pageNumber, true);
      }
    });
  }

  protected onSuccess(data: ILibrary[] | null, headers: HttpHeaders, page: number, navigate: boolean): void {
    this.totalItems = Number(headers.get('X-Total-Count'));
    this.page = page;
    if (navigate) {
      this.router.navigate(['/library'], {
        queryParams: {
          'groupId.contains': this.searchForm.get('groupId')?.value ?? null,
          'artifactId.contains': this.searchForm.get('artifactId')?.value ?? null,
          'version.contains': this.searchForm.get('version')?.value ?? null,
          'linkedLicenseShortIdentifier.contains': this.searchForm.get('license')?.value ?? null,
          page: this.page,
          size: this.itemsPerPage,
          sort: this.predicate + ',' + (this.ascending ? ASC : DESC),
        },
      });
    }
    this.libraries = data ?? [];
    this.ngbPaginationPage = this.page;
  }
}
