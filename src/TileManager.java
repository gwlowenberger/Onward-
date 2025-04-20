import java.awt.*;
import java.util.ArrayList;

public final class TileManager {

    public static final int TILE_WIDTH = 32;
    private static int gridSize;
    private static AnimatedActor[][] tiles = new AnimatedActor[0][0];

    //players
    @SuppressWarnings("Convert2Diamond")
    private static ArrayList<Player> playersList = new ArrayList<Player>(0);
    private static int previousPlayerClicked = -1;
    @SuppressWarnings("Convert2Diamond")
    private static ArrayList<Enemy> enemiesList = new ArrayList<Enemy>(0);

    @SuppressWarnings("Convert2Diamond")
    public TileManager(int gridSize) {

        playersList = new ArrayList<Player>(0);
        enemiesList = new ArrayList<Enemy>(0);

        TileManager.gridSize = Math.max(gridSize, 2);

        CreateTiles();
    }

        //creation
    //#region
    private static void CreateTiles() {
        tiles = new AnimatedActor[gridSize][gridSize];
        float halfWidth = TILE_WIDTH * 0.5f; //DO NOT CHANGE

        //x = 0.5 to touch horizontally, 0.6 to be close, 0.75 to be far
        //y = 0.25 to touch vertically, 0.28 to be close, 0.3333 to be far
        Vector tileOffsets = new Vector(TILE_WIDTH * MyPanel.tilePercentOffsets.x, TILE_WIDTH * MyPanel.tilePercentOffsets.y);

        float horizontalOffset = 0;
        float verticalOffset = tileOffsets.y * gridSize / -2 + TILE_WIDTH - 50;
        float centerOffset = (gridSize + 1) * TILE_WIDTH * 0.25f;

        Vector colliderOffset = new Vector(TILE_WIDTH / 4 + 0.5f, 0.5f); //new Vector(8, 4);
        Vector collisionRect = new Vector(TILE_WIDTH / 2 - -0.25f, TILE_WIDTH / 2 - 0.25f); //new Vector(16, 8);

        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                Vector position = new Vector();
                position.x = (MyPanel.width / 2) + j * tileOffsets.x - i * tileOffsets.x - halfWidth + horizontalOffset;
                position.y = (MyPanel.height / 2) + j * tileOffsets.y + i * tileOffsets.y - centerOffset + verticalOffset;
                tiles[i][j] = new AnimatedActor(position, collisionRect, colliderOffset, Vector.defaultSpriteDimensions, 
                    new Animation[]{new Animation("Sprites/Tiles/Tile" + (int) (Math.random() * 8) + ".png"),
                                    new MobileAnimation("Sprites/Tiles/DisabledTile.png", Vector.defaultSpriteDimensions, 10, new Vector[] {Vector.south, Vector.north}),
                                    new Animation("Sprites/Tiles/WinTile.png")});
            }
        }

        betweenTiles.x = tiles[1][0].position.x - tiles[0][0].position.x;
        betweenTiles.y = tiles[1][0].position.y - tiles[0][0].position.y;

        southWest.x = betweenTiles.x * 0.25f;
        southWest.y = betweenTiles.y * 0.25f;

        southEast.x = betweenTiles.x * -0.25f;
        southEast.y = betweenTiles.y * 0.25f;

        northWest.x = betweenTiles.x * 0.25f;
        northWest.y = betweenTiles.y * -0.25f;

        northEast.x = betweenTiles.x * -0.25f;
        northEast.y = betweenTiles.y * -0.25f;

        fall.x = 0;
        fall.y = 8 * MyPanel.PIXEL_SCALE_FACTOR;
    }

    @SuppressWarnings("Convert2Diamond")
    public static void randomlyCreateNewPlayers() {

        Player[] possiblePlayers = Players();

        Enemy[] possibleEnemies = Enemies();

        //DO NOT ASSIGN MULTIPLE PLAYERS TO THE SAME SQUARE
        int playerTracker = 0;
        int enemyTracker = 0;
        for (int i = 0; i < gridSize * gridSize; i++) {
            if (Math.random() <= MyPanel.playerSpawnChance && playerTracker < MyPanel.maxPlayers)
            {
                playersList.add(new Player(possiblePlayers[(int) (Math.random() * possiblePlayers.length)]));
                Vector pos = new Vector(i % gridSize, i / gridSize % gridSize);
                playersList.get(playerTracker).tilePosition = pos;
                playersList.get(playerTracker).position = GetTilePositionWithOffset(pos);
                playerTracker++;
            }
            else if (Math.random() <= MyPanel.enemySpawnChance && enemyTracker < MyPanel.maxEnemies)
            {
                enemiesList.add(new Enemy(possibleEnemies[(int) (Math.random() * possibleEnemies.length)]));
                Vector pos = new Vector(i % gridSize, i / gridSize % gridSize);
                enemiesList.get(enemyTracker).tilePosition = pos;
                enemiesList.get(enemyTracker).position = GetTilePositionWithOffset(pos);
                enemyTracker++;
            }
        }

        //if no enemies or players spawn, a player is spawned right next to an enemy
        //if gridSize < 2, this method fails. Gridsize is set to a minimum of 2 upon initialization
        if (playersList.isEmpty() || enemiesList.isEmpty()) {
            playersList = new ArrayList<Player>();
            enemiesList = new ArrayList<Enemy>();

            playersList.add(new Player(possiblePlayers[(int) (Math.random() * possiblePlayers.length)]));
            Vector pos = new Vector((int) (Math.random() * gridSize), (int) (Math.random() * gridSize));
            playersList.get(0).tilePosition = pos;
            playersList.get(0).position = GetTilePositionWithOffset(pos);

            enemiesList.add(new Enemy(possibleEnemies[(int) (Math.random() * possibleEnemies.length)]));
                
            Vector ePos = new Vector(pos);
                
            if (ePos.x > 0) {
                ePos.x--;
            }
            else {
                ePos.x++;
            }

            enemiesList.get(0).tilePosition = ePos;
            enemiesList.get(0).position = GetTilePositionWithOffset(ePos);
        }

        ResetEnemies();
    }

    @SuppressWarnings("ManualArrayToCollectionCopy")
    public static void createNewPlayers(Player[] players, Enemy[] enemies) {
        for (Player player : players) {
            playersList.add(player);
        }
        
        for (Enemy enemy : enemies) {
            enemiesList.add(enemy);
        }
        
        ResetEnemies();
    }

    public static void NextRound(int gridSize) {
        TileManager.gridSize = gridSize = Math.max(gridSize, 2);

        CreateTiles();

        for (int i = 0; i < playersList.size(); i++) {
            Player p = playersList.get(i);
            p.tilePosition = new Vector(gridSize - i - 1, gridSize - 1);
            p.position = GetTilePositionWithOffset(p.tilePosition);
        }

        Enemy[] possibleEnemies = Enemies();

        int enemyTracker = 0;
        for (int i = 0; i < gridSize * (gridSize - 1); i++) {
            if (Math.random() <= MyPanel.enemySpawnChance && enemyTracker < MyPanel.maxEnemies)
            {
                enemiesList.add(new Enemy(possibleEnemies[(int) (Math.random() * possibleEnemies.length)]));
                Vector pos = new Vector(i % gridSize, i / gridSize % gridSize);
                enemiesList.get(enemyTracker).tilePosition = pos;
                enemiesList.get(enemyTracker).position = GetTilePositionWithOffset(pos);
                enemyTracker++;
            }
        }

        if (enemiesList.isEmpty()) {
            enemiesList.add(new Enemy(possibleEnemies[(int) (Math.random() * possibleEnemies.length)]));
            Vector pos = new Vector((int) (Math.random() * gridSize), (int) (Math.random() * (gridSize - 1)));
            enemiesList.get(0).tilePosition = pos;
            enemiesList.get(0).position = GetTilePositionWithOffset(pos);
        }

        ResetEnemies();
    }

    @SuppressWarnings("Convert2Diamond")
    private static void ResetEnemies() {
        ArrayList<Enemy> removeEnemies = new ArrayList<Enemy>(0);
        for (Enemy enemy : enemiesList) {
            if (enemy == null) {
                removeEnemies.add(enemy);
            }
        }
        enemiesList.removeAll(removeEnemies);

        if (enemiesList.isEmpty()) {
            MyPanel.gameState = MyPanel.STATE_WIN_GAME;
        }

        for (int i = 0; i < enemiesList.size(); i++) {
            enemiesList.get(i).SetNumber(i + 1);
        }
        
        //we account for players after enemies, because the final gamestate that is set is what changes when the code finishes.
        ArrayList<Player> removePlayers = new ArrayList<Player>(0);
        for (Player player : playersList) {
            if (player == null) {
                removePlayers.add(player);
            }
        }
        playersList.removeAll(removePlayers);

        if (playersList.isEmpty()) {
            MyPanel.gameState = MyPanel.STATE_LOSE;
        }
    }

    private static Player[] Players() {
        return new Player[] {
            //Fox
            new Player(Vector.defaultSpriteCollider, Vector.defaultSpriteColliderOffset, Vector.quarterSpriteDimensions,
                    new Animation[] {
                        new Animation("Sprites/Players/Fox/Idle.png", Vector.quarterSpriteDimensions,40), //IDLE
                        new Animation("Sprites/Players/Fox/Click.png", Vector.quarterSpriteDimensions, 12), //CLICK
                        new Animation(),//new Animation("Sprites/Players/DefaultPlayer.png", Vector.quarterSpriteDimensions), //HOVER
    
                        new MobileAnimation("Sprites/Players/Fox/Fall.png", Vector.quarterSpriteDimensions, 10, new Vector[] //FALLING
                            {fall, fall, fall, fall, fall, fall, fall, fall, fall, fall, fall, fall,}), 

                        new MobileAnimation("Sprites/Players/Fox/SouthWest.png", Vector.quarterSpriteDimensions, 12, new Vector[] //SOUTHWEST
                            {southWest, southWest, southWest, southWest,}),
                        new MobileAnimation("Sprites/Players/Fox/SouthEast.png", Vector.quarterSpriteDimensions, 12, new Vector[] //SOUTHEAST
                            {southEast, southEast, southEast, southEast,}),
                        new MobileAnimation("Sprites/Players/Fox/NorthWest.png", Vector.quarterSpriteDimensions, 12, new Vector[] //NORTHWEST
                            {northWest, northWest, northWest, northWest,}),
                        new MobileAnimation("Sprites/Players/Fox/NorthEast.png", Vector.quarterSpriteDimensions, 12, new Vector[] //NORTHEAST
                            {northEast, northEast, northEast, northEast,}),

                        new Animation("Sprites/Players/Fox/Idle.png", Vector.quarterSpriteDimensions,40), //NONE MOVE

                        new Animation("Sprites/Players/Fox/Death.png", Vector.quarterSpriteDimensions,20), //DEATH
                    }),
            //Bunny
            new Player(Vector.defaultSpriteCollider, Vector.defaultSpriteColliderOffset, Vector.quarterSpriteDimensions,
                    new Animation[] {
                        new Animation("Sprites/Players/Bunny/Idle.png", Vector.quarterSpriteDimensions,40), //IDLE
                        new Animation("Sprites/Players/Bunny/Click.png", Vector.quarterSpriteDimensions, 12), //CLICK
                        new Animation(),//new Animation("Sprites/Players/DefaultPlayer.png", Vector.quarterSpriteDimensions), //HOVER
    
                        new MobileAnimation("Sprites/Players/Bunny/Fall.png", Vector.quarterSpriteDimensions, 10, new Vector[] //FALLING
                            {fall, fall, fall, fall, fall, fall, fall, fall, fall, fall, fall, fall,}), 

                        new MobileAnimation("Sprites/Players/Bunny/SouthWest.png", Vector.quarterSpriteDimensions, 12, new Vector[] //SOUTHWEST
                            {southWest, southWest, southWest, southWest,}),
                        new MobileAnimation("Sprites/Players/Bunny/SouthEast.png", Vector.quarterSpriteDimensions, 12, new Vector[] //SOUTHEAST
                            {southEast, southEast, southEast, southEast,}),
                        new MobileAnimation("Sprites/Players/Bunny/NorthWest.png", Vector.quarterSpriteDimensions, 12, new Vector[] //NORTHWEST
                            {northWest, northWest, northWest, northWest,}),
                        new MobileAnimation("Sprites/Players/Bunny/NorthEast.png", Vector.quarterSpriteDimensions, 12, new Vector[] //NORTHEAST
                            {northEast, northEast, northEast, northEast,}),

                        new Animation("Sprites/Players/Bunny/Idle.png", Vector.quarterSpriteDimensions,40), //NONE MOVE

                        new Animation("Sprites/Players/Bunny/Death.png", Vector.quarterSpriteDimensions,20), //DEATH
                    }),
            //Pig
            new Player(Vector.defaultSpriteCollider, Vector.defaultSpriteColliderOffset, Vector.quarterSpriteDimensions,
                    new Animation[] {
                        new Animation("Sprites/Players/Pig/Idle.png", Vector.quarterSpriteDimensions,40), //IDLE
                        new Animation("Sprites/Players/Pig/Click.png", Vector.quarterSpriteDimensions, 12), //CLICK
                        new Animation(),//new Animation("Sprites/Players/DefaultPlayer.png", Vector.quarterSpriteDimensions), //HOVER
    
                        new MobileAnimation("Sprites/Players/Pig/Fall.png", Vector.quarterSpriteDimensions, 10, new Vector[] //FALLING
                            {fall, fall, fall, fall, fall, fall, fall, fall, fall, fall, fall, fall,}), 

                        new MobileAnimation("Sprites/Players/Pig/SouthWest.png", Vector.quarterSpriteDimensions, 12, new Vector[] //SOUTHWEST
                            {southWest, southWest, southWest, southWest,}),
                        new MobileAnimation("Sprites/Players/Pig/SouthEast.png", Vector.quarterSpriteDimensions, 12, new Vector[] //SOUTHEAST
                            {southEast, southEast, southEast, southEast,}),
                        new MobileAnimation("Sprites/Players/Pig/NorthWest.png", Vector.quarterSpriteDimensions, 12, new Vector[] //NORTHWEST
                            {northWest, northWest, northWest, northWest,}),
                        new MobileAnimation("Sprites/Players/Pig/NorthEast.png", Vector.quarterSpriteDimensions, 12, new Vector[] //NORTHEAST
                            {northEast, northEast, northEast, northEast,}),

                        new Animation("Sprites/Players/Pig/Idle.png", Vector.quarterSpriteDimensions,40), //NONE MOVE

                        new Animation("Sprites/Players/Pig/Death.png", Vector.quarterSpriteDimensions,20), //DEATH
                    }),
        };
    }

    private static Enemy[] Enemies() {
        return new Enemy[] {
            //Snakes - - - - - - - - - - - - - - - - - - - - - - - - - - - -

            //(0) Snake #1
            new Enemy(Vector.zero, Vector.defaultSpriteColliderOffset, Vector.quarterSpriteDimensions,
                    new Animation[] {
                        new Animation("Sprites/Players/Snake/SnakeIdle.png", Vector.quarterSpriteDimensions, 20), //IDLE
                        new Animation("Sprites/Players/Snake/SnakeIdle.png", Vector.quarterSpriteDimensions, 20), //CLICK
                        new Animation(), //HOVER

                        new MobileAnimation("Sprites/Players/Snake/SnakeIdle.png", Vector.quarterSpriteDimensions, 10, new Vector[] //FALLING
                            {fall, fall, fall, fall, fall, fall, fall, fall, fall, fall, fall, fall,}), 
    
                        new MobileAnimation("Sprites/Players/Snake/SnakeJump.png", Vector.quarterSpriteDimensions, 12, new Vector[] //SOUTHWEST
                            {southWest, southWest, southWest, southWest,}),
                        new MobileAnimation("Sprites/Players/Snake/SnakeJump.png", Vector.quarterSpriteDimensions, 12, new Vector[] //SOUTHEAST
                            {southEast, southEast, southEast, southEast,}),
                        new MobileAnimation("Sprites/Players/Snake/SnakeJump.png", Vector.quarterSpriteDimensions, 12, new Vector[] //NORTHWEST
                            {northWest, northWest, northWest, northWest,}),
                        new MobileAnimation("Sprites/Players/Snake/SnakeJump.png", Vector.quarterSpriteDimensions, 12, new Vector[] //NORTHEAST
                            {northEast, northEast, northEast, northEast,}),

                        new Animation("Sprites/Players/Snake/SnakeIdle.png", Vector.quarterSpriteDimensions,5), //NONE MOVE
                    },
                    new Vector[] {Vector.north}),
            //(1) Snake #2
            new Enemy(Vector.zero, Vector.defaultSpriteColliderOffset, Vector.quarterSpriteDimensions,
                    new Animation[] {
                        new Animation("Sprites/Players/Snake/SnakeIdle.png", Vector.quarterSpriteDimensions, 20), //IDLE
                        new Animation("Sprites/Players/Snake/SnakeIdle.png", Vector.quarterSpriteDimensions, 20), //CLICK
                        new Animation(), //HOVER

                        new MobileAnimation("Sprites/Players/Snake/SnakeIdle.png", Vector.quarterSpriteDimensions, 10, new Vector[] //FALLING
                            {fall, fall, fall, fall, fall, fall, fall, fall, fall, fall, fall, fall,}), 
    
                        new MobileAnimation("Sprites/Players/Snake/SnakeJump.png", Vector.quarterSpriteDimensions, 12, new Vector[] //SOUTHWEST
                            {southWest, southWest, southWest, southWest,}),
                        new MobileAnimation("Sprites/Players/Snake/SnakeJump.png", Vector.quarterSpriteDimensions, 12, new Vector[] //SOUTHEAST
                            {southEast, southEast, southEast, southEast,}),
                        new MobileAnimation("Sprites/Players/Snake/SnakeJump.png", Vector.quarterSpriteDimensions, 12, new Vector[] //NORTHWEST
                            {northWest, northWest, northWest, northWest,}),
                        new MobileAnimation("Sprites/Players/Snake/SnakeJump.png", Vector.quarterSpriteDimensions, 12, new Vector[] //NORTHEAST
                            {northEast, northEast, northEast, northEast,}),

                        new Animation("Sprites/Players/Snake/SnakeIdle.png", Vector.quarterSpriteDimensions,5), //NONE MOVE
                    },
                    new Vector[] {Vector.south}),
            //(2) Snake #3
            new Enemy(Vector.zero, Vector.defaultSpriteColliderOffset, Vector.quarterSpriteDimensions,
                    new Animation[] {
                        new Animation("Sprites/Players/Snake/SnakeIdle.png", Vector.quarterSpriteDimensions, 20), //IDLE
                        new Animation("Sprites/Players/Snake/SnakeIdle.png", Vector.quarterSpriteDimensions, 20), //CLICK
                        new Animation(), //HOVER

                        new MobileAnimation("Sprites/Players/Snake/SnakeIdle.png", Vector.quarterSpriteDimensions, 10, new Vector[] //FALLING
                            {fall, fall, fall, fall, fall, fall, fall, fall, fall, fall, fall, fall,}), 
    
                        new MobileAnimation("Sprites/Players/Snake/SnakeJump.png", Vector.quarterSpriteDimensions, 12, new Vector[] //SOUTHWEST
                            {southWest, southWest, southWest, southWest,}),
                        new MobileAnimation("Sprites/Players/Snake/SnakeJump.png", Vector.quarterSpriteDimensions, 12, new Vector[] //SOUTHEAST
                            {southEast, southEast, southEast, southEast,}),
                        new MobileAnimation("Sprites/Players/Snake/SnakeJump.png", Vector.quarterSpriteDimensions, 12, new Vector[] //NORTHWEST
                            {northWest, northWest, northWest, northWest,}),
                        new MobileAnimation("Sprites/Players/Snake/SnakeJump.png", Vector.quarterSpriteDimensions, 12, new Vector[] //NORTHEAST
                            {northEast, northEast, northEast, northEast,}),

                        new Animation("Sprites/Players/Snake/SnakeIdle.png", Vector.quarterSpriteDimensions,5), //NONE MOVE
                    },
                    new Vector[] {Vector.west}),
            //(3) Snake #4
            new Enemy(Vector.zero, Vector.defaultSpriteColliderOffset, Vector.quarterSpriteDimensions,
                    new Animation[] {
                        new Animation("Sprites/Players/Snake/SnakeIdle.png", Vector.quarterSpriteDimensions, 20), //IDLE
                        new Animation("Sprites/Players/Snake/SnakeIdle.png", Vector.quarterSpriteDimensions, 20), //CLICK
                        new Animation(), //HOVER

                        new MobileAnimation("Sprites/Players/Snake/SnakeIdle.png", Vector.quarterSpriteDimensions, 10, new Vector[] //FALLING
                            {fall, fall, fall, fall, fall, fall, fall, fall, fall, fall, fall, fall,}), 
    
                        new MobileAnimation("Sprites/Players/Snake/SnakeJump.png", Vector.quarterSpriteDimensions, 12, new Vector[] //SOUTHWEST
                            {southWest, southWest, southWest, southWest,}),
                        new MobileAnimation("Sprites/Players/Snake/SnakeJump.png", Vector.quarterSpriteDimensions, 12, new Vector[] //SOUTHEAST
                            {southEast, southEast, southEast, southEast,}),
                        new MobileAnimation("Sprites/Players/Snake/SnakeJump.png", Vector.quarterSpriteDimensions, 12, new Vector[] //NORTHWEST
                            {northWest, northWest, northWest, northWest,}),
                        new MobileAnimation("Sprites/Players/Snake/SnakeJump.png", Vector.quarterSpriteDimensions, 12, new Vector[] //NORTHEAST
                            {northEast, northEast, northEast, northEast,}),
                        
                        new Animation("Sprites/Players/Snake/SnakeIdle.png", Vector.quarterSpriteDimensions,5), //NONE MOVE
                    },
                    new Vector[] {Vector.east}),

            //Crocs - - - - - - - - - - - - - - - - - - - - - - - - - - - -

            //(4) Croc #1
            new Enemy(Vector.zero, Vector.defaultSpriteColliderOffset, Vector.quarterSpriteDimensions,
                    new Animation[] {
                        new Animation("Sprites/Players/Croc/Idle.png", Vector.quarterSpriteDimensions, 30), //IDLE
                        new Animation("Sprites/Players/Croc/Idle.png", Vector.quarterSpriteDimensions, 20), //CLICK
                        new Animation(), //HOVER

                        new MobileAnimation("Sprites/Players/Croc/Fall.png", Vector.quarterSpriteDimensions, 10, new Vector[] //FALLING
                            {fall, fall, fall, fall, fall, fall, fall, fall, fall, fall, fall, fall,}), 
    
                        new MobileAnimation("Sprites/Players/Croc/SouthWest.png", Vector.quarterSpriteDimensions, 12, new Vector[] //SOUTHWEST
                            {southWest, southWest, southWest, southWest,}),
                        new MobileAnimation("Sprites/Players/Croc/SouthEast.png", Vector.quarterSpriteDimensions, 12, new Vector[] //SOUTHEAST
                            {southEast, southEast, southEast, southEast,}),
                        new MobileAnimation("Sprites/Players/Croc/NorthWest.png", Vector.quarterSpriteDimensions, 12, new Vector[] //NORTHWEST
                            {northWest, northWest, northWest, northWest,}),
                        new MobileAnimation("Sprites/Players/Croc/NorthEast.png", Vector.quarterSpriteDimensions, 12, new Vector[] //NORTHEAST
                            {northEast, northEast, northEast, northEast,}),

                        new Animation("Sprites/Players/Croc/Idle.png", Vector.quarterSpriteDimensions,5), //NONE MOVE
                    },
                    new Vector[] {Vector.east, Vector.zero, Vector.north, Vector.zero, Vector.west, Vector.zero, Vector.south, Vector.zero}),
            //(5) Croc #2
            new Enemy(Vector.zero, Vector.defaultSpriteColliderOffset, Vector.quarterSpriteDimensions,
                    new Animation[] {
                        new Animation("Sprites/Players/Croc/Idle.png", Vector.quarterSpriteDimensions, 30), //IDLE
                        new Animation("Sprites/Players/Croc/Idle.png", Vector.quarterSpriteDimensions, 20), //CLICK
                        new Animation(), //HOVER

                        new MobileAnimation("Sprites/Players/Croc/Fall.png", Vector.quarterSpriteDimensions, 10, new Vector[] //FALLING
                            {fall, fall, fall, fall, fall, fall, fall, fall, fall, fall, fall, fall,}), 
    
                        new MobileAnimation("Sprites/Players/Croc/SouthWest.png", Vector.quarterSpriteDimensions, 12, new Vector[] //SOUTHWEST
                            {southWest, southWest, southWest, southWest,}),
                        new MobileAnimation("Sprites/Players/Croc/SouthEast.png", Vector.quarterSpriteDimensions, 12, new Vector[] //SOUTHEAST
                            {southEast, southEast, southEast, southEast,}),
                        new MobileAnimation("Sprites/Players/Croc/NorthWest.png", Vector.quarterSpriteDimensions, 12, new Vector[] //NORTHWEST
                            {northWest, northWest, northWest, northWest,}),
                        new MobileAnimation("Sprites/Players/Croc/NorthEast.png", Vector.quarterSpriteDimensions, 12, new Vector[] //NORTHEAST
                            {northEast, northEast, northEast, northEast,}),

                        new Animation("Sprites/Players/Croc/Idle.png", Vector.quarterSpriteDimensions,5), //NONE MOVE
                    },
                    new Vector[] {Vector.north, Vector.zero, Vector.west, Vector.zero, Vector.south, Vector.zero, Vector.east, Vector.zero}),
            //(6) Croc #3
            new Enemy(Vector.zero, Vector.defaultSpriteColliderOffset, Vector.quarterSpriteDimensions,
                    new Animation[] {
                        new Animation("Sprites/Players/Croc/Idle.png", Vector.quarterSpriteDimensions, 30), //IDLE
                        new Animation("Sprites/Players/Croc/Idle.png", Vector.quarterSpriteDimensions, 20), //CLICK
                        new Animation(), //HOVER

                        new MobileAnimation("Sprites/Players/Croc/Fall.png", Vector.quarterSpriteDimensions, 10, new Vector[] //FALLING
                            {fall, fall, fall, fall, fall, fall, fall, fall, fall, fall, fall, fall,}), 
    
                        new MobileAnimation("Sprites/Players/Croc/SouthWest.png", Vector.quarterSpriteDimensions, 12, new Vector[] //SOUTHWEST
                            {southWest, southWest, southWest, southWest,}),
                        new MobileAnimation("Sprites/Players/Croc/SouthEast.png", Vector.quarterSpriteDimensions, 12, new Vector[] //SOUTHEAST
                            {southEast, southEast, southEast, southEast,}),
                        new MobileAnimation("Sprites/Players/Croc/NorthWest.png", Vector.quarterSpriteDimensions, 12, new Vector[] //NORTHWEST
                            {northWest, northWest, northWest, northWest,}),
                        new MobileAnimation("Sprites/Players/Croc/NorthEast.png", Vector.quarterSpriteDimensions, 12, new Vector[] //NORTHEAST
                            {northEast, northEast, northEast, northEast,}),

                        new Animation("Sprites/Players/Croc/Idle.png", Vector.quarterSpriteDimensions,5), //NONE MOVE
                    },
                    new Vector[] {Vector.west, Vector.zero, Vector.south, Vector.zero, Vector.east, Vector.zero, Vector.north, Vector.zero}),
            //(7) Croc #4
            new Enemy(Vector.zero, Vector.defaultSpriteColliderOffset, Vector.quarterSpriteDimensions,
                    new Animation[] {
                        new Animation("Sprites/Players/Croc/Idle.png", Vector.quarterSpriteDimensions, 30), //IDLE
                        new Animation("Sprites/Players/Croc/Idle.png", Vector.quarterSpriteDimensions, 20), //CLICK
                        new Animation(), //HOVER

                        new MobileAnimation("Sprites/Players/Croc/Fall.png", Vector.quarterSpriteDimensions, 10, new Vector[] //FALLING
                            {fall, fall, fall, fall, fall, fall, fall, fall, fall, fall, fall, fall,}), 
    
                        new MobileAnimation("Sprites/Players/Croc/SouthWest.png", Vector.quarterSpriteDimensions, 12, new Vector[] //SOUTHWEST
                            {southWest, southWest, southWest, southWest,}),
                        new MobileAnimation("Sprites/Players/Croc/SouthEast.png", Vector.quarterSpriteDimensions, 12, new Vector[] //SOUTHEAST
                            {southEast, southEast, southEast, southEast,}),
                        new MobileAnimation("Sprites/Players/Croc/NorthWest.png", Vector.quarterSpriteDimensions, 12, new Vector[] //NORTHWEST
                            {northWest, northWest, northWest, northWest,}),
                        new MobileAnimation("Sprites/Players/Croc/NorthEast.png", Vector.quarterSpriteDimensions, 12, new Vector[] //NORTHEAST
                            {northEast, northEast, northEast, northEast,}),

                        new Animation("Sprites/Players/Croc/Idle.png", Vector.quarterSpriteDimensions,5), //NONE MOVE
                    },
                    new Vector[] {Vector.south, Vector.zero, Vector.east, Vector.zero, Vector.north, Vector.zero, Vector.west, Vector.zero}),
            //(8) Croc #5
            new Enemy(Vector.zero, Vector.defaultSpriteColliderOffset, Vector.quarterSpriteDimensions,
                    new Animation[] {
                        new Animation("Sprites/Players/Croc/Idle.png", Vector.quarterSpriteDimensions, 30), //IDLE
                        new Animation("Sprites/Players/Croc/Idle.png", Vector.quarterSpriteDimensions, 20), //CLICK
                        new Animation(), //HOVER

                        new MobileAnimation("Sprites/Players/Croc/Fall.png", Vector.quarterSpriteDimensions, 10, new Vector[] //FALLING
                            {fall, fall, fall, fall, fall, fall, fall, fall, fall, fall, fall, fall,}), 
    
                        new MobileAnimation("Sprites/Players/Croc/SouthWest.png", Vector.quarterSpriteDimensions, 12, new Vector[] //SOUTHWEST
                            {southWest, southWest, southWest, southWest,}),
                        new MobileAnimation("Sprites/Players/Croc/SouthEast.png", Vector.quarterSpriteDimensions, 12, new Vector[] //SOUTHEAST
                            {southEast, southEast, southEast, southEast,}),
                        new MobileAnimation("Sprites/Players/Croc/NorthWest.png", Vector.quarterSpriteDimensions, 12, new Vector[] //NORTHWEST
                            {northWest, northWest, northWest, northWest,}),
                        new MobileAnimation("Sprites/Players/Croc/NorthEast.png", Vector.quarterSpriteDimensions, 12, new Vector[] //NORTHEAST
                            {northEast, northEast, northEast, northEast,}),

                        new Animation("Sprites/Players/Croc/Idle.png", Vector.quarterSpriteDimensions,5), //NONE MOVE
                    },
                    new Vector[] {Vector.south, Vector.zero, Vector.west, Vector.zero, Vector.north, Vector.zero, Vector.east, Vector.zero}),
            //(9) Croc #6
            new Enemy(Vector.zero, Vector.defaultSpriteColliderOffset, Vector.quarterSpriteDimensions,
                    new Animation[] {
                        new Animation("Sprites/Players/Croc/Idle.png", Vector.quarterSpriteDimensions, 30), //IDLE
                        new Animation("Sprites/Players/Croc/Idle.png", Vector.quarterSpriteDimensions, 20), //CLICK
                        new Animation(), //HOVER

                        new MobileAnimation("Sprites/Players/Croc/Fall.png", Vector.quarterSpriteDimensions, 10, new Vector[] //FALLING
                            {fall, fall, fall, fall, fall, fall, fall, fall, fall, fall, fall, fall,}), 
    
                        new MobileAnimation("Sprites/Players/Croc/SouthWest.png", Vector.quarterSpriteDimensions, 12, new Vector[] //SOUTHWEST
                            {southWest, southWest, southWest, southWest,}),
                        new MobileAnimation("Sprites/Players/Croc/SouthEast.png", Vector.quarterSpriteDimensions, 12, new Vector[] //SOUTHEAST
                            {southEast, southEast, southEast, southEast,}),
                        new MobileAnimation("Sprites/Players/Croc/NorthWest.png", Vector.quarterSpriteDimensions, 12, new Vector[] //NORTHWEST
                            {northWest, northWest, northWest, northWest,}),
                        new MobileAnimation("Sprites/Players/Croc/NorthEast.png", Vector.quarterSpriteDimensions, 12, new Vector[] //NORTHEAST
                            {northEast, northEast, northEast, northEast,}),

                        new Animation("Sprites/Players/Croc/Idle.png", Vector.quarterSpriteDimensions,5), //NONE MOVE
                    },
                    new Vector[] {Vector.east, Vector.zero, Vector.south, Vector.zero, Vector.west, Vector.zero, Vector.north, Vector.zero}),
            //(10) Croc #7
            new Enemy(Vector.zero, Vector.defaultSpriteColliderOffset, Vector.quarterSpriteDimensions,
                    new Animation[] {
                        new Animation("Sprites/Players/Croc/Idle.png", Vector.quarterSpriteDimensions, 30), //IDLE
                        new Animation("Sprites/Players/Croc/Idle.png", Vector.quarterSpriteDimensions, 20), //CLICK
                        new Animation(), //HOVER

                        new MobileAnimation("Sprites/Players/Croc/Fall.png", Vector.quarterSpriteDimensions, 10, new Vector[] //FALLING
                            {fall, fall, fall, fall, fall, fall, fall, fall, fall, fall, fall, fall,}), 
    
                        new MobileAnimation("Sprites/Players/Croc/SouthWest.png", Vector.quarterSpriteDimensions, 12, new Vector[] //SOUTHWEST
                            {southWest, southWest, southWest, southWest,}),
                        new MobileAnimation("Sprites/Players/Croc/SouthEast.png", Vector.quarterSpriteDimensions, 12, new Vector[] //SOUTHEAST
                            {southEast, southEast, southEast, southEast,}),
                        new MobileAnimation("Sprites/Players/Croc/NorthWest.png", Vector.quarterSpriteDimensions, 12, new Vector[] //NORTHWEST
                            {northWest, northWest, northWest, northWest,}),
                        new MobileAnimation("Sprites/Players/Croc/NorthEast.png", Vector.quarterSpriteDimensions, 12, new Vector[] //NORTHEAST
                            {northEast, northEast, northEast, northEast,}),

                        new Animation("Sprites/Players/Croc/Idle.png", Vector.quarterSpriteDimensions,5), //NONE MOVE
                    },
                    new Vector[] {Vector.west, Vector.zero, Vector.north, Vector.zero, Vector.east, Vector.zero, Vector.south, Vector.zero}),
            //(11) Croc #8
            new Enemy(Vector.zero, Vector.defaultSpriteColliderOffset, Vector.quarterSpriteDimensions,
                    new Animation[] {
                        new Animation("Sprites/Players/Croc/Idle.png", Vector.quarterSpriteDimensions, 30), //IDLE
                        new Animation("Sprites/Players/Croc/Idle.png", Vector.quarterSpriteDimensions, 20), //CLICK
                        new Animation(), //HOVER

                        new MobileAnimation("Sprites/Players/Croc/Fall.png", Vector.quarterSpriteDimensions, 10, new Vector[] //FALLING
                            {fall, fall, fall, fall, fall, fall, fall, fall, fall, fall, fall, fall,}), 
    
                        new MobileAnimation("Sprites/Players/Croc/SouthWest.png", Vector.quarterSpriteDimensions, 12, new Vector[] //SOUTHWEST
                            {southWest, southWest, southWest, southWest,}),
                        new MobileAnimation("Sprites/Players/Croc/SouthEast.png", Vector.quarterSpriteDimensions, 12, new Vector[] //SOUTHEAST
                            {southEast, southEast, southEast, southEast,}),
                        new MobileAnimation("Sprites/Players/Croc/NorthWest.png", Vector.quarterSpriteDimensions, 12, new Vector[] //NORTHWEST
                            {northWest, northWest, northWest, northWest,}),
                        new MobileAnimation("Sprites/Players/Croc/NorthEast.png", Vector.quarterSpriteDimensions, 12, new Vector[] //NORTHEAST
                            {northEast, northEast, northEast, northEast,}),

                        new Animation("Sprites/Players/Croc/Idle.png", Vector.quarterSpriteDimensions,5), //NONE MOVE
                    },
                    new Vector[] {Vector.north, Vector.zero, Vector.east, Vector.zero, Vector.south, Vector.zero, Vector.west, Vector.zero}),

            //Squids - - - - - - - - - - - - - - - - - - - - - - - - - - - -

            //(12) Squid #1
            new Enemy(Vector.zero, Vector.defaultSpriteColliderOffset, Vector.quarterSpriteDimensions,
                    new Animation[] {
                        new Animation("Sprites/Players/Squid/Idle.png", Vector.quarterSpriteDimensions, 30), //IDLE
                        new Animation("Sprites/Players/Squid/Idle.png", Vector.quarterSpriteDimensions, 20), //CLICK
                        new Animation(), //HOVER

                        new MobileAnimation("Sprites/Players/Squid/Fall.png", Vector.quarterSpriteDimensions, 10, new Vector[] //FALLING
                            {fall, fall, fall, fall, fall, fall, fall, fall, fall, fall, fall, fall,}), 
    
                        new MobileAnimation("Sprites/Players/Squid/SouthWest.png", Vector.quarterSpriteDimensions, 12, new Vector[] //SOUTHWEST
                            {southWest, southWest, southWest, southWest,}),
                        new MobileAnimation("Sprites/Players/Squid/SouthEast.png", Vector.quarterSpriteDimensions, 12, new Vector[] //SOUTHEAST
                            {southEast, southEast, southEast, southEast,}),
                        new MobileAnimation("Sprites/Players/Squid/NorthWest.png", Vector.quarterSpriteDimensions, 12, new Vector[] //NORTHWEST
                            {northWest, northWest, northWest, northWest,}),
                        new MobileAnimation("Sprites/Players/Squid/NorthEast.png", Vector.quarterSpriteDimensions, 12, new Vector[] //NORTHEAST
                            {northEast, northEast, northEast, northEast,}),

                        new Animation("Sprites/Players/Squid/Idle.png", Vector.quarterSpriteDimensions,5), //NONE MOVE
                    },
                    new Vector[] {Vector.east, Vector.north, Vector.north, Vector.west, Vector.south, Vector.south}),
            //(13) Squid #2
            new Enemy(Vector.zero, Vector.defaultSpriteColliderOffset, Vector.quarterSpriteDimensions,
                    new Animation[] {
                        new Animation("Sprites/Players/Squid/Idle.png", Vector.quarterSpriteDimensions, 30), //IDLE
                        new Animation("Sprites/Players/Squid/Idle.png", Vector.quarterSpriteDimensions, 20), //CLICK
                        new Animation(), //HOVER

                        new MobileAnimation("Sprites/Players/Squid/Fall.png", Vector.quarterSpriteDimensions, 10, new Vector[] //FALLING
                            {fall, fall, fall, fall, fall, fall, fall, fall, fall, fall, fall, fall,}), 
    
                        new MobileAnimation("Sprites/Players/Squid/SouthWest.png", Vector.quarterSpriteDimensions, 12, new Vector[] //SOUTHWEST
                            {southWest, southWest, southWest, southWest,}),
                        new MobileAnimation("Sprites/Players/Squid/SouthEast.png", Vector.quarterSpriteDimensions, 12, new Vector[] //SOUTHEAST
                            {southEast, southEast, southEast, southEast,}),
                        new MobileAnimation("Sprites/Players/Squid/NorthWest.png", Vector.quarterSpriteDimensions, 12, new Vector[] //NORTHWEST
                            {northWest, northWest, northWest, northWest,}),
                        new MobileAnimation("Sprites/Players/Squid/NorthEast.png", Vector.quarterSpriteDimensions, 12, new Vector[] //NORTHEAST
                            {northEast, northEast, northEast, northEast,}),

                        new Animation("Sprites/Players/Squid/Idle.png", Vector.quarterSpriteDimensions,5), //NONE MOVE
                    },
                    new Vector[] {Vector.north, Vector.north, Vector.west, Vector.south, Vector.south, Vector.east}),
            //(14) Squid #3
            new Enemy(Vector.zero, Vector.defaultSpriteColliderOffset, Vector.quarterSpriteDimensions,
                    new Animation[] {
                        new Animation("Sprites/Players/Squid/Idle.png", Vector.quarterSpriteDimensions, 30), //IDLE
                        new Animation("Sprites/Players/Squid/Idle.png", Vector.quarterSpriteDimensions, 20), //CLICK
                        new Animation(), //HOVER

                        new MobileAnimation("Sprites/Players/Squid/Fall.png", Vector.quarterSpriteDimensions, 10, new Vector[] //FALLING
                            {fall, fall, fall, fall, fall, fall, fall, fall, fall, fall, fall, fall,}), 
    
                        new MobileAnimation("Sprites/Players/Squid/SouthWest.png", Vector.quarterSpriteDimensions, 12, new Vector[] //SOUTHWEST
                            {southWest, southWest, southWest, southWest,}),
                        new MobileAnimation("Sprites/Players/Squid/SouthEast.png", Vector.quarterSpriteDimensions, 12, new Vector[] //SOUTHEAST
                            {southEast, southEast, southEast, southEast,}),
                        new MobileAnimation("Sprites/Players/Squid/NorthWest.png", Vector.quarterSpriteDimensions, 12, new Vector[] //NORTHWEST
                            {northWest, northWest, northWest, northWest,}),
                        new MobileAnimation("Sprites/Players/Squid/NorthEast.png", Vector.quarterSpriteDimensions, 12, new Vector[] //NORTHEAST
                            {northEast, northEast, northEast, northEast,}),

                        new Animation("Sprites/Players/Squid/Idle.png", Vector.quarterSpriteDimensions,5), //NONE MOVE
                    },
                    new Vector[] {Vector.west, Vector.south, Vector.south, Vector.east, Vector.north, Vector.north}),
            //(15) Squid #4
            new Enemy(Vector.zero, Vector.defaultSpriteColliderOffset, Vector.quarterSpriteDimensions,
                    new Animation[] {
                        new Animation("Sprites/Players/Squid/Idle.png", Vector.quarterSpriteDimensions, 30), //IDLE
                        new Animation("Sprites/Players/Squid/Idle.png", Vector.quarterSpriteDimensions, 20), //CLICK
                        new Animation(), //HOVER

                        new MobileAnimation("Sprites/Players/Squid/Fall.png", Vector.quarterSpriteDimensions, 10, new Vector[] //FALLING
                            {fall, fall, fall, fall, fall, fall, fall, fall, fall, fall, fall, fall,}), 
    
                        new MobileAnimation("Sprites/Players/Squid/SouthWest.png", Vector.quarterSpriteDimensions, 12, new Vector[] //SOUTHWEST
                            {southWest, southWest, southWest, southWest,}),
                        new MobileAnimation("Sprites/Players/Squid/SouthEast.png", Vector.quarterSpriteDimensions, 12, new Vector[] //SOUTHEAST
                            {southEast, southEast, southEast, southEast,}),
                        new MobileAnimation("Sprites/Players/Squid/NorthWest.png", Vector.quarterSpriteDimensions, 12, new Vector[] //NORTHWEST
                            {northWest, northWest, northWest, northWest,}),
                        new MobileAnimation("Sprites/Players/Squid/NorthEast.png", Vector.quarterSpriteDimensions, 12, new Vector[] //NORTHEAST
                            {northEast, northEast, northEast, northEast,}),

                        new Animation("Sprites/Players/Squid/Idle.png", Vector.quarterSpriteDimensions,5), //NONE MOVE
                    },
                    new Vector[] {Vector.south, Vector.south, Vector.east, Vector.north, Vector.north, Vector.west}),
            //(16) Squid #5
            new Enemy(Vector.zero, Vector.defaultSpriteColliderOffset, Vector.quarterSpriteDimensions,
                    new Animation[] {
                        new Animation("Sprites/Players/Squid/Idle.png", Vector.quarterSpriteDimensions, 30), //IDLE
                        new Animation("Sprites/Players/Squid/Idle.png", Vector.quarterSpriteDimensions, 20), //CLICK
                        new Animation(), //HOVER

                        new MobileAnimation("Sprites/Players/Squid/Fall.png", Vector.quarterSpriteDimensions, 10, new Vector[] //FALLING
                            {fall, fall, fall, fall, fall, fall, fall, fall, fall, fall, fall, fall,}), 
    
                        new MobileAnimation("Sprites/Players/Squid/SouthWest.png", Vector.quarterSpriteDimensions, 12, new Vector[] //SOUTHWEST
                            {southWest, southWest, southWest, southWest,}),
                        new MobileAnimation("Sprites/Players/Squid/SouthEast.png", Vector.quarterSpriteDimensions, 12, new Vector[] //SOUTHEAST
                            {southEast, southEast, southEast, southEast,}),
                        new MobileAnimation("Sprites/Players/Squid/NorthWest.png", Vector.quarterSpriteDimensions, 12, new Vector[] //NORTHWEST
                            {northWest, northWest, northWest, northWest,}),
                        new MobileAnimation("Sprites/Players/Squid/NorthEast.png", Vector.quarterSpriteDimensions, 12, new Vector[] //NORTHEAST
                            {northEast, northEast, northEast, northEast,}),

                        new Animation("Sprites/Players/Squid/Idle.png", Vector.quarterSpriteDimensions,5), //NONE MOVE
                    },
                    new Vector[] {Vector.south, Vector.south, Vector.west, Vector.north, Vector.north, Vector.east}),
            //(17) Squid #6
            new Enemy(Vector.zero, Vector.defaultSpriteColliderOffset, Vector.quarterSpriteDimensions,
                    new Animation[] {
                        new Animation("Sprites/Players/Squid/Idle.png", Vector.quarterSpriteDimensions, 30), //IDLE
                        new Animation("Sprites/Players/Squid/Idle.png", Vector.quarterSpriteDimensions, 20), //CLICK
                        new Animation(), //HOVER

                        new MobileAnimation("Sprites/Players/Squid/Fall.png", Vector.quarterSpriteDimensions, 10, new Vector[] //FALLING
                            {fall, fall, fall, fall, fall, fall, fall, fall, fall, fall, fall, fall,}), 
    
                        new MobileAnimation("Sprites/Players/Squid/SouthWest.png", Vector.quarterSpriteDimensions, 12, new Vector[] //SOUTHWEST
                            {southWest, southWest, southWest, southWest,}),
                        new MobileAnimation("Sprites/Players/Squid/SouthEast.png", Vector.quarterSpriteDimensions, 12, new Vector[] //SOUTHEAST
                            {southEast, southEast, southEast, southEast,}),
                        new MobileAnimation("Sprites/Players/Squid/NorthWest.png", Vector.quarterSpriteDimensions, 12, new Vector[] //NORTHWEST
                            {northWest, northWest, northWest, northWest,}),
                        new MobileAnimation("Sprites/Players/Squid/NorthEast.png", Vector.quarterSpriteDimensions, 12, new Vector[] //NORTHEAST
                            {northEast, northEast, northEast, northEast,}),

                        new Animation("Sprites/Players/Squid/Idle.png", Vector.quarterSpriteDimensions,5), //NONE MOVE
                    },
                    new Vector[] {Vector.west, Vector.north, Vector.north, Vector.east, Vector.south, Vector.south}),
            //(18) Squid #7
            new Enemy(Vector.zero, Vector.defaultSpriteColliderOffset, Vector.quarterSpriteDimensions,
                    new Animation[] {
                        new Animation("Sprites/Players/Squid/Idle.png", Vector.quarterSpriteDimensions, 30), //IDLE
                        new Animation("Sprites/Players/Squid/Idle.png", Vector.quarterSpriteDimensions, 20), //CLICK
                        new Animation(), //HOVER

                        new MobileAnimation("Sprites/Players/Squid/Fall.png", Vector.quarterSpriteDimensions, 10, new Vector[] //FALLING
                            {fall, fall, fall, fall, fall, fall, fall, fall, fall, fall, fall, fall,}), 
    
                        new MobileAnimation("Sprites/Players/Squid/SouthWest.png", Vector.quarterSpriteDimensions, 12, new Vector[] //SOUTHWEST
                            {southWest, southWest, southWest, southWest,}),
                        new MobileAnimation("Sprites/Players/Squid/SouthEast.png", Vector.quarterSpriteDimensions, 12, new Vector[] //SOUTHEAST
                            {southEast, southEast, southEast, southEast,}),
                        new MobileAnimation("Sprites/Players/Squid/NorthWest.png", Vector.quarterSpriteDimensions, 12, new Vector[] //NORTHWEST
                            {northWest, northWest, northWest, northWest,}),
                        new MobileAnimation("Sprites/Players/Squid/NorthEast.png", Vector.quarterSpriteDimensions, 12, new Vector[] //NORTHEAST
                            {northEast, northEast, northEast, northEast,}),

                        new Animation("Sprites/Players/Squid/Idle.png", Vector.quarterSpriteDimensions,5), //NONE MOVE
                    },
                    new Vector[] {Vector.north, Vector.north, Vector.east, Vector.south, Vector.south, Vector.west}),
            //(19) Squid #8
            new Enemy(Vector.zero, Vector.defaultSpriteColliderOffset, Vector.quarterSpriteDimensions,
                    new Animation[] {
                        new Animation("Sprites/Players/Squid/Idle.png", Vector.quarterSpriteDimensions, 30), //IDLE
                        new Animation("Sprites/Players/Squid/Idle.png", Vector.quarterSpriteDimensions, 20), //CLICK
                        new Animation(), //HOVER

                        new MobileAnimation("Sprites/Players/Squid/Fall.png", Vector.quarterSpriteDimensions, 10, new Vector[] //FALLING
                            {fall, fall, fall, fall, fall, fall, fall, fall, fall, fall, fall, fall,}), 
    
                        new MobileAnimation("Sprites/Players/Squid/SouthWest.png", Vector.quarterSpriteDimensions, 12, new Vector[] //SOUTHWEST
                            {southWest, southWest, southWest, southWest,}),
                        new MobileAnimation("Sprites/Players/Squid/SouthEast.png", Vector.quarterSpriteDimensions, 12, new Vector[] //SOUTHEAST
                            {southEast, southEast, southEast, southEast,}),
                        new MobileAnimation("Sprites/Players/Squid/NorthWest.png", Vector.quarterSpriteDimensions, 12, new Vector[] //NORTHWEST
                            {northWest, northWest, northWest, northWest,}),
                        new MobileAnimation("Sprites/Players/Squid/NorthEast.png", Vector.quarterSpriteDimensions, 12, new Vector[] //NORTHEAST
                            {northEast, northEast, northEast, northEast,}),

                        new Animation("Sprites/Players/Squid/Idle.png", Vector.quarterSpriteDimensions,5), //NONE MOVE
                    },
                    new Vector[] {Vector.east, Vector.south, Vector.south, Vector.west, Vector.north, Vector.north}),
            //(20) Squid #9
            new Enemy(Vector.zero, Vector.defaultSpriteColliderOffset, Vector.quarterSpriteDimensions,
                    new Animation[] {
                        new Animation("Sprites/Players/Squid/Idle.png", Vector.quarterSpriteDimensions, 30), //IDLE
                        new Animation("Sprites/Players/Squid/Idle.png", Vector.quarterSpriteDimensions, 20), //CLICK
                        new Animation(), //HOVER

                        new MobileAnimation("Sprites/Players/Squid/Fall.png", Vector.quarterSpriteDimensions, 10, new Vector[] //FALLING
                            {fall, fall, fall, fall, fall, fall, fall, fall, fall, fall, fall, fall,}), 
    
                        new MobileAnimation("Sprites/Players/Squid/SouthWest.png", Vector.quarterSpriteDimensions, 12, new Vector[] //SOUTHWEST
                            {southWest, southWest, southWest, southWest,}),
                        new MobileAnimation("Sprites/Players/Squid/SouthEast.png", Vector.quarterSpriteDimensions, 12, new Vector[] //SOUTHEAST
                            {southEast, southEast, southEast, southEast,}),
                        new MobileAnimation("Sprites/Players/Squid/NorthWest.png", Vector.quarterSpriteDimensions, 12, new Vector[] //NORTHWEST
                            {northWest, northWest, northWest, northWest,}),
                        new MobileAnimation("Sprites/Players/Squid/NorthEast.png", Vector.quarterSpriteDimensions, 12, new Vector[] //NORTHEAST
                            {northEast, northEast, northEast, northEast,}),

                        new Animation("Sprites/Players/Squid/Idle.png", Vector.quarterSpriteDimensions,5), //NONE MOVE
                    },
                    new Vector[] {Vector.east, Vector.east, Vector.south, Vector.west, Vector.west, Vector.north}),
            //(21) Squid #10
            new Enemy(Vector.zero, Vector.defaultSpriteColliderOffset, Vector.quarterSpriteDimensions,
                    new Animation[] {
                        new Animation("Sprites/Players/Squid/Idle.png", Vector.quarterSpriteDimensions, 30), //IDLE
                        new Animation("Sprites/Players/Squid/Idle.png", Vector.quarterSpriteDimensions, 20), //CLICK
                        new Animation(), //HOVER

                        new MobileAnimation("Sprites/Players/Squid/Fall.png", Vector.quarterSpriteDimensions, 10, new Vector[] //FALLING
                            {fall, fall, fall, fall, fall, fall, fall, fall, fall, fall, fall, fall,}), 
    
                        new MobileAnimation("Sprites/Players/Squid/SouthWest.png", Vector.quarterSpriteDimensions, 12, new Vector[] //SOUTHWEST
                            {southWest, southWest, southWest, southWest,}),
                        new MobileAnimation("Sprites/Players/Squid/SouthEast.png", Vector.quarterSpriteDimensions, 12, new Vector[] //SOUTHEAST
                            {southEast, southEast, southEast, southEast,}),
                        new MobileAnimation("Sprites/Players/Squid/NorthWest.png", Vector.quarterSpriteDimensions, 12, new Vector[] //NORTHWEST
                            {northWest, northWest, northWest, northWest,}),
                        new MobileAnimation("Sprites/Players/Squid/NorthEast.png", Vector.quarterSpriteDimensions, 12, new Vector[] //NORTHEAST
                            {northEast, northEast, northEast, northEast,}),

                        new Animation("Sprites/Players/Squid/Idle.png", Vector.quarterSpriteDimensions,5), //NONE MOVE
                    },
                    new Vector[] {Vector.south, Vector.west, Vector.west, Vector.north, Vector.east, Vector.east}),
            //(22) Squid #11
            new Enemy(Vector.zero, Vector.defaultSpriteColliderOffset, Vector.quarterSpriteDimensions,
                    new Animation[] {
                        new Animation("Sprites/Players/Squid/Idle.png", Vector.quarterSpriteDimensions, 30), //IDLE
                        new Animation("Sprites/Players/Squid/Idle.png", Vector.quarterSpriteDimensions, 20), //CLICK
                        new Animation(), //HOVER

                        new MobileAnimation("Sprites/Players/Squid/Fall.png", Vector.quarterSpriteDimensions, 10, new Vector[] //FALLING
                            {fall, fall, fall, fall, fall, fall, fall, fall, fall, fall, fall, fall,}), 
    
                        new MobileAnimation("Sprites/Players/Squid/SouthWest.png", Vector.quarterSpriteDimensions, 12, new Vector[] //SOUTHWEST
                            {southWest, southWest, southWest, southWest,}),
                        new MobileAnimation("Sprites/Players/Squid/SouthEast.png", Vector.quarterSpriteDimensions, 12, new Vector[] //SOUTHEAST
                            {southEast, southEast, southEast, southEast,}),
                        new MobileAnimation("Sprites/Players/Squid/NorthWest.png", Vector.quarterSpriteDimensions, 12, new Vector[] //NORTHWEST
                            {northWest, northWest, northWest, northWest,}),
                        new MobileAnimation("Sprites/Players/Squid/NorthEast.png", Vector.quarterSpriteDimensions, 12, new Vector[] //NORTHEAST
                            {northEast, northEast, northEast, northEast,}),

                        new Animation("Sprites/Players/Squid/Idle.png", Vector.quarterSpriteDimensions,5), //NONE MOVE
                    },
                    new Vector[] {Vector.west, Vector.west, Vector.north, Vector.east, Vector.east, Vector.south}),
            //(23) Squid #12
            new Enemy(Vector.zero, Vector.defaultSpriteColliderOffset, Vector.quarterSpriteDimensions,
                    new Animation[] {
                        new Animation("Sprites/Players/Squid/Idle.png", Vector.quarterSpriteDimensions, 30), //IDLE
                        new Animation("Sprites/Players/Squid/Idle.png", Vector.quarterSpriteDimensions, 20), //CLICK
                        new Animation(), //HOVER

                        new MobileAnimation("Sprites/Players/Squid/Fall.png", Vector.quarterSpriteDimensions, 10, new Vector[] //FALLING
                            {fall, fall, fall, fall, fall, fall, fall, fall, fall, fall, fall, fall,}), 
    
                        new MobileAnimation("Sprites/Players/Squid/SouthWest.png", Vector.quarterSpriteDimensions, 12, new Vector[] //SOUTHWEST
                            {southWest, southWest, southWest, southWest,}),
                        new MobileAnimation("Sprites/Players/Squid/SouthEast.png", Vector.quarterSpriteDimensions, 12, new Vector[] //SOUTHEAST
                            {southEast, southEast, southEast, southEast,}),
                        new MobileAnimation("Sprites/Players/Squid/NorthWest.png", Vector.quarterSpriteDimensions, 12, new Vector[] //NORTHWEST
                            {northWest, northWest, northWest, northWest,}),
                        new MobileAnimation("Sprites/Players/Squid/NorthEast.png", Vector.quarterSpriteDimensions, 12, new Vector[] //NORTHEAST
                            {northEast, northEast, northEast, northEast,}),

                        new Animation("Sprites/Players/Squid/Idle.png", Vector.quarterSpriteDimensions,5), //NONE MOVE
                    },
                    new Vector[] {Vector.north, Vector.east, Vector.east, Vector.south, Vector.west, Vector.west}),
            //(24) Squid #13
            new Enemy(Vector.zero, Vector.defaultSpriteColliderOffset, Vector.quarterSpriteDimensions,
                    new Animation[] {
                        new Animation("Sprites/Players/Squid/Idle.png", Vector.quarterSpriteDimensions, 30), //IDLE
                        new Animation("Sprites/Players/Squid/Idle.png", Vector.quarterSpriteDimensions, 20), //CLICK
                        new Animation(), //HOVER

                        new MobileAnimation("Sprites/Players/Squid/Fall.png", Vector.quarterSpriteDimensions, 10, new Vector[] //FALLING
                            {fall, fall, fall, fall, fall, fall, fall, fall, fall, fall, fall, fall,}), 
    
                        new MobileAnimation("Sprites/Players/Squid/SouthWest.png", Vector.quarterSpriteDimensions, 12, new Vector[] //SOUTHWEST
                            {southWest, southWest, southWest, southWest,}),
                        new MobileAnimation("Sprites/Players/Squid/SouthEast.png", Vector.quarterSpriteDimensions, 12, new Vector[] //SOUTHEAST
                            {southEast, southEast, southEast, southEast,}),
                        new MobileAnimation("Sprites/Players/Squid/NorthWest.png", Vector.quarterSpriteDimensions, 12, new Vector[] //NORTHWEST
                            {northWest, northWest, northWest, northWest,}),
                        new MobileAnimation("Sprites/Players/Squid/NorthEast.png", Vector.quarterSpriteDimensions, 12, new Vector[] //NORTHEAST
                            {northEast, northEast, northEast, northEast,}),

                        new Animation("Sprites/Players/Squid/Idle.png", Vector.quarterSpriteDimensions,5), //NONE MOVE
                    },
                    new Vector[] {Vector.south, Vector.east, Vector.east, Vector.north, Vector.west, Vector.west}),
            //(25) Squid #14
            new Enemy(Vector.zero, Vector.defaultSpriteColliderOffset, Vector.quarterSpriteDimensions,
                    new Animation[] {
                        new Animation("Sprites/Players/Squid/Idle.png", Vector.quarterSpriteDimensions, 30), //IDLE
                        new Animation("Sprites/Players/Squid/Idle.png", Vector.quarterSpriteDimensions, 20), //CLICK
                        new Animation(), //HOVER

                        new MobileAnimation("Sprites/Players/Squid/Fall.png", Vector.quarterSpriteDimensions, 10, new Vector[] //FALLING
                            {fall, fall, fall, fall, fall, fall, fall, fall, fall, fall, fall, fall,}), 
    
                        new MobileAnimation("Sprites/Players/Squid/SouthWest.png", Vector.quarterSpriteDimensions, 12, new Vector[] //SOUTHWEST
                            {southWest, southWest, southWest, southWest,}),
                        new MobileAnimation("Sprites/Players/Squid/SouthEast.png", Vector.quarterSpriteDimensions, 12, new Vector[] //SOUTHEAST
                            {southEast, southEast, southEast, southEast,}),
                        new MobileAnimation("Sprites/Players/Squid/NorthWest.png", Vector.quarterSpriteDimensions, 12, new Vector[] //NORTHWEST
                            {northWest, northWest, northWest, northWest,}),
                        new MobileAnimation("Sprites/Players/Squid/NorthEast.png", Vector.quarterSpriteDimensions, 12, new Vector[] //NORTHEAST
                            {northEast, northEast, northEast, northEast,}),

                        new Animation("Sprites/Players/Squid/Idle.png", Vector.quarterSpriteDimensions,5), //NONE MOVE
                    },
                    new Vector[] {Vector.east, Vector.east, Vector.north, Vector.west, Vector.west, Vector.south}),
            //(26) Squid #15
            new Enemy(Vector.zero, Vector.defaultSpriteColliderOffset, Vector.quarterSpriteDimensions,
                    new Animation[] {
                        new Animation("Sprites/Players/Squid/Idle.png", Vector.quarterSpriteDimensions, 30), //IDLE
                        new Animation("Sprites/Players/Squid/Idle.png", Vector.quarterSpriteDimensions, 20), //CLICK
                        new Animation(), //HOVER

                        new MobileAnimation("Sprites/Players/Squid/Fall.png", Vector.quarterSpriteDimensions, 10, new Vector[] //FALLING
                            {fall, fall, fall, fall, fall, fall, fall, fall, fall, fall, fall, fall,}), 
    
                        new MobileAnimation("Sprites/Players/Squid/SouthWest.png", Vector.quarterSpriteDimensions, 12, new Vector[] //SOUTHWEST
                            {southWest, southWest, southWest, southWest,}),
                        new MobileAnimation("Sprites/Players/Squid/SouthEast.png", Vector.quarterSpriteDimensions, 12, new Vector[] //SOUTHEAST
                            {southEast, southEast, southEast, southEast,}),
                        new MobileAnimation("Sprites/Players/Squid/NorthWest.png", Vector.quarterSpriteDimensions, 12, new Vector[] //NORTHWEST
                            {northWest, northWest, northWest, northWest,}),
                        new MobileAnimation("Sprites/Players/Squid/NorthEast.png", Vector.quarterSpriteDimensions, 12, new Vector[] //NORTHEAST
                            {northEast, northEast, northEast, northEast,}),

                        new Animation("Sprites/Players/Squid/Idle.png", Vector.quarterSpriteDimensions,5), //NONE MOVE
                    },
                    new Vector[] {Vector.north, Vector.west, Vector.west, Vector.south, Vector.east, Vector.east}),
            //(27) Squid #16
            new Enemy(Vector.zero, Vector.defaultSpriteColliderOffset, Vector.quarterSpriteDimensions,
                    new Animation[] {
                        new Animation("Sprites/Players/Squid/Idle.png", Vector.quarterSpriteDimensions, 30), //IDLE
                        new Animation("Sprites/Players/Squid/Idle.png", Vector.quarterSpriteDimensions, 20), //CLICK
                        new Animation(), //HOVER

                        new MobileAnimation("Sprites/Players/Squid/Fall.png", Vector.quarterSpriteDimensions, 10, new Vector[] //FALLING
                            {fall, fall, fall, fall, fall, fall, fall, fall, fall, fall, fall, fall,}), 
    
                        new MobileAnimation("Sprites/Players/Squid/SouthWest.png", Vector.quarterSpriteDimensions, 12, new Vector[] //SOUTHWEST
                            {southWest, southWest, southWest, southWest,}),
                        new MobileAnimation("Sprites/Players/Squid/SouthEast.png", Vector.quarterSpriteDimensions, 12, new Vector[] //SOUTHEAST
                            {southEast, southEast, southEast, southEast,}),
                        new MobileAnimation("Sprites/Players/Squid/NorthWest.png", Vector.quarterSpriteDimensions, 12, new Vector[] //NORTHWEST
                            {northWest, northWest, northWest, northWest,}),
                        new MobileAnimation("Sprites/Players/Squid/NorthEast.png", Vector.quarterSpriteDimensions, 12, new Vector[] //NORTHEAST
                            {northEast, northEast, northEast, northEast,}),

                        new Animation("Sprites/Players/Squid/Idle.png", Vector.quarterSpriteDimensions,5), //NONE MOVE
                    },
                    new Vector[] {Vector.west, Vector.west, Vector.south, Vector.east, Vector.east, Vector.north}),
        };
    }

    public static Player getPlayer(int i) {
        Player[] players = Players();

        if (i < 0 || i >= players.length) {
            //The default player
            return new Player(Vector.defaultSpriteCollider, Vector.defaultSpriteColliderOffset, Vector.quarterSpriteDimensions,
                new Animation[] {
                    new Animation("Sprites/Players/DefaultPlayer/DefaultPlayer.png", Vector.quarterSpriteDimensions,20), //IDLE
                    new Animation("Sprites/Players/DefaultPlayer/DefaultPlayerClicked.png", Vector.quarterSpriteDimensions), //CLICK
                    new Animation(),//new Animation("Sprites/Players/DefaultPlayer.png", Vector.quarterSpriteDimensions), //HOVER

                    new MobileAnimation("Sprites/Players/DefaultPlayer/DefaultPlayer.png", Vector.quarterSpriteDimensions, new Vector[] //FALLING
                        {fall, fall, fall, fall, fall, fall, fall, fall, fall, fall, fall, fall,}), 

                    new MobileAnimation("Sprites/Players/DefaultPlayer/DefaultPlayerWalking.png", Vector.quarterSpriteDimensions, new Vector[] //SOUTHWEST
                        {southWest, southWest, southWest, southWest,}),
                    new MobileAnimation("Sprites/Players/DefaultPlayer/DefaultPlayerWalking.png", Vector.quarterSpriteDimensions, new Vector[] //SOUTHEAST
                        {southEast, southEast, southEast, southEast,}),
                    new MobileAnimation("Sprites/Players/DefaultPlayer/DefaultPlayerWalking.png", Vector.quarterSpriteDimensions, new Vector[] //NORTHWEST
                        {northWest, northWest, northWest, northWest,}),
                    new MobileAnimation("Sprites/Players/DefaultPlayer/DefaultPlayerWalking.png", Vector.quarterSpriteDimensions, new Vector[] //NORTHEAST
                        {northEast, northEast, northEast, northEast,}),

                    new Animation("Sprites/Players/DefaultPlayer/DefaultPlayer.png", Vector.quarterSpriteDimensions, 5), //NONE MOVE
                });
        }
        else
        {
            return new Player(players[i]);
        }
    }

    public static Enemy getEnemy(int i) {
        Enemy[] enemies = Enemies();
        
        if (i < 0 || i >= enemies.length) {
            return new Enemy(Vector.zero, Vector.defaultSpriteColliderOffset, Vector.quarterSpriteDimensions,
                new Animation[] {
                    new Animation("Sprites/Players/DefaultEnemy/Idle.png", Vector.quarterSpriteDimensions, 10), //IDLE
                    new Animation("Sprites/Players/DefaultEnemy/Idle.png", Vector.quarterSpriteDimensions), //CLICK
                    new Animation(), //HOVER

                    new MobileAnimation("Sprites/Players/DefaultEnemy/Idle.png", Vector.quarterSpriteDimensions, new Vector[] //FALLING
                        {fall, fall, fall, fall, fall, fall, fall, fall, fall, fall, fall, fall,}), 

                    new MobileAnimation("Sprites/Players/DefaultEnemy/Idle.png", Vector.quarterSpriteDimensions, new Vector[] //SOUTHWEST
                        {southWest, southWest, southWest, southWest,}),
                    new MobileAnimation("Sprites/Players/DefaultEnemy/Idle.png", Vector.quarterSpriteDimensions, new Vector[] //SOUTHEAST
                        {southEast, southEast, southEast, southEast,}),
                    new MobileAnimation("Sprites/Players/DefaultEnemy/Idle.png", Vector.quarterSpriteDimensions, new Vector[] //NORTHWEST
                        {northWest, northWest, northWest, northWest,}),
                    new MobileAnimation("Sprites/Players/DefaultEnemy/Idle.png", Vector.quarterSpriteDimensions, new Vector[] //NORTHEAST
                        {northEast, northEast, northEast, northEast,}),

                    new Animation("Sprites/Players/DefaultEnemy/Idle.png", Vector.quarterSpriteDimensions, 5), //NONE MOVE
                },
                new Vector[] {Vector.zero});
        }
        else
        {
            return new Enemy(enemies[i]);
        }
    }

    private static final Vector betweenTiles = new Vector();
    private static final Vector southWest = new Vector();
    private static final Vector southEast = new Vector();
    private static final Vector northWest = new Vector();
    private static final Vector northEast = new Vector();
    private static final Vector fall = new Vector();

    //#endregion
    
        //rendering
    //#region
    public static void drawGame(Graphics2D g) {
        drawFallingPlayers(g); //falling players are rendered seperately here to ensure that they render behind tiles
        drawTiles(g);
        drawArrows(g);
        drawPlayers(g);
    }

    public static void updateGame() {
        for (int i = 0; i < playersList.size(); i++) {
            Player p = playersList.get(i);
            
            if (p != null) {
                p.update();
            }
        }
        for (int i = 0; i < enemiesList.size(); i++) {
            Enemy e = enemiesList.get(i);
            
            if (e != null) {
                e.update();
            }
        }
        for (AnimatedActor[] a : tiles) {
            for (AnimatedActor aa : a) {
                aa.update();
            }
        }
    }

    public static void drawFallingPlayers(Graphics2D g) {
        for (int i = 0; i < playersList.size(); i++) {
            Player player = playersList.get(i);
            if (player != null && player.drawBehindTiles()) {
                player.draw(g);
            }
        }

        for (int i = 0; i < enemiesList.size(); i++) {
            Enemy enemy = enemiesList.get(i);
            if (enemy != null && enemy.drawBehindTiles()) {
                enemy.draw(g);
            }
        }
    }

    public static void drawTiles(Graphics2D g) {
        for (AnimatedActor[] tile : tiles) {
            for (AnimatedActor tile1 : tile) {
                tile1.draw(g);
            }
        }
    }

    public static void drawPlayers(Graphics2D g) {
        for (int i = 0; i < playersList.size(); i++) {
            Player player = playersList.get(i);
            if (player != null && !player.drawBehindTiles()) {
                player.draw(g);
            }
        }

        for (int i = 0; i < enemiesList.size(); i++) {
            Enemy enemy = enemiesList.get(i);
            if (enemy != null && !enemy.drawBehindTiles()) {
                enemy.draw(g);
            }
        }
    }
    
    public static void drawArrows(Graphics2D g) {
        for (Enemy enemy : enemiesList) {
            if (enemy != null) {
                enemy.drawArrow(g);
            }
        }
    }
    //#endregion

        //clicking detection
    //#region
    public static boolean TileClicked() {
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                if (tiles[i][j].DidCollide(MyPanel.mouseX, MyPanel.mouseY)) {
                    //only let the player try to move if all of the enemies are finished moving
                    if (previousPlayerClicked >= 0 && previousPlayerClicked < playersList.size() && lastEnemyMoved == 0) {
                        //if the player tries to move to a tile that they can't, dont let it
                        Player p = playersList.get(previousPlayerClicked);
                        Vector pos = new Vector(i, j);
                        if (p.tilePosition.IsAdjacent(pos, 1) && !p.tilePosition.Equals(pos)) {
                            p.Push(Vector.Subtract(new Vector(i, j), p.tilePosition), true);
                            SoundManager.validTileClicked();
                        }
                        else
                        {
                            //DeactivateClick(); choose whether or not to deactivate the player from being clicked
                            SoundManager.invalidTileClicked();
                        }
                    }
                    else
                    {
                        SoundManager.invalidTileClicked();
                    }

                    tiles[i][j].ChangeAnimation(AnimatedActor.ANIMATION_ACT_CLICK);

                    return true;
                }
            }
        }

        return false;
    }

    public static boolean PlayerClicked() {
        for (Player player : playersList) {
            if (player != null && player.DidCollide(MyPanel.mouseX, MyPanel.mouseY)) {
                previousPlayerClicked = playersList.indexOf(player);
                player.OnClick();
                return true;
            }
        }

        return false;
    }

    public static boolean EnemyClicked() {
        for (Enemy enemy : enemiesList) {
            if (enemy != null && enemy.DidCollide(MyPanel.mouseX, MyPanel.mouseY)) {
                previousPlayerClicked = -1;
                enemy.OnClick();
                return true;
            }
        }

        return false;
    }
    
    public static boolean NothingClicked() {
        return DeactivateClick();
    }
    
    public static boolean DeactivateClick() {
        previousPlayerClicked = -1;
        return true;
    }
    //#endregion

        //hovering detection
    //#region
    public static boolean TileHovered() {
        if (previousPlayerClicked < 0) {
            return false;
        }        
        
        boolean output = false;

        for (AnimatedActor[] tile : tiles) {
            for (AnimatedActor tile1 : tile) {
                if (tile1.DidCollide(MyPanel.mouseX, MyPanel.mouseY)) {
                    tile1.ChangeAnimation(AnimatedActor.ANIMATION_IDLE_HOVER);
                    /*
                    if (lastEnemyMoved == 0) {
                    tiles[i][j].sprite = highlightedTileImage;
                    }
                    else
                    {
                    tiles[i][j].sprite = highlightedDisabledTileImage;
                    } */
                    output = true;
                } else {
                    tile1.ChangeAnimation(AnimatedActor.ANIMATION_IDLE);
                }
            }
        }

        return output;
    }

    public static void NoTilesHovered() {
        for (AnimatedActor[] tile : tiles) {
            for (AnimatedActor tile1 : tile) {
                tile1.ChangeAnimation(AnimatedActor.ANIMATION_IDLE);
            }
        }
    }
    
    public static boolean PlayerHovered() {
        for (Player player : playersList) {
            if (player != null && player.DidCollide(MyPanel.mouseX, MyPanel.mouseY)) {
                player.ChangeAnimation(AnimatedActor.ANIMATION_IDLE_HOVER);
                return true;
            }
        }
        return false;
    }
    //#endregion

        //getting tile positions
    //#region
    public static Vector GetTilePosition(Vector a) {
        int x = (int) a.x;
        int y = (int) a.y;
        
        if (x >= tiles.length) {
            x = tiles.length - 1;
        }
        if (x < 0) {
            x = 0;
        }

        if (y >= tiles[x].length) {
            y = tiles[x].length - 1;
        }
        if (y < 0) {
            y = 0;
        }

        return new Vector(tiles[x][y].position);
    }

    public static final Vector offset = new Vector(TILE_WIDTH * 0.25f, TILE_WIDTH * -0.25f);
    public static Vector GetTilePositionWithOffset(Vector a) {
        return Vector.Add(GetTilePosition(a), offset);
    }
    //#endregion

        //player manipulation
    //#region
    public static void PushPlayers(Vector origin, int velocity, int knockback) {
        for (Player player : playersList) {
            if (player.tilePosition.IsAdjacent(origin, velocity)) {
                player.Push(Vector.Subtract(player.tilePosition, origin).Normalize(), knockback);
            }
        }
    }
    
    public static Player PlayerAt(Vector location) {
        for (Enemy enemy : enemiesList) {
            if (enemy != null && enemy.tilePosition.Equals(location)) {
                return enemy;
            }
        }

        for (Player player : playersList) {
            if (player != null && player.tilePosition.Equals(location)) {
                return player;
            }
        }

        return null;
    }
    
    public static boolean KillPlayer(Player p) {
        if (p instanceof Enemy e) {
            if (enemiesList.contains(e)) {
                enemiesList.set(enemiesList.indexOf(e), null);
                return true;
            }

            return false;
        }
        else
        {
            int i = playersList.indexOf(p);
            if (i >= 0) {
                
                if (previousPlayerClicked == i) {
                    previousPlayerClicked = -1;
                }
                else if (previousPlayerClicked > i) {
                    previousPlayerClicked--;
                }
                
                playersList.set(i, null);

                return true;
            }

            return false;
        }
    }
    
    private static int lastEnemyMoved = 0;
    //moves one enemy at a time, in order
    public static void ShiftEnemy() {
        if (lastEnemyMoved >= enemiesList.size()) {
            ResetEnemies();
            lastEnemyMoved = 0;
        }
        else
        {
            if (enemiesList.get(lastEnemyMoved) == null) {
                lastEnemyMoved++;
                ShiftEnemy(); //recusion, be careful
            }
            else
            {
                lastEnemyMoved++;
                enemiesList.get(lastEnemyMoved - 1).Shift();
            }
        }

        //TileHovered(); //resets tile sprites to disabled
    }
    
    public static boolean IsOffGrid(Vector a) {
        return a.x >= gridSize || a.y >= gridSize || a.x < 0 || a.y < 0;
    }
    //#endregion
}