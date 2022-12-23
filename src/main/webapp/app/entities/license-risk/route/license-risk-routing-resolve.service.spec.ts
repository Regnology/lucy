import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { ILicenseRisk, LicenseRisk } from '../license-risk.model';
import { LicenseRiskService } from '../service/license-risk.service';

import { LicenseRiskRoutingResolveService } from './license-risk-routing-resolve.service';

describe('LicenseRisk routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: LicenseRiskRoutingResolveService;
  let service: LicenseRiskService;
  let resultLicenseRisk: ILicenseRisk | undefined;

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
    routingResolveService = TestBed.inject(LicenseRiskRoutingResolveService);
    service = TestBed.inject(LicenseRiskService);
    resultLicenseRisk = undefined;
  });

  describe('resolve', () => {
    it('should return ILicenseRisk returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultLicenseRisk = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultLicenseRisk).toEqual({ id: 123 });
    });

    it('should return new ILicenseRisk if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultLicenseRisk = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultLicenseRisk).toEqual(new LicenseRisk());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as LicenseRisk })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultLicenseRisk = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultLicenseRisk).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
