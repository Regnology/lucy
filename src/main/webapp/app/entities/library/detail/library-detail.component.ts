import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ILibrary } from '../library.model';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-library-detail',
  templateUrl: './library-detail.component.html',
})
export class LibraryDetailComponent implements OnInit {
  library: ILibrary | null = null;

  constructor(protected dataUtils: DataUtils, protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ library }) => {
      this.library = library;
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  previousState(): void {
    window.history.back();
  }
}
