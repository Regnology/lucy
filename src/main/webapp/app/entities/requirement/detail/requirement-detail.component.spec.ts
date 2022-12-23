import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { RequirementDetailComponent } from './requirement-detail.component';

describe('Requirement Management Detail Component', () => {
  let comp: RequirementDetailComponent;
  let fixture: ComponentFixture<RequirementDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [RequirementDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ requirement: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(RequirementDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(RequirementDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load requirement on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.requirement).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
