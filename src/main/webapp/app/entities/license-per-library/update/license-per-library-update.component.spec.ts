import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { LicensePerLibraryService } from '../service/license-per-library.service';
import { ILicensePerLibrary, LicensePerLibrary } from '../license-per-library.model';
import { ILicense } from 'app/entities/license/license.model';
import { LicenseService } from 'app/entities/license/service/license.service';
import { ILibrary } from 'app/entities/library/library.model';
import { LibraryService } from 'app/entities/library/service/library.service';

import { LicensePerLibraryUpdateComponent } from './license-per-library-update.component';

describe('LicensePerLibrary Management Update Component', () => {
  let comp: LicensePerLibraryUpdateComponent;
  let fixture: ComponentFixture<LicensePerLibraryUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let licensePerLibraryService: LicensePerLibraryService;
  let licenseService: LicenseService;
  let libraryService: LibraryService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [LicensePerLibraryUpdateComponent],
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
      .overrideTemplate(LicensePerLibraryUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(LicensePerLibraryUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    licensePerLibraryService = TestBed.inject(LicensePerLibraryService);
    licenseService = TestBed.inject(LicenseService);
    libraryService = TestBed.inject(LibraryService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call License query and add missing value', () => {
      const licensePerLibrary: ILicensePerLibrary = { id: 456 };
      const license: ILicense = { id: 41338 };
      licensePerLibrary.license = license;

      const licenseCollection: ILicense[] = [{ id: 84451 }];
      jest.spyOn(licenseService, 'query').mockReturnValue(of(new HttpResponse({ body: licenseCollection })));
      const additionalLicenses = [license];
      const expectedCollection: ILicense[] = [...additionalLicenses, ...licenseCollection];
      jest.spyOn(licenseService, 'addLicenseToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ licensePerLibrary });
      comp.ngOnInit();

      expect(licenseService.query).toHaveBeenCalled();
      expect(licenseService.addLicenseToCollectionIfMissing).toHaveBeenCalledWith(licenseCollection, ...additionalLicenses);
      expect(comp.licensesSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Library query and add missing value', () => {
      const licensePerLibrary: ILicensePerLibrary = { id: 456 };
      const library: ILibrary = { id: 40618 };
      licensePerLibrary.library = library;

      const libraryCollection: ILibrary[] = [{ id: 86862 }];
      jest.spyOn(libraryService, 'query').mockReturnValue(of(new HttpResponse({ body: libraryCollection })));
      const additionalLibraries = [library];
      const expectedCollection: ILibrary[] = [...additionalLibraries, ...libraryCollection];
      jest.spyOn(libraryService, 'addLibraryToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ licensePerLibrary });
      comp.ngOnInit();

      expect(libraryService.query).toHaveBeenCalled();
      expect(libraryService.addLibraryToCollectionIfMissing).toHaveBeenCalledWith(libraryCollection, ...additionalLibraries);
      expect(comp.librariesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const licensePerLibrary: ILicensePerLibrary = { id: 456 };
      const license: ILicense = { id: 44663 };
      licensePerLibrary.license = license;
      const library: ILibrary = { id: 46102 };
      licensePerLibrary.library = library;

      activatedRoute.data = of({ licensePerLibrary });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(licensePerLibrary));
      expect(comp.licensesSharedCollection).toContain(license);
      expect(comp.librariesSharedCollection).toContain(library);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<LicensePerLibrary>>();
      const licensePerLibrary = { id: 123 };
      jest.spyOn(licensePerLibraryService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ licensePerLibrary });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: licensePerLibrary }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(licensePerLibraryService.update).toHaveBeenCalledWith(licensePerLibrary);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<LicensePerLibrary>>();
      const licensePerLibrary = new LicensePerLibrary();
      jest.spyOn(licensePerLibraryService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ licensePerLibrary });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: licensePerLibrary }));
      saveSubject.complete();

      // THEN
      expect(licensePerLibraryService.create).toHaveBeenCalledWith(licensePerLibrary);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<LicensePerLibrary>>();
      const licensePerLibrary = { id: 123 };
      jest.spyOn(licensePerLibraryService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ licensePerLibrary });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(licensePerLibraryService.update).toHaveBeenCalledWith(licensePerLibrary);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackLicenseById', () => {
      it('Should return tracked License primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackLicenseById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });

    describe('trackLibraryById', () => {
      it('Should return tracked Library primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackLibraryById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
