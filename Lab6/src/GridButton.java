import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;

class GridButton extends Button {
	CustomImageView cover, mineGrey, mineRed, flag, misflagged, dent;
	private int state = 0; // 0 = Covered, 1 = Clicked, 2 = Flagged
	private int tile = -1; // Tile value = near by bombs. If value = 9, tile is a bomb.
	private double size;
	private boolean peeking = false;


	public GridButton(FaceButton face, Timer timer,
			MineGrid mineGrid, MineCounter mineCounter) {
		size = 16;
		setMinWidth(size);
		setMaxWidth(size);
		setMinHeight(size);
		setMaxHeight(size);

		cover = new CustomImageView(new Image("file:res/cover.png"), size);
		mineGrey = new CustomImageView(new Image("file:res/mine-grey.png"), size);
		mineRed = new CustomImageView(new Image("file:res/mine-red.png"), size);
		flag = new CustomImageView(new Image("file:res/flag.png"), size);
		misflagged = new CustomImageView(new Image("file:res/mine-misflagged.png"), size);
		dent = new CustomImageView(new Image("file:res/0.png"), size);

		setGraphic(cover);
		setStyle("-fx-focus-color: transparent;"
				+ "-fx-faint-focus-color: transparent");

		setOnMouseClicked(e -> {
			MouseButton mouseButton = e.getButton();
			if (mouseButton == MouseButton.PRIMARY) {
				face.setGraphic(face.smile);
				if (timer.getStatus() == 0) {
					if (getTile() != 0) mineGrid.clickZero(this); // This is fake Ken minesweeper code
					/* This is real minesweeper code
					if (getTile() == 9) {
						mineGrid.moveMine(this);
						mineGrid.determineNumbers();
					}
					*/
					timer.start();
				}
				if (getState() == 1) {
					peek(mineGrid);
				}
				if (getState() != 2) 
					mineGrid.checkClick(this, face, timer);
			}
		});
		setOnDragDetected(e -> {
			MouseButton mouseButton = e.getButton();
			if (mouseButton == MouseButton.PRIMARY) {
				peek(mineGrid);
				startFullDrag();
			}
		});
		setOnMousePressed(e -> {
			MouseButton mouseButton = e.getButton();
			if (mouseButton == MouseButton.PRIMARY) {
				face.setGraphic(face.o);
				peek(mineGrid);
			}
			else if (mouseButton == MouseButton.SECONDARY) {
				if (getState() == 2) {
					setState(0);
					mineCounter.removeFlag();
				}
				else if (getState() == 0) {
					setState(2);
					mineCounter.addFlag();
				}
			}
		});
		setOnMouseDragEntered(e -> {
			peek(mineGrid);
		});
		setOnMouseDragExited(e ->{
			peek(mineGrid);
		});
		setOnMouseDragReleased(e -> {
			face.setGraphic(face.smile);
			if (timer.getStatus() == 0) {
				if (getTile() == 9) {
					mineGrid.moveMine(this);
					mineGrid.determineNumbers();
				}
				timer.start();
			}
			if (getState() == 0)
				peekMany(mineGrid);
			if (getState() != 2) {
				mineGrid.checkClick(this, face, timer);
			}
		});
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;

		switch (state) {
		case 0:
			setGraphic(cover);
			break;
		case 2:
			setGraphic(flag);
			break;
		}
	}

	public int getTile() {
		return tile;
	}

	public void setTile(int tile) {
		this.tile = tile;
	}

	public void peek(MineGrid mineGrid) {
		if (state == 0) {
			peeking = !peeking;
			if (peeking) {
				setGraphic(dent);
			}
			else {
				setGraphic(cover);
			}
		}
		else if(state ==1)
			peekMany(mineGrid);
	}

	public void peekMany(MineGrid mineGrid) {
		int xStart = MineGrid.getRowIndex(this);
		int yStart = MineGrid.getColumnIndex(this);
		int xEnd = (xStart + 1 == mineGrid.mineField.length) ? xStart : xStart + 1;
		int yEnd = (yStart + 1 == mineGrid.mineField[0].length) ? yStart : yStart + 1;
		xStart = (xStart - 1 < 0) ? xStart : xStart - 1;
		yStart = (yStart - 1 < 0) ? yStart : yStart - 1;

		GridButton c;

		for (int i = xStart; i <= xEnd; i++) {
			for (int j = yStart; j <= yEnd; j++) {
				c = mineGrid.mineField[i][j];
				if (c.getState() == 0) {
					c.peek(mineGrid);
				}
			}
		}
	}


	public void uncover() {
		switch (tile) {
		case 9:
			setGraphic(mineGrey);
			break;
		default:
			setGraphic(new CustomImageView(new Image("file:res/" + tile + ".png"), size));
			break;
		}
		setState(1);
	}
}