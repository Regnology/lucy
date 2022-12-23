package net.regnology.lucy.service.helper.hashing;

import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;

class ArchiveHasherV2Test {

    ArchiveHasherV2 v2;
    File archiveFile;
    File compressedFile;
    File textFile;

    @BeforeEach
    void setUp() {
        v2 = new ArchiveHasherV2();

        try {
            archiveFile = ResourceUtils.getFile("classpath:files/archiveHasher/archive.zip");
            compressedFile = ResourceUtils.getFile("classpath:files/archiveHasher/compressed.tar.xz");
            textFile = ResourceUtils.getFile("classpath:files/archiveHasher/textfile.txt");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testCreateArchiveInputStreamSimpleWithCompress() {
        try (
            InputStream compressedStream = new FileInputStream(compressedFile);
            InputStream archiveStream = new FileInputStream(archiveFile)
        ) {
            v2.createArchiveInputStreamSimpleWithCompress(compressedStream);
            v2.createArchiveInputStreamSimpleWithCompress(archiveStream);
        } catch (ArchiveException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testCreateArchiveInputStreamSimpleWithCompressThrowsException() {
        try (InputStream textFileStream = new FileInputStream(textFile)) {
            assertThrows(ArchiveException.class, () -> v2.createArchiveInputStreamSimpleWithCompress(textFileStream));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testProcessArchiveStream2() {
        System.gc();
        System.out.println(Runtime.getRuntime().freeMemory());
        try (
            InputStream compressedStream = new FileInputStream(compressedFile);
            ArchiveInputStream archiveInputStream = v2.createArchiveInputStreamSimpleWithCompress(compressedStream)
        ) {
            v2.processArchiveStream2(archiveInputStream);
        } catch (ArchiveException | IOException e) {
            throw new RuntimeException(e);
        }
        System.gc();
        System.out.println(Runtime.getRuntime().freeMemory());
    }
}
