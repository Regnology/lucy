import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { LibraryPerProductService } from '../service/library-per-product.service';
import { ILibraryPerProduct, LibraryPerProduct } from '../library-per-product.model';
import { ILibrary } from 'app/entities/library/library.model';
import { LibraryService } from 'app/entities/library/service/library.service';
import { IProduct } from 'app/entities/product/product.model';
import { ProductService } from 'app/entities/product/service/product.service';

import { LibraryPerProductUpdateComponent } from './library-per-product-update.component';

describe('LibraryPerProduct Management Update Component', () => {
  let comp: LibraryPerProductUpdateComponent;
  let fixture: ComponentFixture<LibraryPerProductUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let libraryPerProductService: LibraryPerProductService;
  let libraryService: LibraryService;
  let productService: ProductService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [LibraryPerProductUpdateComponent],
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
      .overrideTemplate(LibraryPerProductUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(LibraryPerProductUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    libraryPerProductService = TestBed.inject(LibraryPerProductService);
    libraryService = TestBed.inject(LibraryService);
    productService = TestBed.inject(ProductService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Library query and add missing value', () => {
      const libraryPerProduct: ILibraryPerProduct = { id: 456 };
      const library: ILibrary = { id: 53813 };
      libraryPerProduct.library = library;

      const libraryCollection: ILibrary[] = [{ id: 14405 }];
      jest.spyOn(libraryService, 'query').mockReturnValue(of(new HttpResponse({ body: libraryCollection })));
      const additionalLibraries = [library];
      const expectedCollection: ILibrary[] = [...additionalLibraries, ...libraryCollection];
      jest.spyOn(libraryService, 'addLibraryToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ libraryPerProduct });
      comp.ngOnInit();

      expect(libraryService.query).toHaveBeenCalled();
      expect(libraryService.addLibraryToCollectionIfMissing).toHaveBeenCalledWith(libraryCollection, ...additionalLibraries);
      expect(comp.librariesSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Product query and add missing value', () => {
      const libraryPerProduct: ILibraryPerProduct = { id: 456 };
      const product: IProduct = { id: 67413 };
      libraryPerProduct.product = product;

      const productCollection: IProduct[] = [{ id: 66848 }];
      jest.spyOn(productService, 'query').mockReturnValue(of(new HttpResponse({ body: productCollection })));
      const additionalProducts = [product];
      const expectedCollection: IProduct[] = [...additionalProducts, ...productCollection];
      jest.spyOn(productService, 'addProductToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ libraryPerProduct });
      comp.ngOnInit();

      expect(productService.query).toHaveBeenCalled();
      expect(productService.addProductToCollectionIfMissing).toHaveBeenCalledWith(productCollection, ...additionalProducts);
      expect(comp.productsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const libraryPerProduct: ILibraryPerProduct = { id: 456 };
      const library: ILibrary = { id: 95884 };
      libraryPerProduct.library = library;
      const product: IProduct = { id: 46154 };
      libraryPerProduct.product = product;

      activatedRoute.data = of({ libraryPerProduct });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(libraryPerProduct));
      expect(comp.librariesSharedCollection).toContain(library);
      expect(comp.productsSharedCollection).toContain(product);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<LibraryPerProduct>>();
      const libraryPerProduct = { id: 123 };
      jest.spyOn(libraryPerProductService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ libraryPerProduct });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: libraryPerProduct }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(libraryPerProductService.update).toHaveBeenCalledWith(libraryPerProduct);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<LibraryPerProduct>>();
      const libraryPerProduct = new LibraryPerProduct();
      jest.spyOn(libraryPerProductService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ libraryPerProduct });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: libraryPerProduct }));
      saveSubject.complete();

      // THEN
      expect(libraryPerProductService.create).toHaveBeenCalledWith(libraryPerProduct);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<LibraryPerProduct>>();
      const libraryPerProduct = { id: 123 };
      jest.spyOn(libraryPerProductService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ libraryPerProduct });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(libraryPerProductService.update).toHaveBeenCalledWith(libraryPerProduct);
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

    describe('trackProductById', () => {
      it('Should return tracked Product primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackProductById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
