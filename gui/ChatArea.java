/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import client.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Hussein
 */
public class ChatArea extends javax.swing.JFrame {

    /**
     * Creates new form ChatArea
     */
    private chatclient cc = null;
    private String user;
    public ArrayList<String> recipients = new ArrayList<String>();
    private boolean isConnected = false;
    public String type = "";
    
    public ChatArea(String user, ArrayList<String> recipients, String type) {
        this.user = user;
        this.recipients = new ArrayList<>(recipients);
        isConnected = false;
        this.type = type;
        
        initComponents();
        Run();
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Metal".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ChatArea.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ChatArea.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ChatArea.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ChatArea.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane3 = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        textarea = new javax.swing.JTextArea();
        sendField = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        label_chatTitl = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(new java.awt.CardLayout());

        jScrollPane3.setMaximumSize(new java.awt.Dimension(402, 302));

        jPanel1.setBackground(new java.awt.Color(66, 78, 106));
        jPanel1.setMaximumSize(new java.awt.Dimension(630, 470));
        jPanel1.setMinimumSize(new java.awt.Dimension(630, 470));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        textarea.setBackground(new java.awt.Color(204, 204, 204));
        textarea.setColumns(20);
        textarea.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        textarea.setRows(5);
        textarea.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jScrollPane1.setViewportView(textarea);

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 30, 410, 390));

        sendField.setBackground(new java.awt.Color(104, 121, 161));
        sendField.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        sendField.setText("Enter Text");
        sendField.setMinimumSize(new java.awt.Dimension(50, 20));
        sendField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sendFieldActionPerformed(evt);
            }
        });
        jPanel1.add(sendField, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 420, 410, 50));

        jButton1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jButton1.setForeground(new java.awt.Color(204, 204, 204));
        jButton1.setText("Clear Conversation");
        jButton1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton1.setContentAreaFilled(false);
        jPanel1.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 0, 160, 30));

        jScrollPane2.setBackground(new java.awt.Color(66, 78, 106));
        jScrollPane2.getViewport().setBackground(new java.awt.Color(68, 78, 106));

        jTable1.setBackground(new java.awt.Color(66, 78, 106));
        jTable1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jTable1.setForeground(new java.awt.Color(204, 204, 204));
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Chat area members"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane2.setViewportView(jTable1);

        jPanel1.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 0, 220, 470));

        label_chatTitl.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        label_chatTitl.setForeground(new java.awt.Color(204, 204, 204));
        jPanel1.add(label_chatTitl, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 4, 190, 20));

        jScrollPane3.setViewportView(jPanel1);

        getContentPane().add(jScrollPane3, "card3");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void Run() {
        cc = new chatclient("localhost", 1555, this, this.user, recipients, type); //create the chatclient and link it to this textarea
        cc.start();
        isConnected = true;
        
        updateTable();
        if (type.equals("chat")) {
            getHistory();
            setRead();
        }
    }
    
    private void getHistory() {
        if (recipients.size() == 2) {
            Message get = new Message();
            get.command = "retreiveMessagesFromUser";
            get.sender = user;
            get.recipients = new ArrayList<String>(recipients);
            cc.sendMessage(get);
            
        }
        
    }

    public void updateTable() {
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setRowCount(0);
        
        label_chatTitl.setText("Chat");
        for (int i = 0; i < recipients.size(); i++) {
            
            model.addRow(new Object[]{recipients.get(i)});
            
        }
    }

    private void setRead() {
        if (recipients.size() == 2) {
            Message get = new Message();
            get.command = "setRead";
            get.sender = user;
            get.recipients = new ArrayList<String>(recipients);
            cc.sendMessage(get);
            
        }
        
    }
    private void sendFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sendFieldActionPerformed
        // TODO add your handling code here:
        String content = sendField.getText();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd::HH:mm:ss");
        sdf.format(System.currentTimeMillis());
        String date = sdf.format(new Date());
        Message msg = new Message(user, content, date, recipients);
        if (type.equals("chat")) {
            msg.type = "personal"; //FIX THIS TO SUPPORT CHAT ROOM TYPE , THEN DONT SAVE MESSAGES
        }
        
        else if (type.equals("connectTTT")) {
            msg.command = "chatTTT";
            msg.type = "chatTTT";
        }
        
        else if (type.equals("connectDANK")) {
            msg.command = "chatDANK";
            msg.type = "chatDANK";
            
        }
        else if (type.equals("group")) {
            msg.command = "chat";
            msg.type = "group";
        }
        cc.sendMessage(msg);
        System.out.println(msg.date + msg.content);
    }//GEN-LAST:event_sendFieldActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
        setRead();
        JFrame frame = (JFrame) evt.getSource();
        int result = JOptionPane.showConfirmDialog(
                frame,
                "Are you sure you want to exit the application?",
                "Exit Application",
                JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            try {                
                
                if (type.equals("chat")) {
                    mainpage.openChatClients.remove(recipients.get(1));
                }
            } catch (IndexOutOfBoundsException e) {
                
            }
            cc.terminate();
            //frame.dispose();
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            
        }
        

    }//GEN-LAST:event_formWindowClosing

    /**
     * @param args the command line arguments
     */
//    public static void main(String args[]) {
//        /* Set the Nimbus look and feel */
//        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
//        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
//         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
//         */
//        try {
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
//        } catch (ClassNotFoundException ex) {
//            java.util.logging.Logger.getLogger(ChatArea.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (InstantiationException ex) {
//            java.util.logging.Logger.getLogger(ChatArea.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (IllegalAccessException ex) {
//            java.util.logging.Logger.getLogger(ChatArea.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
//            java.util.logging.Logger.getLogger(ChatArea.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        }
//        //</editor-fold>
//
//        /* Create and display the form */
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                new ChatArea(null).setVisible(true);
//            }
//        });
//    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel label_chatTitl;
    private javax.swing.JTextField sendField;
    public javax.swing.JTextArea textarea;
    // End of variables declaration//GEN-END:variables
}
