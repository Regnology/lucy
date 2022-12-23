export interface IBasicAuth {
  username: string;
  password: string;
}

export class BasicAuth implements IBasicAuth {
  constructor(public username: string, public password: string) {}
}
