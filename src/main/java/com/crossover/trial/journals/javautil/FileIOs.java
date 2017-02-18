package com.crossover.trial.journals.javautil;

import java.io.File;

public class FileIOs {
    public static boolean createDirectoryIfNotExist(File dir) {
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            if (!created) {
                return false;
            }
        }
        return true;
    }
}
