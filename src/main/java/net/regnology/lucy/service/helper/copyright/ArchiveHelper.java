package net.regnology.lucy.service.helper.copyright;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import net.regnology.lucy.domain.helper.Copyright;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.compress.utils.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArchiveHelper {

    private static final Logger log = LoggerFactory.getLogger(ArchiveHelper.class);

    public static final String COPYRIGHT_FILES_PATTERN = "licen[cs]e|readme|notice|copying|copyright";

    /**
     * Anaylse an archive for copyright information. Detects automatically if the archive is compressed and which kind of
     * archive type it is.<br>
     * If the archive type is unknown or unsupported an {@link net.regnology.lucy.service.exceptions.ArchiveException ArchiveException}
     * will be thrown.
     *
     * @param input the archive as an InputStream. The InputStream must support marks, like BufferedInputStream
     * @param inAllFiles search all files in an archive or only files that matches the {@link #COPYRIGHT_FILES_PATTERN}
     * @return a {@link Copyright} with full and simple Sets of copyrights. If the a Set is empty, than no copyright was found
     * @throws net.regnology.lucy.service.exceptions.ArchiveException ArchiveException if the archive compression or
     * type is unknown or unsupported
     */
    public static Copyright analyseForCopyright(InputStream input, boolean inAllFiles)
        throws net.regnology.lucy.service.exceptions.ArchiveException {
        try {
            InputStream uncompressedInput = new BufferedInputStream(new CompressorStreamFactory().createCompressorInputStream(input));
            return searchForCopyright(uncompressedInput, inAllFiles);
        } catch (CompressorException e) {
            return searchForCopyright(input, inAllFiles);
        }
    }

    /**
     * Search in an archive InputStream for copyright information.
     *
     * @param input InputStream to search for copyrights.
     * @param inAllFiles if true then search all files, otherwise search only in files like license or readme .
     * @return a {@link Copyright} with full and simple Sets of copyrights. If the a Set is empty, than no copyright was found
     * @throws net.regnology.lucy.service.exceptions.ArchiveException if the archive cannot be read or the archive is not a file or unsupported
     */
    private static Copyright searchForCopyright(InputStream input, boolean inAllFiles)
        throws net.regnology.lucy.service.exceptions.ArchiveException {
        try {
            ArchiveInputStream archiveInputStream = new ArchiveStreamFactory().createArchiveInputStream(input);
            Set<String> copyrightsFromAllFiles = new HashSet<>(4);
            Set<String> copyrightsDocumentationFiles = new HashSet<>(4);

            ArchiveEntry entry = null;
            while (true) {
                try {
                    if ((entry = archiveInputStream.getNextEntry()) == null) break;

                    // If inAllFiles is false, then check if the current file matches the file pattern to search for copyrights
                    /*if (
                        !inAllFiles && !Pattern.compile(COPYRIGHT_FILES_PATTERN, Pattern.CASE_INSENSITIVE).matcher(entry.getName()).find()
                    ) {
                        continue;
                    }*/

                    ByteArrayOutputStream o = new ByteArrayOutputStream(1000);
                    IOUtils.copy(archiveInputStream, o);
                    String content = o.toString(StandardCharsets.UTF_8);

                    Set<String> copyrights = CopyrightAnalyser.extractCopyright(content);
                    copyrightsFromAllFiles.addAll(copyrights);

                    if (Pattern.compile(COPYRIGHT_FILES_PATTERN, Pattern.CASE_INSENSITIVE).matcher(entry.getName()).find()) {
                        copyrightsDocumentationFiles.addAll(copyrights);
                    }
                } catch (IOException e) {
                    log.error("Error while {}", e.getMessage());
                }
            }

            return new Copyright(copyrightsFromAllFiles, copyrightsDocumentationFiles);
        } catch (ArchiveException e) {
            throw new net.regnology.lucy.service.exceptions.ArchiveException(
                "Archive cannot be read. File is not a archive or not supported."
            );
        }
    }
}
