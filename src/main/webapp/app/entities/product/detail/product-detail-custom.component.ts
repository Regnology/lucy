import { AfterViewInit, Component, OnInit, QueryList, TemplateRef, ViewChildren, ViewChild } from '@angular/core';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';
import { combineLatest, Observable } from 'rxjs';
import { UntypedFormBuilder, Validators } from '@angular/forms';

import { NgbModal, NgbOffcanvas } from '@ng-bootstrap/ng-bootstrap';

import { IProduct } from '../product.model';
import { Upload } from 'app/core/upload/upload.model';
import { UploadState } from 'app/entities/enumerations/upload-state.model';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { ILibrary } from 'app/entities/library/library.model';
import { ILibraryPerProduct, LibraryPerProduct } from 'app/entities/library-per-product/library-per-product.model';
import { File, IFile } from 'app/core/file/file.model';
import { ICountOccurrences } from 'app/shared/statistics/count-occurrences.model';
import { IProductOverview } from 'app/shared/statistics/product-overview/product-overview.model';
import { ILicenseRisk } from 'app/entities/license-risk/license-risk.model';

import { AlertService } from 'app/core/util/alert.service';

import { NO_COPYRIGHT } from 'app/config/library-field.constants';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ProductDetailComponent } from 'app/entities/product/detail/product-detail.component';
import { LicenseRiskCustomService } from 'app/entities/license-risk/service/license-risk-custom.service';
import { LibraryPerProductCustomService } from 'app/entities/library-per-product/service/library-per-product-custom.service';
import { LibraryCustomService } from 'app/entities/library/service/library-custom.service';
import { ProductCustomService } from 'app/entities/product/service/product-custom.service';
import { LibraryPerProductDeleteDialogCustomComponent } from 'app/entities/library-per-product/delete/library-per-product-delete-dialog-custom.component';
import { UploadCustomService } from 'app/entities/upload/service/upload-custom.service';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from '../../../config/pagination.constants';
import { ProductUpdateLibraryComponent } from '../update-library/product-update-library.component';
import { finalize } from 'rxjs/operators';
import { DifferenceViewComponent } from '../../../shared/modals/difference-view-modal/difference-view.component';
import { IProductStatistic } from '../../../shared/statistics/product-overview/product-statistic.model';

@Component({
  selector: 'jhi-product-detail-custom',
  templateUrl: './product-detail-custom.component.html',
})
export class ProductDetailCustomComponent extends ProductDetailComponent implements OnInit, AfterViewInit {
  @ViewChild(ProductUpdateLibraryComponent)
  productUpdateLibraryComponent?: ProductUpdateLibraryComponent;

  @ViewChildren(DifferenceViewComponent)
  differenceView: QueryList<DifferenceViewComponent>;

  no_copyright = NO_COPYRIGHT;

  librariesPerProduct?: ILibraryPerProduct[] | null;
  totalItems = 0;
  itemsPerPage = ITEMS_PER_PAGE;
  page?: number;
  predicate!: string;
  ascending!: boolean;
  ngbPaginationPage = 1;
  uploadStateType = UploadState.SUCCESSFUL;
  isLoading = false;
  isLoadingAddSearch = false;
  isDownloadingOss = false;
  isCreatingArchive = false;
  isDownloadingZip = false;
  isTransferring = false;
  ossFormat = 'CSV';
  ossType = 'DEFAULT';
  archiveFormat = 'FULL';
  archiveShipment = 'DOWNLOAD';
  showMoreOss = false;
  showMoreSca = false;
  isCollapsedUploadFilter = true;
  isCollapsedUploadWithAuthentication = true;
  growthLibraries?: number | undefined;
  growthLicenses?: number | undefined;
  percentageReviewedLibraries?: number | undefined;
  overview?: IProductOverview | null;
  statistic?: IProductStatistic | null;
  licenseRisks: ILicenseRisk[] | null = [];
  licenseDistribution: ICountOccurrences[] = [];
  addSearchLibraries?: ILibrary[];

  dtn = 0;

  isSaving = false;
  saved = false;
  libraryPerProduct?: ILibraryPerProduct;
  libraryPerProductForm = this.fb.group({
    hide: [],
    localComment: [],
  });

  tableForm = this.fb.group({
    groupId: true,
    artifactId: true,
    version: true,
    type: false,
    licenses: false,
    licenseToPublish: true,
    risk: true,
    licenseUrl: false,
    licenseText: true,
    sourceCodeUrl: false,
    copyright: false,
    reviewed: false,
    hideForPublishing: false,
    localComment: false,
    errorLog: false,
    manuallyAdded: false,
  });

  uploadForm = this.fb.group({
    url: [],
    username: [],
    password: [],
    file: [],
    fileContentType: [],
    fileName: [],
    additionalLibraries: [],
    additionalLibrariesContentType: [],
    additionalLibrariesFileName: [],
    deleteData: true,
  });

  addLibraryForm = this.fb.group({
    groupId: [],
    artifactId: [],
    version: [],
    libraries: [],
  });

  searchForm = this.fb.group({
    artifactId: [],
    licenses: [],
  });

  nextProductForm = this.fb.group({
    version: [null, [Validators.required]],
    delivered: true,
    copy: true,
  });

  riskForm = this.fb.group({
    '1': false,
    '2': false,
    '3': false,
    '4': false,
    '5': false,
    '6': false,
    '7': false,
  });

  filterForm = this.fb.group({
    licenseConflicts: false,
    newLibrariesSinceLastUpload: false,
  });

  constructor(
    protected productService: ProductCustomService,
    protected libraryService: LibraryCustomService,
    protected libraryPerProductService: LibraryPerProductCustomService,
    protected activatedRoute: ActivatedRoute,
    protected router: Router,
    protected dataUtils: DataUtils,
    protected alertService: AlertService,
    protected fb: UntypedFormBuilder,
    protected modalService: NgbModal,
    protected eventManager: EventManager,
    protected uploadService: UploadCustomService,
    protected licenseRiskService: LicenseRiskCustomService,
    protected offcanvasService: NgbOffcanvas
  ) {
    super(dataUtils, activatedRoute);
  }

  ngAfterViewInit(): void {
    this.differenceView.changes.subscribe((comps: QueryList<DifferenceViewComponent>) => {
      comps.get(0)?.compareWithParameters(this.product?.previousProductId, this.product?.id);
    });
  }

  loadPage(page?: number, dontNavigate?: boolean): void {
    if (this.product?.id) {
      this.productService.find(this.product.id).subscribe(
        (res: HttpResponse<IProduct>) => {
          this.onProductSuccess(res.body);
        },
        () => {
          this.onError();
        }
      );

      this.loadLibraries(page, dontNavigate);

      this.loadOverview();
    }
  }

  loadLibraries(page?: number, dontNavigate?: boolean): void {
    if (this.product?.id && this.product.lastUpdatedDate) {
      this.isLoading = true;
      const pageToLoad: number = page ?? this.page ?? 1;
      const licenseRiskFilter = this.createLicenseRiskFilter();

      this.libraryPerProductService
        .query({
          'productId.equals': this.product.id,
          'artifactId.contains': this.searchForm.get('artifactId')?.value ?? null,
          'licensesShortIdentifier.contains': this.searchForm.get('licenses')?.value ?? null,
          'libraryRiskId.in': licenseRiskFilter?.join(',') ?? null,
          'errorLogMessage.contains': this.filterForm.get('licenseConflicts')!.value ? 'License Conflict' : null,
          'errorLogStatus.notEquals': this.filterForm.get('licenseConflicts')!.value ? 'CLOSED' : null,
          'libraryCreatedDate.greaterThanOrEqual': this.filterForm.get('newLibrariesSinceLastUpload')!.value
            ? this.product.lastUpdatedDate.format(DATE_FORMAT)
            : null,
          page: pageToLoad - 1,
          size: this.itemsPerPage,
          sort: this.sort(),
        })
        .subscribe(
          (res: HttpResponse<ILibraryPerProduct[]>) => {
            this.isLoading = false;
            this.onLLPSuccess(res.body, res.headers, pageToLoad, !dontNavigate);
          },
          () => {
            this.isLoading = false;
            this.onError();
          }
        );
    }
  }

  loadOverview(): void {
    if (this.product?.id) {
      this.productService.overview(this.product.id).subscribe((res: HttpResponse<IProductOverview>) => {
        this.overview = res.body;

        if (this.overview?.reviewedLibraries != null && this.overview.numberOfLibraries != null) {
          if (this.overview.numberOfLibraries === 0) {
            this.percentageReviewedLibraries = 0;
          } else {
            this.percentageReviewedLibraries = +((this.overview.reviewedLibraries / this.overview.numberOfLibraries) * 100).toFixed();
          }
        }

        if (this.product?.previousProductId) {
          if (this.overview?.numberOfLibraries != null && this.overview.numberOfLibrariesPrevious != null) {
            if (this.overview.numberOfLibraries === 0 && this.overview.numberOfLibrariesPrevious === 0) {
              this.growthLibraries = 0;
            } else if (this.overview.numberOfLibraries !== 0 && this.overview.numberOfLibrariesPrevious === 0) {
              this.growthLibraries = +((this.overview.numberOfLibraries / 1 - 1) * 100).toFixed();
            } else {
              this.growthLibraries = +((this.overview.numberOfLibraries / this.overview.numberOfLibrariesPrevious - 1) * 100).toFixed();
            }
          }
          if (this.overview?.numberOfLicenses != null && this.overview.numberOfLicensesPrevious != null) {
            if (this.overview.numberOfLicenses === 0 && this.overview.numberOfLicensesPrevious === 0) {
              this.growthLicenses = 0;
            } else if (this.overview.numberOfLicenses !== 0 && this.overview.numberOfLicensesPrevious === 0) {
              this.growthLicenses = +((this.overview.numberOfLicenses / 1 - 1) * 100).toFixed();
            } else {
              this.growthLicenses = +((this.overview.numberOfLicenses / this.overview.numberOfLicensesPrevious - 1) * 100).toFixed();
            }
          }
        }
      });
    }
  }

  loadStatistics(): void {
    if (this.product?.id) {
      this.productService.overview(this.product.id).subscribe((res: HttpResponse<IProductOverview>) => {
        this.overview = res.body;

        if (this.overview?.reviewedLibraries != null && this.overview.numberOfLibraries != null) {
          if (this.overview.numberOfLibraries === 0) {
            this.percentageReviewedLibraries = 0;
          } else {
            this.percentageReviewedLibraries = +((this.overview.reviewedLibraries / this.overview.numberOfLibraries) * 100).toFixed();
          }
        }

        if (this.product?.previousProductId) {
          if (this.overview?.numberOfLibraries != null && this.overview.numberOfLibrariesPrevious != null) {
            if (this.overview.numberOfLibraries === 0 && this.overview.numberOfLibrariesPrevious === 0) {
              this.growthLibraries = 0;
            } else if (this.overview.numberOfLibraries !== 0 && this.overview.numberOfLibrariesPrevious === 0) {
              this.growthLibraries = +((this.overview.numberOfLibraries / 1 - 1) * 100).toFixed();
            } else {
              this.growthLibraries = +((this.overview.numberOfLibraries / this.overview.numberOfLibrariesPrevious - 1) * 100).toFixed();
            }
          }
          if (this.overview?.numberOfLicenses != null && this.overview.numberOfLicensesPrevious != null) {
            if (this.overview.numberOfLicenses === 0 && this.overview.numberOfLicensesPrevious === 0) {
              this.growthLicenses = 0;
            } else if (this.overview.numberOfLicenses !== 0 && this.overview.numberOfLicensesPrevious === 0) {
              this.growthLicenses = +((this.overview.numberOfLicenses / 1 - 1) * 100).toFixed();
            } else {
              this.growthLicenses = +((this.overview.numberOfLicenses / this.overview.numberOfLicensesPrevious - 1) * 100).toFixed();
            }
          }
        }
      });
    }
  }

  ngOnInit(): void {
    this.router.routeReuseStrategy.shouldReuseRoute = () => false;
    this.router.onSameUrlNavigation = 'reload';

    this.loadLocalStorage();
    this.onLibraryTableChanges();

    this.activatedRoute.data.subscribe(({ product }) => {
      this.product = product;
      this.uploadStateType = this.product?.uploadState ?? UploadState.SUCCESSFUL;
      this.isLoading = true;
    });

    this.licenseRiskService
      .query({
        sort: ['level', 'asc'],
        page: 0,
        size: 10,
      })
      .subscribe((res: HttpResponse<ILicenseRisk[]>) => {
        this.licenseRisks = res.body ?? [];
      });

    this.handleNavigation();
  }

  downloadOssList(): void {
    this.isDownloadingOss = true;
    if (this.product?.id) {
      this.productService.oss(this.product.id, { format: this.ossFormat, type: this.ossType }).subscribe(
        (res: HttpResponse<IFile>) => {
          if (res.body) {
            this.dataUtils.downloadFile(res.body.fileContentType, res.body.file, res.body.fileName);
            this.isDownloadingOss = false;
          }
        },
        () => (this.isDownloadingOss = false)
      );
    }
  }

  createArchive(): void {
    this.isCreatingArchive = true;
    if (this.product?.id) {
      this.productService
        .archive(this.product.id, {
          format: this.archiveFormat,
          shipment: this.archiveShipment,
        })
        .subscribe(
          (res: HttpResponse<IFile>) => {
            if (res.body) {
              this.dataUtils.downloadFile(res.body.fileContentType, res.body.file, res.body.fileName);
            } else {
              const platform = res.headers.get('PLATFORM')!;
              this.alertService.addAlert({
                type: 'success',
                message: platform,
                timeout: 0,
                toast: false,
              });
            }
            const complete = res.headers.get('COMPLETE');
            if (complete) {
              this.alertService.addAlert({
                type: 'danger',
                message: complete,
                timeout: 0,
                toast: false,
              });
            }
            this.isCreatingArchive = false;
          },
          () => (this.isCreatingArchive = false)
        );
    }
  }

  downloadZip(): void {
    this.isDownloadingZip = true;
    if (this.product?.id) {
      this.productService.zip(this.product.id).subscribe(
        (res: HttpResponse<IFile>) => {
          if (res.body) {
            this.dataUtils.downloadFile(res.body.fileContentType, res.body.file, res.body.fileName);
            this.isDownloadingZip = false;
          }
        },
        () => (this.isDownloadingZip = false)
      );
    }
  }

  transferToTarget(): void {
    this.isTransferring = true;
    if (this.product?.id) {
      this.productService.transferToTarget(this.product.id).subscribe(() => (this.isTransferring = false));
    }
  }

  openScrollableContent(longContent: TemplateRef<any>, windowSize: string): void {
    this.modalService.open(longContent, { scrollable: true, size: windowSize });
  }

  open(content: TemplateRef<any>): void {
    this.modalService.open(content).result.then(
      () => {
        // Empty
      },
      () => {
        // Empty
      }
    );
  }

  openSide(content: TemplateRef<any>, libraryPerProduct: ILibraryPerProduct): void {
    this.saved = false;
    this.libraryPerProduct = libraryPerProduct;
    this.updateForm(libraryPerProduct);
    if (!this.offcanvasService.hasOpenOffcanvas()) {
      this.offcanvasService.open(content, { position: 'bottom', scroll: true, backdrop: false });
    }
  }

  findLibraryPerProductInList(libraryPerProduct: ILibraryPerProduct): void {
    const index = this.librariesPerProduct?.findIndex(
      lpp =>
        lpp.library?.groupId === libraryPerProduct.library?.groupId &&
        lpp.library?.artifactId === libraryPerProduct.library?.artifactId &&
        lpp.library?.version === libraryPerProduct.library?.version &&
        lpp.library?.type === libraryPerProduct.library?.type
    );
    this.librariesPerProduct![index!] = libraryPerProduct;
  }

  setFileData(event: Event, field: string): void {
    this.uploadForm.get('fileName')!.setValue((event.target as HTMLInputElement).files![0].name);

    this.dataUtils.loadFileToForm(event, this.uploadForm, field, false).subscribe({
      error: (err: FileLoadError) =>
        this.eventManager.broadcast(new EventWithContent<AlertError>('lucyApp.error', { message: err.message })),
    });
  }

  setAdditionalLibrariesData(event: Event, field: string): void {
    this.uploadForm.get('additionalLibrariesFileName')!.setValue((event.target as HTMLInputElement).files![0].name);

    this.dataUtils.loadFileToForm(event, this.uploadForm, field, false).subscribe({
      error: (err: FileLoadError) =>
        this.eventManager.broadcast(new EventWithContent<AlertError>('lucyApp.error', { message: err.message })),
    });
  }

  upload(): void {
    if (this.uploadForm.get('url')!.value) {
      this.uploadByUrl();
    } else {
      if (this.product?.id) {
        const fileUpload = new File(
          this.uploadForm.get('fileName')!.value,
          this.uploadForm.get('fileContentType')!.value,
          this.uploadForm.get('file')!.value
        );
        const additionalLibrariesUpload = new File(
          this.uploadForm.get('additionalLibrariesFileName')!.value,
          this.uploadForm.get('additionalLibrariesContentType')!.value,
          this.uploadForm.get('additionalLibraries')!.value
        );

        this.productService
          .upload(this.product.id, new Upload(fileUpload, additionalLibrariesUpload), {
            delete: this.uploadForm.get('deleteData')!.value,
          })
          .subscribe(() => {
            this.resetUploadFormSuccess();
          });
      }
    }
  }

  uploadByUrl(): void {
    if (this.product?.id) {
      this.productService
        .uploadByUrl(
          this.product.id,
          {
            username: this.uploadForm.get('username')!.value,
            password: this.uploadForm.get('password')!.value,
          },
          {
            url: this.uploadForm.get('url')!.value,
            delete: this.uploadForm.get('deleteData')!.value,
          }
        )
        .subscribe({
          next: () => {
            this.resetUploadFormSuccess();
          },
          error: () => {
            this.resetUploadFormError();
          },
        });
    }
  }

  addLibrariesToProduct(): void {
    if (this.product?.id) {
      this.productService.addLibrary(this.product.id, this.addLibraryForm.get('libraries')!.value ?? []).subscribe(() => {
        this.modalService.dismissAll();
        this.loadPage(1);
      });
    }
  }

  hasErrors(library?: ILibrary | null): boolean | null {
    if (library) {
      return this.libraryService.hasErrors(library);
    }

    return null;
  }

  onLibraryTableChanges(): void {
    this.tableForm.valueChanges.subscribe(() => {
      this.saveLocalStorage();
    });
  }

  filterRisk(): void {
    if (this.product?.lastUpdatedDate) {
      this.isLoading = true;
      const licenseRiskFilter = this.createLicenseRiskFilter();

      this.libraryPerProductService
        .query({
          'productId.equals': this.product.id,
          'artifactId.contains': this.searchForm.get('artifactId')?.value ?? null,
          'licensesShortIdentifier.contains': this.searchForm.get('licenses')?.value ?? null,
          'libraryRiskId.in': licenseRiskFilter?.join(',') ?? null,
          'errorLogMessage.contains': this.filterForm.get('licenseConflicts')!.value ? 'License Conflict' : null,
          'errorLogStatus.notEquals': this.filterForm.get('licenseConflicts')!.value ? 'CLOSED' : null,
          'libraryCreatedDate.greaterThanOrEqual': this.filterForm.get('newLibrariesSinceLastUpload')!.value
            ? this.product.lastUpdatedDate.format(DATE_FORMAT)
            : null,
          sort: this.sort(),
          page: 0,
          size: this.itemsPerPage,
        })
        .subscribe(
          (res: HttpResponse<ILibraryPerProduct[]>) => {
            this.isLoading = false;
            this.onLLPSuccess(res.body, res.headers, 1, true);
          },
          () => {
            this.isLoading = false;
            this.onError();
          }
        );
    }
  }

  search(): void {
    if (this.product?.lastUpdatedDate) {
      this.isLoading = true;
      const riskFilter = this.createLicenseRiskFilter();

      this.libraryPerProductService
        .query({
          'productId.equals': this.product.id,
          'artifactId.contains': this.searchForm.get('artifactId')?.value ?? null,
          'licensesShortIdentifier.contains': this.searchForm.get('licenses')?.value ?? null,
          'libraryRiskId.in': riskFilter?.join(',') ?? null,
          'errorLogMessage.contains': this.filterForm.get('licenseConflicts')!.value ? 'License Conflict' : null,
          'errorLogStatus.notEquals': this.filterForm.get('licenseConflicts')!.value ? 'CLOSED' : null,
          'libraryCreatedDate.greaterThanOrEqual': this.filterForm.get('newLibrariesSinceLastUpload')!.value
            ? this.product.lastUpdatedDate.format(DATE_FORMAT)
            : null,
          sort: this.sort(),
          page: 0,
          size: this.itemsPerPage,
        })
        .subscribe(
          (res: HttpResponse<ILibraryPerProduct[]>) => {
            this.isLoading = false;
            this.onLLPSuccess(res.body, res.headers, 1, true);
          },
          () => {
            this.isLoading = false;
            this.onError();
          }
        );
    }
  }

  searchAddLibrary(): void {
    this.isLoadingAddSearch = true;

    this.libraryService
      .query({
        'groupId.contains': this.addLibraryForm.get('groupId')?.value ?? null,
        'artifactId.contains': this.addLibraryForm.get('artifactId')?.value ?? null,
        'version.contains': this.addLibraryForm.get('version')?.value ?? null,
        page: 0,
        size: this.itemsPerPage,
      })
      .subscribe(
        (res: HttpResponse<ILibrary[]>) => {
          this.isLoadingAddSearch = false;
          this.addSearchLibraries = res.body ?? [];
        },
        () => {
          this.isLoadingAddSearch = false;
        }
      );
  }

  addLibraryToAddLibraryForm(library: ILibrary): void {
    let libraries = this.addLibraryForm.get('libraries')!.value;
    if (!libraries) {
      libraries = [];
    }
    libraries.push(library);
    this.addLibraryForm.get('libraries')!.setValue(libraries);
  }

  removeLibraryFromAddLibraryForm(index: number): void {
    const libraries = this.addLibraryForm.get('libraries')!.value;
    if (libraries) {
      libraries.splice(index, 1);
    }
  }

  createNextProduct(): void {
    if (this.product?.id) {
      this.productService
        .createNextVersion(this.product.id, {
          version: this.nextProductForm.get('version')!.value,
          delivered: this.nextProductForm.get('delivered')!.value ?? null,
          copy: this.nextProductForm.get('copy')!.value ?? null,
        })
        .subscribe((res: HttpResponse<IProduct>) => {
          this.modalService.dismissAll();
          this.router.navigate(['/product', res.body?.id, 'view']);
        });
    }
  }

  delete(libraryPerProduct: ILibraryPerProduct): void {
    const modalRef = this.modalService.open(LibraryPerProductDeleteDialogCustomComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.libraryPerProduct = libraryPerProduct;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadPage();
      }
    });
  }

  filter(): void {
    if (this.product?.lastUpdatedDate) {
      this.isLoading = true;
      const riskFilter = this.createLicenseRiskFilter();

      this.libraryPerProductService
        .query({
          'productId.equals': this.product.id,
          'artifactId.contains': this.searchForm.get('artifactId')?.value ?? null,
          'licensesShortIdentifier.contains': this.searchForm.get('licenses')?.value ?? null,
          'libraryRiskId.in': riskFilter?.join(',') ?? null,
          'errorLogMessage.contains': this.filterForm.get('licenseConflicts')!.value ? 'License Conflict' : null,
          'errorLogStatus.notEquals': this.filterForm.get('licenseConflicts')!.value ? 'CLOSED' : null,
          'libraryCreatedDate.greaterThanOrEqual': this.filterForm.get('newLibrariesSinceLastUpload')!.value
            ? this.product.lastUpdatedDate.format(DATE_FORMAT)
            : null,
          sort: this.sort(),
          page: 0,
          size: this.itemsPerPage,
        })
        .subscribe(
          (res: HttpResponse<ILibraryPerProduct[]>) => {
            this.isLoading = false;
            this.onLLPSuccess(res.body, res.headers, 1, true);
          },
          () => {
            this.isLoading = false;
            this.onError();
          }
        );
    }
  }

  saveLocalStorage(): void {
    const tableFormSettings = {
      groupId: this.tableForm.get(['groupId'])!.value,
      artifactId: this.tableForm.get(['artifactId'])!.value,
      version: this.tableForm.get(['version'])!.value,
      type: this.tableForm.get(['type'])!.value,
      licenses: this.tableForm.get(['licenses'])!.value,
      licenseToPublish: this.tableForm.get(['licenseToPublish'])!.value,
      risk: this.tableForm.get(['risk'])!.value,
      licenseUrl: this.tableForm.get(['licenseUrl'])!.value,
      licenseText: this.tableForm.get(['licenseText'])!.value,
      sourceCodeUrl: this.tableForm.get(['sourceCodeUrl'])!.value,
      copyright: this.tableForm.get(['copyright'])!.value,
      reviewed: this.tableForm.get(['reviewed'])!.value,
      hideForPublishing: this.tableForm.get(['hideForPublishing'])!.value,
      localComment: this.tableForm.get(['localComment'])!.value,
      errorLog: this.tableForm.get(['errorLog'])!.value,
      manuallyAdded: this.tableForm.get(['manuallyAdded'])!.value,
    };

    localStorage.setItem('product.libraryTableSettings', JSON.stringify(tableFormSettings));
  }

  loadLocalStorage(): void {
    const productLibraryTableSettings = localStorage.getItem('product.libraryTableSettings');

    if (productLibraryTableSettings) {
      const tableFormSettings = JSON.parse(productLibraryTableSettings);

      this.tableForm.patchValue({
        groupId: tableFormSettings.groupId,
        artifactId: tableFormSettings.artifactId,
        version: tableFormSettings.version,
        type: tableFormSettings.type,
        licenses: tableFormSettings.licenses,
        licenseToPublish: tableFormSettings.licenseToPublish,
        risk: tableFormSettings.risk,
        licenseUrl: tableFormSettings.licenseUrl,
        licenseText: tableFormSettings.licenseText,
        sourceCodeUrl: tableFormSettings.sourceCodeUrl,
        copyright: tableFormSettings.copyright,
        reviewed: tableFormSettings.reviewed,
        hideForPublishing: tableFormSettings.hideForPublishing,
        localComment: tableFormSettings.localComment,
        errorLog: tableFormSettings.errorLog,
        manuallyAdded: tableFormSettings.manuallyAdded,
      });
    }
  }

  dismiss(reason?: string): void {
    this.offcanvasService.dismiss(reason);
  }

  save(): void {
    this.isSaving = true;
    const libraryPerProduct = this.createFromForm();
    this.subscribeToSaveResponse(this.libraryPerProductService.partialUpdate(libraryPerProduct));
  }

  navTabChange(event: any): void {
    if (event.nextId === 'Statistics' && !this.statistic && this.product?.id) {
      this.productService.statistic(this.product.id).subscribe((res: HttpResponse<IProductStatistic>) => {
        this.statistic = res.body;
      });
    }
  }

  protected resetUploadFormSuccess(): void {
    this.modalService.dismissAll();
    this.uploadForm.get('url')!.setValue(null);
    this.uploadForm.get('file')!.setValue(null);
    this.uploadForm.get('fileContentType')!.setValue(null);
    this.uploadForm.get('fileName')!.setValue(null);
    this.uploadForm.get('additionalLibraries')!.setValue(null);
    this.uploadForm.get('additionalLibrariesContentType')!.setValue(null);
    this.uploadForm.get('additionalLibrariesFileName')!.setValue(null);
    this.uploadForm.get('deleteData')!.setValue(true);
    this.loadPage(1);
  }

  protected resetUploadFormError(): void {
    this.modalService.dismissAll();
    this.uploadForm.get('url')!.setValue(null);
    this.uploadForm.get('username')!.setValue(null);
    this.uploadForm.get('password')!.setValue(null);
    this.uploadForm.get('file')!.setValue(null);
    this.uploadForm.get('fileContentType')!.setValue(null);
    this.uploadForm.get('fileName')!.setValue(null);
    this.uploadForm.get('additionalLibraries')!.setValue(null);
    this.uploadForm.get('additionalLibrariesContentType')!.setValue(null);
    this.uploadForm.get('additionalLibrariesFileName')!.setValue(null);
    this.uploadForm.get('deleteData')!.setValue(true);
  }

  protected updateForm(libraryPerProduct: ILibraryPerProduct): void {
    this.libraryPerProductForm.patchValue({
      hide: libraryPerProduct.hideForPublishing,
      addedManually: libraryPerProduct.addedManually,
      localComment: libraryPerProduct.comment,
    });
  }

  protected createFromForm(): ILibraryPerProduct {
    return {
      ...new LibraryPerProduct(),
      id: this.libraryPerProduct?.id,
      hideForPublishing: this.libraryPerProductForm.get(['hide'])!.value,
      comment: this.libraryPerProductForm.get(['localComment'])!.value,
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ILibraryPerProduct>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: (lib: HttpResponse<ILibraryPerProduct>) => this.onSaveSuccess(lib.body!),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected onSaveSuccess(library: ILibraryPerProduct): void {
    this.saved = true;
    this.findLibraryPerProductInList(library);
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected createLicenseRiskFilter(): string[] | null {
    const filter = Object.keys(this.riskForm.controls).filter(key => this.riskForm.get(key)?.value === true);

    if (filter.length === 7) {
      return null;
    }

    return filter;
  }

  protected handleNavigation(): void {
    combineLatest([this.activatedRoute.data, this.activatedRoute.queryParamMap]).subscribe(([data, params]) => {
      const page = params.get('page');
      const pageNumber = +(page ?? 1);
      const sort = (params.get(SORT) ?? data['defaultSort']).split(',');
      const predicate = sort[0];
      const ascending = sort[1] === ASC;

      const artifactId = params.get('artifactId');
      const licensesShortIdentifier = params.get('licenses');

      const licenseConflicts = params.get('licenseConflicts');
      const newLibraries = params.get('newLibraries');

      this.searchForm.get('artifactId')?.setValue(artifactId);
      this.searchForm.get('licenses')?.setValue(licensesShortIdentifier);

      if (params.get('libraryRiskId')) {
        const libraryRiskId = params.get('libraryRiskId')?.split(',');

        Object.keys(this.riskForm.controls).forEach(key => {
          if (libraryRiskId?.includes(key)) {
            this.riskForm.get(key)?.setValue(true);
          } else {
            this.riskForm.get(key)?.setValue(false);
          }
        });
      }

      if (licenseConflicts === 'true') {
        this.filterForm.get('licenseConflicts')!.setValue(true);
      } else {
        this.filterForm.get('licenseConflicts')!.setValue(false);
      }
      if (newLibraries === 'true') {
        this.filterForm.get('newLibrariesSinceLastUpload')!.setValue(true);
      } else {
        this.filterForm.get('newLibrariesSinceLastUpload')!.setValue(false);
      }

      if (pageNumber !== this.page || predicate !== this.predicate || ascending !== this.ascending) {
        this.predicate = predicate;
        this.ascending = ascending;
        this.loadPage(pageNumber, true);
      }
    });
  }

  protected sort(): string[] {
    const result = [this.predicate + ',' + (this.ascending ? ASC : DESC)];
    if (this.predicate !== 'library.artifactId') {
      result.push('library.artifactId');
    }
    return result;
  }

  protected onProductSuccess(data: IProduct | null): void {
    this.product = data ?? null;
    this.uploadStateType = this.product?.uploadState ?? UploadState.SUCCESSFUL;
  }

  protected onLLPSuccess(data: ILibraryPerProduct[] | null, headers: HttpHeaders, page: number, navigate: boolean): void {
    this.totalItems = Number(headers.get('X-Total-Count'));
    this.page = page;

    const riskFilter = this.createLicenseRiskFilter();

    if (navigate) {
      this.router.navigate([`/product/${this.product!.id!}/view`], {
        queryParams: {
          artifactId: this.searchForm.get('artifactId')?.value ?? null,
          licenses: this.searchForm.get('licenses')?.value ?? null,
          libraryRiskId: riskFilter?.join(',') ?? null,
          licenseConflicts: this.filterForm.get('licenseConflicts')!.value ?? null,
          newLibraries: this.filterForm.get('newLibrariesSinceLastUpload')!.value ?? null,
          page: this.page,
          size: this.itemsPerPage,
          sort: this.predicate + ',' + (this.ascending ? ASC : DESC),
        },
      });
    }

    this.librariesPerProduct = data;
    this.ngbPaginationPage = this.page;
  }

  protected onError(): void {
    this.ngbPaginationPage = this.page ?? 1;
  }
}
