import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { RequirementService } from '../service/requirement.service';
import { IRequirement, Requirement } from '../requirement.model';

import { RequirementUpdateComponent } from './requirement-update.component';

describe('Requirement Management Update Component', () => {
  let comp: RequirementUpdateComponent;
  let fixture: ComponentFixture<RequirementUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let requirementService: RequirementService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [RequirementUpdateComponent],
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
      .overrideTemplate(RequirementUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(RequirementUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    requirementService = TestBed.inject(RequirementService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const requirement: IRequirement = { id: 456 };

      activatedRoute.data = of({ requirement });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(requirement));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Requirement>>();
      const requirement = { id: 123 };
      jest.spyOn(requirementService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ requirement });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: requirement }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(requirementService.update).toHaveBeenCalledWith(requirement);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Requirement>>();
      const requirement = new Requirement();
      jest.spyOn(requirementService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ requirement });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: requirement }));
      saveSubject.complete();

      // THEN
      expect(requirementService.create).toHaveBeenCalledWith(requirement);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Requirement>>();
      const requirement = { id: 123 };
      jest.spyOn(requirementService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ requirement });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(requirementService.update).toHaveBeenCalledWith(requirement);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
