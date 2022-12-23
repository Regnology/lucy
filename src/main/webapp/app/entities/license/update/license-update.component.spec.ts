import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { LicenseService } from '../service/license.service';
import { ILicense, License } from '../license.model';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { ILicenseRisk } from 'app/entities/license-risk/license-risk.model';
import { LicenseRiskService } from 'app/entities/license-risk/service/license-risk.service';
import { IRequirement } from 'app/entities/requirement/requirement.model';
import { RequirementService } from 'app/entities/requirement/service/requirement.service';

import { LicenseUpdateComponent } from './license-update.component';

describe('License Management Update Component', () => {
  let comp: LicenseUpdateComponent;
  let fixture: ComponentFixture<LicenseUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let licenseService: LicenseService;
  let userService: UserService;
  let licenseRiskService: LicenseRiskService;
  let requirementService: RequirementService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [LicenseUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(LicenseUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(LicenseUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    licenseService = TestBed.inject(LicenseService);
    userService = TestBed.inject(UserService);
    licenseRiskService = TestBed.inject(LicenseRiskService);
    requirementService = TestBed.inject(RequirementService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call User query and add missing value', () => {
      const license: ILicense = { id: 456 };
      const lastReviewedBy: IUser = { id: 83159 };
      license.lastReviewedBy = lastReviewedBy;

      const userCollection: IUser[] = [{ id: 4311 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [lastReviewedBy];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ license });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(userCollection, ...additionalUsers);
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should call LicenseRisk query and add missing value', () => {
      const license: ILicense = { id: 456 };
      const licenseRisk: ILicenseRisk = { id: 76526 };
      license.licenseRisk = licenseRisk;

      const licenseRiskCollection: ILicenseRisk[] = [{ id: 55364 }];
      jest.spyOn(licenseRiskService, 'query').mockReturnValue(of(new HttpResponse({ body: licenseRiskCollection })));
      const additionalLicenseRisks = [licenseRisk];
      const expectedCollection: ILicenseRisk[] = [...additionalLicenseRisks, ...licenseRiskCollection];
      jest.spyOn(licenseRiskService, 'addLicenseRiskToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ license });
      comp.ngOnInit();

      expect(licenseRiskService.query).toHaveBeenCalled();
      expect(licenseRiskService.addLicenseRiskToCollectionIfMissing).toHaveBeenCalledWith(licenseRiskCollection, ...additionalLicenseRisks);
      expect(comp.licenseRisksSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Requirement query and add missing value', () => {
      const license: ILicense = { id: 456 };
      const requirements: IRequirement[] = [{ id: 90601 }];
      license.requirements = requirements;

      const requirementCollection: IRequirement[] = [{ id: 24208 }];
      jest.spyOn(requirementService, 'query').mockReturnValue(of(new HttpResponse({ body: requirementCollection })));
      const additionalRequirements = [...requirements];
      const expectedCollection: IRequirement[] = [...additionalRequirements, ...requirementCollection];
      jest.spyOn(requirementService, 'addRequirementToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ license });
      comp.ngOnInit();

      expect(requirementService.query).toHaveBeenCalled();
      expect(requirementService.addRequirementToCollectionIfMissing).toHaveBeenCalledWith(requirementCollection, ...additionalRequirements);
      expect(comp.requirementsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const license: ILicense = { id: 456 };
      const lastReviewedBy: IUser = { id: 2805 };
      license.lastReviewedBy = lastReviewedBy;
      const licenseRisk: ILicenseRisk = { id: 11656 };
      license.licenseRisk = licenseRisk;
      const requirements: IRequirement = { id: 6533 };
      license.requirements = [requirements];

      activatedRoute.data = of({ license });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(license));
      expect(comp.usersSharedCollection).toContain(lastReviewedBy);
      expect(comp.licenseRisksSharedCollection).toContain(licenseRisk);
      expect(comp.requirementsSharedCollection).toContain(requirements);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<License>>();
      const license = { id: 123 };
      jest.spyOn(licenseService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ license });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: license }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(licenseService.update).toHaveBeenCalledWith(license);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<License>>();
      const license = new License();
      jest.spyOn(licenseService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ license });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: license }));
      saveSubject.complete();

      // THEN
      expect(licenseService.create).toHaveBeenCalledWith(license);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<License>>();
      const license = { id: 123 };
      jest.spyOn(licenseService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ license });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(licenseService.update).toHaveBeenCalledWith(license);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackUserById', () => {
      it('Should return tracked User primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackUserById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });

    describe('trackLicenseRiskById', () => {
      it('Should return tracked LicenseRisk primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackLicenseRiskById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });

    describe('trackRequirementById', () => {
      it('Should return tracked Requirement primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackRequirementById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });

  describe('Getting selected relationships', () => {
    describe('getSelectedRequirement', () => {
      it('Should return option if no Requirement is selected', () => {
        const option = { id: 123 };
        const result = comp.getSelectedRequirement(option);
        expect(result === option).toEqual(true);
      });

      it('Should return selected Requirement for according option', () => {
        const option = { id: 123 };
        const selected = { id: 123 };
        const selected2 = { id: 456 };
        const result = comp.getSelectedRequirement(option, [selected2, selected]);
        expect(result === selected).toEqual(true);
        expect(result === selected2).toEqual(false);
        expect(result === option).toEqual(false);
      });

      it('Should return option if this Requirement is not selected', () => {
        const option = { id: 123 };
        const selected = { id: 456 };
        const result = comp.getSelectedRequirement(option, [selected]);
        expect(result === option).toEqual(true);
        expect(result === selected).toEqual(false);
      });
    });
  });
});
