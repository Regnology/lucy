import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { LogSeverity } from 'app/entities/enumerations/log-severity.model';
import { LogStatus } from 'app/entities/enumerations/log-status.model';
import { ILibraryErrorLog, LibraryErrorLog } from '../library-error-log.model';

import { LibraryErrorLogService } from './library-error-log.service';

describe('LibraryErrorLog Service', () => {
  let service: LibraryErrorLogService;
  let httpMock: HttpTestingController;
  let elemDefault: ILibraryErrorLog;
  let expectedResult: ILibraryErrorLog | ILibraryErrorLog[] | boolean | null;
  let currentDate: dayjs.Dayjs;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(LibraryErrorLogService);
    httpMock = TestBed.inject(HttpTestingController);
    currentDate = dayjs();

    elemDefault = {
      id: 0,
      message: 'AAAAAAA',
      severity: LogSeverity.LOW,
      status: LogStatus.CLOSED,
      timestamp: currentDate,
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign(
        {
          timestamp: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a LibraryErrorLog', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
          timestamp: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          timestamp: currentDate,
        },
        returnedFromService
      );

      service.create(new LibraryErrorLog()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a LibraryErrorLog', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          message: 'BBBBBB',
          severity: 'BBBBBB',
          status: 'BBBBBB',
          timestamp: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          timestamp: currentDate,
        },
        returnedFromService
      );

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a LibraryErrorLog', () => {
      const patchObject = Object.assign(
        {
          severity: 'BBBBBB',
        },
        new LibraryErrorLog()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign(
        {
          timestamp: currentDate,
        },
        returnedFromService
      );

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of LibraryErrorLog', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          message: 'BBBBBB',
          severity: 'BBBBBB',
          status: 'BBBBBB',
          timestamp: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          timestamp: currentDate,
        },
        returnedFromService
      );

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a LibraryErrorLog', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addLibraryErrorLogToCollectionIfMissing', () => {
      it('should add a LibraryErrorLog to an empty array', () => {
        const libraryErrorLog: ILibraryErrorLog = { id: 123 };
        expectedResult = service.addLibraryErrorLogToCollectionIfMissing([], libraryErrorLog);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(libraryErrorLog);
      });

      it('should not add a LibraryErrorLog to an array that contains it', () => {
        const libraryErrorLog: ILibraryErrorLog = { id: 123 };
        const libraryErrorLogCollection: ILibraryErrorLog[] = [
          {
            ...libraryErrorLog,
          },
          { id: 456 },
        ];
        expectedResult = service.addLibraryErrorLogToCollectionIfMissing(libraryErrorLogCollection, libraryErrorLog);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a LibraryErrorLog to an array that doesn't contain it", () => {
        const libraryErrorLog: ILibraryErrorLog = { id: 123 };
        const libraryErrorLogCollection: ILibraryErrorLog[] = [{ id: 456 }];
        expectedResult = service.addLibraryErrorLogToCollectionIfMissing(libraryErrorLogCollection, libraryErrorLog);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(libraryErrorLog);
      });

      it('should add only unique LibraryErrorLog to an array', () => {
        const libraryErrorLogArray: ILibraryErrorLog[] = [{ id: 123 }, { id: 456 }, { id: 76525 }];
        const libraryErrorLogCollection: ILibraryErrorLog[] = [{ id: 123 }];
        expectedResult = service.addLibraryErrorLogToCollectionIfMissing(libraryErrorLogCollection, ...libraryErrorLogArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const libraryErrorLog: ILibraryErrorLog = { id: 123 };
        const libraryErrorLog2: ILibraryErrorLog = { id: 456 };
        expectedResult = service.addLibraryErrorLogToCollectionIfMissing([], libraryErrorLog, libraryErrorLog2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(libraryErrorLog);
        expect(expectedResult).toContain(libraryErrorLog2);
      });

      it('should accept null and undefined values', () => {
        const libraryErrorLog: ILibraryErrorLog = { id: 123 };
        expectedResult = service.addLibraryErrorLogToCollectionIfMissing([], null, libraryErrorLog, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(libraryErrorLog);
      });

      it('should return initial array if no LibraryErrorLog is added', () => {
        const libraryErrorLogCollection: ILibraryErrorLog[] = [{ id: 123 }];
        expectedResult = service.addLibraryErrorLogToCollectionIfMissing(libraryErrorLogCollection, undefined, null);
        expect(expectedResult).toEqual(libraryErrorLogCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
