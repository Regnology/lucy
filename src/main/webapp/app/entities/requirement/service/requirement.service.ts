import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IRequirement, getRequirementIdentifier } from '../requirement.model';

export type EntityResponseType = HttpResponse<IRequirement>;
export type EntityArrayResponseType = HttpResponse<IRequirement[]>;

@Injectable({ providedIn: 'root' })
export class RequirementService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/requirements');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(requirement: IRequirement): Observable<EntityResponseType> {
    return this.http.post<IRequirement>(this.resourceUrl, requirement, { observe: 'response' });
  }

  update(requirement: IRequirement): Observable<EntityResponseType> {
    return this.http.put<IRequirement>(`${this.resourceUrl}/${getRequirementIdentifier(requirement) as number}`, requirement, {
      observe: 'response',
    });
  }

  partialUpdate(requirement: IRequirement): Observable<EntityResponseType> {
    return this.http.patch<IRequirement>(`${this.resourceUrl}/${getRequirementIdentifier(requirement) as number}`, requirement, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IRequirement>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IRequirement[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addRequirementToCollectionIfMissing(
    requirementCollection: IRequirement[],
    ...requirementsToCheck: (IRequirement | null | undefined)[]
  ): IRequirement[] {
    const requirements: IRequirement[] = requirementsToCheck.filter(isPresent);
    if (requirements.length > 0) {
      const requirementCollectionIdentifiers = requirementCollection.map(requirementItem => getRequirementIdentifier(requirementItem)!);
      const requirementsToAdd = requirements.filter(requirementItem => {
        const requirementIdentifier = getRequirementIdentifier(requirementItem);
        if (requirementIdentifier == null || requirementCollectionIdentifiers.includes(requirementIdentifier)) {
          return false;
        }
        requirementCollectionIdentifiers.push(requirementIdentifier);
        return true;
      });
      return [...requirementsToAdd, ...requirementCollection];
    }
    return requirementCollection;
  }
}
