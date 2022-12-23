package net.regnology.lucy.service.helper.hashing;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import net.regnology.lucy.domain.helper.PackageInfo;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Used for recursively hashing all files of a delivery archive.<br>
 * Usage: <br>
 * Create a new ArchiveHasher with a File input.<br>
 * Call the start method on the new ArchiveHasher.<br>
 * Call the join method on the new ArchiveHasher.<br>
 * A HashSet of Type PackageInfo with the results can be fetched with getPackageList() from the ArchiveHasher.
 *
 * @see PackageInfo
 */
public class ArchiveHasher extends Thread {

    private static final Logger log = LoggerFactory.getLogger(ArchiveHasher.class);
    private final Set<PackageInfo> packageList = Collections.synchronizedSet(new HashSet<>());
    private final ArchiveInputStream archiveInputStream;

    public ArchiveHasher(byte[] file) throws ArchiveException {
        log.debug("IN ARCHIVE HASHER KONSTRUKTOR");
        ArchiveInputStream ais;
        ByteArrayInputStream bis = new ByteArrayInputStream(file);
        BufferedInputStream inStream = new BufferedInputStream(bis);
        try {
            BufferedInputStream is = new BufferedInputStream(new CompressorStreamFactory().createCompressorInputStream(inStream));
            ais = new ArchiveStreamFactory().createArchiveInputStream(is);
        } catch (CompressorException e) {
            ais = new ArchiveStreamFactory().createArchiveInputStream(inStream);
        }
        this.archiveInputStream = ais;
    }

    public ArchiveHasher(InputStream filestream) throws ArchiveException {
        ArchiveInputStream ais;
        BufferedInputStream inStream = new BufferedInputStream(filestream);
        try {
            BufferedInputStream is = new BufferedInputStream(new CompressorStreamFactory().createCompressorInputStream(inStream));
            ais = new ArchiveStreamFactory().createArchiveInputStream(is);
        } catch (CompressorException e) {
            ais = new ArchiveStreamFactory().createArchiveInputStream(inStream);
        }
        this.archiveInputStream = ais;
    }

    public Set<PackageInfo> getPackageList() {
        return packageList;
    }

    /*
    Runs the archive processing thread
     */
    public void run() {
        if (archiveInputStream != null) {
            try {
                log.debug("PROCESSING ARCHIVE STREAM");
                processArchiveStream(archiveInputStream);
            } catch (IOException | ArchiveException | InterruptedException | NoSuchAlgorithmException e) {
                log.error("An archive stream could not be read or a Thread was interrupted! {}", e.getMessage());
            }
        }
    }

    /*
    Reads an Archive stream and handles directories and files
     */
    private void processArchiveStream(ArchiveInputStream i)
        throws IOException, ArchiveException, InterruptedException, NoSuchAlgorithmException {
        ArchiveEntry entry;
        while ((entry = i.getNextEntry()) != null) {
            if (!i.canReadEntryData(entry)) {
                log.error("An archive entry could not be read while unpacking!");
                continue;
            }
            // Found a file, reading bytes
            // If file is a .jar, .war or .zip, add to hash list and unpack further
            // If the file is not an archive, add it to the hash list
            boolean isArchive =
                (
                    entry.getName().endsWith(".jar") ||
                    entry.getName().endsWith(".zip") ||
                    entry.getName().endsWith(".war") ||
                    entry.getName().endsWith(".tar") ||
                    entry.getName().endsWith(".gz")
                );
            // Prepare for hashing
            MessageDigest md5digest = MessageDigest.getInstance("MD5");
            MessageDigest sha1digest = MessageDigest.getInstance("SHA-1");
            // Read bytestream and save what's needed
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = i.read(buffer)) != -1) {
                outStream.write(buffer, 0, length);
                md5digest.update(buffer, 0, length);
                sha1digest.update(buffer, 0, length);
            }

            // Generate MD5 hash
            byte[] bytes = md5digest.digest();
            StringBuilder md5 = new StringBuilder();
            for (byte b : bytes) {
                md5.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
            }
            // Generate SHA-1
            bytes = sha1digest.digest();
            StringBuilder sha1 = new StringBuilder();
            for (byte b : bytes) {
                sha1.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
            }

            if (isArchive) {
                // Save result
                packageList.add(new PackageInfo(entry.getName(), md5.toString(), sha1.toString()));
                // Since we are reading an archive, unpack and read recursively in new thread
                ArchiveHasher ap = new ArchiveHasher(outStream.toByteArray());

                outStream.close();

                ap.start();
                ap.join();
                addAllToPackageList(packageList, ap.getPackageList());
            }
        }
    }

    private void addToPackageList(Set<PackageInfo> packageList, PackageInfo entry) {
        packageList.add(entry);
    }

    private void addAllToPackageList(Set<PackageInfo> packageList, Set<PackageInfo> entries) {
        for (PackageInfo pi : entries) {
            addToPackageList(packageList, pi);
        }
    }
}
