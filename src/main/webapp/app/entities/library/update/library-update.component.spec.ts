import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { LibraryService } from '../service/library.service';
import { ILibrary, Library } from '../library.model';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { ILicense } from 'app/entities/license/license.model';
import { LicenseService } from 'app/entities/license/service/license.service';

import { LibraryUpdateComponent } from './library-update.component';

describe('Library Management Update Component', () => {
  let comp: LibraryUpdateComponent;
  let fixture: ComponentFixture<LibraryUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let libraryService: LibraryService;
  let userService: UserService;
  let licenseService: LicenseService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [LibraryUpdateComponent],
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
      .overrideTemplate(LibraryUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(LibraryUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    libraryService = TestBed.inject(LibraryService);
    userService = TestBed.inject(UserService);
    licenseService = TestBed.inject(LicenseService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call User query and add missing value', () => {
      const library: ILibrary = { id: 456 };
      const lastReviewedBy: IUser = { id: 41969 };
      library.lastReviewedBy = lastReviewedBy;

      const userCollection: IUser[] = [{ id: 44911 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [lastReviewedBy];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ library });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(userCollection, ...additionalUsers);
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should call License query and add missing value', () => {
      const library: ILibrary = { id: 456 };
      const licenseToPublishes: ILicense[] = [{ id: 6540 }];
      library.licenseToPublishes = licenseToPublishes;
      const licenseOfFiles: ILicense[] = [{ id: 90862 }];
      library.licenseOfFiles = licenseOfFiles;

      const licenseCollection: ILicense[] = [{ id: 67138 }];
      jest.spyOn(licenseService, 'query').mockReturnValue(of(new HttpResponse({ body: licenseCollection })));
      const additionalLicenses = [...licenseToPublishes, ...licenseOfFiles];
      const expectedCollection: ILicense[] = [...additionalLicenses, ...licenseCollection];
      jest.spyOn(licenseService, 'addLicenseToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ library });
      comp.ngOnInit();

      expect(licenseService.query).toHaveBeenCalled();
      expect(licenseService.addLicenseToCollectionIfMissing).toHaveBeenCalledWith(licenseCollection, ...additionalLicenses);
      expect(comp.licensesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const library: ILibrary = { id: 456 };
      const lastReviewedBy: IUser = { id: 11575 };
      library.lastReviewedBy = lastReviewedBy;
      const licenseToPublishes: ILicense = { id: 71226 };
      library.licenseToPublishes = [licenseToPublishes];
      const licenseOfFiles: ILicense = { id: 97199 };
      library.licenseOfFiles = [licenseOfFiles];

      activatedRoute.data = of({ library });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(library));
      expect(comp.usersSharedCollection).toContain(lastReviewedBy);
      expect(comp.licensesSharedCollection).toContain(licenseToPublishes);
      expect(comp.licensesSharedCollection).toContain(licenseOfFiles);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Library>>();
      const library = { id: 123 };
      jest.spyOn(libraryService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ library });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: library }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(libraryService.update).toHaveBeenCalledWith(library);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Library>>();
      const library = new Library();
      jest.spyOn(libraryService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ library });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: library }));
      saveSubject.complete();

      // THEN
      expect(libraryService.create).toHaveBeenCalledWith(library);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Library>>();
      const library = { id: 123 };
      jest.spyOn(libraryService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ library });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(libraryService.update).toHaveBeenCalledWith(library);
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

    describe('trackLicenseById', () => {
      it('Should return tracked License primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackLicenseById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });

  describe('Getting selected relationships', () => {
    describe('getSelectedLicense', () => {
      it('Should return option if no License is selected', () => {
        const option = { id: 123 };
        const result = comp.getSelectedLicense(option);
        expect(result === option).toEqual(true);
      });

      it('Should return selected License for according option', () => {
        const option = { id: 123 };
        const selected = { id: 123 };
        const selected2 = { id: 456 };
        const result = comp.getSelectedLicense(option, [selected2, selected]);
        expect(result === selected).toEqual(true);
        expect(result === selected2).toEqual(false);
        expect(result === option).toEqual(false);
      });

      it('Should return option if this License is not selected', () => {
        const option = { id: 123 };
        const selected = { id: 456 };
        const result = comp.getSelectedLicense(option, [selected]);
        expect(result === option).toEqual(true);
        expect(result === selected).toEqual(false);
      });
    });
  });
});
