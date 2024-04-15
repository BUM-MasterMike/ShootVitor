/*
 * Copyright (c) 2024 by MasterMike, https://www.youtube.com/@MasterMikeHalo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ch.autumo.games.shootvitor;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Scale;
import javafx.stage.Screen;
import javafx.stage.WindowEvent;
import javafx.util.Duration;


/**
 * 
 * Shoot the Vitor - He's a Bich!
 * 2D shooter game.
 * Version 2.0
 * 
 * @author MasterMike
 * @author ChatGPT
 * 
 * This JavaFX game has been initially developed with ChatGPT and
 * then completed by MasterMike.
 * 
 * The initial development process with ChatGPT can be seen here:
 * https://chat.openai.com/share/eadc5b0b-7164-4ed0-9be7-a9cbd5f6a249
 * 
 */
public class App extends Application {

    private static final String VERSION = "2.0";
    
    private final String urlStyleSheet = App.class.getResource("/app.css").toExternalForm();
    
    private static final int BAR_HEIGHT = 28;
    
    private static final int DEFAULT_WIDTH = 800;
    private static final int DEFAULT_HEIGHT = 600;
    
    private static double width = DEFAULT_WIDTH;
    private static double height = DEFAULT_HEIGHT;
    
    private static final String FONT_FAMILY = "Verdana";
        
    private static final double SPEED_INCREASE_PER_LEVEL = 0.1; // Constant increase in speed per level
        
    private static final int VITOR_SIZE = 48;
    
    private static final int MAX_SHOTS = 10;
    private static final int MAX_AUDIO_SHOTS = 20;
    private static final int MAX_AUDIO_KILLS = 10;
    
    private static final double HIT_INDICATOR_DURATION = 0.5; // 0.5 seconds
    private static final double HIT_INDICATOR_DURATION_2 = 1.0; // 1.0 seconds
    
    private static final double CHANGE_DIRECTION_PROBABILITY = 0.01; // 1% chance of changing direction per frame
    
    private static final double BASE_VELOCITY = 0.5; // Base velocity

    private GraphicsContext gc = null;

    private double upScale = 1.0;
    
    private double vitorX = width / 2.0;
    private double vitorY = height / 2.0;

    private double levelSpeed = BASE_VELOCITY; // Speed of Vitor adjusted for level

    private double vx = levelSpeed; // Initial velocity along x-axis
    private double vy = levelSpeed; // Initial velocity along y-axis

    private String timeText = "";
    private int shotsLeft = MAX_SHOTS;
    private int score = 0;
    private int level = 1;

    private boolean vitorHit = false;
    private boolean levelComplete = false;

    private double hueOffset = 0; // Initial hue offset value

    private Stage primaryStage = null;
    private StackPane root = null;
    private Scene scene = null;
    private StackPane gameContent = null;
    private Canvas canvas = null;
    
    private Image vitorImage = null;
    private Image vitorImageFlash = null;
    private Image vitorImageFlashBlood = null;
    
    private boolean flashVitor = false;
    
    private MediaPlayer gameOverPlayer;
    private List<MediaPlayer> shotPlayers;
    private List<MediaPlayer> doubleKillPlayers;
    private List<MediaPlayer> trippleKillPlayers;
    private MediaPlayer[] mediaPlayerList;

    private int hitCounter = 1;
    
    private final List<Long> killTimes2 = new ArrayList<>();
    private final List<Long> killTimes3 = new ArrayList<>();
    
    private long hitTime2_1 = -1;
    private long hitTime2_2 = -1;
    private long hitTime3_1 = -1;
    private long hitTime3_2 = -1;
    private long hitTime3_3 = -1;
    
    private final int multiKillDistanceInMillis = 300;
    private final int multiKillAudioLevel = 6;
    
    private boolean doubleKill = false;
    private boolean trippleKill = false;
    
    private final long startTime = System.nanoTime();

    private boolean bichLevelReached = false;
    private boolean bastardLevelReached = false;
    
    private boolean initCursor = true;
    
    private final boolean loadMusic = true;

    
    /**
     * Java FX start.
     * 
     * @param primaryStage primary stage
     */
    @Override
    public void start(Stage primaryStage) {

        this.primaryStage = primaryStage;
        
        
        // NOTE: The initial scene size might not be the fullscreen size,
        // so the scaling calculations not work for the fullscreen, e.g. the
        // macOS stage manager resizes the the initial scene, therefore
        // when going to real full screen the scene should take the dimensions
        // of the full screen, but it doesn't, that's possible a Java FX issue
        // and a workaround should be found. The AWT Tookit does the same!
        
        
        final Rectangle2D screenBounds = Screen.getPrimary().getBounds();
        width = screenBounds.getWidth();
        height = screenBounds.getHeight();
        
        if (loadMusic) {
            // Initialize music
            initBackgroundMusic(
                    "/short-8-bit-background-music-for-video-mobile-game-old-school-37sec-164704.mp3",
                    "/the-final-boss-battle-158700.mp3",
                    "/machiavellian-nightmare-electronic-dystopia-ai-robot-machine-139385.mp3",
                    "/pixel-perfect-112527.mp3"
            );
            initPlayers();
        }
        
        root = new StackPane();
        scene = new Scene(root, width, height);        
        scene.getStylesheets().add(urlStyleSheet);
        

        // Create game content
        gameContent = createGameContent();
        root.getChildren().addAll(gameContent);
        StackPane.setAlignment(gameContent, Pos.TOP_LEFT);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Shoot the Vitor: He's a Bich!");
        primaryStage.show();
        

        // Once before the dialog
        renderDynamicBackground(gc);

        
        String title = "About";
        final boolean fullscreen = showStartDialog(title, 640);
        
        
        if (fullscreen) {

            final double ratio = DEFAULT_WIDTH / DEFAULT_HEIGHT;
            upScale = width / height > ratio
                            ? height / DEFAULT_HEIGHT
                            : width / DEFAULT_WIDTH;
            
            vitorX = width / 2.0;
            vitorY = height / 2.0;
            
            // FULLSCREEEN
            // https://stackoverflow.com/questions/16606162/javafx-fullscreen-resizing-elements-based-upon-screen-size
            letterbox(scene, root);
            primaryStage.setFullScreen(true);
            
        } else {
            
            this.endFullScreen();
            primaryStage.setResizable(false);
        }

        
        vitorImage = new Image(getClass().getResourceAsStream("/Vitor.png"), VITOR_SIZE * upScale, VITOR_SIZE * upScale, false, false); // Load the image
        vitorImageFlash = new Image(getClass().getResourceAsStream("/Flash.png"), VITOR_SIZE * upScale, VITOR_SIZE * upScale, false, false); // Load the image
        vitorImageFlashBlood = new Image(getClass().getResourceAsStream("/FlashBlood.png"), VITOR_SIZE * upScale, VITOR_SIZE * upScale, false, false); // Load the image
        
        
        // Start playing the background music
        if (loadMusic) {
            Platform.runLater(() -> {
                playBackgroundMusic();
            });
        }
        
        final AnimationTimer at = new AnimationTimer() {
            @Override
            public void handle(long now) {
                gc.clearRect(0, 0, width, height);
                renderDynamicBackground(gc);
                renderGame(gc);
                determineMultiKills();
            }
        };
        
        at.start();
        
        // Handle close
        primaryStage.setOnCloseRequest((WindowEvent event) -> {
            
            event.consume();
            
            at.stop();
            
            if (loadMusic) {
                for (MediaPlayer mediaPlayerList1 : mediaPlayerList) {
                    mediaPlayerList1.stop();
                }
                gameOverPlayer.play();
            }
            
            String title0 = "End Results";
            showEndDialog(title0, 300);
            
            Platform.exit();
            System.exit(0);
        });
        
        primaryStage.fullScreenProperty().addListener((ChangeListener) (obs,oldValue,newValue) -> {
            if (!primaryStage.isFullScreen()) {
                this.endFullScreen();
            }
        });
        
        
        // Hit detection
        scene.setOnMouseClicked((MouseEvent e) -> {
            
            if (!vitorHit && shotsLeft > 0) {
                
                int mouseX = (int) e.getX();
                int mouseY = (int) e.getY();
                
                // Vitor the Bich is hit!
                if (Math.sqrt(Math.pow(mouseX - vitorX, 2) + Math.pow(mouseY - vitorY, 2)) < VITOR_SIZE * upScale / 2) {

                    killTimes2.add(System.currentTimeMillis());
                    killTimes3.add(System.currentTimeMillis());
                    
                    flashVitor();
        
                    if (initCursor) {
                        this.cursorWorkaround();
                        initCursor = false;
                    }                
                    
                    if (loadMusic) {
                        Platform.runLater(() -> {
                            // Find an available MediaPlayer for playing the shot sound
                            boolean played = false;
                            for (MediaPlayer player : shotPlayers) {
                                if (player.getStatus() == MediaPlayer.Status.STOPPED) {
                                    player.seek(Duration.ZERO);
                                    player.play();
                                    played = true;
                                    break;
                                }
                            }
                            if (!played) {
                                // If no available MediaPlayer found, create a new one and play the sound
                                Media media = new Media(App.this.getClass().getResource("/shot.wav").toExternalForm());
                                MediaPlayer newPlayer = new MediaPlayer(media);
                                newPlayer.play();
                                shotPlayers.add(newPlayer);
                            }
                        });
                    }
                    
                    vitorHit = true;
                    shotsLeft--;
                    score++;
                    if (shotsLeft == 0) {
                        level++;
                        levelComplete = true;
                    }
                }
            }
        });
        
        this.initCursors();
    }
    
    /**
     * Init mouse crosshair cursors on nodes.
     */
    private void initCursors() {

        root.setCursor(Cursor.CROSSHAIR);
        root.setOnMouseEntered(event -> {
            root.setCursor(Cursor.CROSSHAIR);
        });
        scene.setCursor(Cursor.CROSSHAIR);
        scene.setOnMouseEntered(event -> {
            scene.setCursor(Cursor.CROSSHAIR);
        });
        scene.setOnMouseExited(event -> {
            scene.setCursor(Cursor.DEFAULT);
        });
        gameContent.setCursor(Cursor.CROSSHAIR);
        gameContent.setOnMouseEntered(event -> {
            gameContent.setCursor(Cursor.CROSSHAIR);
        });
        gameContent.setOnMouseExited(event -> {
            gameContent.setCursor(Cursor.DEFAULT);
        });     
        canvas.setCursor(Cursor.CROSSHAIR);
        canvas.setOnMouseEntered(event -> {
            canvas.setCursor(Cursor.CROSSHAIR);
        });
        canvas.setOnMouseExited(event -> {
            canvas.setCursor(Cursor.DEFAULT);
        });  
    }
    
    /**
     * Therw's an issue with windows focus and scene levels,
     * where the crosshair cursor is reset to teh defaukr cursor.
     * This method is used as a workaround.
     * 
     * Though sometimes the crosshair cursor still is lost
     * in the windowed execution.
     */
    private void cursorWorkaround() {
        Platform.runLater(() -> {
            root.setCursor(Cursor.CROSSHAIR);
            scene.setCursor(Cursor.CROSSHAIR);
            gameContent.setCursor(Cursor.CROSSHAIR);
            canvas.setCursor(Cursor.CROSSHAIR);
            primaryStage.getScene().setCursor(Cursor.WAIT);
            primaryStage.getScene().getRoot().setCursor(Cursor.WAIT);
            initCursor = false;
        });
    }
    
    /**
     * Determine if a multi-kill occured and plays sounds.
     */
    private void determineMultiKills() {
        
        Iterator<Long> iterator = killTimes2.iterator();
        if (iterator.hasNext())
            hitTime2_1 = iterator.next();
        if (iterator.hasNext())
            hitTime2_2 = iterator.next();

        iterator = killTimes3.iterator();
        if (iterator.hasNext())
            hitTime3_1 = iterator.next();
        if (iterator.hasNext())
            hitTime3_2 = iterator.next();
        if (iterator.hasNext())
            hitTime3_3 = iterator.next();
        
        if (hitTime2_1 != -1 && hitTime2_2 != -1) {
            if (hitTime2_2 - hitTime2_1 < multiKillDistanceInMillis)
                doubleKill = true;
            killTimes2.remove(0);
            killTimes2.remove(0);
        }

        if (hitTime3_1 != -1 && hitTime3_2 != -1 && hitTime3_3 != -1) {
            if (hitTime3_2 - hitTime3_1 < multiKillDistanceInMillis) {
                if  (hitTime3_3 - hitTime3_2 < multiKillDistanceInMillis)
                    trippleKill = true;
            }
            killTimes3.remove(0);
            killTimes3.remove(0);
            killTimes3.remove(0);
        }

        if (loadMusic && level >= multiKillAudioLevel) {
            if (doubleKill && !trippleKill) {
                if (doubleKillPlayers != null) {
                    Platform.runLater(()-> {
                        boolean played = false;
                        for (MediaPlayer player : doubleKillPlayers) {
                            if (player.getStatus() == MediaPlayer.Status.STOPPED) {
                                player.seek(Duration.ZERO);
                                player.play();
                                played = true;
                                break;
                            }
                        }
                        if (!played) {
                            Media media = new Media(App.this.getClass().getResource("/doublekill.wav").toExternalForm());
                            MediaPlayer newPlayer = new MediaPlayer(media);
                            newPlayer.play();
                            doubleKillPlayers.add(newPlayer);
                        }
                    });
                } else {
                    System.out.println("DOUBLE-KILL !!");
                }
            }
            if (trippleKill) {
                if (trippleKillPlayers != null) {
                    Platform.runLater(()-> {
                        // Delay the tripple kill output
                        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0.5), event -> {
                            boolean played = false;
                            for (MediaPlayer player : trippleKillPlayers) {
                                if (player.getStatus() == MediaPlayer.Status.STOPPED) {
                                    player.seek(Duration.ZERO);
                                    player.play();
                                    played = true;
                                    break;
                                }
                            }
                            if (!played) {
                                Media media = new Media(App.this.getClass().getResource("/tripplekill.wav").toExternalForm());
                                MediaPlayer newPlayer = new MediaPlayer(media);
                                newPlayer.play();
                                trippleKillPlayers.add(newPlayer);
                            }
                        }));
                        timeline.play();
                    });
                } else {
                    System.err.println("TRIPPLE-KILL !!");
                }
            }            
        }
        
        hitTime2_1 = -1;
        hitTime2_2 = -1;
        hitTime3_1 = -1;
        hitTime3_2 = -1;
        hitTime3_3 = -1;
        
        doubleKill = false;
        trippleKill = false;
    }
    
    /**
     * End fullscreen mode.
     * 
     * Re-calcuulate dimensions for objects.
     */
    private void endFullScreen() {

        upScale = 1.0;
        width = DEFAULT_WIDTH;
        height = DEFAULT_HEIGHT;
        vitorX = width / 2;
        vitorY = height / 2;

        scene.setCursor(Cursor.CROSSHAIR);
        gameContent.setCursor(Cursor.CROSSHAIR);
        canvas.setCursor(Cursor.CROSSHAIR);

        vitorImage = new Image(getClass().getResourceAsStream("/Vitor.png"), VITOR_SIZE, VITOR_SIZE, false, false); // Load the image
        vitorImageFlash = new Image(getClass().getResourceAsStream("/Flash.png"), VITOR_SIZE, VITOR_SIZE, false, false); // Load the image
        vitorImageFlashBlood = new Image(getClass().getResourceAsStream("/FlashBlood.png"), VITOR_SIZE, VITOR_SIZE, false, false); // Load the image

        primaryStage.setHeight(height + BAR_HEIGHT);
        primaryStage.setWidth(width);
        primaryStage.centerOnScreen();
        primaryStage.setResizable(false);        
    }
    
    /**
     * Letter-box the scene, install scene change listener for fullscreen.
     * Would also word for different sizes of the window.
     * 
     * @param scene scene
     * @param contentPane the root content pane 
     */
    private void letterbox(final Scene scene, final Pane contentPane) {
        
        final double initWidth = scene.getWidth();
        final double initHeight = scene.getHeight();
        
        final double ratio = initWidth / initHeight;

        SceneSizeChangeListener sizeListener = new SceneSizeChangeListener(scene, ratio, initHeight, initWidth, contentPane);
        scene.widthProperty().addListener(sizeListener);
        scene.heightProperty().addListener(sizeListener);
    }
  
    /**
     * Create game content pane.
     * 
     * @return game pane.
     */
    private StackPane createGameContent() {
        
        gameContent = new StackPane();
        canvas = new Canvas(width, height);
        gameContent.getChildren().add(canvas);
        
        gc = canvas.getGraphicsContext2D();
        
        // Set font
        gc.setFont(Font.font(FONT_FAMILY, FontWeight.BOLD, 18)); // Using Courier New as a monospaced font        

        return gameContent;
    }
    
    /**
     * Define the lengt of the flash stae of the bitch Vitor.
     */
    private void flashVitor() {
        // Configure the semi-transparent circle
        flashVitor = true;
        // Schedule a task to remove the circle after a short delay
        double duration = HIT_INDICATOR_DURATION;
        if (hitCounter % 10 == 0)
            duration = HIT_INDICATOR_DURATION_2;
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(duration), event -> {
            flashVitor = false;
        }));
        timeline.play();
    }
    
    /**
     * Render the dynamic rainbow background.
     * 
     * @param gc  graphics
     */
    private void renderDynamicBackground(GraphicsContext gc) {
        // Update hue offset value for shifting colors
        hueOffset += 0.1; // Adjust speed of color shifting

        // Define the number of sections horizontally and vertically
        int numSectionsX = 10;
        int numSectionsY = 10;

        // Calculate the width and height of each section
        double sectionWidth = (double) width / numSectionsX;
        double sectionHeight = (double) height / numSectionsY;

        // Iterate over each section
        for (int y = 0; y < numSectionsY; y++) {
            for (int x = 0; x < numSectionsX; x++) {
                // Calculate hue value based on the position of the section and hue offset
                double hue = (hueOffset + x * 10 + y * 10) % 360;

                // Calculate colors for the current section and its neighbors
                Color color = Color.hsb(hue, 1.0, 0.2); // Saturation remains constant, brightness reduced to 0.2
                Color nextXColor = Color.hsb((hueOffset + (x + 1) * 10 + y * 10) % 360, 1.0, 0.2);
                Color nextYColor = Color.hsb((hueOffset + x * 10 + (y + 1) * 10) % 360, 1.0, 0.2);

                // Interpolate colors for smooth transitions between sections
                Color interpolatedColor = color.interpolate(nextXColor, 0.5).interpolate(nextYColor.interpolate(color, 0.5), 0.5);
                String rgbCode = toRGBCode(interpolatedColor);

                // Set background color for the current section
                double startX = x * sectionWidth;
                double startY = y * sectionHeight;
                
                //double endX = startX + sectionWidth;
                //double endY = startY + sectionHeight;
                
                gc.setFill(Color.web(rgbCode));
                gc.fillRect(startX, startY, sectionWidth, sectionHeight);
            }
        }
    }
    
    /**
     * Add special messages, depending on the score achieved.
     * 
     * @param gc graphics
     */
    private void showSpecialMessages(GraphicsContext gc) {
        
        if (score == 100 && !bichLevelReached)
            bichLevelReached = true;

        if (score == 150 && !bastardLevelReached)
            bastardLevelReached = true;
        
        if (bichLevelReached) {
            gc.setFill(Color.BEIGE);
            gc.setFont(Font.font(FONT_FAMILY, FontWeight.BOLD, 42 * upScale));
            gc.setTextAlign(TextAlignment.CENTER);
            gc.setTextBaseline(VPos.CENTER);
            gc.fillText("100 points! You are a Bich too!", width / 2, height / 2);
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(10), event -> {
                bichLevelReached = false;
            }));
            timeline.play();            
        }
        if (bastardLevelReached) {
            gc.setFill(Color.CORAL);
            gc.setFont(Font.font(FONT_FAMILY, FontWeight.BOLD, 42 * upScale));
            gc.setTextAlign(TextAlignment.CENTER);
            gc.setTextBaseline(VPos.CENTER);
            gc.fillText("150 points! You are a BASTARD!", width / 2, height / 2);
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(10), event -> {
                bastardLevelReached = false;
            }));
            timeline.play();            
        }
    }

    /**
     * Render the game; Vitor's movement, appearance and special messages
     * as well as game information (level, score, shots left and playtime).
     * 
     * @param gc graphics
     */
    private void renderGame(GraphicsContext gc) {

        if (vitorHit) {
            vitorHit = false; // Reset hit indicator
            hitCounter++;
            
        }

        showSpecialMessages(gc);
        
        // Draw and move Vitor
        moveVitor(); // Move Vitor only if not hit

        // Inside your renderGame method, replace the code for drawing Vitor with:
        if (flashVitor) {
            if (hitCounter % 10 == 0) {
                gc.drawImage(vitorImageFlashBlood, vitorX - VITOR_SIZE * upScale / 2, vitorY - VITOR_SIZE * upScale / 2);
                hitCounter = 0;
            } else {
                gc.drawImage(vitorImageFlash, vitorX - VITOR_SIZE * upScale / 2, vitorY - VITOR_SIZE * upScale / 2);
            }
        } else {
            gc.drawImage(vitorImage, vitorX - VITOR_SIZE * upScale / 2, vitorY - VITOR_SIZE * upScale / 2);
        }
        
        //gc.fillOval(vitorX - VITOR_SIZE / 2, vitorY - VITOR_SIZE / 2, VITOR_SIZE, VITOR_SIZE);

        gc.setTextAlign(TextAlignment.LEFT);
        gc.setTextBaseline(VPos.BASELINE);
        
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font(FONT_FAMILY, FontWeight.BOLD, 16)); // Set larger font
        gc.fillText("Shots left: " + shotsLeft, 10, 20);
        gc.fillText("Score: " + score, width - 100, 20);
        gc.fillText("Level: " + level, (width - 50) / 2, 20);

        // Calculate elapsed time
        long elapsedTime = (System.nanoTime() - startTime) / 1_000_000_000;
        long hours = elapsedTime / 3600;
        long minutes = (elapsedTime % 3600) / 60;
        long seconds = elapsedTime % 60;

        // Draw text for time passed
        timeText = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        gc.setFont(Font.font(FONT_FAMILY, FontWeight.NORMAL, 14)); // Set smaller font
        gc.fillText("Playtime: " + timeText, 10, height - 10);
        
        if (levelComplete) {
            // Increase difficulty for the next level
            // For simplicity, let's just increase Vitor's speed
            // You can add more features to make the game more challenging
            levelSpeed += SPEED_INCREASE_PER_LEVEL * level; // Increase level speed

            vx = levelSpeed * upScale;
            vy = levelSpeed * upScale;

            levelComplete = false;
            shotsLeft = MAX_SHOTS;
        } 
    }
    
    /**
     * Move vitor; calculate position based on speed and random movements.
     * Alos redpect window borders and reverse vectors if a border is
     * avenged.
     * 
     * Note: Even the speed increases with the level it is not constant;
     * this is because vectors vx and vy should be calculated based on a
     * real velocity, but speed is only simlated by increased x- and y-
     * vectors per level. Therefore, and depending on the angle of Vitor,
     * the speed may even slow a bit down for one movement in a direction.
     * Though, this "fake" calculation is perfect for creating random speeds
     * per level and makes the game more interesting.
     */
    private void moveVitor() {

        // Occasionally change direction
        if (Math.random() < CHANGE_DIRECTION_PROBABILITY) {
            vx = Math.random() * (2 * levelSpeed * upScale) - levelSpeed * upScale;
            vy = Math.random() * (2 * levelSpeed * upScale) - levelSpeed * upScale;
        }

        // Move Vitor with levelSpeed
        vitorX += vx;
        vitorY += vy;

        // Bounce off the canvas borders
        if (vitorX < VITOR_SIZE * upScale / 2 || vitorX > width - VITOR_SIZE * upScale / 2) {
            vx = -vx;
            vitorX += 2 * vx; // Move Vitor back inside the canvas
        }
        if (vitorY < VITOR_SIZE * upScale / 2 || vitorY > height - VITOR_SIZE * upScale / 2) {
            vy = -vy;
            vitorY += 2 * vy; // Move Vitor back inside the canvas
        }
    }

    /**
     * Show start dialog.
     * 
     * @param title title
     * @param width width
     * @return true, if fullscreen mode has been chosen, otherwise false
     */
    private boolean showStartDialog(String title, int width) {
        
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.getDialogPane().getStylesheets().add(urlStyleSheet);
        
        alert.getDialogPane().getButtonTypes().setAll(
            ButtonType.OK, 
            ButtonType.CANCEL
        );

        BorderPane borderpane = new BorderPane();
        GridPane gridpane = new GridPane();
        borderpane.setCenter(gridpane);
        BorderPane.setMargin(gridpane, new Insets(12, 12, 12, 12));
        
        Label s0 = new Label("");
        Label l1 = new Label("Shoot the Vitor - He's a Bich "+VERSION);
        Label s1 = new Label("");
        Label l2 = new Label("This JavaFX game has been initially developed with");
        Label l3 = new Label("ChatGPT and and then completed by MasterMike.");
        Label s2 = new Label("");
        Label l4 = new Label("The initial development process with ChatGPT can be seen here:");
        Hyperlink link1 = new Hyperlink("https://chat.openai.com/share/eadc5b0b-7164-4ed0-9be7-a9cbd5f6a249");
        Label s3 = new Label("");
        Label l5 = new Label("Chiptune sounds from:");
        Hyperlink link2 = new Hyperlink("https://pixabay.com/de/music/search/chiptune");
        Label s4 = new Label("");
        Label l6 = new Label("You can use CRTL-Q or CMD-Q to end game.");
        Label s5 = new Label("");
        Label l7 = new Label("Enjoy, shoot the Bich!");
        
        l1.setFont(Font.font(FONT_FAMILY, FontWeight.BOLD, 14));
        
        gridpane.add(s0, 0, 0);
        gridpane.add(l1, 0, 1);
        gridpane.add(s1, 0, 2);
        gridpane.add(l2, 0, 3);
        gridpane.add(l3, 0, 4);
        gridpane.add(s2, 0, 5);
        gridpane.add(l4, 0, 6);
        gridpane.add(link1, 0, 7);
        gridpane.add(s3, 0, 8);
        gridpane.add(l5, 0, 9);
        gridpane.add(link2, 0, 10);
        gridpane.add(s4, 0, 11);
        gridpane.add(l6, 0, 12);
        gridpane.add(s5, 0, 13);
        gridpane.add(l7, 0, 14);
        
        link1.setOnAction((ActionEvent evt) -> {
            getHostServices().showDocument("https://chat.openai.com/share/eadc5b0b-7164-4ed0-9be7-a9cbd5f6a249");
        } );
        link2.setOnAction((ActionEvent evt) -> {
            getHostServices().showDocument("https://pixabay.com/de/music/search/chiptune");
        });

        alert.getDialogPane().setMinWidth(width);
        alert.setTitle(title);
        alert.setHeaderText("ShootVitor "+ VERSION);
        alert.getDialogPane().contentProperty().set(borderpane);
        
        // We misuse the cancel button for the fullscreen choice.
        ((Button) alert.getDialogPane().lookupButton(ButtonType.CANCEL)).setText("Fullscreen");
        ((Button) alert.getDialogPane().lookupButton(ButtonType.OK)).setText("Go!");
        
        Optional<ButtonType> result = alert.showAndWait();
        return result.get() == ButtonType.CANCEL;
    }
    
    /**
     * Show end dialog.
     *
     * @param title title
     * @param width width
     */
    private void showEndDialog(String title, int width) {
        
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.getDialogPane().getStylesheets().add(urlStyleSheet);
        
        alert.getDialogPane().getButtonTypes().setAll(
            ButtonType.OK, 
            ButtonType.CANCEL
        );
        
        BorderPane borderpane = new BorderPane();
        GridPane gridpane = new GridPane();
        borderpane.setCenter(gridpane);
        BorderPane.setMargin(gridpane, new Insets(12, 12, 12, 12));

        Label l0_0 = new Label("Your Score : ");
        Label l0_1 = new Label("Your Level : ");
        Label l0_2 = new Label("Playtime : ");
        Label s0_3 = new Label("");
        Label l0_4 = new Label("Bye Bastard!");

        Label l1_0 = new Label(""+score);
        Label l1_1 = new Label(""+level);
        Label l1_2 = new Label(timeText);

        l1_0.setStyle("-fx-font-weight: bold;");
        l1_1.setStyle("-fx-font-weight: bold;");
        l1_2.setStyle("-fx-font-weight: bold;");
        
        gridpane.add(l0_0, 0, 0);
        gridpane.add(l0_1, 0, 1);
        gridpane.add(l0_2, 0, 2);
        gridpane.add(s0_3, 0, 3);
        gridpane.add(l0_4, 0, 4);

        gridpane.add(l1_0, 1, 0);
        gridpane.add(l1_1, 1, 1);
        gridpane.add(l1_2, 1, 2);
        
        alert.getDialogPane().setMinWidth(width);
        alert.setTitle(title);
        alert.setHeaderText("Carnage Report");
        
        //GridPane header = (GridPane) alert.getDialogPane().lookup(".header-panel"); 
        //header.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-background-color: #222222;");
        
        alert.getDialogPane().contentProperty().set(borderpane);
        
        // We misuse the cancel button for the rq/qq choice.
        ((Button) alert.getDialogPane().lookupButton(ButtonType.CANCEL)).setText("rq");
        ((Button) alert.getDialogPane().lookupButton(ButtonType.OK)).setText("qq");
        
        alert.showAndWait();
    }

    /**
     * Initialize audio players for effects.
     */
    private void initPlayers() {
        
        doubleKillPlayers = new ArrayList<>();
        for (int i = 0; i < MAX_AUDIO_KILLS; i++) {
            Media media = new Media(getClass().getResource("/doublekill.wav").toExternalForm());
            MediaPlayer mediaPlayer = new MediaPlayer(media);
            // TODO: no effect, why?
            mediaPlayer.setVolume(0.6);
            doubleKillPlayers.add(mediaPlayer);
        }

        trippleKillPlayers = new ArrayList<>();
        for (int i = 0; i < MAX_AUDIO_KILLS; i++) {
            Media media = new Media(getClass().getResource("/tripplekill.wav").toExternalForm());
            MediaPlayer mediaPlayer = new MediaPlayer(media);
            // TODO: no effect, why?
            mediaPlayer.setVolume(0.6);
            trippleKillPlayers.add(mediaPlayer);
        }

        shotPlayers = new ArrayList<>();
        for (int i = 0; i < MAX_AUDIO_SHOTS; i++) {
            Media media = new Media(getClass().getResource("/shot.wav").toExternalForm());
            MediaPlayer mediaPlayer = new MediaPlayer(media);
            shotPlayers.add(mediaPlayer);
        }
        
        Media media = new Media(getClass().getResource("/gameover.wav").toExternalForm());
        gameOverPlayer = new MediaPlayer(media);
    }

    /**
     * Initialize background music.
     * 
     * @param mp3Files list of MP3 files
     */
    private void initBackgroundMusic(String... mp3Files) {
        Media[] mediaList = new Media[mp3Files.length];
        mediaPlayerList = new MediaPlayer[mp3Files.length];

        // Create Media objects for each MP3 file
        for (int i = 0; i < mp3Files.length; i++) {
            try {
                mediaList[i] = new Media(getClass().getResource(mp3Files[i]).toURI().toString());
            } catch (URISyntaxException e) {
                System.err.println("Error loading media file: " + mp3Files[i]);
            }
        }

        // Create MediaPlayer objects
        for (int i = 0; i < mp3Files.length; i++) {
            mediaPlayerList[i] = new MediaPlayer(mediaList[i]);
            mediaPlayerList[i].setVolume(0.8);
        }
    }

    /**
     * Start playing background music.
     */
    private void playBackgroundMusic() {
        playNextMusic(0);
    }
    
    /**
     * Play next song; recursive method calls.
     */
    private void playNextMusic(int index) {
        if (index >= mediaPlayerList.length) {
            // Reset index to 0 if it exceeds the array length
            index = 0;
        }

        final int i = index;
        
        mediaPlayerList[index].seek(Duration.ZERO);
        mediaPlayerList[index].setOnEndOfMedia(() -> {
            // Play the next MP3 file recursively
            playNextMusic(i + 1);
        });

        mediaPlayerList[index].play();
    }
    
    /**
     * Helper method to transform a color into RGB codes for dynamic background.
     * 
     * @param color color
     * @return RGB value
     */
    private static String toRGBCode(Color color) {
        // Convert Color object to RGB code
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }
 
    /**
     * Main method.
     * 
     * @param args console arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    
    /**
     * Scene size change listener.
     */
    private static class SceneSizeChangeListener implements ChangeListener<Number> {

        private final Scene scene;
        private final double ratio;
        private final double initHeight;
        private final double initWidth;
        private final Pane contentPane;

        public SceneSizeChangeListener(Scene scene, double ratio, double initHeight, double initWidth, Pane contentPane) {
            this.scene = scene;
            this.ratio = ratio;
            this.initHeight = initHeight;
            this.initWidth = initWidth;
            this.contentPane = contentPane;
        }

        @Override
        public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
            width = scene.getWidth();
            height = scene.getHeight();

            double scaleFactor
                    = width / height > ratio
                            ? height / initHeight
                            : width / initWidth;

            if (scaleFactor >= 1) {
                Scale scale = new Scale(scaleFactor, scaleFactor);
                scale.setPivotX(0);
                scale.setPivotY(0);
                scene.getRoot().getTransforms().setAll(scale);

                contentPane.setPrefWidth(width / scaleFactor);
                contentPane.setPrefHeight(height / scaleFactor);
            } else {
                contentPane.setPrefWidth(Math.max(initWidth, width));
                contentPane.setPrefHeight(Math.max(initHeight, height));
            }
        }
    }
      
}
