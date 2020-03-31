/*
 * Mitchell Vivian
 * COSC121
 * March 6 2020
 * 
 * Minesweeper
 * 
 * TODO:
 * -> Consolidate Image classes into one single class?
 * -> Create reset methods to return to start state without creating entirely new objects.
 * -> Change the whole program so that there are no more static methods in Minesweeper.java :^(
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Minesweeper extends Application {
	int difficulty = 1;
	int height = 8;
	int width = 8;
	int mines = 10;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage theStage) {
		theStage.setTitle("Minesweeper");
		theStage.getIcons().add(new Image("file:res/mine-grey.png"));
		if (theStage.getScene() == null) readState();
		
		VBox containAllVBox = new VBox();
		BorderPane mainBorderPane = new BorderPane();
		BorderPane headerBoarderPane = new BorderPane();
		FaceButton face = new FaceButton();
		Timer timer = new Timer(difficulty);
		MineCounter mineCounter = new MineCounter(mines);
		MineGrid mineGrid = new MineGrid(height, width, mines, face, timer, mineCounter);
		MenuBar menuBar = createMenuBar(theStage, timer);

		// Debug for array. Shows mine locations in terminal.
		/*
		System.out.println("==DEBUG==");
		for (int i = 0; i < mineGrid.mineField.length; i++) {
			for(int j = 0; j < mineGrid.mineField[i].length; j++) {
				System.out.print(mineGrid.mineField[i][j].getTile() + " ");
			}
			System.out.println();
		}
		System.out.println();
		*/

		// Start a new game by reinitializing the stage.
		face.setOnAction(e -> {
			start(theStage);
		});
		theStage.setOnCloseRequest(e-> {
			System.exit(0);
		});

		headerBoarderPane.setLeft(mineCounter);
		headerBoarderPane.setCenter(face);
		headerBoarderPane.setRight(timer);
		mainBorderPane.setCenter(headerBoarderPane);
		mainBorderPane.setBottom(mineGrid);
		containAllVBox.getChildren().addAll(menuBar, mainBorderPane);

		headerBoarderPane.setPadding(new Insets(5));
		mainBorderPane.setPadding(new Insets(5));
		menuBar.setStyle("-fx-background-color: white;"
				+ "-fx-border-color: #BDBDBD;"
				+ "-fx-border-width: 0 0 2 0;");
		headerBoarderPane.setStyle("-fx-border-width:2px;"
				+ "-fx-border-color: #7b7b7b white white #7b7b7b;"
				+ "-fx-border-style: solid;"
				+ "-fx-border-radius: 0.01;");
		mineCounter.setStyle("-fx-border-width:1px;"
				+ "-fx-border-color: #7b7b7b white white #7b7b7b;"
				+ "-fx-border-style: solid;"
				+ "-fx-border-radius: 0.01;");
		timer.setStyle("-fx-border-width:1px;"
				+ "-fx-border-color: #7b7b7b white white #7b7b7b;"
				+ "-fx-border-style: solid;"
				+ "-fx-border-radius: 0.01;");
		mineGrid.setStyle("-fx-border-width:3px;"
				+ "-fx-border-insets: 5 0 0 0;"
				+ "-fx-border-color: #7b7b7b white white #7b7b7b;"
				+ "-fx-border-radius: 0.01;"
				);
		mainBorderPane.setStyle("-fx-background-color: #BDBDBD;"
				+ "-fx-border-width: 3 3 3 3;"
				+ "-fx-border-color: white #7b7b7b #7b7b7b white;"
				+ "-fx-border-radius: 0.01;");
				
		Scene scene = new Scene(containAllVBox);
		theStage.setScene(scene);
		//theStage.setResizable(false); // This causes a strange sizing issue that I cannot figure out.
		theStage.show();
	}

	public MenuBar createMenuBar(Stage theStage, Timer timer) {
		MenuBar menuBar = new MenuBar();
		MenuItem newGameMIT = new MenuItem("New");
		ToggleGroup difficultySelectionTGG = new ToggleGroup();
		RadioMenuItem beginnerRMI = new RadioMenuItem("Beginner");
		RadioMenuItem intermediateRMI = new RadioMenuItem("Intermediate");
		RadioMenuItem expertRMI = new RadioMenuItem("Expert");
		RadioMenuItem customRMI = new RadioMenuItem("Custom...");
		MenuItem highScoresMIT = new MenuItem("Best Times...");
		MenuItem exitMIT = new MenuItem("Exit");
		beginnerRMI.setToggleGroup(difficultySelectionTGG);
		intermediateRMI.setToggleGroup(difficultySelectionTGG);
		expertRMI.setToggleGroup(difficultySelectionTGG);
		customRMI.setToggleGroup(difficultySelectionTGG);

		switch (difficulty) {
		case 1:
			beginnerRMI.setSelected(true);
			break;
		case 2:
			intermediateRMI.setSelected(true);
			break;
		case 3:
			expertRMI.setSelected(true);
			break;
		case 0:
			customRMI.setSelected(true);
		}

		Menu gameMenu = new Menu("Game");
		gameMenu.getItems().addAll(
				newGameMIT,
				new SeparatorMenuItem(),
				beginnerRMI,
				intermediateRMI,
				expertRMI,
				customRMI,
				new SeparatorMenuItem(),
				highScoresMIT,
				new SeparatorMenuItem(),
				exitMIT
				);

		newGameMIT.setOnAction(e -> {
			start(theStage);
		});
		beginnerRMI.setOnAction(e -> {
			setDifficulty(1);
			start(theStage);
		});
		intermediateRMI.setOnAction(e -> {
			setDifficulty(2);
			start(theStage);
		});
		expertRMI.setOnAction(e -> {
			setDifficulty(3);
			start(theStage);
		});
		customRMI.setOnAction(e -> {
			customDifficulty(theStage);
		});
		highScoresMIT.setOnAction(e -> {
			timer.showHighScores();
		});
		exitMIT.setOnAction(e -> {
			System.exit(0);
		});

		menuBar.getMenus().add(gameMenu);
		return menuBar;
	}

	public void customDifficulty(Stage theStage) {
		Stage stage = new Stage();
		HBox containAllHBox = new HBox();
		GridPane gPane = new GridPane();
		VBox buttonsVBox = new VBox();
		stage.setTitle("Custom Field");
		
		Label heightLBL = new Label("Height:");
		gPane.add(heightLBL, 0, 0);
		Label widthLbL = new Label("Width:");
		gPane.add(widthLbL, 0, 1);
		Label minesLBL = new Label("Mines:");
		gPane.add(minesLBL, 0, 2);
		TextField heightTBX = new TextField(String.valueOf(height));
		heightTBX.setMaxWidth(50);
		gPane.add(heightTBX, 1, 0);
		TextField widthTBX = new TextField(String.valueOf(width));
		widthTBX.setMaxWidth(50);
		gPane.add(widthTBX, 1, 1);
		TextField minesTBX = new TextField(String.valueOf(mines));
		minesTBX.setMaxWidth(50);
		gPane.add(minesTBX, 1, 2);
		Button okBTN = new Button("OK");
		okBTN.setMinWidth(60);
		okBTN.setPadding(new Insets(3));
		Button cancelBTN = new Button("Cancel");
		cancelBTN.setMinWidth(60);
		cancelBTN.setPadding(new Insets(3));
		
		okBTN.setOnAction(e -> {
			int maxHeight = 24, maxWidth = 30, maxMines;
			int minHeight = 9, minWidth = 9, minMines = 10;
			int userHeight = Integer.parseInt(heightTBX.getText());
			int userWidth = Integer.parseInt(widthTBX.getText());
			int userMines = Integer.parseInt(minesTBX.getText());
			if (userHeight > maxHeight) height = maxHeight;
			else if (userHeight < minHeight) height = minHeight;
			else height = userHeight;
			if (userWidth > maxWidth) width = maxWidth;
			else if (userWidth < minWidth) width = minWidth;
			else width = userWidth;
			maxMines = (height - 1) * (width - 1);
			if (userMines > maxMines) mines = maxMines;
			else if (userMines < minMines) mines = minMines;
			else mines = userMines;
			stage.close();
			setDifficulty(0);
			start(theStage);
		});
		cancelBTN.setOnAction(e -> {
			stage.close();
		});
		buttonsVBox.getChildren().addAll(okBTN, cancelBTN);
		containAllHBox.getChildren().addAll(gPane, buttonsVBox);
		containAllHBox.setPadding(new Insets(30, 20, 30, 20));
		gPane.setHgap(10);
		gPane.setVgap(5);
		buttonsVBox.setSpacing(20);
		containAllHBox.setSpacing(10);
		Scene scene = new Scene(containAllHBox);
		stage.initStyle(StageStyle.UTILITY);
		stage.setResizable(false);
		stage.setScene(scene);
		stage.sizeToScene();
		stage.showAndWait();
	}

	public void setDifficulty(int difficulty) {
		this.difficulty = difficulty;
		setGridSize();
	}

	public void setGridSize() {
		switch (difficulty) {
		case 1:
			height = 8;
			width = 8;
			mines = 10;
			break;
		case 2:
			height = 16;
			width = 16;
			mines = 40;
			break;
		case 3:
			height = 16;
			width = 32;
			mines = 99;
			break;
		}
		writeState();
	}

	public static void win(GridButton b, FaceButton face,
			Timer timer, MineGrid mineGrid) {
		face.setGraphic(face.glasses);
		endGame(true, b, face, timer, mineGrid);
		timer.checkScore();
		
		
	}

	public static void lose(GridButton b, FaceButton face,
			Timer timer, MineGrid mineGrid) {
		b.setGraphic(b.mineRed);
		b.setState(1);
		face.setGraphic(face.dead);
		endGame(false, b, face, timer, mineGrid);
	}

	// When a game ends, all non exploded and non flagged mines appear.
	// Disable all buttons.
	public static void endGame(boolean win, GridButton b, FaceButton face,
			Timer timer, MineGrid mineGrid) {
		timer.stop();
		GridButton c;
		mineGrid.setMouseTransparent(true);
		for (int i = 0; i < mineGrid.mineField.length; i++)
			for (int j = 0; j < mineGrid.mineField[i].length; j++) {
				c = mineGrid.mineField[i][j];
				if (c.getTile() == 9) {
					if (c.getState() != 2 && !c.equals(b)) {
						if (win)
							c.setGraphic(c.flag);
						else
							c.setGraphic(c.mineGrey);
					}
				}
				else if ( c.getState() == 2)
					c.setGraphic(c.misflagged);
			}
	}

	private void writeState() {
		File stateFile = new File("state.txt");
		if (!stateFile.exists())
			try {
				stateFile.createNewFile();
			} catch (IOException e1) {
				System.out.println("Error creating state.txt file.");
				e1.printStackTrace();
			}
		try (PrintWriter pw = new PrintWriter(stateFile)) {
			pw.println(difficulty);
			pw.println(height);
			pw.println(width);
			pw.println(mines);
		} catch (FileNotFoundException e) {
			System.out.println("State file not found!\n" + e);
		}
	}

	private void readState() {
		File stateFile = new File("state.txt");
		if (stateFile.exists()) {
			try (Scanner scan = new Scanner(stateFile)) {
				difficulty = scan.nextInt();
				height = scan.nextInt();
				width = scan.nextInt();
				mines = scan.nextInt();
			} catch (FileNotFoundException e) {
				System.out.println("State file not found!\n" + e);
			} 
		}
	}

}

class FaceButton extends Button {
	ImageView smile, glasses, dead, o; 
	public FaceButton() {
		double size = 25;

		setMinWidth(size);
		setMaxWidth(size);
		setMinHeight(size);
		setMaxHeight(size);

		smile = new CustomImageView(new Image("file:res/face-smile.png"), size);
		glasses = new CustomImageView(new Image("file:res/face-win.png"), size);
		dead = new CustomImageView(new Image("file:res/face-dead.png"), size);
		o = new CustomImageView(new Image("file:res/face-O.png"), size);

		setStyle("-fx-focus-color: transparent;"
			+ "-fx-faint-focus-color: transparent");
		setGraphic(smile);
	}

}

class CustomImageView extends ImageView {
	public CustomImageView (Image image, double size) {
		super(image);
		setFitWidth(size);
		setFitHeight(size);
	}
	public CustomImageView(Image image) {
		super(image);
		setFitWidth(25);
		setFitHeight(25);
	}
}