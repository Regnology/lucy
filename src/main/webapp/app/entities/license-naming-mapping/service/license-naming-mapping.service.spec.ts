import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ILicenseNamingMapping, LicenseNamingMapping } from '../license-naming-mapping.model';

import { LicenseNamingMappingService } from './license-naming-mapping.service';

describe('LicenseNamingMapping Service', () => {
  let service: LicenseNamingMappingService;
  let httpMock: HttpTestingController;
  let elemDefault: ILicenseNamingMapping;
  let expectedResult: ILicenseNamingMapping | ILicenseNamingMapping[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(LicenseNamingMappingService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      regex: 'AAAAAAA',
      uniformShortIdentifier: 'AAAAAAA',
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

    it('should create a LicenseNamingMapping', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new LicenseNamingMapping()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a LicenseNamingMapping', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          regex: 'BBBBBB',
          uniformShortIdentifier: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a LicenseNamingMapping', () => {
      const patchObject = Object.assign({}, new LicenseNamingMapping());

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of LicenseNamingMapping', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          regex: 'BBBBBB',
          uniformShortIdentifier: 'BBBBBB',
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

    it('should delete a LicenseNamingMapping', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addLicenseNamingMappingToCollectionIfMissing', () => {
      it('should add a LicenseNamingMapping to an empty array', () => {
        const licenseNamingMapping: ILicenseNamingMapping = { id: 123 };
        expectedResult = service.addLicenseNamingMappingToCollectionIfMissing([], licenseNamingMapping);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(licenseNamingMapping);
      });

      it('should not add a LicenseNamingMapping to an array that contains it', () => {
        const licenseNamingMapping: ILicenseNamingMapping = { id: 123 };
        const licenseNamingMappingCollection: ILicenseNamingMapping[] = [
          {
            ...licenseNamingMapping,
          },
          { id: 456 },
        ];
        expectedResult = service.addLicenseNamingMappingToCollectionIfMissing(licenseNamingMappingCollection, licenseNamingMapping);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a LicenseNamingMapping to an array that doesn't contain it", () => {
        const licenseNamingMapping: ILicenseNamingMapping = { id: 123 };
        const licenseNamingMappingCollection: ILicenseNamingMapping[] = [{ id: 456 }];
        expectedResult = service.addLicenseNamingMappingToCollectionIfMissing(licenseNamingMappingCollection, licenseNamingMapping);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(licenseNamingMapping);
      });

      it('should add only unique LicenseNamingMapping to an array', () => {
        const licenseNamingMappingArray: ILicenseNamingMapping[] = [{ id: 123 }, { id: 456 }, { id: 22186 }];
        const licenseNamingMappingCollection: ILicenseNamingMapping[] = [{ id: 123 }];
        expectedResult = service.addLicenseNamingMappingToCollectionIfMissing(licenseNamingMappingCollection, ...licenseNamingMappingArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const licenseNamingMapping: ILicenseNamingMapping = { id: 123 };
        const licenseNamingMapping2: ILicenseNamingMapping = { id: 456 };
        expectedResult = service.addLicenseNamingMappingToCollectionIfMissing([], licenseNamingMapping, licenseNamingMapping2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(licenseNamingMapping);
        expect(expectedResult).toContain(licenseNamingMapping2);
      });

      it('should accept null and undefined values', () => {
        const licenseNamingMapping: ILicenseNamingMapping = { id: 123 };
        expectedResult = service.addLicenseNamingMappingToCollectionIfMissing([], null, licenseNamingMapping, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(licenseNamingMapping);
      });

      it('should return initial array if no LicenseNamingMapping is added', () => {
        const licenseNamingMappingCollection: ILicenseNamingMapping[] = [{ id: 123 }];
        expectedResult = service.addLicenseNamingMappingToCollectionIfMissing(licenseNamingMappingCollection, undefined, null);
        expect(expectedResult).toEqual(licenseNamingMappingCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
