import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { GenericLicenseUrlService } from '../service/generic-license-url.service';
import { IGenericLicenseUrl, GenericLicenseUrl } from '../generic-license-url.model';

import { GenericLicenseUrlUpdateComponent } from './generic-license-url-update.component';

describe('GenericLicenseUrl Management Update Component', () => {
  let comp: GenericLicenseUrlUpdateComponent;
  let fixture: ComponentFixture<GenericLicenseUrlUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let genericLicenseUrlService: GenericLicenseUrlService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [GenericLicenseUrlUpdateComponent],
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
      .overrideTemplate(GenericLicenseUrlUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(GenericLicenseUrlUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    genericLicenseUrlService = TestBed.inject(GenericLicenseUrlService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const genericLicenseUrl: IGenericLicenseUrl = { id: 456 };

      activatedRoute.data = of({ genericLicenseUrl });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(genericLicenseUrl));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<GenericLicenseUrl>>();
      const genericLicenseUrl = { id: 123 };
      jest.spyOn(genericLicenseUrlService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ genericLicenseUrl });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: genericLicenseUrl }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(genericLicenseUrlService.update).toHaveBeenCalledWith(genericLicenseUrl);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<GenericLicenseUrl>>();
      const genericLicenseUrl = new GenericLicenseUrl();
      jest.spyOn(genericLicenseUrlService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ genericLicenseUrl });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: genericLicenseUrl }));
      saveSubject.complete();

      // THEN
      expect(genericLicenseUrlService.create).toHaveBeenCalledWith(genericLicenseUrl);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<GenericLicenseUrl>>();
      const genericLicenseUrl = { id: 123 };
      jest.spyOn(genericLicenseUrlService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ genericLicenseUrl });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(genericLicenseUrlService.update).toHaveBeenCalledWith(genericLicenseUrl);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
