import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'urlTransformation',
})
export class UrlTransformationPipe implements PipeTransform {
  transform(value: string | null | undefined, truncate?: boolean, truncateLimit?: number): string {
    const urlRegex = /(((https?:\/\/)|(www\.))[^\s]+)/g;
    const limit = truncateLimit ?? 21;

    const splitBy = ',';
    const splittedValue = value?.split(splitBy);

    const urls: string[] = [];

    for (const url of splittedValue ?? []) {
      let truncatedUrl: string;
      if (truncate) {
        truncatedUrl = url.trim().substring(0, limit) + '..';
      } else {
        truncatedUrl = url.trim();
      }

      if (url.match(urlRegex)) {
        urls.push(`<a href="${url}" target="_blank">${truncatedUrl}</a>`);
      } else {
        urls.push(url);
      }
    }

    return urls.join('<b>, </b>');
  }
}
