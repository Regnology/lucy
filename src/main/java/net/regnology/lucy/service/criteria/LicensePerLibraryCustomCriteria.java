package net.regnology.lucy.service.criteria;

import java.util.Objects;
import net.regnology.lucy.domain.LicensePerLibrary;
import net.regnology.lucy.web.rest.LicensePerLibraryResource;
import tech.jhipster.service.filter.Filter;

/**
 * Custom criteria class for the {@link LicensePerLibrary} entity. This class is used
 * in {@link LicensePerLibraryResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /license-per-libraries?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class LicensePerLibraryCustomCriteria extends LicensePerLibraryCriteria {

    private static final long serialVersionUID = 1L;

    public LicensePerLibraryCustomCriteria() {
        super();
    }

    public LicensePerLibraryCustomCriteria(LicensePerLibraryCustomCriteria other) {
        super(other);
    }

    @Override
    public LicensePerLibraryCustomCriteria copy() {
        return new LicensePerLibraryCustomCriteria(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final LicensePerLibraryCustomCriteria that = (LicensePerLibraryCustomCriteria) o;
        return (
            Objects.equals(getId(), that.getId()) &&
            Objects.equals(getLinkType(), that.getLinkType()) &&
            Objects.equals(getLicenseId(), that.getLicenseId()) &&
            Objects.equals(getLibraryId(), that.getLibraryId()) &&
            Objects.equals(getDistinct(), that.getDistinct())
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getLinkType(), getLicenseId(), getLibraryId(), getDistinct());
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LicensePerLibraryCustomCriteria{" +
            (getId() != null ? "id=" + getId() + ", " : "") +
            (getLinkType() != null ? "linkType=" + getLinkType() + ", " : "") +
            (getLicenseId() != null ? "licenseId=" + getLicenseId() + ", " : "") +
            (getLibraryId() != null ? "libraryId=" + getLibraryId() + ", " : "") +
            (getDistinct() != null ? "distinct=" + getDistinct() + ", " : "") +
            "}";
    }
}
