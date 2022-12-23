import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { LicensePerLibraryDetailComponent } from './license-per-library-detail.component';

describe('LicensePerLibrary Management Detail Component', () => {
  let comp: LicensePerLibraryDetailComponent;
  let fixture: ComponentFixture<LicensePerLibraryDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [LicensePerLibraryDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ licensePerLibrary: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(LicensePerLibraryDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(LicensePerLibraryDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load licensePerLibrary on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.licensePerLibrary).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
