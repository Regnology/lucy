import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ILibraryErrorLog } from '../library-error-log.model';

@Component({
  selector: 'jhi-library-error-log-detail',
  templateUrl: './library-error-log-detail.component.html',
})
export class LibraryErrorLogDetailComponent implements OnInit {
  libraryErrorLog: ILibraryErrorLog | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ libraryErrorLog }) => {
      this.libraryErrorLog = libraryErrorLog;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
