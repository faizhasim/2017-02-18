package com.crossover.trial.journals.javautil;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.File;

import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class FileIOsTest {
    @Test public void shouldAbleToMkdirRecursively() {
        String subDir1 = RandomStringUtils.randomAscii(5);
        String subDir2 = RandomStringUtils.randomAscii(5);

        File fullPath = new File(System.getProperty("java.io.tmpdir") + File.separator + subDir1 + File.separator + subDir2);

        try {
            assertTrue(FileIOs.createDirectoryIfNotExist(fullPath));
        } finally {
            FileUtils.deleteQuietly(fullPath);
        }
    }
}