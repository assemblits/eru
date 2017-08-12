package com.marlontrujillo.eru.util;

import com.marlontrujillo.eru.exception.LoadPropertiesFileException;
import com.marlontrujillo.eru.logger.LogUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import static java.lang.String.format;
import static java.util.stream.Collectors.toMap;

public class PropertiesLoader {

    public Map<String, String> loadPropertiesAsMap(String name) {
        Properties properties = loadProperties(name);
        return properties.keySet().stream()
                .map(keyObject -> (String) keyObject)
                .collect(toMap(key -> key, properties::getProperty));
    }

    private Properties loadProperties(String name) {
        String propertiesFileName = format("%s.properties", name);
        try {
            Properties properties = new Properties();
            properties.load(openStream(propertiesFileName));
            return properties;
        } catch (IOException e) {
            String errorMessage = format("Error trying to load properties file <%s>", propertiesFileName);
            LogUtil.logger.error(errorMessage);
            throw new LoadPropertiesFileException(errorMessage);
        }
    }

    private InputStream openStream(String propertiesFileName) throws IOException {
        return Optional.ofNullable(Thread.currentThread()
                .getContextClassLoader()
                .getResource(propertiesFileName))
                .orElseThrow(() -> {
                    String errorMessage = format("Properties file <%s> not found", propertiesFileName);
                    LogUtil.logger.error(errorMessage);
                    return new LoadPropertiesFileException(errorMessage);
                })
                .openStream();
    }

}
