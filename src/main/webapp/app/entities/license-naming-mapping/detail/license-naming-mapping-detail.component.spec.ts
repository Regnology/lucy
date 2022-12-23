import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { LicenseNamingMappingDetailComponent } from './license-naming-mapping-detail.component';

describe('LicenseNamingMapping Management Detail Component', () => {
  let comp: LicenseNamingMappingDetailComponent;
  let fixture: ComponentFixture<LicenseNamingMappingDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [LicenseNamingMappingDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ licenseNamingMapping: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(LicenseNamingMappingDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(LicenseNamingMappingDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load licenseNamingMapping on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.licenseNamingMapping).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
