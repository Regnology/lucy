package net.regnology.lucy.domain.helper;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;
import net.regnology.lucy.domain.Library;
import org.apache.commons.lang3.StringUtils;

public class DifferenceView implements Serializable {

    public static class VersionChange {

        Set<Library> librarySet = new HashSet<>(3);
    }

    private List<Library> sameLibraries;
    private List<Library> addedLibraries;
    private List<Library> removedLibraries;
    private List<Library> firstProductNewLibraries;
    private List<Library> secondProductNewLibraries;

    public DifferenceView(
        List<Library> sameLibraries,
        List<Library> addedLibraries,
        List<Library> removedLibraries,
        List<Library> firstProductNewLibraries,
        List<Library> secondProductNewLibraries
    ) {
        this.sameLibraries = sameLibraries;
        this.addedLibraries = addedLibraries;
        this.removedLibraries = removedLibraries;
        this.firstProductNewLibraries = firstProductNewLibraries;
        this.secondProductNewLibraries = secondProductNewLibraries;
    }

    /**
     * @param a First list
     * @param b Second list
     * @return A new Set with libraries
     */
    public List<Library> versionChange(List<Library> a, List<Library> b) {
        List<Library> removedDuplicatesFromB = b.stream().filter(a::contains).collect(Collectors.toList());

        Map<String, VersionChange> newLibraryVersions = new HashMap<>();

        for (Library libraryA : a) {
            for (Library libraryRemovedDuplicatesLibrary : removedDuplicatesFromB) {
                if (isEqualLibraryNameAndType(libraryA, libraryRemovedDuplicatesLibrary)) {
                    DifferenceView.VersionChange versionChange = new DifferenceView.VersionChange();
                }
            }
        }

        return new ArrayList<>(1);
    }

    /**
     * Check if GroupId, ArtifactId and Type of two libraries are the same.
     *
     * @param a First library entity
     * @param b Second library entity
     * @return true, if it has the same GroupId, ArtifactId and Type, otherwise false.
     */
    public static boolean isEqualLibraryNameAndType(Library a, Library b) {
        if (!StringUtils.isBlank(a.getGroupId()) && !StringUtils.isBlank(b.getGroupId())) {
            return a.getGroupId().equals(b.getGroupId()) && a.getArtifactId().equals(b.getArtifactId()) && a.getType().equals(b.getType());
        } else if (StringUtils.isBlank(a.getGroupId()) && StringUtils.isBlank(b.getGroupId())) {
            return a.getArtifactId().equals(b.getArtifactId()) && a.getType().equals(b.getType());
        } else {
            return false;
        }
    }

    /**
     * Create a label for a library from GroupId, ArtifactId and Type.
     * If the GroupId is not present, then only the ArtifactId and Type will be taken.
     *
     * @return Label for the library.
     */
    public static String createLabel(Library library) {
        return StringUtils.isBlank(library.getGroupId())
            ? library.getArtifactId() + ":" + library.getType().getValue()
            : library.getGroupId() + ":" + library.getArtifactId() + ":" + library.getType().getValue();
    }

    public List<Library> getSameLibraries() {
        return sameLibraries;
    }

    public void setSameLibraries(List<Library> sameLibraries) {
        this.sameLibraries = sameLibraries;
    }

    public List<Library> getAddedLibraries() {
        return addedLibraries;
    }

    public void setAddedLibraries(List<Library> addedLibraries) {
        this.addedLibraries = addedLibraries;
    }

    public List<Library> getRemovedLibraries() {
        return removedLibraries;
    }

    public void setRemovedLibraries(List<Library> removedLibraries) {
        this.removedLibraries = removedLibraries;
    }

    public List<Library> getFirstProductNewLibraries() {
        return firstProductNewLibraries;
    }

    public void setFirstProductNewLibraries(List<Library> firstProductNewLibraries) {
        this.firstProductNewLibraries = firstProductNewLibraries;
    }

    public List<Library> getSecondProductNewLibraries() {
        return secondProductNewLibraries;
    }

    public void setSecondProductNewLibraries(List<Library> secondProductNewLibraries) {
        this.secondProductNewLibraries = secondProductNewLibraries;
    }

    @Override
    public String toString() {
        return "DifferenceView{" + "addedLibraries='" + addedLibraries + "'" + ", removedLibraries=" + removedLibraries + "'" + '}';
    }
}
