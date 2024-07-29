package com.iav.utils;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;

import static junit.framework.Assert.assertFalse;

final public class LoggerUtils {
    private static Logger LOGGER = null;
    private static final Level LEVEL = Level.INFO;
    private static final String PATTERN = "yyyy/MM/dd HH:mm:ss";
    private LoggerUtils() {}
    final public static String LEFT_PAD= "                            ";

    public  static synchronized Logger getLOGGER() {
        if (LOGGER == null) {
            Path path = Paths.get("log");
            if (!Files.exists(path)) {
                if (new File("log").mkdirs()) {
                    System.out.println("log directory created");
                }
            }
            FileHandler fileHandler;
            Handler consoleHandler;
            Logger rootLogger = Logger.getLogger("");
            Handler[] handlers = rootLogger.getHandlers();
            if (handlers[0] instanceof ConsoleHandler) {
                rootLogger.removeHandler(handlers[0]);
            }
            LOGGER = Logger.getLogger(LoggerUtils.class.getName());
            LOGGER.setLevel(LEVEL);
            consoleHandler = new ConsoleHandler();
            consoleHandler.setLevel(LEVEL);
            try {
                fileHandler = new FileHandler("log//JsonParser.log", true);
                Formatter formatter = new Formatter() {
                    @Override
                    public String format(LogRecord record) {
                        return String.format("%1$s %2$-7s %3$s.%4$s() -> %5$s\n",
                                new SimpleDateFormat(PATTERN).format(new Date(record.getMillis())),
                                record.getLevel().getName(),
                                record.getSourceClassName(),
                                record.getSourceMethodName(),
                                record.getMessage());
                    }
                };
                fileHandler.setFormatter(formatter);
                consoleHandler.setFormatter(formatter);
                LOGGER.addHandler(fileHandler);
                LOGGER.addHandler(consoleHandler);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }


        }

        return LOGGER;
    }

}
