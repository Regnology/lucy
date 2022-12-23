package net.regnology.lucy.service.helper.hashing;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import net.regnology.lucy.domain.helper.PackageInfo;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

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
@Component
public class ArchiveHasherV2 {

    private static final Logger log = LoggerFactory.getLogger(ArchiveHasherV2.class);
    private static final Set<PackageInfo> packageList = Collections.synchronizedSet(new HashSet<>());
    public static final List<InputStream> inputStreams = Collections.synchronizedList(new ArrayList<>(32));

    public ArchiveInputStream createArchiveInputStream(byte[] file) throws ArchiveException {
        ByteArrayInputStream bis = new ByteArrayInputStream(file);
        BufferedInputStream inStream = new BufferedInputStream(bis);

        ArchiveHasherV2.inputStreams.add(inStream);
        ArchiveHasherV2.inputStreams.add(bis);
        try {
            CompressorInputStream compressorInputStream = new CompressorStreamFactory().createCompressorInputStream(inStream);
            BufferedInputStream is = new BufferedInputStream(compressorInputStream);
            ArchiveHasherV2.inputStreams.add(compressorInputStream);
            ArchiveHasherV2.inputStreams.add(is);
            return new ArchiveStreamFactory().createArchiveInputStream(is);
        } catch (CompressorException e) {
            return new ArchiveStreamFactory().createArchiveInputStream(inStream);
        }
    }

    public ArchiveInputStream createArchiveInputStream(InputStream filestream) throws ArchiveException {
        BufferedInputStream inStream = new BufferedInputStream(filestream);
        ArchiveHasherV2.inputStreams.add(inStream);
        try {
            CompressorInputStream compressorInputStream = new CompressorStreamFactory().createCompressorInputStream(inStream);
            BufferedInputStream is = new BufferedInputStream(compressorInputStream);
            ArchiveHasherV2.inputStreams.add(compressorInputStream);
            ArchiveHasherV2.inputStreams.add(is);
            return new ArchiveStreamFactory().createArchiveInputStream(is);
        } catch (CompressorException e) {
            return new ArchiveStreamFactory().createArchiveInputStream(inStream);
        }
    }

    public static ArchiveInputStream createArchiveInputStreamSimple(InputStream filestream) throws ArchiveException {
        return new ArchiveStreamFactory().createArchiveInputStream(new BufferedInputStream(filestream));
    }

    public static ArchiveInputStream createArchiveInputStreamSimpleWithCompress(InputStream filestream) throws ArchiveException {
        BufferedInputStream bis = new BufferedInputStream(filestream);
        try {
            CompressorInputStream compressorInputStream = new CompressorStreamFactory().createCompressorInputStream(bis);
            BufferedInputStream is = new BufferedInputStream(compressorInputStream);
            return new ArchiveStreamFactory().createArchiveInputStream(is);
        } catch (CompressorException e) {
            return new ArchiveStreamFactory().createArchiveInputStream(bis);
        }
    }

    public Set<PackageInfo> getPackageList() {
        return packageList;
    }

    public static Set<PackageInfo> processArchiveStream2(ArchiveInputStream archiveInputStream) {
        final Set<PackageInfo> packageList = new HashSet<>(64);

        ArchiveEntry entry;
        do {
            try {
                entry = archiveInputStream.getNextEntry();
            } catch (IOException e) {
                log.error("Next entry in archive could not be read : {}", e.getMessage());
                break;
            }

            if (entry == null || entry.isDirectory() || !archiveInputStream.canReadEntryData(entry)) continue;

            log.info("Filename : {}", entry.getName());

            MessageDigest md5Digest = DigestUtils.getMd5Digest();
            MessageDigest sha1Digest = DigestUtils.getSha1Digest();

            ByteArrayOutputStream nestedArchiveOutputStream = new ByteArrayOutputStream();
            final int bufSize = 8192;
            final byte[] buf = new byte[bufSize];
            try {
                int read;
                while ((read = archiveInputStream.read(buf)) != -1) {
                    nestedArchiveOutputStream.write(buf, 0, read);
                    md5Digest.update(buf, 0, read);
                    sha1Digest.update(buf, 0, read);
                }
            } catch (IOException e) {
                log.error("ArchiveInputStream is closed : {}", e.getMessage());
            }
            String md5 = Hex.encodeHexString(md5Digest.digest());
            String sha1 = Hex.encodeHexString(sha1Digest.digest());
            log.info("MD5 : {}", md5);
            log.info("SHA-1 : {}", sha1);
            md5Digest.reset();
            sha1Digest.reset();
            /*                String md5 = DigestUtils.md5Hex(nestedArchiveOutputStream.toByteArray());
                log.info("MD5 : {}", md5);
                String sha1 = DigestUtils.sha1Hex(nestedArchiveOutputStream.toByteArray());
                log.info("SHA1 : {}", sha1);*/

            try (ByteArrayInputStream nestedArchiveInputStream = new ByteArrayInputStream(nestedArchiveOutputStream.toByteArray())) {
                nestedArchiveOutputStream.reset();
                nestedArchiveOutputStream = null;

                packageList.add(new PackageInfo(entry.getName(), md5, sha1));

                try (ArchiveInputStream ais = ArchiveHasherV2.createArchiveInputStreamSimpleWithCompress(nestedArchiveInputStream)) {
                    Set<PackageInfo> packagesFromNestedArchive = ArchiveHasherV2.processArchiveStream2(ais);
                    packageList.addAll(packagesFromNestedArchive);
                } catch (IOException | ArchiveException e) {
                    log.error("Could not read archive {} : {}", entry.getName(), e.getMessage());
                }
                nestedArchiveInputStream.reset();
            } catch (IOException e) {
                log.debug("Closing of nested archive stream failed : {}", e.getMessage());
            }
        } while (entry != null);
        System.gc();

        return packageList;
    }

    /*
    Reads an Archive stream and handles directories and files
     */
    @Async("archiveHasherTaskExecutor")
    public CompletableFuture<Void> processArchiveStream(ArchiveInputStream i) {
        List<CompletableFuture<Void>> futures = new ArrayList<>(8);

        try (i) {
            ArchiveEntry entry;
            while ((entry = i.getNextEntry()) != null) {
                if (!i.canReadEntryData(entry)) {
                    log.error("An archive entry could not be read while unpacking!");
                    continue;
                }

                log.debug("File : {}", entry.getName());

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
                MessageDigest md5digest;
                MessageDigest sha1digest;
                try {
                    md5digest = MessageDigest.getInstance("MD5");
                    sha1digest = MessageDigest.getInstance("SHA-1");
                } catch (NoSuchAlgorithmException e) {
                    log.error("Hash could not be generated for file : {}", entry.getName());
                    continue;
                }
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
                    ArchiveHasherV2 ap = new ArchiveHasherV2();
                    ArchiveInputStream ais;
                    try {
                        log.info("Starting new thread to analyse : {}", entry.getName());
                        ais = ap.createArchiveInputStream(outStream.toByteArray());
                        ArchiveHasherV2.inputStreams.add(ais);
                        outStream.close();
                        CompletableFuture<Void> future = processArchiveStream(ais);
                        futures.add(future);
                    } catch (ArchiveException e) {
                        log.error("Archive could not be read : {}", entry.getName());
                        continue;
                    }

                    addAllToPackageList(packageList, ap.getPackageList());
                }
            }
        } catch (IOException e) {
            log.error("I/O exception. Can be ignored.");
        }

        log.info("FINISHED ITERATING OVER ALL FILES OF ONE LAYER");
        log.info("WAITING FOR FINISH OF UNDERLYING FUTURES");

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        log.info("FINISHED UNDERLYING FUTURE!");
        return CompletableFuture.completedFuture(null);
    }

    private void addToPackageList(Set<PackageInfo> packageList, PackageInfo entry) {
        packageList.add(entry);
    }

    private void addAllToPackageList(Set<PackageInfo> packageList, Set<PackageInfo> entries) {
        for (PackageInfo pi : entries) {
            addToPackageList(packageList, pi);
        }
    }

    public static void closeAllStreams() {
        int counter = 1;
        for (InputStream is : ArchiveHasherV2.inputStreams) {
            log.info("InputStream Counter : {}", counter);
            counter++;
            try {
                is.close();
            } catch (IOException e) {
                log.info("Couldn't close stream : {}", e.getMessage());
            }
        }
        ArchiveHasherV2.inputStreams.clear();
    }
}
