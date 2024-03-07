package level;

import core.Game;
import core.components.PositionComponent;
import core.level.utils.LevelElement;
import core.utils.MissingHeroException;
import core.utils.Point;
import core.utils.components.MissingComponentException;

/**
 * This class is responsible for saving the current state of the dungeon in the game. The "saving"
 * is done by printing the design label of the current level, the position of the hero, and the
 * layout of the current level to the console. This String can then be copied and pasted into a
 * .level file to be loaded later by the {@link DungeonLoader}.
 *
 * @see DungeonLoader
 */
public class DungeonSaver {

  /**
   * The saveCurrentDungeon method is responsible for saving the current state of the dungeon. It
   * does this by first getting the design label of the current level. Then it gets the position of
   * the hero in the game. After that, it compresses the layout of the current level by removing all
   * lines that only contain Empty Tiles. Finally, it concatenates all this information into a
   * single string and prints it.
   */
  public static void saveCurrentDungeon() {
    String designLabel;
    if (Game.currentLevel().endTile() == null) {
      designLabel = Game.currentLevel().randomTile(LevelElement.FLOOR).designLabel().name();
    } else {
      designLabel = Game.currentLevel().endTile().designLabel().name();
    }

    Point heroPos =
        Game.hero()
            .orElseThrow(MissingHeroException::new)
            .fetch(PositionComponent.class)
            .orElseThrow(
                () -> MissingComponentException.build(Game.hero().get(), PositionComponent.class))
            .position();

    // Compress the layout of the current level by removing all lines that only contain 'S'
    String dunLayout = compressDungeonLayout(Game.currentLevel().printLevel());

    String result = designLabel + "\n" + heroPos.x + "," + heroPos.y + "\n" + dunLayout;

    System.out.println(result);
  }

  /**
   * The compressDungeonLayout method takes a multi-line string as input and returns a string where
   * all lines containing only 'S' are removed. It does this by using the replaceAll method with a
   * regular expression that matches lines containing only 'S' and replaces them with an empty
   * string.
   *
   * @param layout The dungeon layout to compress.
   * @return The compressed dungeon layout.
   */
  private static String compressDungeonLayout(String layout) {
    return layout.replaceAll("(?m)^S+$\\n", "");
  }
}
