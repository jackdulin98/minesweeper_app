package com.minesweeper.game;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.control.Label;
import javafx.scene.text.Font;

public class Minesweeper extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	// TODO: Allow for easy, medium, and hard difficulty levels.
	public void start(Stage primaryStage) throws Exception {
		GridPane root = new GridPane();				// want a GridPane, ideal layout for 2D button array
		Scene scene = new Scene(root, 800, 800);	// want an 800px by 800px window
		
		boolean[][] mines = new boolean[20][20];
		boolean[][] uncovered = new boolean[20][20];
		Button[][] buttons = new Button[20][20];
		
		// Add a 20x20 grid of buttons and let's assume the screen is not re-sizable to simplify matters for now.
		// TODO: Be able to adjust to a re-sizable screen.
		for(int rows = 0; rows < 20; rows++) {
			// Right now, we will assume no gaps between the buttons.
			// TODO: Be able to accommodate gaps between the buttons for esthetic purposes.
			for(int cols = 0; cols < 20; cols++) {
				buttons[rows][cols] = new Button();
				buttons[rows][cols].setPrefHeight(scene.getHeight() / 20);
				buttons[rows][cols].setPrefWidth(scene.getWidth() / 20);
				root.add(buttons[rows][cols], cols, rows);
			}
		}
		
		// Initially, there are no mines or uncovered buttons. 
		for(int i = 0; i < 20; i++) {
			for(int j = 0; j < 20; j++) {
				mines[i][j] = false;
				uncovered[i][j] = false;
			}
		}
		
		// Mines will be placed in random locations throughout the board.
		int minesPlaced = 0;
		while(minesPlaced < 30) {
			int row = (int)(Math.random() * 20);
			int col = (int)(Math.random() * 20);
			if (mines[row][col] == false) {
				mines[row][col] = true;
				buttons[row][col].addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
		            @Override
		            public void handle(MouseEvent event) {
		            	// Action #1: If you left click on a mine, it turns black and then the game is over.
		                if(event.getButton() == MouseButton.PRIMARY){
		                	buttons[row][col].setStyle("-fx-background-color: Black");
		                	gameOver();
		                }
		                // Action #2: If you right click on a mine, it turns pink which represents a flag.
		                else if(event.getButton() == MouseButton.SECONDARY) {
		                	if(buttons[row][col].getStyle().equals("-fx-background-color: Pink")) {
		                		buttons[row][col].setStyle("-fx-background-color: Gray");
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
		
		// All squares will be gray when the game starts.
		for(int i = 0; i < 20; i++) {
			for(int j = 0; j < 20; j++) {
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
	
	/**
	 * This method sets the style of a given button depending upon whether the button was left-clicked or right-clicked.
	 * @param buttons : 2D array that represents the current game state
	 * @param mines : 2D array with the positions of the mines
	 * @param uncovered : 2D array with the positions of the uncovered squares
	 * @param i : row number of the given button
	 * @param j : column number of the given button
	 */
	public void styleSet(Button[][] buttons, boolean[][] mines, boolean[][] uncovered, int i, int j) {
		buttons[i][j].addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
            	// Action #1: If you left click on a non-mine, it turns yellow and recursively reveals other non-mines.
                if(event.getButton() == MouseButton.PRIMARY){
                	reveal(buttons, mines, uncovered, i, j);
                	buttons[i][j].setStyle("-fx-background-color: Yellow");
                	// Check to see if the game has been won after uncovering the squares.
                	if(checkWinner(buttons)) {
                		youWon();
                	}
                }
                // Action #2: If you right click on a button that hasn't been uncovered, it turns pink.
                else if(event.getButton() == MouseButton.SECONDARY && uncovered[i][j] == false) {
                	if(buttons[i][j].getStyle().equals("-fx-background-color: Pink")) {
                		buttons[i][j].setStyle("-fx-background-color: Gray");
                	}
                	else {
                		buttons[i][j].setStyle("-fx-background-color: Pink");
                	}
                }
            }
        });
	}
	
	/**
	 * This method checks the board and determines if the game has been won.
	 * @param buttons : 2D array that represents the current game state
	 * @return true if the game has been won, false otherwise
	 */
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
		// TODO: Make this more variable when the difficulty levels are introduced.
		if(yellowBlocks == 370) {
			return true;
		}
		return false;
	}

	/**
	 * This method displays the game over screen after a mine is clicked.
	 */
	// TODO: After the game is over, you should have another window or button that gives the option to reset the board.
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
	
	/**
	 * This method displays the winning screen after all non-mine squares are successfully revealed.
	 */
	public void youWon() {
    	Stage innerStage = new Stage();
    	Label message = new Label("You won the game! Congratulations!");
    	message.setFont(new Font(40));
    	BorderPane dialog = new BorderPane();
    	Scene innerScene = new Scene(dialog, 900, 150);
    	dialog.setCenter(message);
    	innerStage.setScene(innerScene);
    	innerStage.setTitle("Winner's Window");
    	innerStage.show();
	}
	
	/**
	 * This method ensures that you're revealing buttons that are actually on the board.
	 * @param buttons : 2D array that represents the current game state
	 * @param rownum : row number of square to be validated
	 * @param colnum : column number of square to be validated
	 * @return true if the row number and column number are valid, false otherwise
	 */
	public boolean isValid(Button[][] buttons, int rownum, int colnum) {
		if(rownum >= 0 && colnum >= 0 && rownum < buttons.length && 
				colnum < buttons[0].length) {
			return true;
		}
		return false;
	}
	
	/**
	 * This method reveals a square after it has been left-clicked while recursively revealing the surrounding non-mine squares.
	 * @param buttons : 2D array that represents the current game state
	 * @param mines : 2D array with the positions of the mines
	 * @param uncovered : 2D array with the positions of the uncovered squares
	 * @param rownum : row number of the square being revealed
	 * @param colnum : column number of the square being revealed
	 */
	public void reveal(Button[][] buttons, boolean[][] mines, boolean[][] uncovered, int rownum, int colnum) {
		if(!isValid(buttons, rownum, colnum) || !buttons[rownum][colnum].getStyle().equals(
				"-fx-background-color: Gray")) {
			return;
		}
		
		// Count the number of bombs around the given square to determine the number that shows up on the square.
		int bombCount = 0;
		int[][] tuples = {{-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 1}, {1, -1}, 
				{1, 0}, {1, 1}};
		for(int i = 0; i < tuples.length; i++) {
			int rowAdd = tuples[i][0];
			int colAdd = tuples[i][1];
			if(isValid(buttons, rownum + rowAdd, colnum + colAdd)) {
				if(mines[rownum + rowAdd][colnum + colAdd] == true) {
					bombCount++;
				}
			}
		}
		
        // Place a digit marker in the uncovered box.
        if(bombCount > 0){
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