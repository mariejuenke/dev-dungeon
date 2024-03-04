package level;

import core.Game;
import core.level.elements.ILevel;
import core.utils.components.path.IPath;
import core.utils.components.path.SimpleIPath;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class LevelLoader {

  private static final Logger LOGGER = Logger.getLogger(LevelLoader.class.getSimpleName());
  private static final Random RANDOM = new Random();
  private static final String LEVEL_PATH_PREFIX = "levels/";
  private static final Map<Integer, List<String>> levels = new HashMap<>();
  public static int CURRENT_LEVEL = 0;

  static {
    getAllLevelFilePaths();
  }

  private static void getAllLevelFilePaths() {
    try {
      URI uri = Objects.requireNonNull(LevelLoader.class.getResource("/levels")).toURI();
      Path path = Paths.get(uri);
      try (Stream<Path> paths = Files.walk(path)) {
        paths
            .filter(Files::isRegularFile)
            .forEach(
                file -> {
                  String fileName = file.getFileName().toString();
                  if (fileName.endsWith(".level")) {
                    String[] parts = fileName.split("_");
                    if (parts.length == 2) {
                      int levelNumber = Integer.parseInt(parts[0]);
                        String levelFilePath = file.toString();
                        levels.computeIfAbsent(levelNumber, k -> new ArrayList<>()).add(levelFilePath);
                    } else {
                      LOGGER.warning("Invalid level file name: " + fileName);
                    }
                  }
                });
      }
    } catch (IOException | URISyntaxException e) {
      throw new RuntimeException("Error loading level files", e);
    }
  }

  private static ILevel getRandomVariant(int levelNumber) {
    List<String> levelVariants = levels.get(levelNumber);

    if (levelVariants == null || levelVariants.isEmpty()) {
      throw new MissingLevelException(levelNumber);
    }

    // Random Level Variant Path
    IPath levelPath =
        new SimpleIPath(levelVariants.get(RANDOM.nextInt(levelVariants.size())));

    return DevDungeonLevel.loadFromPath(levelPath);
  }

  public static void loadNextLevel() {
    CURRENT_LEVEL++;
    Game.currentLevel(getRandomVariant(CURRENT_LEVEL));
  }
}
