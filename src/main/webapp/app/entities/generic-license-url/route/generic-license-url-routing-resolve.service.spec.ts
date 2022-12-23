import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { IGenericLicenseUrl, GenericLicenseUrl } from '../generic-license-url.model';
import { GenericLicenseUrlService } from '../service/generic-license-url.service';

import { GenericLicenseUrlRoutingResolveService } from './generic-license-url-routing-resolve.service';

describe('GenericLicenseUrl routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: GenericLicenseUrlRoutingResolveService;
  let service: GenericLicenseUrlService;
  let resultGenericLicenseUrl: IGenericLicenseUrl | undefined;

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
    routingResolveService = TestBed.inject(GenericLicenseUrlRoutingResolveService);
    service = TestBed.inject(GenericLicenseUrlService);
    resultGenericLicenseUrl = undefined;
  });

  describe('resolve', () => {
    it('should return IGenericLicenseUrl returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultGenericLicenseUrl = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultGenericLicenseUrl).toEqual({ id: 123 });
    });

    it('should return new IGenericLicenseUrl if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultGenericLicenseUrl = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultGenericLicenseUrl).toEqual(new GenericLicenseUrl());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as GenericLicenseUrl })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultGenericLicenseUrl = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultGenericLicenseUrl).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
