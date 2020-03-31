import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class Timer extends HBox {
	private int difficulty, status = 0, time = 0;
	private Timeline tick;
	private Digit hundreds, tens, ones;
	private String[] highScores;

	public Timer(int difficulty) {
		setAlignment(Pos.CENTER_RIGHT);
		this.difficulty = difficulty;
		hundreds = new Digit();
		tens = new Digit();
		ones = new Digit();
		tick = new Timeline(new KeyFrame(Duration.millis(1000), e -> {
			time++;
			updateDigits();
		}));
		getChildren().addAll(hundreds, tens, ones);
		tick.setCycleCount(999);

		highScores = new String[3];
		File scoresFile = new File("high_scores.txt");
		if (scoresFile.exists()) {
			try (Scanner scan = new Scanner(scoresFile);) {
				highScores[0] = scan.nextLine();
				highScores[1] = scan.nextLine();
				highScores[2] = scan.nextLine();
			} catch (FileNotFoundException e1) {
				System.out.println("high_scores.txt not found");
				e1.printStackTrace();
			}
		}
		else {
			highScores[0] = "999 seconds\tAnonymous";
			highScores[1] = "999 seconds\tAnonymous";
			highScores[2] = "999 seconds\tAnonymous";
		}

	}

	private void updateDigits() {
		int onesInt = time % 10;
		int tensInt = time / 10 % 10;
		int hundredsInt = time / 100 % 10;

		ones.setDigit(onesInt);
		tens.setDigit(tensInt);
		hundreds.setDigit(hundredsInt);
	}

	public int getStatus() {
		return status;
	}

	public void start() {
		tick.play();
		status = 1;
	}

	public void stop() {
		tick.stop();
		status = 0;
	}

	@SuppressWarnings("resource")
	public void checkScore() {
		int selection = difficulty - 1;
		Scanner scan = new Scanner(highScores[selection]);
		int highScore = scan.nextInt();
		if (time < highScore)
			enterHighScore(selection);
	}

	public void enterHighScore(int selection) {
		String currentDifficulty = "";
		switch (selection){
		case 0:
			currentDifficulty = "beginner";
			break;
		case 1:
			currentDifficulty = "intermediate";
			break;
		case 2:
			currentDifficulty = "expert";
			break;
		}
		Stage stage = new Stage();
		Label infoLBL = new Label("You have the fastest time\nfor "
				+ currentDifficulty
				+ " level.\n\nPlease enter your name.");
		TextField nameTBX = new TextField("Anonymous");
		Button okBTN = new Button("OK");
		VBox vBox = new VBox();
		
		okBTN.setOnAction(e -> {
			highScores[selection] = time + " seconds\t" + nameTBX.getText();
			saveScores();
			showHighScores();
			stage.close();
		});
		nameTBX.setOnAction(e -> {
			okBTN.fire();
		});
		nameTBX.setOnMouseClicked(e -> {
			nameTBX.selectAll();
		});

		nameTBX.setMaxWidth(120);
		stage.initStyle(StageStyle.UNDECORATED);
		vBox.getChildren().addAll(infoLBL, nameTBX, okBTN);
		vBox.setSpacing(10);
		vBox.setPadding(new Insets(10));
		vBox.setAlignment(Pos.CENTER);
		
		Scene scene = new Scene(vBox);
		stage.setScene(scene);
		stage.showAndWait();
	}

	public void showHighScores() {
		Stage stage = new Stage();
		stage.setTitle("Fastest Mine Sweepers");
		GridPane gPane = new GridPane();
		VBox vBox = new VBox();
		HBox hBox = new HBox();
		Label beginnerLBL = new Label("Beginner:\t\t");
		Label intermediateLBL = new Label("Intermediate:\t");
		Label expertLBL = new Label("Expert:\t\t");
		Label beginnerScoreLBL = new Label(highScores[0]);
		Label intermediateScoreLBL = new Label(highScores[1]);
		Label expertScoreLBL = new Label(highScores[2]);

		Button resetScoresBTN = new Button("Reset Scores");
		Button okBTN = new Button("OK");

		okBTN.setOnAction(e -> {
			stage.close();
		});
		resetScoresBTN.setOnAction(e -> {
			highScores[0] = "999 seconds\tAnonymous";
			highScores[1] = "999 seconds\tAnonymous";
			highScores[2] = "999 seconds\tAnonymous";
			saveScores();
			stage.close();
			showHighScores();
		});
		okBTN.setOnKeyTyped(e -> {
			if (e.getCode() == KeyCode.ENTER)
				okBTN.fire();
		});
		resetScoresBTN.setOnKeyTyped(e -> {
			if (e.getCode() == KeyCode.ENTER)
				resetScoresBTN.fire();
		});

		hBox.getChildren().addAll(resetScoresBTN, okBTN);
		gPane.add(beginnerLBL, 0, 0);
		gPane.add(beginnerScoreLBL, 1, 0);
		gPane.add(intermediateLBL, 0, 1);
		gPane.add(intermediateScoreLBL, 1, 1);
		gPane.add(expertLBL, 0, 2);
		gPane.add(expertScoreLBL, 1, 2);
		hBox.setSpacing(50);
		hBox.setPadding(new Insets(20, 0, 0, 0));
		hBox.setAlignment(Pos.CENTER);
		vBox.getChildren().addAll(gPane, hBox);
		vBox.setPadding(new Insets(20, 20, 10, 20));

		okBTN.requestFocus();
		Scene scene = new Scene(vBox);
		stage.setScene(scene);
		stage.setResizable(false);
		stage.setAlwaysOnTop(true);
		stage.initStyle(StageStyle.UTILITY);
		stage.showAndWait();
	}

	private void saveScores() {
		File scoresFile = new File("high_scores.txt");
		try (PrintWriter pw = new PrintWriter(scoresFile);){
			pw.println(highScores[0]);
			pw.println(highScores[1]);
			pw.println(highScores[2]);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
	}

}

class MineCounter extends HBox {
	private int count, mines;
	private int flags = 0;
	private Digit hundreds, tens, ones;

	public MineCounter(int mines) {
		setAlignment(Pos.CENTER_LEFT);
		this.mines = mines;
		count = mines;

		ones = new Digit();
		tens = new Digit();
		hundreds = new Digit();

		updateCount();
		getChildren().addAll(hundreds, tens, ones);


	}

	public void addFlag() {
		flags++;
		updateCount();
	}

	public void removeFlag() {
		flags--;
		updateCount();
	}

	public int getMines() {
		return mines;
	}

	private void updateCount() {
		count = mines - flags;
		if (count < 0)
			count = 0;

		int onesInt = count % 10;
		int tensInt = count / 10 % 10;
		int hundredsInt = count / 100 % 10;

		ones.setDigit(onesInt);
		tens.setDigit(tensInt);
		hundreds.setDigit(hundredsInt);
	}
}

class Digit extends ImageView {
	double xSize = 13;
	double ySize = 23;
	public Digit () {
		super(new Image("file:res/digits/0.png"));
		setFitWidth(xSize);
		setFitHeight(ySize);
	}

	public void setDigit(int num) {
		setImage(new Image("file:res/digits/" + num + ".png"));
	}

}