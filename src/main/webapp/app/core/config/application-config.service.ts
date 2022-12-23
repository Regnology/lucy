import { Injectable } from '@angular/core';
import { IFossologyConfig } from 'app/entities/fossology/config/fossology-config.model';

@Injectable({
  providedIn: 'root',
})
export class ApplicationConfigService {
  private endpointPrefix = '';
  private microfrontend = false;
  private fossologyConfig?: IFossologyConfig | null;

  setEndpointPrefix(endpointPrefix: string): void {
    this.endpointPrefix = endpointPrefix;
  }

  setMicrofrontend(microfrontend = true): void {
    this.microfrontend = microfrontend;
  }

  isMicrofrontend(): boolean {
    return this.microfrontend;
  }

  getEndpointFor(api: string, microservice?: string): string {
    if (microservice) {
      return `${this.endpointPrefix}services/${microservice}/${api}`;
    }
    return `${this.endpointPrefix}${api}`;
  }

  setFossologyConfig(config: IFossologyConfig | null): void {
    this.fossologyConfig = config;
  }

  getFossologyConfig(): IFossologyConfig | null | undefined {
    return this.fossologyConfig;
  }
}
