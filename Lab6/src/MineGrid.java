import javafx.scene.layout.GridPane;
public class MineGrid extends GridPane {
	private int numMines, numUncovered;
	public GridButton[][] mineField;

	public MineGrid (int height, int width, int mines,
			FaceButton face, Timer timer, MineCounter mineCounter) {
		mineField = new GridButton[height][width];
		numMines = mines;
		int rand1 = 0;
		int rand2 = 0;
		GridButton b;
		for (int i = 0; i < mineField.length; i++) {
			for (int j = 0; j < mineField[i].length; j++) {
				b = new GridButton(face, timer, this, mineCounter);
				mineField[i][j] = b;
				add(b, j, i);
				
			}
		}
		for(int i = 0; i < numMines; i++) {
			rand1 = (int)(Math.random() * mineField.length) ;
			rand2 = (int)(Math.random() * mineField[0].length);
			b = mineField[rand1][rand2];
			if (b.getTile() == 9) moveMine(b);
			b.setTile(9);
		}
		determineNumbers();
		
	}
	
	public void determineNumbers() {
		int xStart = 0;
		int xEnd = 0;
		int yStart = 0;
		int yEnd = 0;
		int numMines = 0;

		GridButton c;
		GridButton d;
		for (int x = 0; x < mineField.length; x++) {
			for (int y = 0; y < mineField[x].length; y++) {
				c = mineField[x][y];
				if (!(c.getTile() == 9)) {
					numMines = 0;
					xStart = x - 1;
					xEnd = x + 1;
					yStart = y - 1;
					yEnd = y + 1;
					if (xStart < 0)
						xStart++;
					if (xEnd > mineField.length - 1)
						xEnd--;

					if (yStart < 0)
						yStart++;
					if (yEnd > mineField[0].length - 1)
						yEnd--;
					for (int i = xStart; i <= xEnd; i++) {
						for (int j = yStart; j <= yEnd; j++) {
							d = mineField[i][j];
							if (d.getTile() == 9)
								numMines++;
						}
					}
					c.setTile(numMines);
				}
			}
		}

	}
	
	public void clickZero(GridButton b) {
		int rowStart = MineGrid.getRowIndex(b);
		int colStart = MineGrid.getColumnIndex(b);
		int rowEnd = (rowStart + 1 == mineField.length) ? rowStart : rowStart + 1;
		int colEnd = (colStart + 1 == mineField[0].length) ? colStart : colStart + 1;
		rowStart = (rowStart - 1 < 0) ? rowStart : rowStart - 1;
		colStart = (colStart - 1 < 0) ? colStart : colStart - 1;
		GridButton c;
		for (int col = colStart; col <= colEnd; col++) {
			for (int row = rowStart; row <= rowEnd; row++) {
				c = mineField[row][col];
				if (c.getTile()==9) {
					moveMine(c);
				}
			}
		}
		determineNumbers();
	}

	public void moveMine(GridButton b) {
		int row = getRowIndex(b);
		int col = getColumnIndex(b);

		row += 1;
		if (row > mineField.length - 1) {
			row = 0;
			col++;
		}
		if (col > mineField[0].length - 1) col = 0;
		
		GridButton c = mineField[row][col];
		if (c.getTile() == 9)
			moveMine(c);
		b.setTile(0);
		c.setTile(9);
	}
	
	public void checkClick(GridButton b, FaceButton face, Timer timer) {
		int mines, flags = 0;
		int xStart = MineGrid.getRowIndex(b);
		int yStart = MineGrid.getColumnIndex(b);
		int xEnd = (xStart + 1 == mineField.length) ? xStart : xStart + 1;
		int yEnd = (yStart + 1 == mineField[0].length) ? yStart : yStart + 1;
		xStart = (xStart - 1 < 0) ? xStart : xStart - 1;
		yStart = (yStart - 1 < 0) ? yStart : yStart - 1;

		if (b.getState() == 0) {
			if (b.getTile() == 9) {
				Minesweeper.lose(b, face, timer, this);
				return;
			}

			b.uncover();
			numUncovered++;
		}
		
		else if (b.getState() == 1) {
			GridButton c;
			for (int i = xStart; i <= xEnd; i++) {
				for (int j = yStart; j <= yEnd; j++) {
					c = mineField[i][j];
					if (c.getState() == 2)
						flags++;
				}
			}
		}
		
		mines = b.getTile() - flags;
		if (mines == 0) {
			GridButton c;
			for (int i = xStart; i <= xEnd; i++) {
				for (int j = yStart; j <= yEnd; j++) {
					c = mineField[i][j];
					if (c.getState() == 0)
						checkClick(c, face, timer);
				}
			}
		}
		checkWin(b, face, timer);
	}
	
	// Checks the board for the victory condition.
	public void checkWin(GridButton b, FaceButton face, Timer timer) {
		int spacesRemaining = mineField.length * mineField[0].length - numUncovered;
		if (spacesRemaining == numMines)
			Minesweeper.win(b, face, timer, this);
	}
}
