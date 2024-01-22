import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.applet.Applet;
import java.applet.AudioClip;
import java.net.URL;

/**
 * A class modelling a tic-tac-toe (noughts and crosses, Xs and Os) game.
 * 
 * @author Lynn Marshall
 * @version November 8, 2012
 * 
 * @author Areej Mahmoud 101218260
 * @version March 25, 2023
 * 
 * @author Areej Mahmoud 101218260
 * @version April 7, 2023
 */

public class TicTacToe implements ActionListener
{
   public static final String PLAYER_X = "X"; // player using "X"
   public static final String PLAYER_O = "O"; // player using "O"
   public static final String EMPTY = "";  // empty cell
   public static final String TIE = "T"; // game ended in a tie
 
   private String player;   // current player (PLAYER_X or PLAYER_O)

   private String winner;   // winner: PLAYER_X, PLAYER_O, TIE, EMPTY = in progress

   private int numFreeSquares; // number of squares still free
   
   private JPanel board; // JPanel with grid layout representing the board
   
   private JButton buttons[][]; // buttons representing grid squares.
   
   private JMenuItem newItem; // New menu Item resets the game to a new game
   private JMenuItem quitItem; // Quit Menu Item to quit the game
   
   private JTextArea status; // text area to print game status
   private JTextField tracker; // text area to print game tracker
   
   /*game tracker variables*/
   private int xWins;
   private int oWins;
   private int numTies;

   /* some icons */
   private static ImageIcon xIcon = new ImageIcon("blue_x.jpg");
   private static ImageIcon oIcon = new ImageIcon("blue_o.jpg");
   private static ImageIcon backgroundIcon = new ImageIcon("background.jpg");
   
   /*Audio Clips*/
   AudioClip click; //plays when a button is clicked
   AudioClip tieGame; //plays when the game is tied
   AudioClip winGame; // plays when the game is won
   
   /** 
    * Constructs a new Tic-Tac-Toe board.
    */
   public TicTacToe()
   {
       JFrame frame = new JFrame("TicTacToe");
       Container contentPane = frame.getContentPane();
       contentPane.setLayout(new BorderLayout()); 

       JMenuBar menubar = new JMenuBar();
       frame.setJMenuBar(menubar); // add menu bar to our frame
       
       JMenu GameMenu = new JMenu("Game"); // create a Game menu
       menubar.add(GameMenu); // add to our menu bar
       newItem = new JMenuItem("New"); // create a menu item called "New"
       GameMenu.add(newItem); // add to our menu
       quitItem = new JMenuItem("Quit"); // create a menu item called "New"
       GameMenu.add(quitItem); // add to our menu
       
       final int SHORTCUT_MASK = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx(); // to save typing
       newItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, SHORTCUT_MASK));
       
       //listen for menu selections
       newItem.addActionListener(this);
       quitItem.addActionListener(this);
       
       board = new JPanel();
       board.setLayout(new GridLayout(3, 3)); // 3 x 3 tic tac toe grid
       buttons = new JButton[3][3];
       //initialize the 3x3 buttons grid and add buttons to the board.
       for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j] = new JButton();
                buttons[i][j].addActionListener(this); //listen for button clicks
                board.add(buttons[i][j]);
            }
        }
       
       /* Place the board at the center. */
       contentPane.add(board, BorderLayout.CENTER); // north side
       
       /* place the status string at the bottom*/
       status = new JTextArea();
       status.setFont(new Font(null, Font.BOLD, 16));
       contentPane.add(status, BorderLayout.SOUTH);
       status.setEditable(false);
       
       /*place the tracker string at the top*/
       tracker = new JTextField();
       contentPane.add(tracker, BorderLayout.NORTH);
       tracker.setHorizontalAlignment(JTextField.CENTER);
       tracker.setEditable(false);
       
       /*set game trackers to zero*/
       xWins = 0;
       oWins = 0;
       numTies = 0;
       
       // finish setting up the frame
       frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); // exit when we hit the "X"
       frame.setResizable(false); // can not resize the window
       frame.setVisible(true);
       frame.setBounds(200,200,450,600);
       clearBoard(); // clears the board to start a new game
   }

   /**
    * Sets everything up for a new game.  Marks all buttons in the Tic Tac Toe board as empty,
    * and indicates no winner yet, 9 free squares and the current player is player X.
    */
   private void clearBoard()
   {
      winner = EMPTY;
      numFreeSquares = 9;
      player = PLAYER_X;     // Player X always has the first turn.
      
      for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                //clear the board and enable all buttons
                buttons[i][j].setText(EMPTY);
                buttons[i][j].setEnabled(true);
                buttons[i][j].setIcon(backgroundIcon);
            }
      }
      printStatus(); 
      printTracker();
   }

   /**
    * Returns true if clicking the given button gives us a winner, and false
    * otherwise. (this method is reused from lab 10, but I added the getText() method call for comparisons)
    *
    * @param int row of square just set
    * @param int col of square just set
    * 
    * @return true if we have a winner, false otherwise
    */
   private boolean haveWinner(int row, int col) 
   {
       // unless at least 5 squares have been filled, we don't need to go any further
       // (the earliest we can have a winner is after player X's 3rd move).

       if (numFreeSquares>4) return false;

       // Note: We don't need to check all rows, columns, and diagonals, only those
       // that contain the latest filled square.  We know that we have a winner 
       // if all 3 squares are the same, as they can't all be blank (as the latest
       // filled square is one of them).

       // check row "row"
       if ( buttons[row][0].getText().equals(buttons[row][1].getText()) &&
            buttons[row][0].getText().equals(buttons[row][2].getText()) ) return true;
       
       // check column "col"
       if ( buttons[0][col].getText().equals(buttons[1][col].getText()) &&
            buttons[0][col].getText().equals(buttons[2][col].getText()) ) return true;

       // if row=col check one diagonal
       if (row==col)
          if ( buttons[0][0].getText().equals(buttons[1][1].getText()) &&
               buttons[0][0].getText().equals(buttons[2][2].getText()) ) return true;

       // if row=2-col check other diagonal
       if (row==2-col)
          if ( buttons[0][2].getText().equals(buttons[1][1].getText()) &&
               buttons[0][2].getText().equals(buttons[2][0].getText()) ) return true;

       // no winner yet
       return false;
   }

   /**
   * Sets the text for the tracker JPanel. This should
   * display a counter of the number of games X has won, the number of games Y has won, 
   * as well as the number of tied games
   */
   public void printTracker() 
   {
       tracker.setFont(new Font(null, Font.BOLD, 16));
       tracker.setText("Player X: " +xWins+ "      Player O: " +oWins+ "      Games Tied: " + numTies);
   }
   
   /**
    * Sets the text for the status JPanel of the game using toString().
    */
    public void printStatus() 
    {
        status.setText(toString());
    }
    
   /**
   * Returns a string representing the current state of the game.  This should give
   * a message if the game is over that says who won (or indicates a tie).
   * (This method was reused from Lab 10, and the original code to create
   *  a string board representation was ommitted)
   * 
   * @return String representing the tic tac toe game state
   */
   public String toString() 
   {
       String s = "";
       if(winner == TIE){
           s= "Game Over: Its a tie!";
       }
       else if(winner != EMPTY){
           s= "Game Over: Player " + winner + " is the winner";
       }
       else{
           s= "Game in Progress: Player " + player + "'s turn";
       }
       return s;
   }

   /**
   * Plays the appropriate sound when a button is clicked or when a game is won or tied.
   * 
   */
   public void playSounds() 
   {
       if(winner == TIE){
          //play tie sound
          URL urlTie = TicTacToe.class.getResource("tieGame.wav"); // tie sound
          tieGame = Applet.newAudioClip(urlTie);
          tieGame.play();
       }
       else if(winner != EMPTY){
          //play win audio
          URL urlWin = TicTacToe.class.getResource("winGame.wav"); // win sound
          winGame = Applet.newAudioClip(urlWin);
          winGame.play(); 
       }
       else{
          //play click audio
          URL urlClick = TicTacToe.class.getResource("click.wav"); // click sound
          click = Applet.newAudioClip(urlClick);
          click.play(); 
       }
   }
    
   public void actionPerformed(ActionEvent e)
   {
        Object o = e.getSource(); // get the action 
        
        // see if it's a JButton
        if (o instanceof JButton) {
            JButton button = (JButton)o;
            button.setText(player); //set the button text to X or O
            button.setEnabled(false); //disable the button
            
            //change the icon to represent X or O
            if(player==PLAYER_X){
                //set the button icon after disabling to prevent greyscale image.
                button.setDisabledIcon(xIcon);
                button.setIcon(xIcon);
            }
            else{
                //set the button icon after disabling to prevent greyscale image.
                button.setDisabledIcon(oIcon);
                button.setIcon(oIcon);
            }
            
            numFreeSquares--; // decrement the number of free squares;
            
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (button.equals(buttons[i][j])){
                        //check if the game is over
                        if (haveWinner(i,j)){ 
                            winner = player; // must be the player who just went
                            //increment game tracker
                            if (player==PLAYER_X) 
                                xWins+=1;           
                            else 
                                oWins+=1;
                        }
                        else if (numFreeSquares==0){
                            winner = TIE; // board is full so it's a tie 
                            //increment game tracker
                            numTies += 1;
                        }
                        else{
                            // change to other player (this won't do anything if game has ended)
                            if (player==PLAYER_X) 
                                player=PLAYER_O;
                            else 
                                player=PLAYER_X;
                        }
                    }
                    
                }
            }
            
            if(winner!= EMPTY){ //disable all buttons once game is over
                for (int a = 0; a < 3; a++) {
                    for (int b = 0; b < 3; b++) {
                        buttons[a][b].setEnabled(false); 
                    }
                }
            }
            
            //play appropriate sound (click/win/tie)
            playSounds();
            //print the status of the game and the game tracker
            printStatus();
            printTracker();
        }
        else { // it's a JMenuItem
            JMenuItem item = (JMenuItem)o;
                
            if (item == newItem) { // start a new game
                clearBoard();
            } else if (item == quitItem) { // quit
                System.exit(0);
            }
        }        
   }    
}

