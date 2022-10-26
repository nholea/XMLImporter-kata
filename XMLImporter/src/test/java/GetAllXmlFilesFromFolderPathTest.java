import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

public class GetAllXmlFilesFromFolderPathTest {

  final Path path = Path.of(
    System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator + "resources");

  @Test
  void readOnlyXmlFilesFromFolderPath() throws IOException {
    final String fileExtension = ".xml";
    List<Path> paths = findAllFiles(path, fileExtension);
    assertThat(paths).hasSize(2);
  }

  private List<Path> findAllFiles(Path folderPath, String fileExtension) throws IOException {
    return Files.walk(folderPath)
      .filter(Files::isRegularFile)
      .filter(filePath ->
        filePath.toString()
          .endsWith(fileExtension))
      .collect(Collectors.toList());
  }

}
