import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { LibraryErrorLogService } from '../service/library-error-log.service';
import { ILibraryErrorLog, LibraryErrorLog } from '../library-error-log.model';
import { ILibrary } from 'app/entities/library/library.model';
import { LibraryService } from 'app/entities/library/service/library.service';

import { LibraryErrorLogUpdateComponent } from './library-error-log-update.component';

describe('LibraryErrorLog Management Update Component', () => {
  let comp: LibraryErrorLogUpdateComponent;
  let fixture: ComponentFixture<LibraryErrorLogUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let libraryErrorLogService: LibraryErrorLogService;
  let libraryService: LibraryService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [LibraryErrorLogUpdateComponent],
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
      .overrideTemplate(LibraryErrorLogUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(LibraryErrorLogUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    libraryErrorLogService = TestBed.inject(LibraryErrorLogService);
    libraryService = TestBed.inject(LibraryService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Library query and add missing value', () => {
      const libraryErrorLog: ILibraryErrorLog = { id: 456 };
      const library: ILibrary = { id: 78814 };
      libraryErrorLog.library = library;

      const libraryCollection: ILibrary[] = [{ id: 66858 }];
      jest.spyOn(libraryService, 'query').mockReturnValue(of(new HttpResponse({ body: libraryCollection })));
      const additionalLibraries = [library];
      const expectedCollection: ILibrary[] = [...additionalLibraries, ...libraryCollection];
      jest.spyOn(libraryService, 'addLibraryToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ libraryErrorLog });
      comp.ngOnInit();

      expect(libraryService.query).toHaveBeenCalled();
      expect(libraryService.addLibraryToCollectionIfMissing).toHaveBeenCalledWith(libraryCollection, ...additionalLibraries);
      expect(comp.librariesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const libraryErrorLog: ILibraryErrorLog = { id: 456 };
      const library: ILibrary = { id: 9211 };
      libraryErrorLog.library = library;

      activatedRoute.data = of({ libraryErrorLog });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(libraryErrorLog));
      expect(comp.librariesSharedCollection).toContain(library);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<LibraryErrorLog>>();
      const libraryErrorLog = { id: 123 };
      jest.spyOn(libraryErrorLogService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ libraryErrorLog });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: libraryErrorLog }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(libraryErrorLogService.update).toHaveBeenCalledWith(libraryErrorLog);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<LibraryErrorLog>>();
      const libraryErrorLog = new LibraryErrorLog();
      jest.spyOn(libraryErrorLogService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ libraryErrorLog });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: libraryErrorLog }));
      saveSubject.complete();

      // THEN
      expect(libraryErrorLogService.create).toHaveBeenCalledWith(libraryErrorLog);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<LibraryErrorLog>>();
      const libraryErrorLog = { id: 123 };
      jest.spyOn(libraryErrorLogService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ libraryErrorLog });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(libraryErrorLogService.update).toHaveBeenCalledWith(libraryErrorLog);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackLibraryById', () => {
      it('Should return tracked Library primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackLibraryById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
