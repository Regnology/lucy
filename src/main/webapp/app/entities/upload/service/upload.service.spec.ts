import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import dayjs from 'dayjs/esm';

import { DATE_FORMAT } from 'app/config/input.constants';
import { EntityUploadChoice } from 'app/entities/enumerations/entity-upload-choice.model';
import { IUpload, Upload } from '../upload.model';

import { UploadService } from './upload.service';

describe('Upload Service', () => {
  let service: UploadService;
  let httpMock: HttpTestingController;
  let elemDefault: IUpload;
  let expectedResult: IUpload | IUpload[] | boolean | null;
  let currentDate: dayjs.Dayjs;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(UploadService);
    httpMock = TestBed.inject(HttpTestingController);
    currentDate = dayjs();

    elemDefault = {
      id: 0,
      fileContentType: 'image/png',
      file: 'AAAAAAA',
      entityToUpload: EntityUploadChoice.PRODUCT,
      record: 0,
      overwriteData: false,
      uploadedDate: currentDate,
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign(
        {
          uploadedDate: currentDate.format(DATE_FORMAT),
        },
        elemDefault
      );

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a Upload', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
          uploadedDate: currentDate.format(DATE_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          uploadedDate: currentDate,
        },
        returnedFromService
      );

      service.create(new Upload()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Upload', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          file: 'BBBBBB',
          entityToUpload: 'BBBBBB',
          record: 1,
          overwriteData: true,
          uploadedDate: currentDate.format(DATE_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          uploadedDate: currentDate,
        },
        returnedFromService
      );

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Upload', () => {
      const patchObject = Object.assign(
        {
          entityToUpload: 'BBBBBB',
          uploadedDate: currentDate.format(DATE_FORMAT),
        },
        new Upload()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign(
        {
          uploadedDate: currentDate,
        },
        returnedFromService
      );

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Upload', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          file: 'BBBBBB',
          entityToUpload: 'BBBBBB',
          record: 1,
          overwriteData: true,
          uploadedDate: currentDate.format(DATE_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          uploadedDate: currentDate,
        },
        returnedFromService
      );

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a Upload', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addUploadToCollectionIfMissing', () => {
      it('should add a Upload to an empty array', () => {
        const upload: IUpload = { id: 123 };
        expectedResult = service.addUploadToCollectionIfMissing([], upload);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(upload);
      });

      it('should not add a Upload to an array that contains it', () => {
        const upload: IUpload = { id: 123 };
        const uploadCollection: IUpload[] = [
          {
            ...upload,
          },
          { id: 456 },
        ];
        expectedResult = service.addUploadToCollectionIfMissing(uploadCollection, upload);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Upload to an array that doesn't contain it", () => {
        const upload: IUpload = { id: 123 };
        const uploadCollection: IUpload[] = [{ id: 456 }];
        expectedResult = service.addUploadToCollectionIfMissing(uploadCollection, upload);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(upload);
      });

      it('should add only unique Upload to an array', () => {
        const uploadArray: IUpload[] = [{ id: 123 }, { id: 456 }, { id: 70134 }];
        const uploadCollection: IUpload[] = [{ id: 123 }];
        expectedResult = service.addUploadToCollectionIfMissing(uploadCollection, ...uploadArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const upload: IUpload = { id: 123 };
        const upload2: IUpload = { id: 456 };
        expectedResult = service.addUploadToCollectionIfMissing([], upload, upload2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(upload);
        expect(expectedResult).toContain(upload2);
      });

      it('should accept null and undefined values', () => {
        const upload: IUpload = { id: 123 };
        expectedResult = service.addUploadToCollectionIfMissing([], null, upload, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(upload);
      });

      it('should return initial array if no Upload is added', () => {
        const uploadCollection: IUpload[] = [{ id: 123 }];
        expectedResult = service.addUploadToCollectionIfMissing(uploadCollection, undefined, null);
        expect(expectedResult).toEqual(uploadCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
