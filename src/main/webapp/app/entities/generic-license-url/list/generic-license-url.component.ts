import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IGenericLicenseUrl } from '../generic-license-url.model';
import { GenericLicenseUrlService } from '../service/generic-license-url.service';
import { GenericLicenseUrlDeleteDialogComponent } from '../delete/generic-license-url-delete-dialog.component';

@Component({
  selector: 'jhi-generic-license-url',
  templateUrl: './generic-license-url.component.html',
})
export class GenericLicenseUrlComponent implements OnInit {
  genericLicenseUrls?: IGenericLicenseUrl[];
  isLoading = false;

  constructor(protected genericLicenseUrlService: GenericLicenseUrlService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.genericLicenseUrlService.query().subscribe({
      next: (res: HttpResponse<IGenericLicenseUrl[]>) => {
        this.isLoading = false;
        this.genericLicenseUrls = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: IGenericLicenseUrl): number {
    return item.id!;
  }

  delete(genericLicenseUrl: IGenericLicenseUrl): void {
    const modalRef = this.modalService.open(GenericLicenseUrlDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.genericLicenseUrl = genericLicenseUrl;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
