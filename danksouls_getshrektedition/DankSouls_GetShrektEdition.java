/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package danksouls_getshrektedition;

/**
 *
 * @author pantsu
 */
import java.net.DatagramSocket;
import javax.swing.JFrame;

public class DankSouls_GetShrektEdition extends JFrame {

    public DankSouls_GetShrektEdition(String player, int port, DatagramSocket dts) {
        initUI(player, port, dts);
    }
    
    private void initUI(String player, int port, DatagramSocket dts) {
        
        add(new Board(player, port, dts));
        
        setSize(900, 300);
        setResizable(false);
        
        setTitle("DaNk SOuLs");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

}
