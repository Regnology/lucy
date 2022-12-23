import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IRequirement, Requirement } from '../requirement.model';

import { RequirementService } from './requirement.service';

describe('Requirement Service', () => {
  let service: RequirementService;
  let httpMock: HttpTestingController;
  let elemDefault: IRequirement;
  let expectedResult: IRequirement | IRequirement[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(RequirementService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      shortText: 'AAAAAAA',
      description: 'AAAAAAA',
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

    it('should create a Requirement', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Requirement()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Requirement', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          shortText: 'BBBBBB',
          description: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Requirement', () => {
      const patchObject = Object.assign(
        {
          shortText: 'BBBBBB',
        },
        new Requirement()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Requirement', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          shortText: 'BBBBBB',
          description: 'BBBBBB',
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

    it('should delete a Requirement', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addRequirementToCollectionIfMissing', () => {
      it('should add a Requirement to an empty array', () => {
        const requirement: IRequirement = { id: 123 };
        expectedResult = service.addRequirementToCollectionIfMissing([], requirement);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(requirement);
      });

      it('should not add a Requirement to an array that contains it', () => {
        const requirement: IRequirement = { id: 123 };
        const requirementCollection: IRequirement[] = [
          {
            ...requirement,
          },
          { id: 456 },
        ];
        expectedResult = service.addRequirementToCollectionIfMissing(requirementCollection, requirement);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Requirement to an array that doesn't contain it", () => {
        const requirement: IRequirement = { id: 123 };
        const requirementCollection: IRequirement[] = [{ id: 456 }];
        expectedResult = service.addRequirementToCollectionIfMissing(requirementCollection, requirement);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(requirement);
      });

      it('should add only unique Requirement to an array', () => {
        const requirementArray: IRequirement[] = [{ id: 123 }, { id: 456 }, { id: 11804 }];
        const requirementCollection: IRequirement[] = [{ id: 123 }];
        expectedResult = service.addRequirementToCollectionIfMissing(requirementCollection, ...requirementArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const requirement: IRequirement = { id: 123 };
        const requirement2: IRequirement = { id: 456 };
        expectedResult = service.addRequirementToCollectionIfMissing([], requirement, requirement2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(requirement);
        expect(expectedResult).toContain(requirement2);
      });

      it('should accept null and undefined values', () => {
        const requirement: IRequirement = { id: 123 };
        expectedResult = service.addRequirementToCollectionIfMissing([], null, requirement, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(requirement);
      });

      it('should return initial array if no Requirement is added', () => {
        const requirementCollection: IRequirement[] = [{ id: 123 }];
        expectedResult = service.addRequirementToCollectionIfMissing(requirementCollection, undefined, null);
        expect(expectedResult).toEqual(requirementCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
