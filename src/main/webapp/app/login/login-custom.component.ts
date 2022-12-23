import { Component, ElementRef, ViewChild } from '@angular/core';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';

import { LoginService } from 'app/login/login.service';
import { AccountService } from 'app/core/auth/account.service';
import { LoginComponent } from './login.component';

@Component({
  selector: 'jhi-login-custom',
  templateUrl: './login-custom.component.html',
})
export class LoginCustomComponent extends LoginComponent {
  @ViewChild('username', { static: false })
  username!: ElementRef;

  authenticationError = false;

  loginForm = this.fb.group({
    username: [null, [Validators.required]],
    password: [null, [Validators.required]],
    rememberMe: [false],
  });

  constructor(
    protected accountService: AccountService,
    protected loginService: LoginService,
    protected router: Router,
    protected fb: UntypedFormBuilder
  ) {
    super(accountService, loginService, router, fb);
  }
}
