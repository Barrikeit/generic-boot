package org.barrikeit.util;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Normalizer;
import java.util.*;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class FileUtil {
  private FileUtil() {
    throw new IllegalStateException("FileUtil class");
  }

  public static File getResourceFile(String resourcePath) {
    try {
      URL url = Thread.currentThread().getContextClassLoader().getResource(resourcePath);
      if (url == null) {
        log.error("Resource not found on classpath: {}", resourcePath);
        throw new RuntimeException("Resource not found: " + resourcePath);
      }
      File file = new File(url.toURI());
      log.info("Loaded resource from classpath: {}", file);
      return file;
    } catch (URISyntaxException e) {
      log.error("Invalid URI for resource {}: {}", resourcePath, e.getMessage());
      throw new RuntimeException(e);
    }
  }

  public static File tempFile(String fileName, String extension) {
    File temp = null;
    try {
      fileName = sanitizeFileName(fileName);
      String time = TimeUtil.formatLocalDateTime(TimeUtil.localDateTimeNow());
      temp = File.createTempFile("temp_" + fileName + "_" + time + "_", extension);
      temp.deleteOnExit();
    } catch (IOException e) {
      log.error(
          "IOException al intentar crear el fichero temporal {}.\n{}", fileName, e.getMessage());
      throw new RuntimeException("Error al crear el fichero " + fileName + ".");
    }
    return temp;
  }

  public static File copyFile(File file) {
    File copy = null;
    try {
      String originalName = file.getName();
      String extension = getFileExtension(originalName);
      String fileName = originalName.replaceAll(extension, "");

      copy = tempFile(fileName, extension);
      org.apache.commons.io.FileUtils.copyFile(file, copy);
    } catch (IOException e) {
      log.error(
          "IOException al intentar copiar el fichero {}.\n{}", file.getName(), e.getMessage());
    }
    return copy;
  }

  public static void deleteFile(File file) {
    try {
      Path path = file.toPath();
      if (Files.deleteIfExists(path)) {
        log.info("El archivo {} ha sido eliminado correctamente", path.toAbsolutePath());
      } else {
        log.warn("El archivo {} no se ha podido eliminar", path.toAbsolutePath());
      }
    } catch (IOException e) {
      log.error(
          "Error al intentar eliminar el archivo {}: {}", file.getAbsolutePath(), e.getMessage());
    }
  }

  public static String getFileExtension(String filePath) {
    int index = filePath.lastIndexOf('.');
    if (index > 0) {
      return filePath.substring(index);
    } else {
      log.error("Error al obtener extension del archivo para el path {}", filePath);
      return "";
    }
  }

  public static String sanitizeFileName(String fileName) {
    return Normalizer.normalize(fileName.trim(), Normalizer.Form.NFD)
        .replace(" ", "_")
        .replace("ñ", "ny")
        .replace("Ñ", "Ny")
        .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
        .replaceAll("[^\\p{ASCII}]", "");
  }
}
