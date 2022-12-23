import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AccountService } from 'app/core/auth/account.service';
import { LoginService } from 'app/login/login.service';
import { ProfileService } from 'app/layouts/profiles/profile.service';
import { NavbarComponent } from './navbar.component';

@Component({
  selector: 'jhi-navbar-custom',
  templateUrl: './navbar-custom.component.html',
  styleUrls: ['./navbar.component.scss'],
})
export class NavbarCustomComponent extends NavbarComponent {
  constructor(
    protected loginService: LoginService,
    protected accountService: AccountService,
    protected profileService: ProfileService,
    protected router: Router
  ) {
    super(loginService, accountService, profileService, router);
  }
}
