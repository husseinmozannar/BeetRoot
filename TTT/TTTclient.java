package TTT;

import client.client;
import client.request;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class TTTclient {

    private JFrame frame = new JFrame("Tic Tac Toe");
    private JLabel messageLabel = new JLabel("");
    private ImageIcon icon;
    private ImageIcon opponentIcon;

    private Square[] board = new Square[9];
    private Square currentSquare;

    private  int PORT = 8901;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    // Constructs the client by connecting to a server, laying out the GUI and registering GUI listeners.
    public TTTclient(String serverAddress) throws Exception {

        // Setup networking
//        socket = new Socket(serverAddress, PORT);
//        in = new BufferedReader(new InputStreamReader(
//            socket.getInputStream()));
//        out = new PrintWriter(socket.getOutputStream(), true);
//
//        // Layout GUI
//        messageLabel.setBackground(Color.lightGray);
//        frame.getContentPane().add(messageLabel, "South");
//
//        JPanel boardPanel = new JPanel();
//        boardPanel.setBackground(Color.black);
//        boardPanel.setLayout(new GridLayout(3, 3, 2, 2));
//        for (int i = 0; i < board.length; i++) {
//            final int j = i;
//            board[i] = new Square();
//            board[i].addMouseListener(new MouseAdapter() {
//                public void mousePressed(MouseEvent e) {
//                    currentSquare = board[j];
//                    out.println("MOVE " + j);}});
//            boardPanel.add(board[i]);
//        }
//        frame.getContentPane().add(boardPanel, "Center");
//        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    }

    //* The main thread of the client will listen for messages from the server.  
    //The first message will be a "WELCOME" message in which we receive our mark.  
    //Then we go into a loop listening for: 
    //--> "VALID_MOVE", --> "OPPONENT_MOVED", --> "VICTORY", --> "DEFEAT", --> "TIE", --> "OPPONENT_QUIT, --> "MESSAGE" messages, and handling each message appropriately.
    //The "VICTORY","DEFEAT" and "TIE" ask the user whether or not to play another game. 
    //If the answer is no, the loop is exited and the server is sent a "QUIT" message.  If an OPPONENT_QUIT message is recevied then the loop will exit and the server will be sent a "QUIT" message also.
    public void play() throws Exception {
        String response;
        try {
            response = in.readLine();
            if (response.startsWith("WELCOME")) {
                char mark = response.charAt(8);
                URL url = getClass().getResource("/Resources/X.png");
                URL url2 = getClass().getResource("/Resources/O.png");
                icon = new ImageIcon(mark == 'X' ? url : url2);
                opponentIcon = new ImageIcon(mark == 'X' ? url2 : url);
                frame.setTitle("Tic Tac Toe - Player " + mark);
            }
            while (true) {
                response = in.readLine();
                if (response.startsWith("VALID_MOVE")) {
                    messageLabel.setText("Valid move, please wait");
                    currentSquare.setIcon(icon);
                    currentSquare.repaint();
                }
                else if (response.startsWith("OPPONENT_MOVED")) {
                    int loc = Integer.parseInt(response.substring(15));
                    board[loc].setIcon(opponentIcon);
                    board[loc].repaint();
                    messageLabel.setText("Opponent moved, your turn");
                }
                else if (response.startsWith("VICTORY")) {
                    messageLabel.setText("You win");
                    break;
                }
                else if (response.startsWith("DEFEAT")) {
                    messageLabel.setText("You lose");
                    break;
                }
                else if (response.startsWith("TIE")) {
                    messageLabel.setText("You tied");
                    break;
                }
                else if (response.startsWith("MESSAGE")) {
                    messageLabel.setText(response.substring(8));
                }
            }
            out.println("QUIT");
        } finally {
            socket.close();
        }
    }

    private boolean wantsToPlayAgain() {
        int response = JOptionPane.showConfirmDialog(frame,
                "Want to play again?",
                "Tic Tac Toe is Fun Fun Fun",
                JOptionPane.YES_NO_OPTION);
        frame.dispose();
        return response == JOptionPane.YES_OPTION;
    }

    //Graphical square in the client window.  
    static class Square extends JPanel {

        JLabel label = new JLabel((Icon) null);

        public Square() {
            setBackground(Color.white);
            add(label);
        }

        public void setIcon(Icon icon) {
            label.setIcon(icon);
        }
    }

    //main
    public void Run(String serverAddress,int port,String user) throws Exception {

        while (true) {
            frame = new JFrame("Tic Tac Toe");
            PORT=port;
            socket = new Socket(serverAddress, PORT);
            in = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // Layout GUI
            messageLabel.setBackground(Color.lightGray);
            frame.getContentPane().add(messageLabel, "South");

            JPanel boardPanel = new JPanel();
            boardPanel.setBackground(Color.black);
            boardPanel.setLayout(new GridLayout(3, 3, 2, 2));
            for (int i = 0; i < board.length; i++) {
                final int j = i;
                board[i] = new Square();
                board[i].addMouseListener(new MouseAdapter() {
                    public void mousePressed(MouseEvent e) {
                        currentSquare = board[j];
                        out.println("MOVE " + j);
                    }
                });
                boardPanel.add(board[i]);
            }
            frame.getContentPane().add(boardPanel, "Center");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
            frame.setSize(350, 350);
            frame.setVisible(true);
            frame.setResizable(false);
            client temp =new client("192.168.1.73", 1555);
            temp.start(); request r =new request(); 
            r.command="createSession";r.username=user; r.number=socket.getInetAddress().toString();
            r.dof=String.valueOf(socket.getLocalPort());
            temp.sendRequest(r);
            
          
            
            
            play();
            if (!this.wantsToPlayAgain()) {
                r =new request(); 
            r.command="removeSession";r.username=user; r.number=socket.getInetAddress().toString();
            r.dof=String.valueOf(socket.getLocalPort());
            temp.sendRequest(r);
            temp.terminate();
                break;
            }
        }
    }
}
