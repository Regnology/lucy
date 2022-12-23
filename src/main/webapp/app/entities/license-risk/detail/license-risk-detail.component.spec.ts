import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { LicenseRiskDetailComponent } from './license-risk-detail.component';

describe('LicenseRisk Management Detail Component', () => {
  let comp: LicenseRiskDetailComponent;
  let fixture: ComponentFixture<LicenseRiskDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [LicenseRiskDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ licenseRisk: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(LicenseRiskDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(LicenseRiskDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load licenseRisk on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.licenseRisk).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
