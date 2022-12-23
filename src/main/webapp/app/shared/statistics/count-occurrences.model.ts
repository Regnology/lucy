export interface ICountOccurrences {
  name: string;
  value: number;
}

export class CountOccurrences implements ICountOccurrences {
  constructor(public name: string, public value: number) {}
}
