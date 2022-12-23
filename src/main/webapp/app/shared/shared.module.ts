import { NgModule } from '@angular/core';
import { SharedLibsModule } from './shared-libs.module';
import { AlertComponent } from './alert/alert.component';
import { AlertErrorComponent } from './alert/alert-error.component';
import { HasAnyAuthorityDirective } from './auth/has-any-authority.directive';
import { DurationPipe } from './date/duration.pipe';
import { FormatMediumDatetimePipe } from './date/format-medium-datetime.pipe';
import { FormatMediumDatePipe } from './date/format-medium-date.pipe';
import { FormatSimpleDatetimePipe } from './date/format-simple-datetime.pipe';
import { FormatSimpleDatePipe } from './date/format-simple-date.pipe';
import { SortByDirective } from './sort/sort-by.directive';
import { SortDirective } from './sort/sort.directive';
import { ItemCountComponent } from './pagination/item-count.component';
import { CopyrightModalComponent } from './modals/copyright-modal/copyright-modal.component';
import { UrlTransformationPipe } from './url/string-to-url.pipe';
import { LibraryLabelPipe } from './label/library-label.pipe';

@NgModule({
  imports: [SharedLibsModule],
  declarations: [
    AlertComponent,
    AlertErrorComponent,
    HasAnyAuthorityDirective,
    DurationPipe,
    FormatMediumDatetimePipe,
    FormatMediumDatePipe,
    FormatSimpleDatetimePipe,
    FormatSimpleDatePipe,
    SortByDirective,
    SortDirective,
    ItemCountComponent,
    CopyrightModalComponent,
    UrlTransformationPipe,
    LibraryLabelPipe,
  ],
  exports: [
    SharedLibsModule,
    AlertComponent,
    AlertErrorComponent,
    HasAnyAuthorityDirective,
    DurationPipe,
    FormatMediumDatetimePipe,
    FormatMediumDatePipe,
    FormatSimpleDatetimePipe,
    FormatSimpleDatePipe,
    SortByDirective,
    SortDirective,
    ItemCountComponent,
    UrlTransformationPipe,
    LibraryLabelPipe,
  ],
})
export class SharedModule {}
