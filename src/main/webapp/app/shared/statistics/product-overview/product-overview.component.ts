import { Component, Input } from '@angular/core';
import { ICountOccurrences } from '../count-occurrences.model';
import { ISeries } from '../series.model';
import { LegendPosition } from '@swimlane/ngx-charts';

@Component({
  selector: 'jhi-product-overview',
  templateUrl: './product-overview.component.html',
})
export class ProductOverviewComponent {
  legendPosition: LegendPosition = LegendPosition.Right;

  @Input()
  numberOfLibraries?: number | null;
  @Input()
  numberOfLibrariesPrevious?: number | null;

  @Input()
  numberOfLicenses?: number | null;
  @Input()
  numberOfLicensesPrevious?: number | null;

  @Input()
  reviewedLibraries?: number | null;

  @Input()
  licenseRiskResult?: ICountOccurrences[] | null;

  @Input()
  licenseResult?: ICountOccurrences[] | null;

  @Input()
  licenseRiskSeries?: ISeries[] | null;

  @Input()
  numberOfLibrariesSeries?: ISeries[] | null;

  @Input()
  licenseRiskPieChart?: ICountOccurrences[] | null;

  @Input()
  licenseDistribution?: ICountOccurrences[] | null;

  colorSchemeLicenseRisk = [
    { name: 'Permissive', value: '#85CF00' },
    { name: 'Limited Copyleft', value: '#FFC300' },
    { name: 'Strong Copyleft', value: '#FF5733' },
    { name: 'Commercial', value: '#20A8D8' },
    { name: 'Forbidden', value: '#C70039' },
    { name: 'Unknown', value: '#CCCCCC' },
    { name: 'Proprietary Free', value: '#9933FF' },
  ];

  colorSchemePieChart = {
    domain: ['#004c6d', '#005f81', '#007296', '#0086a9', '#009abc', '#00afcf', '#00c5e0', '#00dbf0', '#00f1ff'],
  };
}
