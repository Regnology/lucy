import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IGenericLicenseUrl, GenericLicenseUrl } from '../generic-license-url.model';

import { GenericLicenseUrlService } from './generic-license-url.service';

describe('GenericLicenseUrl Service', () => {
  let service: GenericLicenseUrlService;
  let httpMock: HttpTestingController;
  let elemDefault: IGenericLicenseUrl;
  let expectedResult: IGenericLicenseUrl | IGenericLicenseUrl[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(GenericLicenseUrlService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      url: 'AAAAAAA',
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

    it('should create a GenericLicenseUrl', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new GenericLicenseUrl()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a GenericLicenseUrl', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          url: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a GenericLicenseUrl', () => {
      const patchObject = Object.assign({}, new GenericLicenseUrl());

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of GenericLicenseUrl', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          url: 'BBBBBB',
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

    it('should delete a GenericLicenseUrl', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addGenericLicenseUrlToCollectionIfMissing', () => {
      it('should add a GenericLicenseUrl to an empty array', () => {
        const genericLicenseUrl: IGenericLicenseUrl = { id: 123 };
        expectedResult = service.addGenericLicenseUrlToCollectionIfMissing([], genericLicenseUrl);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(genericLicenseUrl);
      });

      it('should not add a GenericLicenseUrl to an array that contains it', () => {
        const genericLicenseUrl: IGenericLicenseUrl = { id: 123 };
        const genericLicenseUrlCollection: IGenericLicenseUrl[] = [
          {
            ...genericLicenseUrl,
          },
          { id: 456 },
        ];
        expectedResult = service.addGenericLicenseUrlToCollectionIfMissing(genericLicenseUrlCollection, genericLicenseUrl);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a GenericLicenseUrl to an array that doesn't contain it", () => {
        const genericLicenseUrl: IGenericLicenseUrl = { id: 123 };
        const genericLicenseUrlCollection: IGenericLicenseUrl[] = [{ id: 456 }];
        expectedResult = service.addGenericLicenseUrlToCollectionIfMissing(genericLicenseUrlCollection, genericLicenseUrl);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(genericLicenseUrl);
      });

      it('should add only unique GenericLicenseUrl to an array', () => {
        const genericLicenseUrlArray: IGenericLicenseUrl[] = [{ id: 123 }, { id: 456 }, { id: 75585 }];
        const genericLicenseUrlCollection: IGenericLicenseUrl[] = [{ id: 123 }];
        expectedResult = service.addGenericLicenseUrlToCollectionIfMissing(genericLicenseUrlCollection, ...genericLicenseUrlArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const genericLicenseUrl: IGenericLicenseUrl = { id: 123 };
        const genericLicenseUrl2: IGenericLicenseUrl = { id: 456 };
        expectedResult = service.addGenericLicenseUrlToCollectionIfMissing([], genericLicenseUrl, genericLicenseUrl2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(genericLicenseUrl);
        expect(expectedResult).toContain(genericLicenseUrl2);
      });

      it('should accept null and undefined values', () => {
        const genericLicenseUrl: IGenericLicenseUrl = { id: 123 };
        expectedResult = service.addGenericLicenseUrlToCollectionIfMissing([], null, genericLicenseUrl, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(genericLicenseUrl);
      });

      it('should return initial array if no GenericLicenseUrl is added', () => {
        const genericLicenseUrlCollection: IGenericLicenseUrl[] = [{ id: 123 }];
        expectedResult = service.addGenericLicenseUrlToCollectionIfMissing(genericLicenseUrlCollection, undefined, null);
        expect(expectedResult).toEqual(genericLicenseUrlCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
