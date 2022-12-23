export interface IFossologyConfig {
  enabled: boolean;
  url: string;
}

export class FossologyConfig implements IFossologyConfig {
  constructor(public enabled: boolean, public url: string) {}
}
