import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { ILicensePerLibrary, LicensePerLibrary } from '../license-per-library.model';
import { LicensePerLibraryService } from '../service/license-per-library.service';

import { LicensePerLibraryRoutingResolveService } from './license-per-library-routing-resolve.service';

describe('LicensePerLibrary routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: LicensePerLibraryRoutingResolveService;
  let service: LicensePerLibraryService;
  let resultLicensePerLibrary: ILicensePerLibrary | undefined;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: convertToParamMap({}),
            },
          },
        },
      ],
    });
    mockRouter = TestBed.inject(Router);
    jest.spyOn(mockRouter, 'navigate').mockImplementation(() => Promise.resolve(true));
    mockActivatedRouteSnapshot = TestBed.inject(ActivatedRoute).snapshot;
    routingResolveService = TestBed.inject(LicensePerLibraryRoutingResolveService);
    service = TestBed.inject(LicensePerLibraryService);
    resultLicensePerLibrary = undefined;
  });

  describe('resolve', () => {
    it('should return ILicensePerLibrary returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultLicensePerLibrary = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultLicensePerLibrary).toEqual({ id: 123 });
    });

    it('should return new ILicensePerLibrary if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultLicensePerLibrary = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultLicensePerLibrary).toEqual(new LicensePerLibrary());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as LicensePerLibrary })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultLicensePerLibrary = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultLicensePerLibrary).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
