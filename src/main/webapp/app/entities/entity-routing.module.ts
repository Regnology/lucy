import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'license',
        data: { pageTitle: 'Licenses' },
        loadChildren: () => import('./license/license.module').then(m => m.LicenseModule),
      },
      {
        path: 'license-risk',
        data: { pageTitle: 'LicenseRisks' },
        loadChildren: () => import('./license-risk/license-risk.module').then(m => m.LicenseRiskModule),
      },
      {
        path: 'requirement',
        data: { pageTitle: 'Requirements' },
        loadChildren: () => import('./requirement/requirement.module').then(m => m.RequirementModule),
      },
      {
        path: 'product',
        data: { pageTitle: 'Products' },
        loadChildren: () => import('./product/product.module').then(m => m.ProductModule),
      },
      {
        path: 'library-per-product',
        data: { pageTitle: 'LibraryPerProducts' },
        loadChildren: () => import('./library-per-product/library-per-product.module').then(m => m.LibraryPerProductModule),
      },
      {
        path: 'license-naming-mapping',
        data: { pageTitle: 'LicenseNamingMappings' },
        loadChildren: () => import('./license-naming-mapping/license-naming-mapping.module').then(m => m.LicenseNamingMappingModule),
      },
      {
        path: 'generic-license-url',
        data: { pageTitle: 'GenericLicenseUrls' },
        loadChildren: () => import('./generic-license-url/generic-license-url.module').then(m => m.GenericLicenseUrlModule),
      },
      {
        path: 'upload',
        data: { pageTitle: 'Uploads' },
        loadChildren: () => import('./upload/upload.module').then(m => m.UploadModule),
      },
      {
        path: 'license-per-library',
        data: { pageTitle: 'LicensePerLibraries' },
        loadChildren: () => import('./license-per-library/license-per-library.module').then(m => m.LicensePerLibraryModule),
      },
      {
        path: 'library',
        data: { pageTitle: 'Libraries' },
        loadChildren: () => import('./library/library.module').then(m => m.LibraryModule),
      },
      {
        path: 'library-error-log',
        data: { pageTitle: 'LibraryErrorLogs' },
        loadChildren: () => import('./library-error-log/library-error-log.module').then(m => m.LibraryErrorLogModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
