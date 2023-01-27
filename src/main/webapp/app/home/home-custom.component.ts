import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { HttpResponse } from '@angular/common/http';

import { AccountService } from 'app/core/auth/account.service';

import { IProduct } from 'app/entities/product/product.model';

import { ILibrary } from 'app/entities/library/library.model';

import { ILicense } from 'app/entities/license/license.model';

import { HomeComponent } from 'app/home/home.component';
import { ProductCustomService } from 'app/entities/product/service/product-custom.service';
import { LicenseCustomService } from 'app/entities/license/service/license-custom.service';
import { LibraryCustomService } from 'app/entities/library/service/library-custom.service';
import { DESC } from '../config/pagination.constants';

@Component({
  selector: 'jhi-home-custom',
  templateUrl: './home-custom.component.html',
  styleUrls: ['./home.component.scss'],
})
export class HomeCustomComponent extends HomeComponent implements OnInit {
  products?: IProduct[];
  libraries?: ILibrary[];
  licenses?: ILicense[];

  constructor(
    protected accountService: AccountService,
    protected router: Router,
    protected productService: ProductCustomService,
    protected libraryService: LibraryCustomService,
    protected licenseService: LicenseCustomService
  ) {
    super(accountService, router);
  }

  loadPage(): void {
    const pageToLoad = 0;
    const pageSize = 3;

    this.productService
      .query({
        'lastUpdatedDate.specified': true, // show only products where the last lastUpdatedDate field is not null
        page: pageToLoad,
        size: pageSize,
        sort: ['lastUpdatedDate' + ',' + DESC, 'name'], // sort first by lastUpdatedDate and than by name
      })
      .subscribe((res: HttpResponse<IProduct[]>) => {
        this.products = res.body ?? [];
      });

    this.libraryService
      .query({
        page: pageToLoad,
        size: pageSize,
        sort: ['createdDate' + ',' + DESC, 'artifactId'], // sort first by createdDate and than by artifactId
      })
      .subscribe((res: HttpResponse<ILibrary[]>) => {
        this.libraries = res.body ?? [];
      });
    this.licenseService
      .query({
        'reviewed.equals': true, // show only reviewed licenses
        page: pageToLoad,
        size: pageSize,
        sort: ['lastReviewedDate' + ',' + DESC, 'fullName'], // sort first by reviewedDate and than by fullName
      })
      .subscribe((res: HttpResponse<ILibrary[]>) => {
        this.licenses = res.body ?? [];
      });
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.loadPage();
  }
}
