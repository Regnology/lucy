package net.regnology.lucy.domain.helper;

import java.io.Serializable;
import java.util.Set;

/**
 * Object to hold full and simple copyright information of an analysed source code archive.<br>
 * <b>Full: </b><br>
 * Result of the copyright analysis from all files in the archive<br>
 * <b>Simple: </b><br>
 * Result of the copyright analysis from "copyright" files in the archive. "copyright" files are license, readme, notice
 * etc. files
 */
public class Copyright implements Serializable {

    private Set<String> fullCopyright;
    private Set<String> simpleCopyright;

    public Copyright(Set<String> fullCopyright, Set<String> simpleCopyright) {
        this.fullCopyright = fullCopyright;
        this.simpleCopyright = simpleCopyright;
    }

    public Set<String> getFullCopyright() {
        return fullCopyright;
    }

    public void setFullCopyright(Set<String> fullCopyright) {
        this.fullCopyright = fullCopyright;
    }

    public Set<String> getSimpleCopyright() {
        return simpleCopyright;
    }

    public void setSimpleCopyright(Set<String> simpleCopyright) {
        this.simpleCopyright = simpleCopyright;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Copyright{" +
            "fullCopyright='" + fullCopyright + "'" +
            ", simpleCopyright='" + simpleCopyright + "'" +
            "}";
    }
}
