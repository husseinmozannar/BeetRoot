/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package danksouls_getshrektedition;

import danksouls_getshrektedition.World.Ground;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;


public class Board extends JPanel implements ActionListener {

    private Timer timer;
    private Character craft;
    private Enemy enemy;
    private final int DELAY =6 ;
    private final int viewX = 400;
    private final int viewY = 300;
    public static final int worldX = 900;
    public static final int worldY = 300;
    private BufferedImage world;
    int camX = 0;
    int camY = 0;
    int offSetMaxX; int offSetMaxY; int offSetMinX; int offSetMinY;
    public Ground grd;
    
    public DatagramSocket socket = null;
    public DatagramPacket packet = null;
    
    public byte[] buf;
    InetAddress addr = null;
    int port = 0;
    public String player;
    
    int round = 1;
    int score1 = 0;
    int score2 = 0;
    
    public Board(String player, int port, DatagramSocket dts) {
        initBoard(player, port, dts);
    }
    
    private void initBoard(String player, int port, DatagramSocket dts) {
        try {
            this.player = player;
            socket = dts;
            addr = InetAddress.getByName("127.0.0.1");
            this.port = port;
            
            
            setBackground(Color.BLACK);
            addKeyListener(new Controller());
            setFocusable(true);
            world = ImageIO.read(new File(Sheet.class.getResource("/resources/bg.png").toURI()));
            
            if (player.equals("player1")) {
                craft = new Character(90, 200);
                enemy = new Enemy(750, 200);
            }
            else {
                craft = new Character(750, 200);
                enemy = new Enemy(90, 200);
            }
            
            grd = new Ground(0, 270, craft);
            craft.animation.start();
            
            
            
            
            
            
            
            timer = new Timer(DELAY, this);        
            timer.start();
        } catch (IOException | URISyntaxException ex) {
            Logger.getLogger(Board.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
//        g.drawImage(world, 0, 0, null);
        doDrawing(g);
        
        Toolkit.getDefaultToolkit().sync();
    }

    private void doDrawing(Graphics g) {
        Graphics2D g2d = (Graphics2D)g;
        
        g2d.drawImage(craft.animation.getSprite(), craft.getX(), craft.getY(), null);
        g2d.drawImage(enemy.animation.getSprite(), enemy.getX(), enemy.getY(), null);
        
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("monospaced", Font.PLAIN, 18));
        FontMetrics fontMetrics = g2d.getFontMetrics();
        String r = "Round: #" + String.valueOf(round);
        g2d.drawString(r, 450 - fontMetrics.stringWidth(r)/2, 20);
        r = String.valueOf(score1) + " | " + String.valueOf(score2);
        g2d.drawString(r, 450 - fontMetrics.stringWidth(r)/2, 40);
        
        g2d.setColor(Color.WHITE);
        g2d.fillRect(grd.getX(), grd.getY(), grd.getW(), grd.getH());
        g2d.setColor(Color.RED);
        g2d.fillRect(craft.HP.x, craft.HP.y, craft.HP.width, craft.HP.height);
        g2d.fillRect(enemy.x - 10, enemy.y - 15, enemy.HP.width, enemy.HP.height);
        g2d.setColor(Color.GREEN);
        g2d.fillRect(craft.SP.x, craft.SP.y, craft.SP.width, craft.SP.height);
        g2d.fillRect(enemy.x - 10, enemy.y - 10, enemy.SP.width, enemy.SP.height);
    }
    
    private void sendInfo() {
        buf = new byte[256];
        String payload = "DankSouls_GetShrektEdition" + " " + String.valueOf(craft.x) + " " + String.valueOf(craft.y) + " "
                + String.valueOf(craft.HP.value) + " " + String.valueOf(craft.SP.value)
                + " " + craft.getAnimation();
        
        if(player.equals("player1")) {
            payload += " player1 asdf";
        }
        else {
            payload += " player2 asdf";
        }
        
        buf = payload.getBytes();
        packet = new DatagramPacket(buf, buf.length, addr, port);
        try {
            socket.send(packet);
        } catch (IOException ex) {
            Logger.getLogger(Board.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(craft.HP.value <= 0.0) {
            craft.HP.value = 100.0;
        }
    }
    
    private void rcvInfo() {
        buf = new byte[256];
        packet = new DatagramPacket(buf, buf.length);
        try {
            socket.receive(packet);
        } catch (IOException ex) {
            Logger.getLogger(Board.class.getName()).log(Level.SEVERE, null, ex);
        }
        String payload = new String(packet.getData(), 0, packet.getLength());
        String[] arr = payload.split(" ");
        
        if (arr[0].equals("WIN") || arr[0].equals("LOST")) {
            System.out.println(round+ " "+  score1 + " " + score2);
            if(arr[0].equals("WIN")) {
                round += 1;
                resetBoard();
                if(player.equals("player1")) {
                    score1 += 1;
                }
                else {
                    score2 += 1;
                }
            }
            else if(arr[0].equals("LOST")) {
                round += 1;
                resetBoard();
                if(player.equals("player1")) {
                    score2 += 1;
                }
                else {
                    score1 += 1;
                }
            }
            System.out.println(round+ " "+  score1 + " " + score2);
        }
        
        else if (arr[0].equals("DankSouls_GetShrektEdition")) {
            enemy.x = Integer.parseInt(arr[1]);
            enemy.y = Integer.parseInt(arr[2]);
            enemy.HP.value = Double.parseDouble(arr[3]);
            enemy.HP.updateValue();
            enemy.SP.value = Double.parseDouble(arr[4]);
            enemy.SP.updateValue();
            enemy.setAnimation(arr[5]);
        }
    }
    
    public void updateHP() {
        if(enemy.canHit && craft.isColliding(enemy) && enemy.getAnimation().equals("attackRight") && enemy.x < craft.x && !craft.getAnimation().equals("guardLeft")
                || enemy.canHit && craft.isColliding(enemy) && enemy.getAnimation().equals("attackLeft") && enemy.x > craft.x && !craft.getAnimation().equals("guardRight")) {
            craft.HP.reduce(20.0);
            craft.HP.updateValue();
            enemy.canHit = false;
        }
    }
    
    public void checkWinLose() {
        
    }
    
    public void resetBoard() {
        if (player.equals("player1")) {
                craft.x = 90;
                craft.y = 200;
            craft.HP.x = craft.x - 10;
            craft.HP.y = craft.y - 15;
            
            craft.SP.x = craft.x - 10;
            craft.SP.y = craft.y - 10;
 
                enemy.x = 750;
                enemy.y = 200;
                enemy.HP.x = enemy.x - 10;
            enemy.HP.y = enemy.y - 15;
            
            enemy.SP.x = enemy.x - 10;
            enemy.SP.y = enemy.y - 10;
            
            }
            else {
                craft.x = 750;
                craft.y = 200;
                craft.HP.x = craft.x - 10;
            craft.HP.y = craft.y - 15;
            
            craft.SP.x = craft.x - 10;
            craft.SP.y = craft.y - 10;
                
                enemy.x = 90;
                enemy.y = 200;
                enemy.HP.x = enemy.x - 10;
            enemy.HP.y = enemy.y - 15;
            
            enemy.SP.x = enemy.x - 10;
            enemy.SP.y = enemy.y - 10;
            }
        
        craft.resetAnim();
        
        craft.HP.value = craft.SP.value = 100.0;
        enemy.HP.value = enemy.SP.value = 100.0;
        enemy.HP.updateValue();
        enemy.SP.updateValue();
        craft.HP.updateValue();
        craft.SP.updateValue();
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if(craft.animation.isAttack == false) {
            craft.animation.update();
        }
        else {
            craft.animation.play();
        }
        
        enemy.animation.update();
        
        craft.keyPressed();
        craft.moveUpdt();
        craft.move();
        grd.checkCollisions();
        
        updateHP();
        
        sendInfo();
        rcvInfo();
          
        if(craft.getX() > worldX - 50) {
            craft.x = worldX - 50;
            
            craft.HP.x = craft.x - 10;
            craft.HP.y = craft.y - 15;
            
            craft.SP.x = craft.x - 10;
            craft.SP.y = craft.y - 10;
        }
        if(craft.getX() < -10) {
            craft.x = -10;
            
            craft.HP.x = craft.x - 10;
            craft.HP.y = craft.y - 15;
            
            craft.SP.x = craft.x - 10;
            craft.SP.y = craft.y - 10;
        }
        
        repaint();  
    }

}