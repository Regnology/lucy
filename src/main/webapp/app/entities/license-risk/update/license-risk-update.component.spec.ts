import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { LicenseRiskService } from '../service/license-risk.service';
import { ILicenseRisk, LicenseRisk } from '../license-risk.model';

import { LicenseRiskUpdateComponent } from './license-risk-update.component';

describe('LicenseRisk Management Update Component', () => {
  let comp: LicenseRiskUpdateComponent;
  let fixture: ComponentFixture<LicenseRiskUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let licenseRiskService: LicenseRiskService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [LicenseRiskUpdateComponent],
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
      .overrideTemplate(LicenseRiskUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(LicenseRiskUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    licenseRiskService = TestBed.inject(LicenseRiskService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const licenseRisk: ILicenseRisk = { id: 456 };

      activatedRoute.data = of({ licenseRisk });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(licenseRisk));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<LicenseRisk>>();
      const licenseRisk = { id: 123 };
      jest.spyOn(licenseRiskService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ licenseRisk });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: licenseRisk }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(licenseRiskService.update).toHaveBeenCalledWith(licenseRisk);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<LicenseRisk>>();
      const licenseRisk = new LicenseRisk();
      jest.spyOn(licenseRiskService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ licenseRisk });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: licenseRisk }));
      saveSubject.complete();

      // THEN
      expect(licenseRiskService.create).toHaveBeenCalledWith(licenseRisk);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<LicenseRisk>>();
      const licenseRisk = { id: 123 };
      jest.spyOn(licenseRiskService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ licenseRisk });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(licenseRiskService.update).toHaveBeenCalledWith(licenseRisk);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
