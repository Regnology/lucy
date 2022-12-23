import { Pipe, PipeTransform } from '@angular/core';

@Pipe({ name: 'absoluteNumberPipe' })
export class AbsoluteNumberPipe implements PipeTransform {
  transform(value: number): number {
    return Math.abs(value);
  }
}
