import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ILicenseRisk, LicenseRisk } from '../license-risk.model';

import { LicenseRiskService } from './license-risk.service';

describe('LicenseRisk Service', () => {
  let service: LicenseRiskService;
  let httpMock: HttpTestingController;
  let elemDefault: ILicenseRisk;
  let expectedResult: ILicenseRisk | ILicenseRisk[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(LicenseRiskService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      name: 'AAAAAAA',
      level: 0,
      description: 'AAAAAAA',
      color: 'AAAAAAA',
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

    it('should create a LicenseRisk', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new LicenseRisk()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a LicenseRisk', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          name: 'BBBBBB',
          level: 1,
          description: 'BBBBBB',
          color: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a LicenseRisk', () => {
      const patchObject = Object.assign(
        {
          name: 'BBBBBB',
          level: 1,
          description: 'BBBBBB',
          color: 'BBBBBB',
        },
        new LicenseRisk()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of LicenseRisk', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          name: 'BBBBBB',
          level: 1,
          description: 'BBBBBB',
          color: 'BBBBBB',
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

    it('should delete a LicenseRisk', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addLicenseRiskToCollectionIfMissing', () => {
      it('should add a LicenseRisk to an empty array', () => {
        const licenseRisk: ILicenseRisk = { id: 123 };
        expectedResult = service.addLicenseRiskToCollectionIfMissing([], licenseRisk);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(licenseRisk);
      });

      it('should not add a LicenseRisk to an array that contains it', () => {
        const licenseRisk: ILicenseRisk = { id: 123 };
        const licenseRiskCollection: ILicenseRisk[] = [
          {
            ...licenseRisk,
          },
          { id: 456 },
        ];
        expectedResult = service.addLicenseRiskToCollectionIfMissing(licenseRiskCollection, licenseRisk);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a LicenseRisk to an array that doesn't contain it", () => {
        const licenseRisk: ILicenseRisk = { id: 123 };
        const licenseRiskCollection: ILicenseRisk[] = [{ id: 456 }];
        expectedResult = service.addLicenseRiskToCollectionIfMissing(licenseRiskCollection, licenseRisk);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(licenseRisk);
      });

      it('should add only unique LicenseRisk to an array', () => {
        const licenseRiskArray: ILicenseRisk[] = [{ id: 123 }, { id: 456 }, { id: 12956 }];
        const licenseRiskCollection: ILicenseRisk[] = [{ id: 123 }];
        expectedResult = service.addLicenseRiskToCollectionIfMissing(licenseRiskCollection, ...licenseRiskArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const licenseRisk: ILicenseRisk = { id: 123 };
        const licenseRisk2: ILicenseRisk = { id: 456 };
        expectedResult = service.addLicenseRiskToCollectionIfMissing([], licenseRisk, licenseRisk2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(licenseRisk);
        expect(expectedResult).toContain(licenseRisk2);
      });

      it('should accept null and undefined values', () => {
        const licenseRisk: ILicenseRisk = { id: 123 };
        expectedResult = service.addLicenseRiskToCollectionIfMissing([], null, licenseRisk, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(licenseRisk);
      });

      it('should return initial array if no LicenseRisk is added', () => {
        const licenseRiskCollection: ILicenseRisk[] = [{ id: 123 }];
        expectedResult = service.addLicenseRiskToCollectionIfMissing(licenseRiskCollection, undefined, null);
        expect(expectedResult).toEqual(licenseRiskCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
