import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DifferenceViewComponent } from './difference-view.component';

describe('DifferenceViewModalComponent', () => {
  let component: DifferenceViewComponent;
  let fixture: ComponentFixture<DifferenceViewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [DifferenceViewComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DifferenceViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
