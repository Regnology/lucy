import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { LicenseNamingMappingService } from '../service/license-naming-mapping.service';
import { ILicenseNamingMapping, LicenseNamingMapping } from '../license-naming-mapping.model';

import { LicenseNamingMappingUpdateComponent } from './license-naming-mapping-update.component';

describe('LicenseNamingMapping Management Update Component', () => {
  let comp: LicenseNamingMappingUpdateComponent;
  let fixture: ComponentFixture<LicenseNamingMappingUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let licenseNamingMappingService: LicenseNamingMappingService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [LicenseNamingMappingUpdateComponent],
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
      .overrideTemplate(LicenseNamingMappingUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(LicenseNamingMappingUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    licenseNamingMappingService = TestBed.inject(LicenseNamingMappingService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const licenseNamingMapping: ILicenseNamingMapping = { id: 456 };

      activatedRoute.data = of({ licenseNamingMapping });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(licenseNamingMapping));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<LicenseNamingMapping>>();
      const licenseNamingMapping = { id: 123 };
      jest.spyOn(licenseNamingMappingService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ licenseNamingMapping });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: licenseNamingMapping }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(licenseNamingMappingService.update).toHaveBeenCalledWith(licenseNamingMapping);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<LicenseNamingMapping>>();
      const licenseNamingMapping = new LicenseNamingMapping();
      jest.spyOn(licenseNamingMappingService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ licenseNamingMapping });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: licenseNamingMapping }));
      saveSubject.complete();

      // THEN
      expect(licenseNamingMappingService.create).toHaveBeenCalledWith(licenseNamingMapping);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<LicenseNamingMapping>>();
      const licenseNamingMapping = { id: 123 };
      jest.spyOn(licenseNamingMappingService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ licenseNamingMapping });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(licenseNamingMappingService.update).toHaveBeenCalledWith(licenseNamingMapping);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
