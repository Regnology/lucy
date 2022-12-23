import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'library',
        data: { pageTitle: 'Libraries' },
        loadChildren: () => import('./library/library-custom.module').then(m => m.LibraryCustomModule),
      },
      {
        path: 'license',
        data: { pageTitle: 'Licenses' },
        loadChildren: () => import('./license/license-custom.module').then(m => m.LicenseCustomModule),
      },
      {
        path: 'license-risk',
        data: { pageTitle: 'LicenseRisks' },
        loadChildren: () => import('./license-risk/license-risk-custom.module').then(m => m.LicenseRiskCustomModule),
      },
      {
        path: 'requirement',
        data: { pageTitle: 'Requirements' },
        loadChildren: () => import('./requirement/requirement-custom.module').then(m => m.RequirementCustomModule),
      },
      {
        path: 'product',
        data: { pageTitle: 'Products' },
        loadChildren: () => import('./product/product-custom.module').then(m => m.ProductCustomModule),
      },
      {
        path: 'library-per-product',
        data: {
          authorities: ['ROLE_USER', 'ROLE_ADMIN'],
          pageTitle: 'LibraryPerProducts',
        },
        loadChildren: () => import('./library-per-product/library-per-product-custom.module').then(m => m.LibraryPerProductCustomModule),
      },
      {
        path: 'license-naming-mapping',
        data: {
          authorities: ['ROLE_USER', 'ROLE_ADMIN'],
          pageTitle: 'LicenseNamingMappings',
        },
        loadChildren: () =>
          import('./license-naming-mapping/license-naming-mapping-custom.module').then(m => m.LicenseNamingMappingCustomModule),
      },
      {
        path: 'generic-license-url',
        data: {
          authorities: ['ROLE_USER', 'ROLE_ADMIN'],
          pageTitle: 'GenericLicenseUrls',
        },
        loadChildren: () => import('./generic-license-url/generic-license-url-custom.module').then(m => m.GenericLicenseUrlCustomModule),
      },
      {
        path: 'upload',
        data: {
          authorities: ['ROLE_USER', 'ROLE_ADMIN'],
          pageTitle: 'Uploads',
        },
        loadChildren: () => import('./upload/upload-custom.module').then(m => m.UploadCustomModule),
      },
      {
        path: 'license-per-library',
        data: {
          authorities: ['ROLE_USER', 'ROLE_ADMIN'],
          pageTitle: 'LicensePerLibraries',
        },
        loadChildren: () => import('./license-per-library/license-per-library-custom.module').then(m => m.LicensePerLibraryCustomModule),
      },
      {
        path: 'library-error-log',
        data: {
          authorities: ['ROLE_USER', 'ROLE_ADMIN'],
          pageTitle: 'LibraryErrorLogs',
        },
        loadChildren: () => import('./library-error-log/library-error-log-custom.module').then(m => m.LibraryErrorLogCustomModule),
      },
    ]),
  ],
})
export class EntityRoutingCustomModule {}
