import static java.nio.file.Files.walk;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileExtensionFinder {

  public List<Path> findPathsWithSpecificExtension(Path folderPath, String fileExtension) throws IOException {
    //final String fileExtension = ".xml";
    List<Path> paths;
    try (Stream<Path> pathStream = walk(folderPath)
      .filter(Files::isRegularFile)
      .filter(filePath ->
        filePath.toString()
          .endsWith(fileExtension))) {
      paths = pathStream
        .collect(Collectors.toList());
    }
    return paths;
  }

}
