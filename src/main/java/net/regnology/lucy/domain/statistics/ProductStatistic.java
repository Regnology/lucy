package net.regnology.lucy.domain.statistics;

import java.util.List;

public class ProductStatistic {

    private List<CountOccurrences> licenseDistribution;

    private List<CountOccurrences> licenseRiskDistribution;

    private List<Series> licenseRiskSeries;

    private List<Series> numberOfLibrariesSeries;

    public ProductStatistic() {}

    public ProductStatistic(
        List<CountOccurrences> licenseDistribution,
        List<CountOccurrences> licenseRiskDistribution,
        List<Series> licenseRiskSeries,
        List<Series> numberOfLibrariesSeries
    ) {
        this.licenseDistribution = licenseDistribution;
        this.licenseRiskDistribution = licenseRiskDistribution;
        this.licenseRiskSeries = licenseRiskSeries;
        this.numberOfLibrariesSeries = numberOfLibrariesSeries;
    }

    public void add(CountOccurrences seriesEntry) {
        for (Series e : licenseRiskSeries) {
            if (e.getName().equals(seriesEntry.getName())) {
                e.getSeries().add(seriesEntry);
                break;
            }
        }
    }

    public List<CountOccurrences> getLicenseDistribution() {
        return licenseDistribution;
    }

    public void setLicenseDistribution(List<CountOccurrences> licenseDistribution) {
        this.licenseDistribution = licenseDistribution;
    }

    public List<CountOccurrences> getLicenseRiskDistribution() {
        return licenseRiskDistribution;
    }

    public void setLicenseRiskDistribution(List<CountOccurrences> licenseRiskDistribution) {
        this.licenseRiskDistribution = licenseRiskDistribution;
    }

    public List<Series> getLicenseRiskSeries() {
        return licenseRiskSeries;
    }

    public void setLicenseRiskSeries(List<Series> licenseRiskSeries) {
        this.licenseRiskSeries = licenseRiskSeries;
    }

    public List<Series> getNumberOfLibrariesSeries() {
        return numberOfLibrariesSeries;
    }

    public void setNumberOfLibrariesSeries(List<Series> numberOfLibrariesSeries) {
        this.numberOfLibrariesSeries = numberOfLibrariesSeries;
    }
}
