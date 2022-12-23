import * as dayjs from 'dayjs';

import { FormatSimpleDatetimePipe } from './format-simple-datetime.pipe';

describe('FormatSimpleDatePipe', () => {
  const formatSimpleDatetimePipe = new FormatSimpleDatetimePipe();

  it('should return an empty string when receive undefined', () => {
    expect(formatSimpleDatetimePipe.transform(undefined)).toBe('');
  });

  it('should return an empty string when receive null', () => {
    expect(formatSimpleDatetimePipe.transform(null)).toBe('');
  });

  it('should format date like this D MMM', () => {
    expect(formatSimpleDatetimePipe.transform(dayjs('11-16').locale('fr'))).toBe('16 Nov 00:00');
  });
});
