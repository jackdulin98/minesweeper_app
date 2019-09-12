import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.control.Label;
import javafx.scene.text.Font;

public class GameFile extends Application {

	// OVERALL GOAL: After every click, check to see if winning conditions have been satisfied.
	// SHOCKING DISCOVERY: If you make the main method private, the program still runs!
	// However, this might yield a message saying the main method is not public during runtime.
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	// This is where the program begins execution after being launched.
	public void start(Stage primaryStage) throws Exception {
		GridPane root = new GridPane();				// want a GridPane, ideal layout for 2D button array
		Scene scene = new Scene(root, 800, 800);	// want an 800px by 800px window
		
		// To access the buttons later on, maintain a 2D array of them
		// TODO: Over here, maybe keep track of where the mines are in a separate array.
		boolean[][] mines = new boolean[20][20];
		// Also, keep track of the mines that have been uncovered.
		boolean[][] uncovered = new boolean[20][20];
		Button[][] buttons = new Button[20][20];
		
		// Add a 20x20 grid of buttons and let's assume the screen is not resizable to simplify matters for now.
		for(int rows = 0; rows < 20; rows++) {
			for(int cols = 0; cols < 20; cols++) {
				buttons[rows][cols] = new Button();
				// Right now, we will assume no gaps between the buttons.
				buttons[rows][cols].setPrefHeight(scene.getHeight() / 20);
				buttons[rows][cols].setPrefWidth(scene.getWidth() / 20);
				root.add(buttons[rows][cols], cols, rows);
			}
		}
		
		// Initialize the mines 2D array to be all false values.
		for(int i = 0; i < 20; i++) {
			for(int j = 0; j < 20; j++) {
				mines[i][j] = false;
				uncovered[i][j] = false;
			}
		}
		
		// You must place a number of different mines on random locations of the board.
		int minesPlaced = 0;
		while(minesPlaced < 30) {
			int row = (int)(Math.random() * 20);
			int col = (int)(Math.random() * 20);
			/* 
			 * TODO: See if you can improve upon this current design decision.
			We might want to improve upon this because if we place a flag on something that isn't a mine,
			the original color will not be remembered.
			*/
			// The default color for all buttons will be gray.
			if (mines[row][col] == false) {
				mines[row][col] = true;
				// This block of code goes through what happens when you click on a mine.
				// POSSIBLE QUESTION: What does it mean to add a filter to an event?
				buttons[row][col].addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
		            @Override
		            public void handle(MouseEvent event) {
		            	// Action #1: If you left click on a mine, it turns black.
		                if(event.getButton() == MouseButton.PRIMARY){
		                	buttons[row][col].setStyle("-fx-background-color: Black");
		                	gameOver();
		                }
		                // Action #2: If you right click on a mine, it turns pink.
		                else if(event.getButton() == MouseButton.SECONDARY) {
		                	// This means you want to place a flag here.
		                	// If clicked an even number of times, revert it back to its original color, the only color after I finish re-designing.
		                	// PROBLEM TO SOLVE: Must know if it's been uncovered or not.
		                	// TODO: If you double right click a yellow square, it becomes gray again. FIX THIS.
		                	if(buttons[row][col].getStyle().equals("-fx-background-color: Pink")) {
		                		if(uncovered[row][col] == true) {
		                			buttons[row][col].setStyle("-fx-background-color: Yellow");
		                		}
		                		else {
		                			buttons[row][col].setStyle("-fx-background-color: Gray");
		                		}
		                	}
		                	else {
		                		buttons[row][col].setStyle("-fx-background-color: Pink");
		                	}
		                }
		            }
		        });
				minesPlaced++;
			}
		}
		
		// Now, for the regular squares: change the color to purple upon clicking.
		for(int i = 0; i < 20; i++) {
			for(int j = 0; j < 20; j++) {
				// Initialize the screen to have all gray boxes.
				buttons[i][j].setStyle("-fx-background-color: Gray");
				// If the button is not a mine, perform the style set operations.
				if(mines[i][j] == false) {
					styleSet(buttons, mines, uncovered, i, j);
				}
			}
		}

		primaryStage.setScene(scene);
		primaryStage.setTitle("Minesweeper Knockoff");
		primaryStage.setResizable(false);
		primaryStage.show();
	}
	
	// TODO: Make sure you adhere to the commenting standards.
	
	/*
	 * CODE OF CONDUCT: 
	 * Start out with an entirely blue screen. 
	 * -> If you left-click on a mine, it turns pink.
	 * -> If you right-click on a mine, it turns black.
	 * -> If you left-click on a non-mine, it turns pink.
	 * -> If you right-click on a non-mine, it turns yellow.
	*/
	
	public void styleSet(Button[][] buttons, boolean[][] mines, boolean[][] uncovered, int i, int j) {
		// POSSIBLE QUESTION: What is an event filter?
		buttons[i][j].addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
            	// Action #1: If you left click on a non-mine, it turns yellow.
                if(event.getButton() == MouseButton.PRIMARY){
                	reveal(buttons, mines, uncovered, i, j);
                	buttons[i][j].setStyle("-fx-background-color: Yellow");
                	// Here, check to see if you won the game. When doing this, only look for the number of yellow squares.
                	if(checkWinner(buttons)) {
                		Platform.exit();
                		System.out.println("Hooray! You won the game!");
                	}
                }
                // Action #2: If you right click on a mine, it turns pink.
                else if(event.getButton() == MouseButton.SECONDARY) {
                	// This means you want to place a flag here.
                	if(buttons[i][j].getStyle().equals("-fx-background-color: Pink")) {
                		if(uncovered[i][j] == true) {
                			buttons[i][j].setStyle("-fx-background-color: Yellow");
                		}
                		else {
                			buttons[i][j].setStyle("-fx-background-color: Gray");
                		}
                	}
                	else {
                		buttons[i][j].setStyle("-fx-background-color: Pink");
                	}
                }
            }
        });
	}
	
	// Check to see if you won the game every time after you left-click a non-mine.
	public boolean checkWinner(Button[][] buttons) {
		int yellowBlocks = 0;
		for(int i = 0; i < 20; i++) {
			for(int j = 0; j < 20; j++) {
				if(buttons[i][j].getStyle().equals("-fx-background-color: Yellow")) {
					yellowBlocks++;
				}
			}
		}
		// For right now, we expect 370 free squares: (20*20 blocks) - (30 mines) = 370.
		if(yellowBlocks == 370) {
			return true;
		}
		return false;
	}
	
	// Initiate the game over message.
	public void gameOver() {
    	Stage innerStage = new Stage();
    	Label message = new Label("You lost the game!");
    	message.setFont(new Font(40));
    	BorderPane dialog = new BorderPane();
    	Scene innerScene = new Scene(dialog, 500, 150);
    	dialog.setCenter(message);
    	innerStage.setScene(innerScene);
    	innerStage.setTitle("Loser's Window");
    	innerStage.show();
	}
	
	// Make sure you're revealing buttons that are actually on the board.
	public boolean isValid(Button[][] buttons, int rownum, int colnum) {
		if(rownum >= 0 && colnum >= 0 && rownum < buttons.length && 
				colnum < buttons[0].length) {
			return true;
		}
		return false;
	}
	
	public void reveal(Button[][] buttons, boolean[][] mines, boolean[][] uncovered, int rownum, int colnum) {
		if(!isValid(buttons, rownum, colnum) || !buttons[rownum][colnum].getStyle().equals(
				"-fx-background-color: Gray")) {
			return;
		}
		
		// Count the number of bombs to determine the marker.
		int bombCount = 0;
		int[][] tuples = {{-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 1}, {1, -1}, 
				{1, 0}, {1, 1}};
		for(int i = 0; i < tuples.length; i++) {
			int rowAdd = tuples[i][0];
			int colAdd = tuples[i][1];
			if(isValid(buttons, rownum + rowAdd, colnum + colAdd)) {
				// If you see a mine, add it to the mine count.
				if(mines[rownum + rowAdd][colnum + colAdd] == true) {
					bombCount++;
				}
			}
		}
		
        if(bombCount > 0){
            // Place a digit marker in the box.
        	buttons[rownum][colnum].setStyle("-fx-background-color: Yellow");
        	uncovered[rownum][colnum] = true;
            switch(bombCount){
                case 1:
                    buttons[rownum][colnum].setText("1");
                    break;
                case 2:
                    buttons[rownum][colnum].setText("2");
                    break;
                case 3:
                    buttons[rownum][colnum].setText("3");
                    break;
                case 4:
                    buttons[rownum][colnum].setText("4");
                    break;
                case 5:
                    buttons[rownum][colnum].setText("5");
                    break;
                case 6:
                    buttons[rownum][colnum].setText("6");
                    break;
                case 7:
                    buttons[rownum][colnum].setText("7");
                    break;
                case 8:
                    buttons[rownum][colnum].setText("8");
                    break;
            }
            return;
        }
            
        if(bombCount == 0){
        	// Yellow means the box has been left-clicked, and it's not a mine.
        	buttons[rownum][colnum].setStyle("-fx-background-color: Yellow");
        	uncovered[rownum][colnum] = true;
            for(int j = 0; j < tuples.length; j++){
            	int rowAdd = tuples[j][0];
                int colAdd = tuples[j][1];
                if(isValid(buttons, rownum + rowAdd, colnum + colAdd)){
                	reveal(buttons, mines, uncovered, rownum + rowAdd, colnum + colAdd);
                }
            }
        }
	}
}
