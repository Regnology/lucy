import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { LibraryPerProductDetailComponent } from './library-per-product-detail.component';

describe('LibraryPerProduct Management Detail Component', () => {
  let comp: LibraryPerProductDetailComponent;
  let fixture: ComponentFixture<LibraryPerProductDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [LibraryPerProductDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ libraryPerProduct: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(LibraryPerProductDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(LibraryPerProductDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load libraryPerProduct on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.libraryPerProduct).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
