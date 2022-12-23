import { ICountOccurrences } from 'app/shared/statistics/count-occurrences.model';

export interface ISeries {
  name: string;
  series: ICountOccurrences[];
}

export class Series implements ISeries {
  constructor(public name: string, public series: ICountOccurrences[]) {}
}
