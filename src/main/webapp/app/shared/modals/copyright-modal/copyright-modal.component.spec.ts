import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CopyrightModalComponent } from './copyright-modal.component';

describe('CopyrightModalComponent', () => {
  let component: CopyrightModalComponent;
  let fixture: ComponentFixture<CopyrightModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CopyrightModalComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CopyrightModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
