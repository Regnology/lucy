package net.regnology.lucy.domain.helper;

import java.io.Serializable;
import net.regnology.lucy.domain.File;

public class Upload implements Serializable {

    private File file;
    private File additionalLibraries;

    public Upload() {}

    public Upload(File file, File additionalLibraries) {
        this.file = file;
        this.additionalLibraries = additionalLibraries;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public File getAdditionalLibraries() {
        return additionalLibraries;
    }

    public void setAdditionalLibraries(File additionalLibraries) {
        this.additionalLibraries = additionalLibraries;
    }
}
