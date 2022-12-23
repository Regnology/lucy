import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { LinkType } from 'app/entities/enumerations/link-type.model';
import { ILicensePerLibrary, LicensePerLibrary } from '../license-per-library.model';

import { LicensePerLibraryService } from './license-per-library.service';

describe('LicensePerLibrary Service', () => {
  let service: LicensePerLibraryService;
  let httpMock: HttpTestingController;
  let elemDefault: ILicensePerLibrary;
  let expectedResult: ILicensePerLibrary | ILicensePerLibrary[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(LicensePerLibraryService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      linkType: LinkType.AND,
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign({}, elemDefault);

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a LicensePerLibrary', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new LicensePerLibrary()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a LicensePerLibrary', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          linkType: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a LicensePerLibrary', () => {
      const patchObject = Object.assign(
        {
          linkType: 'BBBBBB',
        },
        new LicensePerLibrary()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of LicensePerLibrary', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          linkType: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a LicensePerLibrary', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addLicensePerLibraryToCollectionIfMissing', () => {
      it('should add a LicensePerLibrary to an empty array', () => {
        const licensePerLibrary: ILicensePerLibrary = { id: 123 };
        expectedResult = service.addLicensePerLibraryToCollectionIfMissing([], licensePerLibrary);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(licensePerLibrary);
      });

      it('should not add a LicensePerLibrary to an array that contains it', () => {
        const licensePerLibrary: ILicensePerLibrary = { id: 123 };
        const licensePerLibraryCollection: ILicensePerLibrary[] = [
          {
            ...licensePerLibrary,
          },
          { id: 456 },
        ];
        expectedResult = service.addLicensePerLibraryToCollectionIfMissing(licensePerLibraryCollection, licensePerLibrary);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a LicensePerLibrary to an array that doesn't contain it", () => {
        const licensePerLibrary: ILicensePerLibrary = { id: 123 };
        const licensePerLibraryCollection: ILicensePerLibrary[] = [{ id: 456 }];
        expectedResult = service.addLicensePerLibraryToCollectionIfMissing(licensePerLibraryCollection, licensePerLibrary);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(licensePerLibrary);
      });

      it('should add only unique LicensePerLibrary to an array', () => {
        const licensePerLibraryArray: ILicensePerLibrary[] = [{ id: 123 }, { id: 456 }, { id: 15864 }];
        const licensePerLibraryCollection: ILicensePerLibrary[] = [{ id: 123 }];
        expectedResult = service.addLicensePerLibraryToCollectionIfMissing(licensePerLibraryCollection, ...licensePerLibraryArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const licensePerLibrary: ILicensePerLibrary = { id: 123 };
        const licensePerLibrary2: ILicensePerLibrary = { id: 456 };
        expectedResult = service.addLicensePerLibraryToCollectionIfMissing([], licensePerLibrary, licensePerLibrary2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(licensePerLibrary);
        expect(expectedResult).toContain(licensePerLibrary2);
      });

      it('should accept null and undefined values', () => {
        const licensePerLibrary: ILicensePerLibrary = { id: 123 };
        expectedResult = service.addLicensePerLibraryToCollectionIfMissing([], null, licensePerLibrary, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(licensePerLibrary);
      });

      it('should return initial array if no LicensePerLibrary is added', () => {
        const licensePerLibraryCollection: ILicensePerLibrary[] = [{ id: 123 }];
        expectedResult = service.addLicensePerLibraryToCollectionIfMissing(licensePerLibraryCollection, undefined, null);
        expect(expectedResult).toEqual(licensePerLibraryCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
