import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import dayjs from 'dayjs/esm';

import { DATE_FORMAT } from 'app/config/input.constants';
import { ILibraryPerProduct, LibraryPerProduct } from '../library-per-product.model';

import { LibraryPerProductService } from './library-per-product.service';

describe('LibraryPerProduct Service', () => {
  let service: LibraryPerProductService;
  let httpMock: HttpTestingController;
  let elemDefault: ILibraryPerProduct;
  let expectedResult: ILibraryPerProduct | ILibraryPerProduct[] | boolean | null;
  let currentDate: dayjs.Dayjs;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(LibraryPerProductService);
    httpMock = TestBed.inject(HttpTestingController);
    currentDate = dayjs();

    elemDefault = {
      id: 0,
      addedDate: currentDate,
      addedManually: false,
      hideForPublishing: false,
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign(
        {
          addedDate: currentDate.format(DATE_FORMAT),
        },
        elemDefault
      );

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a LibraryPerProduct', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
          addedDate: currentDate.format(DATE_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          addedDate: currentDate,
        },
        returnedFromService
      );

      service.create(new LibraryPerProduct()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a LibraryPerProduct', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          addedDate: currentDate.format(DATE_FORMAT),
          addedManually: true,
          hideForPublishing: true,
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          addedDate: currentDate,
        },
        returnedFromService
      );

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a LibraryPerProduct', () => {
      const patchObject = Object.assign(
        {
          addedManually: true,
        },
        new LibraryPerProduct()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign(
        {
          addedDate: currentDate,
        },
        returnedFromService
      );

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of LibraryPerProduct', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          addedDate: currentDate.format(DATE_FORMAT),
          addedManually: true,
          hideForPublishing: true,
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          addedDate: currentDate,
        },
        returnedFromService
      );

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a LibraryPerProduct', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addLibraryPerProductToCollectionIfMissing', () => {
      it('should add a LibraryPerProduct to an empty array', () => {
        const libraryPerProduct: ILibraryPerProduct = { id: 123 };
        expectedResult = service.addLibraryPerProductToCollectionIfMissing([], libraryPerProduct);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(libraryPerProduct);
      });

      it('should not add a LibraryPerProduct to an array that contains it', () => {
        const libraryPerProduct: ILibraryPerProduct = { id: 123 };
        const libraryPerProductCollection: ILibraryPerProduct[] = [
          {
            ...libraryPerProduct,
          },
          { id: 456 },
        ];
        expectedResult = service.addLibraryPerProductToCollectionIfMissing(libraryPerProductCollection, libraryPerProduct);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a LibraryPerProduct to an array that doesn't contain it", () => {
        const libraryPerProduct: ILibraryPerProduct = { id: 123 };
        const libraryPerProductCollection: ILibraryPerProduct[] = [{ id: 456 }];
        expectedResult = service.addLibraryPerProductToCollectionIfMissing(libraryPerProductCollection, libraryPerProduct);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(libraryPerProduct);
      });

      it('should add only unique LibraryPerProduct to an array', () => {
        const libraryPerProductArray: ILibraryPerProduct[] = [{ id: 123 }, { id: 456 }, { id: 25346 }];
        const libraryPerProductCollection: ILibraryPerProduct[] = [{ id: 123 }];
        expectedResult = service.addLibraryPerProductToCollectionIfMissing(libraryPerProductCollection, ...libraryPerProductArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const libraryPerProduct: ILibraryPerProduct = { id: 123 };
        const libraryPerProduct2: ILibraryPerProduct = { id: 456 };
        expectedResult = service.addLibraryPerProductToCollectionIfMissing([], libraryPerProduct, libraryPerProduct2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(libraryPerProduct);
        expect(expectedResult).toContain(libraryPerProduct2);
      });

      it('should accept null and undefined values', () => {
        const libraryPerProduct: ILibraryPerProduct = { id: 123 };
        expectedResult = service.addLibraryPerProductToCollectionIfMissing([], null, libraryPerProduct, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(libraryPerProduct);
      });

      it('should return initial array if no LibraryPerProduct is added', () => {
        const libraryPerProductCollection: ILibraryPerProduct[] = [{ id: 123 }];
        expectedResult = service.addLibraryPerProductToCollectionIfMissing(libraryPerProductCollection, undefined, null);
        expect(expectedResult).toEqual(libraryPerProductCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
