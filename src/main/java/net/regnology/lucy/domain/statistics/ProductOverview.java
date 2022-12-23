package net.regnology.lucy.domain.statistics;

public class ProductOverview {

    private long numberOfLibraries;

    private long numberOfLibrariesPrevious;

    private long numberOfLicenses;

    private long numberOfLicensesPrevious;

    private long reviewedLibraries;

    public ProductOverview() {}

    public ProductOverview(
        long numberOfLibraries,
        long numberOfLibrariesPrevious,
        long numberOfLicenses,
        long numberOfLicensesPrevious,
        long reviewedLibraries
    ) {
        this.numberOfLibraries = numberOfLibraries;
        this.numberOfLibrariesPrevious = numberOfLibrariesPrevious;
        this.numberOfLicenses = numberOfLicenses;
        this.numberOfLicensesPrevious = numberOfLicensesPrevious;
        this.reviewedLibraries = reviewedLibraries;
    }

    public long getNumberOfLibraries() {
        return numberOfLibraries;
    }

    public void setNumberOfLibraries(long numberOfLibraries) {
        this.numberOfLibraries = numberOfLibraries;
    }

    public long getNumberOfLibrariesPrevious() {
        return numberOfLibrariesPrevious;
    }

    public void setNumberOfLibrariesPrevious(long numberOfLibrariesPrevious) {
        this.numberOfLibrariesPrevious = numberOfLibrariesPrevious;
    }

    public long getNumberOfLicenses() {
        return numberOfLicenses;
    }

    public void setNumberOfLicenses(long numberOfLicenses) {
        this.numberOfLicenses = numberOfLicenses;
    }

    public long getNumberOfLicensesPrevious() {
        return numberOfLicensesPrevious;
    }

    public void setNumberOfLicensesPrevious(long numberOfLicensesPrevious) {
        this.numberOfLicensesPrevious = numberOfLicensesPrevious;
    }

    public long getReviewedLibraries() {
        return reviewedLibraries;
    }

    public void setReviewedLibraries(long reviewedLibraries) {
        this.reviewedLibraries = reviewedLibraries;
    }
}
