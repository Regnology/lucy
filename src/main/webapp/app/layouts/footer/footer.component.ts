import { Component } from '@angular/core';

import { VERSION } from 'app/app.constants';

@Component({
  selector: 'jhi-footer',
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.scss'],
})
export class FooterComponent {
  version = '';

  constructor() {
    if (VERSION) {
      this.version = VERSION;
    }
  }
}
