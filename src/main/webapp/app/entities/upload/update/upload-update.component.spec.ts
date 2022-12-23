import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { UploadService } from '../service/upload.service';
import { IUpload, Upload } from '../upload.model';

import { UploadUpdateComponent } from './upload-update.component';

describe('Upload Management Update Component', () => {
  let comp: UploadUpdateComponent;
  let fixture: ComponentFixture<UploadUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let uploadService: UploadService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [UploadUpdateComponent],
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
      .overrideTemplate(UploadUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(UploadUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    uploadService = TestBed.inject(UploadService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const upload: IUpload = { id: 456 };

      activatedRoute.data = of({ upload });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(upload));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Upload>>();
      const upload = { id: 123 };
      jest.spyOn(uploadService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ upload });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: upload }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(uploadService.update).toHaveBeenCalledWith(upload);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Upload>>();
      const upload = new Upload();
      jest.spyOn(uploadService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ upload });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: upload }));
      saveSubject.complete();

      // THEN
      expect(uploadService.create).toHaveBeenCalledWith(upload);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Upload>>();
      const upload = { id: 123 };
      jest.spyOn(uploadService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ upload });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(uploadService.update).toHaveBeenCalledWith(upload);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
