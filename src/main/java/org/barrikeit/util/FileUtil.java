package org.barrikeit.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.*;
import lombok.extern.log4j.Log4j2;
import org.barrikeit.util.constants.ConfigurationConstants;
import org.barrikeit.util.constants.ExceptionConstants;
import org.barrikeit.util.exceptions.GenericException;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;

@Log4j2
public class FileUtil {
  private FileUtil() {
    throw new IllegalStateException("FileUtil class");
  }

  public static File createTempFolder(String tempName, int port) {
    try {
      File tempDir = Files.createTempDirectory(tempName + "." + port + ".").toFile();
      tempDir.deleteOnExit();
      return tempDir;
    } catch (IOException e) {
      log.error(e.getMessage());
      throw new GenericException(HttpStatus.BAD_REQUEST, ExceptionConstants.BAD_REQUEST);
    }
  }

  public static boolean scanAndLoadConfigFiles(MutablePropertySources propertySources) {
    for (String path : ConfigurationConstants.CONFIG_LOCATIONS) {
      for (String extension : ConfigurationConstants.CONFIG_EXTENSIONS) {
        if (loadFile(propertySources, resolvePath(path, "application." + extension))) {
          List<String> imports = getPropertyList(propertySources, "spring.config.import");
          imports.forEach(importFile -> loadFile(propertySources, resolvePath(path, importFile)));
          return true;
        }
      }
    }
    return false;
  }

  private static boolean loadFile(MutablePropertySources propertySources, String filePath) {
    Resource resource = new ClassPathResource(filePath);
    if (!resource.exists()) {
      // log.warn("Class path resource [{}] does not exist", filePath);
      return false;
    }

    log.info("Loading config file: {}", filePath);
    if (filePath.endsWith(".properties")) {
      return loadPropertiesFile(propertySources, resource, filePath);
    } else if (filePath.endsWith(".yaml") || filePath.endsWith(".yml")) {
      return loadYamlFile(propertySources, resource, filePath);
    }
    return false;
  }

  private static boolean loadPropertiesFile(
      MutablePropertySources propertySources, Resource resource, String propertiesFile) {
    try (InputStream input = resource.getInputStream()) {
      Properties properties = new Properties();
      properties.load(input);
      propertySources.addLast(new PropertiesPropertySource(propertiesFile, properties));
      return true;
    } catch (IOException e) {
      log.warn("Cannot resolve path for properties file: {}", propertiesFile, e);
      return false;
    }
  }

  private static boolean loadYamlFile(
      MutablePropertySources propertySources, Resource resource, String yamlFile) {
    YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
    yaml.setResources(resource);
    Properties properties = yaml.getObject();
    if (properties != null) {
      propertySources.addLast(new PropertiesPropertySource(yamlFile, properties));
      return true;
    }
    log.warn("Failed to load YAML file: {}", yamlFile);
    return false;
  }

  private static String resolvePath(String basePath, String importFile) {
    if (importFile.startsWith("classpath:") || importFile.startsWith("/")) {
      return importFile;
    }
    basePath = basePath.endsWith("/") ? basePath.substring(0, basePath.length() - 1) : basePath;
    return (importFile.startsWith("/") || importFile.startsWith("../"))
        ? basePath + importFile
        : basePath + "/" + importFile;
  }

  private static List<String> getPropertyList(
      MutablePropertySources propertySources, String keyPrefix) {
    List<String> values = new ArrayList<>();
    propertySources.forEach(
        source -> {
          if (source instanceof EnumerablePropertySource<?> enumerableSource) {
            Arrays.stream(enumerableSource.getPropertyNames())
                .filter(propertyName -> propertyName.startsWith(keyPrefix))
                .map(enumerableSource::getProperty)
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .forEach(values::add);
          }
        });
    return values;
  }
}
