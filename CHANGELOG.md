# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project adheres
to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.6.3] - 2022-12-23

### Added

- Upload to products by URL. Lucy downloads a specified URL and processes the file with the corresponding asset loader.
- Added "application/octet-stream" type for the archive loader.
- Automatic search for Maven licenses on central.sonatype.dev.
- Automatic search for NPM licenses on registry.npmjs.org.

### Changed

- Make Lucy ready for release on Github!
- Rewritten archive loader to process a file from an input stream (upload by URL).
  Files larger than 4 GB can be processed now (BETA).
- Set <validCheckSum> tag for some Liquibase changelogs.
- Disable license incompatibility check for libraries (faster algorithm necessary).

### Fixed

- Fix for broken npm build.
- Fix for queries returning non unique hashes of libraries.
- Fix upload for files with licenses in JSON and CSV format.
- Fix search field for requirements in license view.

### Removed

- Removed unused Spring Cloud dependency

## [0.6.2] - 2022-09-16

### Added

- New license conflict table. For every license the compatibility to every other license can be defined.
  If a library has incompatible license combinations then it will be logged in the library.
- New filter in product detail view for libraries with license conflicts.
- New filter in product detail view for new libraries after an upload.

### Changed

- JHipster upgrade to version 7.9.3.
- The statistics in the product detail view are only loaded when you switch to the statistics tab
  (perfomance improvement).
- When loading lists, such as licenses or license conflicts, not all attributes of the entities are loaded,
  but a minimized variant.

## [0.6.1] - 2022-07-29

### Added

- New license risk "Proprietary Free".

### Changed

- Removed license risk "Unlicensed". Libraries without license information will get the "Unknown" license risk.
- Bugfix for colors/order of license risk filters in the product detail view.

## [0.6.0] - 2022-07-25

### Added

- Difference view between two products. It's possible to compare two products and see the results of the differences.

## [0.5.3] - 2022-06-02

### Added

- Fields for deep scan reviews. A library contains now the fields reviewedDeepScan, lastReviewedDeepScanDate and
  lastReviewedDeepScanBy.
- Added functionality to hide and add a comment for a library in a specific product.

### Changed

- The bug that occurred when deleting products with included libraries has been fixed.
  Products with libraries can now be deleted.
- Upgrade to Angular 14 and other dependency upgrades.

## [0.5.2.1] - 2022-05-10

### Changed

- Removed the asynchronous process again, since this was only necessary for a one-time update.

## [0.5.2] - 2022-05-10

### Changed

- Bugfix for the error when saving libraries with many licenses. Changed the data type for the licenses attribute in the
  library domain from Set (LinkedHashSet) to SortedSet (TreeSet) because this Set didn't retain the order of the elements.
  Also added a new attribute for the LicensePerLibrary entity that persists the order of the licenses.

  **(!!) With this change all libraries must be saved again with the order ID for every license.
  After startup, an asynchronous process will update each library with the order ID.**

## [0.5.1] - 2022-05-09

### Changed

- Bugfix for the Fossology integration. The Fossology scan information disappeared after editing and saving the library.
- Bugfix for the license search field in the product detail view. Search fields works again.
- Bugfix for the upload status of a product. Switch to "Processing" after the upload works again.
- Front end fix for the statistic cards at top of the product detail view. Absolute number and percentage change are in
  one line again.
- Front end fix for login, registration, user management etc. API calls were made to the old endpoint path
  (/api/_ and not /api/v1/_)

## [0.6.0] - 2022-05-04

### Added

- New difference view to compare two products. Shows same, additional and missing libraries between the compared products.

### Changed

- Bugfix for the upload status. Switch to "Processing" works again.
- Bugfix for the deletion of products. Products with libraries can be deleted now.

## [0.3.0, 0.4.0 and 0.5.0] - 2022-05-03

### Added

- MD5 and SHA-1 hash fields for libraries.
- Copyright analysis from source code or / and license texts. Triggered automatically or / and manually.
- Sort / search / filter the library list in the product detail view.
- The currently selected page / sort / search / filter, in the product detail view, is retained when the page is
  changed. Thus, these entries do not have to be re-entered when switching back to the page.
- Upload to products of JAR, WAR, ZIP, TAR and other archive files and auto-detection of libraries in it (detects only Maven libraries).
- Errors can be logged for libraries. These errors are visible in the front end.
- Search fields for products, libraries and licenses.
- Created new user role "READONLY".
- Statistics for products. Shows different charts for a product and the progression over multiple versions.
- New fuzzy search to find a source code archive for a library in the external configured storage.
- Add upload filter for products. It removes libraries from the upload that matches the upload filter.
  Regular expressions can be used.
- New API endpoint to get the product with state "In Development".
- Create new product based on a previous version.
- Start scans from Lucy for Fossology.
- Start using changelog!

### Changed

- Newly registered users automatically receive the READONLY role.
- Authorisation adjusted for all entities in front end and back end. READONLY role can only view specific pages.
- Duplicate libraries in uploads will be automatically removed.
- JHipster upgrade to version 7.8.1.
- Derived generated classes by JHipster.
- Back end optimizations and performance improvements.
- Front end improvements.
- Several bugfixes.

### Removed

- LicenseZip column in the product table.
- License column with relation from the library table.
