import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { ILibraryErrorLog, LibraryErrorLog } from '../library-error-log.model';
import { LibraryErrorLogService } from '../service/library-error-log.service';

import { LibraryErrorLogRoutingResolveService } from './library-error-log-routing-resolve.service';

describe('LibraryErrorLog routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: LibraryErrorLogRoutingResolveService;
  let service: LibraryErrorLogService;
  let resultLibraryErrorLog: ILibraryErrorLog | undefined;

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
    routingResolveService = TestBed.inject(LibraryErrorLogRoutingResolveService);
    service = TestBed.inject(LibraryErrorLogService);
    resultLibraryErrorLog = undefined;
  });

  describe('resolve', () => {
    it('should return ILibraryErrorLog returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultLibraryErrorLog = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultLibraryErrorLog).toEqual({ id: 123 });
    });

    it('should return new ILibraryErrorLog if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultLibraryErrorLog = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultLibraryErrorLog).toEqual(new LibraryErrorLog());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as LibraryErrorLog })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultLibraryErrorLog = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultLibraryErrorLog).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
