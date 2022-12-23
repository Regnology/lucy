import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { GenericLicenseUrlService } from '../service/generic-license-url.service';

import { GenericLicenseUrlComponent } from './generic-license-url.component';

describe('GenericLicenseUrl Management Component', () => {
  let comp: GenericLicenseUrlComponent;
  let fixture: ComponentFixture<GenericLicenseUrlComponent>;
  let service: GenericLicenseUrlService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [GenericLicenseUrlComponent],
    })
      .overrideTemplate(GenericLicenseUrlComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(GenericLicenseUrlComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(GenericLicenseUrlService);

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ id: 123 }],
          headers,
        })
      )
    );
  });

  it('Should call load all on init', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenCalled();
    expect(comp.genericLicenseUrls?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });
});
