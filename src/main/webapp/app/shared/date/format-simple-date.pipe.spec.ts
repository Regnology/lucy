import * as dayjs from 'dayjs';

import { FormatSimpleDatePipe } from './format-simple-date.pipe';

describe('FormatSimpleDatePipe', () => {
  const formatSimpleDatePipe = new FormatSimpleDatePipe();

  it('should return an empty string when receive undefined', () => {
    expect(formatSimpleDatePipe.transform(undefined)).toBe('');
  });

  it('should return an empty string when receive null', () => {
    expect(formatSimpleDatePipe.transform(null)).toBe('');
  });

  it('should format date like this D MMM', () => {
    expect(formatSimpleDatePipe.transform(dayjs('11-16').locale('fr'))).toBe('16 Nov');
  });
});
