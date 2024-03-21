package utils;

import contrib.components.HealthComponent;
import contrib.utils.components.skill.SkillTools;
import core.Entity;
import core.Game;
import core.components.PositionComponent;
import core.level.Tile;
import core.level.elements.ILevel;
import core.level.elements.tile.ExitTile;
import core.level.utils.Coordinate;
import core.utils.MissingHeroException;
import core.utils.Point;
import core.utils.components.MissingComponentException;
import entities.MonsterType;
import entities.SignFactory;
import entities.TorchFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.logging.Logger;
import level.devlevel.TorchRiddleLevel;

public class EntityUtils {

  private static final Logger LOGGER = Logger.getLogger(EntityUtils.class.getName());

  /**
   * Spawns a monster of the given type at the given position and adds it to the game. The Position
   * is cast to a Tile and the monster is spawned at the center of the tile.
   *
   * @param monsterType the type of monster to spawn
   * @param position the position to spawn the monster; the tile at the given point must be
   *     accessible else the monster will not be spawned
   * @throws MissingComponentException if the monster does not have a PositionComponent
   * @throws RuntimeException if an error occurs while spawning the monster
   * @return the spawned monster
   * @see Game#add(Entity)
   * @see MonsterType
   */
  public static Entity spawnMonster(MonsterType monsterType, Point position) {
    Tile tile = Game.tileAT(position);
    if (tile == null || !tile.isAccessible()) {
      LOGGER.warning(
          "Cannot spawn monster at "
              + position
              + " because the tile is not accessible or does not exist");
      return null;
    }
    return spawnMonster(monsterType, tile.coordinate());
  }

  /**
   * Spawns a monster of the given type at the given coordinate and adds it to the game.
   *
   * @param monsterType the type of monster to spawn
   * @param coordinate the coordinate to spawn the monster; the tile at the given coordinate must be
   *     accessible else the monster will not be spawned
   * @throws MissingComponentException if the monster does not have a PositionComponent
   * @throws RuntimeException if an error occurs while spawning the monster
   * @return the spawned monster
   * @see Game#add(Entity)
   * @see MonsterType
   */
  public static Entity spawnMonster(MonsterType monsterType, Coordinate coordinate) {
    Tile tile = Game.tileAT(coordinate);
    if (tile == null || !tile.isAccessible()) {
      LOGGER.warning(
          "Cannot spawn monster at "
              + coordinate
              + " because the tile is not accessible or does not exist");
      return null;
    }
    try {
      Entity newMob = monsterType.buildMonster();
      PositionComponent positionComponent =
          newMob
              .fetch(PositionComponent.class)
              .orElseThrow(() -> MissingComponentException.build(newMob, PositionComponent.class));
      positionComponent.position(tile.position());
      Game.add(newMob);
      return newMob;
    } catch (IOException e) {
      throw new RuntimeException("Error spawning monster", e);
    }
  }

  /**
   * This method is used to spawn a sign entity in the game at a given position. It uses the
   * SignFactory class to create a new sign with the provided text and title. The sign is then added
   * to the game. If an IOException occurs during the creation of the sign, it is caught and a
   * RuntimeException is thrown.
   *
   * @param text The text to be displayed on the sign.
   * @param title The title of the sign.
   * @param pos The position where the sign should be spawned.
   * @param onInteract The action to perform when the sign is interacted with. (sign, whoTriggered)
   * @return The spawned sign entity.
   * @throws RuntimeException if an error occurs while spawning the sign.
   */
  public static Entity spawnSign(
      String text, String title, Point pos, BiConsumer<Entity, Entity> onInteract) {
    Entity sign = SignFactory.createSign(text, title, pos, onInteract);
    Game.add(sign);
    return sign;
  }

  /**
   * This method is used to spawn a sign entity in the game at a given position. It uses the
   * SignFactory class to create a new sign with the provided text and title. The sign is then added
   * to the game. If an IOException occurs during the creation of the sign, it is caught and a
   * RuntimeException is thrown.
   *
   * @param text The text to be displayed on the sign.
   * @param title The title of the sign.
   * @param pos The position where the sign should be spawned.
   * @return The spawned sign entity.
   * @throws RuntimeException if an error occurs while spawning the sign.
   */
  public static Entity spawnSign(String text, String title, Point pos) {
    return spawnSign(text, title, pos, (e, e2) -> {});
  }

  /**
   * This method is used to get the coordinates of the hero in the game. It uses the SkillTools
   * class to get the hero's position as a point and then converts it to a coordinate. If the hero
   * is missing, it catches the MissingHeroException and returns null.
   *
   * @return Coordinate of the hero's position. If the hero is missing, returns null.
   */
  public static Coordinate getHeroCoords() {
    try {
      return SkillTools.heroPositionAsPoint().toCoordinate();
    } catch (MissingHeroException e) {
      return null;
    }
  }

  /**
   * Spawns a torch at the given coordinate and adds it to the game. The torch is created using the
   * TorchFactory class and is then added to the game.
   *
   * @param torchPos The pos where the torch should be spawned.
   * @param lit The initial state of the torch. True if the torch should be lit, false otherwise.
   * @param isInteractable True if the torch should be interactable, false otherwise.
   * @param value The value of the torch. (Used for {@link TorchRiddleLevel Level 1}).
   * @return The spawned torch entity.
   */
  public static Entity spawnTorch(Point torchPos, boolean lit, boolean isInteractable, int value) {
    return spawnTorch(torchPos, lit, isInteractable, (e, e2) -> {}, value);
  }

  /**
   * Spawns a torch at the given coordinate and adds it to the game. The torch is created using the
   * TorchFactory class and is then added to the game.
   *
   * @param torchPos The pos where the torch should be spawned.
   * @param lit The initial state of the torch. True if the torch should be lit, false otherwise.
   * @param onToggle The action to perform when the torch is toggled. (torch, whoTriggered)
   * @param value The value of the torch. (Used for {@link TorchRiddleLevel Level 1}).
   * @return The spawned torch entity.
   */
  public static Entity spawnTorch(
      Point torchPos,
      boolean lit,
      boolean isInteractable,
      BiConsumer<Entity, Entity> onToggle,
      int value) {
    Entity torch = TorchFactory.createTorch(torchPos, lit, isInteractable, onToggle, value);
    Game.add(torch);
    return torch;
  }

  /**
   * This method is used to spawn a specified number of monsters in the game at random positions.
   * Plus a boss monster.
   *
   * @param mobCount The number of monsters to be spawned. This number cannot be greater than the
   *     length of mobSpawns.
   * @param monsterTypes An array of MonsterType that the monsters can be. A random type is chosen
   *     for each monster.
   * @param mobSpawns An array of Coordinates where the monsters can be spawned. Random coordinates
   *     are chosen from this array.
   * @param bossType The type of the level boss monster.
   * @param levelBossSpawn The Coordinate where the bossType monster (level boss) is to be spawned.
   * @param onBossDeath The action to perform when the level boss monster dies.
   * @return A list of the spawned entities. The last entity in the list is the level boss monster.
   * @throws IllegalArgumentException if mobCount is greater than the length of mobSpawns.
   * @throws RuntimeException if an error occurs while spawning a monster.
   * @see #spawnBoss(MonsterType, Coordinate, Consumer) spawnBoss
   */
  public static List<Entity> spawnMobs(
      int mobCount,
      MonsterType[] monsterTypes,
      Coordinate[] mobSpawns,
      MonsterType bossType,
      Coordinate levelBossSpawn,
      Consumer<Entity> onBossDeath) {
    if (mobCount > mobSpawns.length) {
      throw new IllegalArgumentException("mobCount cannot be greater than mobSpawns.length");
    }

    // Gets a list of random spawn points from the mobSpawns array.
    List<Coordinate> randomSpawns = ArrayUtils.getRandomElements(mobSpawns, mobCount - 1);
    List<Entity> spawnedMobs = new ArrayList<>();
    // Spawns the monsters at the random spawn points.
    for (Coordinate mobPos : randomSpawns) {
      try {
        // Choose a random monster type from the monsterTypes array.
        MonsterType randomType = monsterTypes[ILevel.RANDOM.nextInt(monsterTypes.length)];
        // Spawn the monster at the current spawn point.
        spawnedMobs.add(EntityUtils.spawnMonster(randomType, mobPos));
      } catch (RuntimeException e) {
        throw new RuntimeException("Failed to spawn monster: " + e.getMessage());
      }
    }

    // Spawn the level boss monster.
    spawnedMobs.add(spawnBoss(bossType, levelBossSpawn, onBossDeath));

    return spawnedMobs;
  }

  /**
   * This method is used to spawn a specified number of monsters in the game at random positions.
   * Plus a boss monster.
   *
   * @param mobCount The number of monsters to be spawned. This number cannot be greater than the
   *     length of mobSpawns.
   * @param monsterTypes An array of MonsterType that the monsters can be. A random type is chosen
   *     for each monster.
   * @param mobSpawns An array of Coordinates where the monsters can be spawned. Random coordinates
   *     are chosen from this array.
   * @param bossType The type of the level boss monster.
   * @param levelBossSpawn The Coordinate where the bossType monster (level boss) is to be spawned.
   * @return A list of the spawned entities. The last entity in the list is the level boss monster.
   * @throws IllegalArgumentException if mobCount is greater than the length of mobSpawns.
   * @throws RuntimeException if an error occurs while spawning a monster.
   * @see #spawnMobs(int, MonsterType[], Coordinate[], MonsterType, Coordinate, Consumer) spawnMobs
   */
  public static List<Entity> spawnMobs(
      int mobCount,
      MonsterType[] monsterTypes,
      Coordinate[] mobSpawns,
      MonsterType bossType,
      Coordinate levelBossSpawn) {
    return spawnMobs(mobCount, monsterTypes, mobSpawns, bossType, levelBossSpawn, e -> {});
  }

  /**
   * This method is used to spawn a boss monster in the game at a specified position. When the boss
   * monster dies, the exit of the current level is opened and the onBossDeath action is performed.
   *
   * @param bossType The type of the level boss monster.
   * @param levelBossSpawn The Coordinate where the bossType monster (level boss) is to be spawned.
   * @param onBossDeath The action to perform when the level boss monster dies.
   * @return The spawned boss monster entity.
   * @throws RuntimeException if an error occurs while spawning a monster.
   * @see #spawnMobs(int, MonsterType[], Coordinate[], MonsterType, Coordinate, Consumer) spawnMobs
   */
  public static Entity spawnBoss(
      MonsterType bossType, Coordinate levelBossSpawn, Consumer<Entity> onBossDeath) {
    try {
      Entity bossMob = EntityUtils.spawnMonster(bossType, levelBossSpawn);
      if (bossMob == null) {
        throw new RuntimeException();
      }
      // When the level boss monster dies, open the exit of the current level.
      bossMob
          .fetch(HealthComponent.class)
          .ifPresent(
              hc ->
                  hc.onDeath(
                      (e) -> {
                        ((ExitTile) Game.currentLevel().endTile())
                            .open(); // open exit when chort dies
                        onBossDeath.accept(e);
                      }));
      return bossMob;
    } catch (RuntimeException e) {
      throw new RuntimeException("Failed to spawn level boss monster: " + e.getMessage());
    }
  }

  /**
   * Teleports the hero to a specified point in the game.
   *
   * <p>This method retrieves the hero entity from the game. If the hero entity is null (which can
   * happen if the hero has fallen into a pit), the method returns without doing anything.
   *
   * @param point The point to which the hero should be teleported.
   */
  public static void teleportHeroTo(Point point) {
    Entity hero = Game.hero().orElse(null);
    if (hero == null) {
      return;
    }
    PositionComponent heroPc =
        hero.fetch(PositionComponent.class)
            .orElseThrow(() -> MissingComponentException.build(hero, PositionComponent.class));

    heroPc.position(point);
  }

  /**
   * Retrieves the current position of the hero in the game.
   *
   * <p>This method retrieves the hero entity from the game. If the hero entity is not present
   * (which can happen if the hero has fallen into a pit), the method returns null.
   *
   * @return The current position of the hero, or a default position of (0,0) if the hero is not
   *     present.
   */
  public static Point getHeroPosition() {
    return Game.hero()
        .map(
            e ->
                e.fetch(PositionComponent.class)
                    .orElseThrow(() -> MissingComponentException.build(e, PositionComponent.class))
                    .position())
        .orElse(null);
  }

  /**
   * Retrieves the current coordinates of the hero in the game.
   *
   * <p>This method retrieves the hero entity from the game. If the hero entity is not present
   * (which can happen if the hero has fallen into a pit), the method returns null.
   *
   * @return The current coordinates of the hero, or null if the hero is not present.
   */
  public static Coordinate getHeroCoordinate() {
    Point heroPos = getHeroPosition();
    return heroPos == null ? null : heroPos.toCoordinate();
  }
}
