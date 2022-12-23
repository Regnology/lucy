package net.regnology.lucy.service.upload;

import java.util.Set;
import net.regnology.lucy.domain.File;
import net.regnology.lucy.service.exceptions.UploadException;

public interface AssetLoader<T> {
    Set<T> load(File file) throws UploadException;
}
