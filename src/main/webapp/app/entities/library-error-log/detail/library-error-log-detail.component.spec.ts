import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { LibraryErrorLogDetailComponent } from './library-error-log-detail.component';

describe('LibraryErrorLog Management Detail Component', () => {
  let comp: LibraryErrorLogDetailComponent;
  let fixture: ComponentFixture<LibraryErrorLogDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [LibraryErrorLogDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ libraryErrorLog: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(LibraryErrorLogDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(LibraryErrorLogDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load libraryErrorLog on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.libraryErrorLog).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
