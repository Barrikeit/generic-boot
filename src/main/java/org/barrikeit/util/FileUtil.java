package org.barrikeit.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Normalizer;
import java.util.*;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.barrikeit.util.exceptions.BadRequestException;
import org.barrikeit.util.exceptions.NotFoundException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.multipart.MultipartFile;

@Log4j2
public class FileUtil {
  private FileUtil() {
    throw new IllegalStateException("FileUtil class");
  }

  public static File getResourceFile(String filePath) {
    File file = null;
    try {
      file = new ClassPathResource(filePath).getFile();
    } catch (IOException e) {
      log.error("IOException al intentar obtener el fichero {}, {}", filePath, e.getMessage());
      throw new NotFoundException("Error al obtener el fichero {}.", filePath);
    }
    return file;
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
      FileUtils.copyFile(file, copy);
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

  public static File zip(List<File> files, List<String> fileNames, String zipName) {
    if (files == null || files.isEmpty()) {
      throw new BadRequestException("La lista de archivos a comprimir no puede estar vacía.");
    }

    File zipFile = tempFile(zipName, ".zip");

    try (ZipArchiveOutputStream zos = createZipStream(zipFile)) {
      for (File fileToZip : files) {
        String zipEntryName =
            (fileNames != null && fileNames.size() == files.size())
                ? fileNames.get(files.indexOf(fileToZip))
                : fileToZip.getName();
        addFileToZipStream(zos, fileToZip, zipEntryName);
      }
      zos.finish();
    } catch (IOException e) {
      log.error("Error al crear el archivo zip: {}", e.getMessage());
      throw new BadRequestException("Error al intentar comprimir el fichero en un ZIP.");
    }

    return zipFile;
  }

  public static File unZip(MultipartFile zipFile) {
    if (!Objects.requireNonNull(zipFile.getOriginalFilename()).endsWith(".zip")) {
      throw new BadRequestException("Solo se permiten archivos en formato .zip.");
    }

    File decompressedFile;
    // Leer el contenido del archivo zip
    try (InputStream is = new BufferedInputStream(zipFile.getInputStream());
        ZipArchiveInputStream zis = new ZipArchiveInputStream(is)) {

      ZipArchiveEntry entry;
      while ((entry = zis.getNextEntry()) != null) {
        if (!entry.isDirectory()) {
          String originalName = entry.getName();
          String extension = getFileExtension(originalName);
          String fileName = originalName.replace(extension, "");

          // Crear un archivo temporal
          decompressedFile = tempFile(fileName, extension);

          // Extraer el contenido archivo en el archivo temporal
          try (OutputStream os = new FileOutputStream(decompressedFile)) {
            IOUtils.copy(zis, os);
          }
          return decompressedFile;
        }
      }
    } catch (IOException e) {
      log.error("Error al descomprimir el fichero, no es utf8:\n {}", e.getMessage());
      throw new BadRequestException("El archivo no está codificado en UTF-8.");
    } catch (Exception e) {
      log.error(
          "Error al descomprimir el fichero zip {}:\n {}",
          zipFile.getOriginalFilename(),
          e.getMessage());
      throw new BadRequestException("Error al intentar descomprimir el fichero ZIP.");
    }
    throw new BadRequestException("El archivo ZIP no contiene ningún archivo válido.");
  }

  private static ZipArchiveOutputStream createZipStream(File zipFile) throws IOException {
    FileOutputStream fos = new FileOutputStream(zipFile);
    BufferedOutputStream bos = new BufferedOutputStream(fos);
    ZipArchiveOutputStream zos = new ZipArchiveOutputStream(bos);
    zos.setEncoding("UTF-8");
    zos.setFallbackToUTF8(true);
    return zos;
  }

  private static void addFileToZipStream(ZipArchiveOutputStream zos, File file, String entryName) {
    try (FileInputStream fis = new FileInputStream(file)) {
      ZipArchiveEntry zipEntry = new ZipArchiveEntry(entryName);
      zipEntry.setTime(System.currentTimeMillis());
      zos.putArchiveEntry(zipEntry);
      IOUtils.copy(fis, zos);
      zos.closeArchiveEntry();
    } catch (IOException e) {
      log.error("Error al añadir el fichero {} al zip: {}", file.getName(), e.getMessage());
      throw new BadRequestException("Error al añadir el fichero {0} al zip", file.getName());
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
        .replace(",", "")
        .replace("ñ", "ny")
        .replace("Ñ", "Ny")
        .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
        .replaceAll("[^\\p{ASCII}]", "");
  }
}
