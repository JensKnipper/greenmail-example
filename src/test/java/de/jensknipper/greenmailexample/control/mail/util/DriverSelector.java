package de.jensknipper.greenmailexample.control.mail.util;

import java.nio.file.FileSystems;
import java.nio.file.Path;

public final class DriverSelector {
    private DriverSelector() {}

    public static Path getDriver(){
        final String osName = System.getProperty("os.name").toLowerCase();
        if (osName.startsWith("windows")) {
            return FileSystems.getDefault().getPath("src/test/resources/geckodriver/geckodriver.exe");
        }
        return FileSystems.getDefault().getPath("src/test/resources/geckodriver/geckodriver");
    }

}
