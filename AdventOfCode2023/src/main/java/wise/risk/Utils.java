package wise.risk;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Utils {

    public static List<String> openFile(String path) throws IOException, URISyntaxException {
        Path file = Paths.get(Utils.class.getClassLoader().getResource(path).toURI());
        return Files.readAllLines(file);
    }
}
