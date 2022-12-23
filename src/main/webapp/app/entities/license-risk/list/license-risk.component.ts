import { Component, OnInit } from '@angular/core';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ILicenseRisk } from '../license-risk.model';

import { ASC, DESC, ITEMS_PER_PAGE } from 'app/config/pagination.constants';
import { LicenseRiskService } from '../service/license-risk.service';
import { LicenseRiskDeleteDialogComponent } from '../delete/license-risk-delete-dialog.component';
import { ParseLinks } from 'app/core/util/parse-links.service';

@Component({
  selector: 'jhi-license-risk',
  templateUrl: './license-risk.component.html',
})
export class LicenseRiskComponent implements OnInit {
  licenseRisks: ILicenseRisk[];
  isLoading = false;
  itemsPerPage: number;
  links: { [key: string]: number };
  page: number;
  predicate: string;
  ascending: boolean;

  constructor(protected licenseRiskService: LicenseRiskService, protected modalService: NgbModal, protected parseLinks: ParseLinks) {
    this.licenseRisks = [];
    this.itemsPerPage = ITEMS_PER_PAGE;
    this.page = 0;
    this.links = {
      last: 0,
    };
    this.predicate = 'id';
    this.ascending = true;
  }

  loadAll(): void {
    this.isLoading = true;

    this.licenseRiskService
      .query({
        page: this.page,
        size: this.itemsPerPage,
        sort: this.sort(),
      })
      .subscribe({
        next: (res: HttpResponse<ILicenseRisk[]>) => {
          this.isLoading = false;
          this.paginateLicenseRisks(res.body, res.headers);
        },
        error: () => {
          this.isLoading = false;
        },
      });
  }

  reset(): void {
    this.page = 0;
    this.licenseRisks = [];
    this.loadAll();
  }

  loadPage(page: number): void {
    this.page = page;
    this.loadAll();
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: ILicenseRisk): number {
    return item.id!;
  }

  delete(licenseRisk: ILicenseRisk): void {
    const modalRef = this.modalService.open(LicenseRiskDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.licenseRisk = licenseRisk;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.reset();
      }
    });
  }

  protected sort(): string[] {
    const result = [this.predicate + ',' + (this.ascending ? ASC : DESC)];
    if (this.predicate !== 'id') {
      result.push('id');
    }
    return result;
  }

  protected paginateLicenseRisks(data: ILicenseRisk[] | null, headers: HttpHeaders): void {
    const linkHeader = headers.get('link');
    if (linkHeader) {
      this.links = this.parseLinks.parse(linkHeader);
    } else {
      this.links = {
        last: 0,
      };
    }
    if (data) {
      for (const d of data) {
        this.licenseRisks.push(d);
      }
    }
  }
}
