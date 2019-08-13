import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.MouseButton;

public class GameFile extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// want a GridPane, good layout for a 2D array of buttons
		GridPane root = new GridPane();
		Scene scene = new Scene(root, 800, 800);
		
		// IDEA: have a separate 2D array of buttons
		
		// to access the buttons later on, maintain a 2D array of them
		Button[][] buttons = new Button[20][20];
		
		// add a grid of buttons, 20x20 for now
		// let's assume the screen is not resizable
		for(int rows = 0; rows < 20; rows++) {
			for(int cols = 0; cols < 20; cols++) {
				buttons[rows][cols] = new Button();
				buttons[rows][cols].setPrefHeight(scene.getHeight() / 20);
				buttons[rows][cols].setPrefWidth(scene.getWidth() / 20);
				root.add(buttons[rows][cols], cols, rows);
			}
		}
		
		// You must place a number of different mines on random locations of the board.
		int minesPlaced = 0;
		while(minesPlaced < 30) {
			int row = (int)(Math.random() * 20);
			int col = (int)(Math.random() * 20);
			// make sure you don't place a mine in the same square twice,
			// so make the colors very slightly different.
			if (!buttons[row][col].getStyle().equals("-fx-background-color: #00CCCD")) {
				buttons[row][col].setStyle("-fx-background-color: #00CCCD");
				buttons[row][col].addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
		            @Override
		            public void handle(MouseEvent event) {
		            	// Action #1: Left click.
		                if(event.getButton() == MouseButton.PRIMARY){
		                	buttons[row][col].setStyle("-fx-background-color: Black");
		                }
		                // Action #2: Right click.
		                else if(event.getButton() == MouseButton.SECONDARY) {
		                	// This means you want to place a flag here.
		                	// TODO: What if it's already pink? How do you unclick?
		                	// Check the original color, then store it?
		                	buttons[row][col].setStyle("-fx-background-color: Pink");
		                }
		            }
		        });
				minesPlaced++;
			}
		}
		
		// now, for the regular squares: change the color to purple upon clicking
		for(int i = 0; i < 20; i++) {
			for(int j = 0; j < 20; j++) {
				if(!buttons[i][j].getStyle().equals("-fx-background-color: #00CCCD")) {
					buttons[i][j].setStyle("-fx-background-color: #00CCCC");
					styleSet(buttons, i, j);
				}
			}
		}

		primaryStage.setScene(scene);
		primaryStage.setTitle("Minesweeper");
		primaryStage.setResizable(false);
		primaryStage.show();
	}
	
	// TODO: apply this functionality to the mines as well, for consistency.
	
	// NOTE: This functionality only applies to buttons that aren't mines.
	// an odd work-around solution, because lambda variables cannot change
	// check all of the other buttons: how many mines are there?
	// the first thing to do is to update the board
	// DESIGN DECISION: Yellow buttons are buttons that are revealed.
	// FOR RIGHT NOW: Red buttons are mines.
	public void styleSet(Button[][] buttons, int i, int j) {
		// TODO: differentiate between actions.
		// ACTION #1: Left click.
		buttons[i][j].setOnAction(e -> {
			reveal(buttons, i, j);
			buttons[i][j].setStyle("-fx-background-color: Yellow");
		});
		// ACTION #2: Right click.
		// Here, you want to place a flag.
	}
	
	// Make sure you're revealing buttons that are actually on the board.
	public boolean isValid(Button[][] buttons, int rownum, int colnum) {
		if(rownum >= 0 && colnum >= 0 && rownum < buttons.length && 
				colnum < buttons[0].length) {
			return true;
		}
		return false;
	}
	
	public void reveal(Button[][] buttons, int rownum, int colnum) {
		if(!isValid(buttons, rownum, colnum) || !buttons[rownum][colnum].getStyle().equals(
				"-fx-background-color: #00CCCC")) {
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
				if(buttons[rownum + rowAdd][colnum + colAdd].getStyle().equals(
						"-fx-background-color: #00CCCD")) {
					bombCount++;
				}
			}
		}
		
        if(bombCount > 0){
            // Place a digit marker in the box.
        	buttons[rownum][colnum].setStyle("-fx-background-color: Yellow");
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
        	// place a 'B' marker in the box (means you were here before)
        	buttons[rownum][colnum].setStyle("-fx-background-color: Yellow");
            for(int j = 0; j < tuples.length; j++){
            	int rowAdd = tuples[j][0];
                int colAdd = tuples[j][1];
                if(isValid(buttons, rownum + rowAdd, colnum + colAdd)){
                	reveal(buttons, rownum + rowAdd, colnum + colAdd);
                }
            }
        }
	}
}
