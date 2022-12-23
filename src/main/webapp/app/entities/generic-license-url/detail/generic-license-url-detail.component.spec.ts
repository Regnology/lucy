import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { GenericLicenseUrlDetailComponent } from './generic-license-url-detail.component';

describe('GenericLicenseUrl Management Detail Component', () => {
  let comp: GenericLicenseUrlDetailComponent;
  let fixture: ComponentFixture<GenericLicenseUrlDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [GenericLicenseUrlDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ genericLicenseUrl: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(GenericLicenseUrlDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(GenericLicenseUrlDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load genericLicenseUrl on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.genericLicenseUrl).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
