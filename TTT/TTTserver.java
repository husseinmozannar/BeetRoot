package TTT;
 
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.sql;
 
 
public class TTTserver extends Thread{
    private ServerSocket listener = null;
    private int PORT=8901;
    public TTTserver(int port) {
        try {
            PORT=port;
            listener = new ServerSocket(PORT);
        } catch (IOException ex) {
            Logger.getLogger(TTTserver.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Tic Tac Toe Server is Running");
    }
 
    @Override
    public void run() {
        try {
            while (true) {
                Game game = new Game();
                Game.Player playerX = game.new Player(listener.accept(), 'X');
                Game.Player playerO = game.new Player(listener.accept(), 'O');
                playerX.setOpponent(playerO);
                playerO.setOpponent(playerX);
                game.currentPlayer = playerX;
                playerX.start();
                playerO.start();
            }
        } catch (IOException ex) {
            Logger.getLogger(TTTserver.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                listener.close();
            } catch (IOException ex) {
                Logger.getLogger(TTTserver.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
 
class Game {
    // a board of 9 squares

 
    public Game() {
       sql.connect();
    }
 
    private Player[] board = {
        null, null, null,
        null, null, null,
        null, null, null};
 
    //current player
    Player currentPlayer;
 
    // winner
    public boolean hasWinner() {
        return
            (board[0] != null && board[0] == board[1] && board[0] == board[2])
          ||(board[3] != null && board[3] == board[4] && board[3] == board[5])
          ||(board[6] != null && board[6] == board[7] && board[6] == board[8])
          ||(board[0] != null && board[0] == board[3] && board[0] == board[6])
          ||(board[1] != null && board[1] == board[4] && board[1] == board[7])
          ||(board[2] != null && board[2] == board[5] && board[2] == board[8])
          ||(board[0] != null && board[0] == board[4] && board[0] == board[8])
          ||(board[2] != null && board[2] == board[4] && board[2] == board[6]);
    }
 
    // no empty squares
    public boolean boardFilledUp() {
        for (int i = 0; i < board.length; i++) {
            if (board[i] == null) {
                return false;
            }
        }
        return true;
    }
    // thread when player tries a move
    public synchronized boolean legalMove(int location, Player player) {
        if (player == currentPlayer && board[location] == null) {
            board[location] = currentPlayer;
            currentPlayer = currentPlayer.opponent;
            currentPlayer.otherPlayerMoved(location);
            return true;
        }
        return false;
    }
    class Player extends Thread {
        char mark;
        Player opponent;
        Socket socket;
        BufferedReader input;
        PrintWriter output;
        // thread handler to initialize stream fields
        public Player(Socket socket, char mark) {
            this.socket = socket;
            this.mark = mark;
            try {
                input = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
                output = new PrintWriter(socket.getOutputStream(), true);
                output.println("WELCOME " + mark);
                output.println("MESSAGE Waiting for opponent to connect");
            } catch (IOException e) {
                System.out.println("Player died: " + e);
            }
        }
        //Accepts notification of who the opponent is.
        public void setOpponent(Player opponent) {
            this.opponent = opponent;
        }
 
 
         //Handles the otherPlayerMoved message. 
        public void otherPlayerMoved(int location) {
            output.println("OPPONENT_MOVED " + location);
 
            if(hasWinner()) {
                output.println("DEFEAT");
                
                    sql.insertMatchRecord( opponent.socket.getInetAddress().toString() , String.valueOf(opponent.socket.getPort()) ,socket.getInetAddress().toString() ,
                            String.valueOf(socket.getPort()), "TTT" ,"1");
                    
           
               
            }
            else if(boardFilledUp()) {
                output.println("TIE");
                sql.insertMatchRecord( opponent.socket.getInetAddress().toString() , String.valueOf(opponent.socket.getPort()) ,socket.getInetAddress().toString() ,
                            String.valueOf(socket.getPort()), "TTT" ,"1");
            }
            else {
                output.println("");
            }
 
        }
 
        public void run() {
            try {
                // The thread is only started after everyone connects.
                output.println("MESSAGE All players connected");
 
                // Tell the first player that it is his/her turn.
                if (mark == 'X') {
                    output.println("MESSAGE Your move");
                }
 
                // Repeatedly get commands from the client and process them.
                while (true) {
                    String command = input.readLine();
                    if (command.startsWith("MOVE")) {
                        int location = Integer.parseInt(command.substring(5));
                        if (legalMove(location, this)) {
                            output.println("VALID_MOVE");
 
                            if(hasWinner()) {
                                output.println("VICTORY");
 
                            }
                            else if (boardFilledUp()) {
                                output.println("TIE");
                            }
                            else {
                                output.println("");
                            }
 
                        } else {
                            output.println("MESSAGE ?");
                        }
                    } else if (command.startsWith("QUIT")) {
                        return;
                    }
                }
            } catch (IOException e) {
                System.out.println("Player died: " + e);
            } finally {
                try {socket.close();} catch (IOException e) {}
            }
        }
    }
}