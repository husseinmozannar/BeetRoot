/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
// colors : 58 ^3 for grey, grey blue: 33 39 53 , drak grey: 18 19 23
package gui;

import TTT.TTTclient;
import TTT.TTTserver;
import client.*;
import danksouls_getshrektedition.start;
import java.awt.Color;
import java.awt.Dimension;
import static java.awt.SystemColor.menu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.Timer;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Hussein
 */
public class mainpage extends javax.swing.JFrame {

    /**
     * Creates new form mainpage
     */
    public mainpage() {
        initComponents();

        // number of friendrequests
        ActionListener timerRefresh = new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                System.out.println("every 1s");

                request ok = new request();
                ok.command = "getFriendRequests";
                ok.username = user;
                ok = clt.sendRequest(ok);

                if (ok.friends.size() > requestsOld) {
                    dialogFriend();
                    setRequestsOld(ok.friends.size());
                }
                else {
                    setRequestsOld(ok.friends.size());
                }

                ok.command = "retrieveMessages";
                ok.username = user;
                ok = clt.sendRequest(ok);

                if (ok.friends.size() > messagesOld) {
                    dialogMessages(ok.friends);
                    setMessagesOld(ok.friends.size());
                }
                else {
                    setMessagesOld(ok.friends.size());
                }

                ok = new request();
                ok.username = user;
                ok.command = "getInvites";
                ok = clt.sendRequest(ok);
                dialogInvites(ok.friends);

            }
        };

        timer = new Timer(1000, timerRefresh);
        timer.setInitialDelay(5000);
        timer.start();

    }
    static public ArrayList<String> openChatClients = new ArrayList<String>();
    private int requestsOld = 0;
    private int messagesOld = 0;
    Timer timer = null;

    private void setMessagesOld(int a) {
        messagesOld = a;
    }

    private void setRequestsOld(int a) {
        requestsOld = a;

    }

    private void dialogInvites(ArrayList<String> a) {

        for (int i = 0; i < a.size(); i++) {
            String arr[] = a.get(i).split(" ");

            Object[] options = {"Accept Invite",
                "Ignore"};

            int n = JOptionPane.showOptionDialog(this, "You have received a game invite from " + arr[0] + " to play " + arr[1], "Game Invite", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);     //do not use a custom Iconoptions,  //the titles of buttons
            //default button title
            if (n == JOptionPane.YES_OPTION) {
                request ok = new request();
                ok.command = "removeInvite";
                ok.username = arr[0];
                ok.user2 = user;
                ok.number = arr[1];
                ok.dof = arr[2];
                clt.sendRequest(ok);
                // start game and chat
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            ArrayList<String> recipients = new ArrayList<>();
                            recipients.add(user);
                            recipients.add(arr[0]);
                            openChatClients.add(user);
                            ChatArea ca = new ChatArea(user, recipients, "group"); // new chat area oppened
                            ca.setVisible(true);
                            if (arr[1].equals("TTT")) {
                                TTTclient tttc = new TTTclient(ipAdd);
                                tttc.Run(ipAdd, Integer.valueOf(arr[2]),user);
                            }
                            else if (arr[1].equals("DANK")) {
                                start play = new start(Integer.valueOf(arr[2]));
                                play.Run(play);

                            }

                        } catch (Exception ex) {
                            Logger.getLogger(mainpage.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }.start();// MODIFY HEREEEEEEEEEEEEEEEEEEE

            }
            else {
                // dont start game but still invite
                request ok = new request();
                ok.command = "removeInvite";
                ok.username = arr[0];
                ok.user2 = user;
                ok.number = arr[1];
                clt.sendRequest(ok);

            }

        }

    }

    private void dialogFriend() {

        JOptionPane optionPane = new JOptionPane("You have a new Friend Request!", JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);

        JDialog dialog = new JDialog();
        dialog.setTitle("Message");
        dialog.setModal(true);

        dialog.setContentPane(optionPane);

        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialog.pack();

//create timer to dispose of dialog after 1 seconds
        Timer timer2 = new Timer(1000, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                dialog.dispose();
            }
        });
        timer2.setRepeats(false);//the timer should only go off once

//start timer to close JDialog as dialog modal we must start the timer before its visible
        timer2.start();

        dialog.setVisible(true);

//        
//        JOptionPane.showMessageDialog(this,
//                "You have a new Friend Request!");
    }

    private void dialogMessages(ArrayList<String> r) {
        String from = "";
        for (int i = 0; i < r.size(); i++) {
            if (!r.get(i).equals(user) && !from.contains(r.get(i)) && !openChatClients.contains(r.get(i))) {
                from += r.get(i) + ", ";
            }
        }
        if (!from.equals("")) {

            JOptionPane optionPane = new JOptionPane("You have new Messages from: \n" + from, JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);

            JDialog dialog = new JDialog();
            dialog.setTitle("Message");
            dialog.setModal(true);

            dialog.setContentPane(optionPane);

            dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
            dialog.pack();

//create timer to dispose of dialog after 0.5 seconds
            Timer timer2 = new Timer(1000, new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    dialog.dispose();
                }
            });
            timer2.setRepeats(false);//the timer should only go off once
//start timer to close JDialog as dialog modal we must start the timer before its visible
            timer2.start();

            dialog.setVisible(true);

//            JOptionPane.showMessageDialog(this,
//                    "You have new Messages from: \n" + from);
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

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        chatarea = new javax.swing.JTextArea();
        txt_chatsend = new javax.swing.JTextField();
        txt_sendto = new javax.swing.JTextField();
        popmenu_community = new javax.swing.JPopupMenu();
        popmenu_viewFriends = new javax.swing.JMenuItem();
        popmenu_searchFriends = new javax.swing.JMenuItem();
        popmenu_user = new javax.swing.JPopupMenu();
        popitem_sendrequest = new javax.swing.JMenuItem();
        popitem_viewprofile = new javax.swing.JMenuItem();
        popmenu_friends = new javax.swing.JPopupMenu();
        menuitem_chat = new javax.swing.JMenuItem();
        menuitem_removeFriend = new javax.swing.JMenuItem();
        menuitem_friendViewProfile = new javax.swing.JMenuItem();
        popmenu_Profile = new javax.swing.JPopupMenu();
        menuitem_viewProfile = new javax.swing.JMenuItem();
        menuitem_editProfile = new javax.swing.JMenuItem();
        scrollpane_viewFriends4 = new javax.swing.JScrollPane();
        table_friends4 = new javax.swing.JTable();
        popmenu_inviteGame = new javax.swing.JPopupMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jScrollPane1 = new javax.swing.JScrollPane();
        jSplitPane1 = new javax.swing.JSplitPane();
        jInternalFrame1 = new javax.swing.JInternalFrame();
        panel_games = new javax.swing.JPanel();
        jButton8 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jScrollPane7 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        panel_community = new javax.swing.JPanel();
        jSplitPane2 = new javax.swing.JSplitPane();
        jPanel3 = new javax.swing.JPanel();
        btn_searchusers = new javax.swing.JButton();
        txt_search = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        scrollpane_viewFriends1 = new javax.swing.JScrollPane();
        table_friends1 = new javax.swing.JTable();
        scrollpane_viewFriends = new javax.swing.JScrollPane();
        table_friends = new javax.swing.JTable();
        panel_friendrequests = new javax.swing.JPanel();
        scrollpane_viewFriends2 = new javax.swing.JScrollPane();
        table_friends2 = new javax.swing.JTable();
        panel_editProfile = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        edit_bio = new javax.swing.JTextArea();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        edit_firstName = new javax.swing.JTextField();
        edit_lastName = new javax.swing.JTextField();
        edit_country = new javax.swing.JTextField();
        edit_number = new javax.swing.JTextField();
        jSeparator4 = new javax.swing.JSeparator();
        jSeparator5 = new javax.swing.JSeparator();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        pref_avatar = new javax.swing.JCheckBox();
        pref_lastName = new javax.swing.JCheckBox();
        pref_country = new javax.swing.JCheckBox();
        pref_number = new javax.swing.JCheckBox();
        jLabel15 = new javax.swing.JLabel();
        pref_firstName = new javax.swing.JCheckBox();
        pref_bio = new javax.swing.JCheckBox();
        btn_submitChanges = new javax.swing.JButton();
        btn_goChangePassword = new javax.swing.JButton();
        panel_viewprofile = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        view_username = new javax.swing.JLabel();
        view_firstName = new javax.swing.JLabel();
        view_nationality = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jScrollPane4 = new javax.swing.JScrollPane();
        view_bio = new javax.swing.JTextArea();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        view_lastName = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();
        jSeparator1 = new javax.swing.JSeparator();
        jInternalFrame2 = new javax.swing.JInternalFrame();
        jPanel4 = new javax.swing.JPanel();
        view_dof = new javax.swing.JLabel();
        view_email = new javax.swing.JLabel();
        view_number = new javax.swing.JLabel();
        view_gender = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        view_matchesplayed = new javax.swing.JLabel();
        view_wins = new javax.swing.JLabel();
        view_ratio = new javax.swing.JLabel();
        view_losses = new javax.swing.JLabel();
        btn_editProfile = new javax.swing.JButton();
        scrollpane_viewFriends3 = new javax.swing.JScrollPane();
        table_friends3 = new javax.swing.JTable();
        panel_changePassword = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jSeparator6 = new javax.swing.JSeparator();
        jSeparator7 = new javax.swing.JSeparator();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        btn_changePasswordAction = new javax.swing.JButton();
        jLabel26 = new javax.swing.JLabel();
        edit_oldPassword = new javax.swing.JPasswordField();
        edit_newPassword = new javax.swing.JPasswordField();
        edit_confirmnewPassword = new javax.swing.JPasswordField();
        jButton7 = new javax.swing.JButton();
        label_changePassWarning = new javax.swing.JLabel();
        panel_modeSelection = new javax.swing.JPanel();
        jScrollPane8 = new javax.swing.JScrollPane();
        jTextArea3 = new javax.swing.JTextArea();
        jLabel25 = new javax.swing.JLabel();
        jButton10 = new javax.swing.JButton();
        jButton11 = new javax.swing.JButton();
        jLabel27 = new javax.swing.JLabel();
        jScrollPane9 = new javax.swing.JScrollPane();
        jTextArea4 = new javax.swing.JTextArea();
        panel_selectFriend = new javax.swing.JPanel();
        scrollpane_viewFriends5 = new javax.swing.JScrollPane();
        table_friends5 = new javax.swing.JTable();
        panel_scores = new javax.swing.JPanel();
        scrollpane_viewFriends6 = new javax.swing.JScrollPane();
        table_friends6 = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        btn_GAMES = new javax.swing.JButton();
        btn_COMMUNITY = new javax.swing.JButton();
        btn_PROFILE = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jButton6 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        menubar_changeUser = new javax.swing.JMenuItem();
        menubar_quit = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        menubar_about = new javax.swing.JMenuItem();

        jTabbedPane1.setBackground(new java.awt.Color(204, 255, 51));
        jTabbedPane1.setAutoscrolls(true);

        jButton1.setText("jButton1");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        chatarea.setColumns(20);
        chatarea.setRows(5);
        jScrollPane2.setViewportView(chatarea);

        jScrollPane3.setViewportView(jScrollPane2);

        txt_chatsend.setText("jTextField1");
        txt_chatsend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_chatsendActionPerformed(evt);
            }
        });

        txt_sendto.setText("jTextField2");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(191, 191, 191)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 315, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(66, 66, 66)
                .addComponent(txt_sendto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(299, Short.MAX_VALUE)
                .addComponent(txt_chatsend, javax.swing.GroupLayout.PREFERRED_SIZE, 234, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(220, 220, 220)
                .addComponent(jButton1)
                .addGap(575, 575, 575))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(98, 98, 98)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 243, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(307, 307, 307)
                        .addComponent(txt_sendto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(38, 38, 38)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(txt_chatsend, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(538, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("tab1", jPanel1);

        popmenu_community.setBackground(new java.awt.Color(33, 39, 53));
        popmenu_community.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N

        popmenu_viewFriends.setBackground(new java.awt.Color(33, 39, 53));
        popmenu_viewFriends.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        popmenu_viewFriends.setForeground(new java.awt.Color(204, 204, 204));
        popmenu_viewFriends.setText("View Friends");
        popmenu_viewFriends.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                popmenu_viewFriendsActionPerformed(evt);
            }
        });
        popmenu_community.add(popmenu_viewFriends);

        popmenu_searchFriends.setBackground(new java.awt.Color(33, 39, 53));
        popmenu_searchFriends.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        popmenu_searchFriends.setForeground(new java.awt.Color(204, 204, 204));
        popmenu_searchFriends.setText("Search for user");
        popmenu_searchFriends.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                popmenu_searchFriendsActionPerformed(evt);
            }
        });
        popmenu_community.add(popmenu_searchFriends);

        popmenu_user.setBackground(new java.awt.Color(33, 39, 53));

        popitem_sendrequest.setBackground(new java.awt.Color(33, 39, 53));
        popitem_sendrequest.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        popitem_sendrequest.setForeground(new java.awt.Color(204, 204, 204));
        popitem_sendrequest.setText("Send Friend Request");
        popitem_sendrequest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                popitem_sendrequestActionPerformed(evt);
            }
        });
        popmenu_user.add(popitem_sendrequest);

        popitem_viewprofile.setBackground(new java.awt.Color(33, 39, 53));
        popitem_viewprofile.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        popitem_viewprofile.setForeground(new java.awt.Color(204, 204, 204));
        popitem_viewprofile.setText("View Profile");
        popitem_viewprofile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                popitem_viewprofileActionPerformed(evt);
            }
        });
        popmenu_user.add(popitem_viewprofile);

        menuitem_chat.setBackground(new java.awt.Color(33, 39, 53));
        menuitem_chat.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        menuitem_chat.setForeground(new java.awt.Color(204, 204, 204));
        menuitem_chat.setText("Chat");
        menuitem_chat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuitem_chatActionPerformed(evt);
            }
        });
        popmenu_friends.add(menuitem_chat);

        menuitem_removeFriend.setBackground(new java.awt.Color(33, 39, 53));
        menuitem_removeFriend.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        menuitem_removeFriend.setForeground(new java.awt.Color(204, 204, 204));
        menuitem_removeFriend.setText("Remove Friend");
        menuitem_removeFriend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuitem_removeFriendActionPerformed(evt);
            }
        });
        popmenu_friends.add(menuitem_removeFriend);

        menuitem_friendViewProfile.setBackground(new java.awt.Color(33, 39, 53));
        menuitem_friendViewProfile.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        menuitem_friendViewProfile.setForeground(new java.awt.Color(204, 204, 204));
        menuitem_friendViewProfile.setText("View Profile");
        menuitem_friendViewProfile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuitem_friendViewProfileActionPerformed(evt);
            }
        });
        popmenu_friends.add(menuitem_friendViewProfile);

        menuitem_viewProfile.setBackground(new java.awt.Color(33, 39, 53));
        menuitem_viewProfile.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        menuitem_viewProfile.setForeground(new java.awt.Color(204, 204, 204));
        menuitem_viewProfile.setText("View Profile");
        menuitem_viewProfile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuitem_viewProfileActionPerformed(evt);
            }
        });
        popmenu_Profile.add(menuitem_viewProfile);

        menuitem_editProfile.setBackground(new java.awt.Color(33, 39, 53));
        menuitem_editProfile.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        menuitem_editProfile.setForeground(new java.awt.Color(204, 204, 204));
        menuitem_editProfile.setText("Edit Profile");
        menuitem_editProfile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuitem_editProfileActionPerformed(evt);
            }
        });
        popmenu_Profile.add(menuitem_editProfile);

        scrollpane_viewFriends4.setBackground(new java.awt.Color(33, 39, 53));
        scrollpane_viewFriends4.setBorder(null);

        table_friends4.setBackground(new java.awt.Color(33, 39, 53));
        table_friends4.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, new java.awt.Color(33, 39, 53), new java.awt.Color(33, 39, 53), new java.awt.Color(33, 39, 53), new java.awt.Color(33, 39, 53)));
        table_friends4.setFont(new java.awt.Font("Cambria Math", 1, 24)); // NOI18N
        table_friends4.setForeground(new java.awt.Color(204, 204, 204));
        table_friends4.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Friends List"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        table_friends4.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        table_friends4.setAutoscrolls(false);
        table_friends4.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        table_friends4.setGridColor(new java.awt.Color(0, 0, 0));
        table_friends4.setMinimumSize(new java.awt.Dimension(200, 50));
        table_friends4.setPreferredSize(new java.awt.Dimension(200, 50));
        table_friends4.setRowHeight(50);
        table_friends4.setSelectionBackground(new java.awt.Color(204, 204, 204));
        table_friends4.setSelectionForeground(new java.awt.Color(33, 39, 53));
        table_friends4.setShowHorizontalLines(false);
        table_friends4.getTableHeader().setResizingAllowed(false);
        table_friends4.getTableHeader().setReorderingAllowed(false);
        table_friends4.getTableHeader().setBackground(new java.awt.Color(33,39,53));
        table_friends4.getTableHeader().setForeground(new java.awt.Color(107,92,172));
        table_friends4.getTableHeader().setFont(new java.awt.Font("Arial", 1, 22));
        table_friends4.getTableHeader().setPreferredSize(new Dimension(400,40));
        table_friends4.getTableHeader().setBorder(null);
        scrollpane_viewFriends4.setViewportView(table_friends4);
        table_friends4.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);

        scrollpane_viewFriends3.getViewport().setBackground(new java.awt.Color(33, 39, 53));

        popmenu_inviteGame.setBackground(new java.awt.Color(33, 39, 53));
        popmenu_inviteGame.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        popmenu_inviteGame.setForeground(new java.awt.Color(204, 204, 204));

        jMenuItem1.setBackground(new java.awt.Color(33, 39, 53));
        jMenuItem1.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jMenuItem1.setForeground(new java.awt.Color(204, 204, 204));
        jMenuItem1.setText("Invite to Game");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        popmenu_inviteGame.add(jMenuItem1);

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.LINE_AXIS));

        jScrollPane1.setBorder(null);
        jScrollPane1.setAutoscrolls(true);
        jScrollPane1.setPreferredSize(new java.awt.Dimension(1250, 800));

        jSplitPane1.setBorder(null);
        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane1.setPreferredSize(new java.awt.Dimension(1250, 800));
        jSplitPane1.setDividerSize(0);

        ((javax.swing.plaf.basic.BasicInternalFrameUI)jInternalFrame1.getUI()).setNorthPane(null);
        jInternalFrame1.setBackground(new java.awt.Color(18, 19, 23));
        jInternalFrame1.setBorder(null);
        jInternalFrame1.setBorder(javax.swing.BorderFactory.createRaisedBevelBorder());
        jInternalFrame1.setPreferredSize(new java.awt.Dimension(1250, 800));
        jInternalFrame1.setVisible(true);
        jInternalFrame1.getContentPane().setLayout(new java.awt.CardLayout());

        panel_games.setBackground(new java.awt.Color(33, 39, 53));

        jButton8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/fd21caf6f32836a3f2e51860fd4701.png"))); // NOI18N
        jButton8.setContentAreaFilled(false);
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jButton9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/tictactoe_hd_itunesartwork.png"))); // NOI18N
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        jLabel19.setFont(new java.awt.Font("Arial Black", 0, 24)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(204, 204, 204));
        jLabel19.setText("DANK SOULS");

        jLabel20.setFont(new java.awt.Font("Arial Black", 0, 24)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(204, 204, 204));
        jLabel20.setText("Tic Tac Toe");

        jScrollPane6.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane6.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        jTextArea1.setBackground(new java.awt.Color(33, 39, 53));
        jTextArea1.setColumns(20);
        jTextArea1.setForeground(new java.awt.Color(204, 204, 204));
        jTextArea1.setRows(5);
        jTextArea1.setText("About: Two players, X and O,\nwho take turns marking the spaces\nin a 3×3 grid\n");
        jTextArea1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jScrollPane6.setViewportView(jTextArea1);

        jScrollPane7.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane7.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        jTextArea2.setBackground(new java.awt.Color(33, 39, 53));
        jTextArea2.setColumns(20);
        jTextArea2.setForeground(new java.awt.Color(204, 204, 204));
        jTextArea2.setRows(5);
        jTextArea2.setText("About: A 2-player comptetitve fighting \ngame based in the  Medieval times.\nAn original Creation by the BEATROOT team\n");
        jTextArea2.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jScrollPane7.setViewportView(jTextArea2);

        javax.swing.GroupLayout panel_gamesLayout = new javax.swing.GroupLayout(panel_games);
        panel_games.setLayout(panel_gamesLayout);
        panel_gamesLayout.setHorizontalGroup(
            panel_gamesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_gamesLayout.createSequentialGroup()
                .addGap(197, 197, 197)
                .addGroup(panel_gamesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel_gamesLayout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(77, 77, 77))
                    .addGroup(panel_gamesLayout.createSequentialGroup()
                        .addGap(77, 77, 77)
                        .addComponent(jLabel19)
                        .addGap(322, 322, 322)
                        .addComponent(jLabel20)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 128, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel_gamesLayout.createSequentialGroup()
                        .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 373, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(112, 112, 112)
                        .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 373, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(195, 195, 195))
        );
        panel_gamesLayout.setVerticalGroup(
            panel_gamesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_gamesLayout.createSequentialGroup()
                .addGap(104, 104, 104)
                .addGroup(panel_gamesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(29, 29, 29)
                .addGroup(panel_gamesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
                    .addComponent(jLabel19))
                .addGap(28, 28, 28)
                .addGroup(panel_gamesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(195, Short.MAX_VALUE))
        );

        jInternalFrame1.getContentPane().add(panel_games, "card3");

        panel_community.setBackground(new java.awt.Color(33, 39, 53));
        panel_community.setLayout(new java.awt.CardLayout());

        jSplitPane2.setBackground(new java.awt.Color(33, 39, 53));
        jSplitPane2.setBorder(null);
        jSplitPane2.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane2.setDividerSize(0);

        jPanel3.setBackground(new java.awt.Color(33, 39, 53));
        jPanel3.setMinimumSize(new java.awt.Dimension(1400, 70));
        jPanel3.setPreferredSize(new java.awt.Dimension(1489, 70));

        btn_searchusers.setBackground(new java.awt.Color(204, 204, 204));
        btn_searchusers.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        btn_searchusers.setText("SEARCH");
        btn_searchusers.setBorder(null);
        btn_searchusers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_searchusersActionPerformed(evt);
            }
        });

        txt_search.setBackground(new java.awt.Color(82, 97, 132));
        txt_search.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        txt_search.setForeground(new java.awt.Color(204, 204, 204));
        txt_search.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel18.setFont(new java.awt.Font("Arial Black", 2, 18)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(107, 92, 172));
        jLabel18.setText(".Search Users");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(79, 79, 79)
                .addComponent(txt_search, javax.swing.GroupLayout.PREFERRED_SIZE, 219, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32)
                .addComponent(btn_searchusers, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 227, Short.MAX_VALUE)
                .addComponent(jLabel18)
                .addGap(449, 449, 449))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_search, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_searchusers, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18))
                .addContainerGap(22, Short.MAX_VALUE))
        );

        jSplitPane2.setLeftComponent(jPanel3);

        scrollpane_viewFriends1.setBackground(new java.awt.Color(33, 39, 53));
        scrollpane_viewFriends1.setBorder(null);

        table_friends1.setBackground(new java.awt.Color(33, 39, 53));
        table_friends1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        table_friends1.setFont(new java.awt.Font("Cambria Math", 1, 36)); // NOI18N
        table_friends1.setForeground(new java.awt.Color(204, 204, 204));
        table_friends1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Username"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        table_friends1.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        table_friends1.setAutoscrolls(false);
        table_friends1.setColumnSelectionAllowed(true);
        table_friends1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        table_friends1.setDropMode(javax.swing.DropMode.ON);
        table_friends1.setGridColor(new java.awt.Color(0, 0, 0));
        table_friends1.setRowHeight(50);
        table_friends1.setSelectionBackground(new java.awt.Color(204, 204, 204));
        table_friends1.setSelectionForeground(new java.awt.Color(33, 39, 53));
        table_friends1.setShowHorizontalLines(false);
        table_friends1.getTableHeader().setResizingAllowed(false);
        table_friends1.getTableHeader().setReorderingAllowed(false);
        table_friends1.getTableHeader().setBackground(new java.awt.Color(33,39,53));
        table_friends1.getTableHeader().setForeground(new java.awt.Color(204,204,204));
        table_friends1.getTableHeader().setBorder(javax.swing.BorderFactory.createEmptyBorder());
        table_friends1.getTableHeader().setFont(new java.awt.Font("Arial", 1, 22));
        scrollpane_viewFriends1.setViewportView(table_friends1);
        table_friends1.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        table_friends1.addMouseListener( new MouseAdapter()
            {
                public void mousePressed(MouseEvent e)
                {
                    System.out.println("pressed");
                }

                public void mouseReleased(MouseEvent e)
                {
                    if (e.isPopupTrigger())
                    {
                        JTable source = (JTable)e.getSource();
                        int row = source.rowAtPoint( e.getPoint() );
                        int column = source.columnAtPoint( e.getPoint() );

                        if (! source.isRowSelected(row))
                        source.changeSelection(row, column, false, false);

                        popmenu_user.show(e.getComponent(), e.getX(), e.getY());
                    }
                }
            });

            scrollpane_viewFriends1.getViewport().setBackground(new java.awt.Color(33, 39, 53));

            jSplitPane2.setRightComponent(scrollpane_viewFriends1);

            panel_community.add(jSplitPane2, "card4");

            scrollpane_viewFriends.setBackground(new java.awt.Color(51, 0, 255));

            table_friends.setBackground(new java.awt.Color(33, 39, 53));
            table_friends.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
            table_friends.setFont(new java.awt.Font("Cambria Math", 1, 36)); // NOI18N
            table_friends.setForeground(new java.awt.Color(204, 204, 204));
            table_friends.setModel(new javax.swing.table.DefaultTableModel(
                new Object [][] {

                },
                new String [] {
                    "Status", "Username"
                }
            ) {
                Class[] types = new Class [] {
                    java.lang.String.class, java.lang.String.class
                };
                boolean[] canEdit = new boolean [] {
                    false, false
                };

                public Class getColumnClass(int columnIndex) {
                    return types [columnIndex];
                }

                public boolean isCellEditable(int rowIndex, int columnIndex) {
                    return canEdit [columnIndex];
                }
            });
            table_friends.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
            table_friends.setAutoscrolls(false);
            table_friends.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
            table_friends.setDropMode(javax.swing.DropMode.ON);
            table_friends.setGridColor(new java.awt.Color(0, 0, 0));
            table_friends.setRowHeight(50);
            table_friends.setSelectionBackground(new java.awt.Color(204, 204, 204));
            table_friends.setSelectionForeground(new java.awt.Color(33, 39, 53));
            table_friends.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
            table_friends.setShowHorizontalLines(false);
            table_friends.getTableHeader().setResizingAllowed(false);
            table_friends.getTableHeader().setReorderingAllowed(false);
            table_friends.getTableHeader().setBackground(new java.awt.Color(33,39,53));
            table_friends.getTableHeader().setForeground(new java.awt.Color(204,204,204));
            table_friends.getTableHeader().setBorder(javax.swing.BorderFactory.createEmptyBorder());
            table_friends.getTableHeader().setFont(new java.awt.Font("Arial", 1, 22));
            scrollpane_viewFriends.setViewportView(table_friends);
            table_friends.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
            if (table_friends.getColumnModel().getColumnCount() > 0) {
                table_friends.getColumnModel().getColumn(0).setMinWidth(600);
                table_friends.getColumnModel().getColumn(0).setPreferredWidth(600);
                table_friends.getColumnModel().getColumn(0).setMaxWidth(600);
            }
            table_friends.addMouseListener( new MouseAdapter()
                {
                    public void mousePressed(MouseEvent e)
                    {
                        System.out.println("pressed");
                    }

                    public void mouseReleased(MouseEvent e)
                    {
                        if (e.isPopupTrigger())
                        {
                            JTable source = (JTable)e.getSource();
                            int row = source.rowAtPoint( e.getPoint() );
                            int column = source.columnAtPoint( e.getPoint() );

                            if (! source.isRowSelected(row))
                            source.changeSelection(row, column, false, false);

                            popmenu_friends.show(e.getComponent(), e.getX(), e.getY());
                        }
                    }
                });

                scrollpane_viewFriends.getViewport().setBackground(new java.awt.Color(33, 39, 53));

                panel_community.add(scrollpane_viewFriends, "card2");

                jInternalFrame1.getContentPane().add(panel_community, "card2");

                panel_friendrequests.setBackground(new java.awt.Color(33, 39, 53));
                panel_friendrequests.setPreferredSize(new java.awt.Dimension(1300, 1000));
                panel_friendrequests.setLayout(new java.awt.CardLayout());

                scrollpane_viewFriends2.setBackground(new java.awt.Color(51, 0, 255));

                table_friends2.setBackground(new java.awt.Color(33, 39, 53));
                table_friends2.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
                table_friends2.setFont(new java.awt.Font("Cambria Math", 1, 36)); // NOI18N
                table_friends2.setForeground(new java.awt.Color(204, 204, 204));
                table_friends2.setModel(new javax.swing.table.DefaultTableModel(
                    new Object [][] {

                    },
                    new String [] {
                        "Username", "", ""
                    }
                ) {
                    Class[] types = new Class [] {
                        java.lang.String.class, java.lang.Object.class, java.lang.Object.class
                    };
                    boolean[] canEdit = new boolean [] {
                        false, true, true
                    };

                    public Class getColumnClass(int columnIndex) {
                        return types [columnIndex];
                    }

                    public boolean isCellEditable(int rowIndex, int columnIndex) {
                        return canEdit [columnIndex];
                    }
                });
                table_friends2.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
                table_friends2.setAutoscrolls(false);
                table_friends2.setColumnSelectionAllowed(true);
                table_friends2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
                table_friends2.setDropMode(javax.swing.DropMode.ON);
                table_friends2.setGridColor(new java.awt.Color(0, 0, 0));
                table_friends2.setRowHeight(50);
                table_friends2.setSelectionBackground(new java.awt.Color(204, 204, 204));
                table_friends2.setSelectionForeground(new java.awt.Color(33, 39, 53));
                table_friends2.setShowHorizontalLines(false);
                table_friends2.getTableHeader().setResizingAllowed(false);
                table_friends2.getTableHeader().setReorderingAllowed(false);
                table_friends2.getTableHeader().setBackground(new java.awt.Color(33,39,53));
                table_friends2.getTableHeader().setForeground(new java.awt.Color(204,204,204));
                table_friends2.getTableHeader().setBorder(javax.swing.BorderFactory.createEmptyBorder());
                table_friends2.getTableHeader().setFont(new java.awt.Font("Cambria Math", 1, 22));
                scrollpane_viewFriends2.setViewportView(table_friends2);
                Action accept = new AbstractAction()
                {
                    public void actionPerformed(ActionEvent e)
                    {
                        JTable table = (JTable)e.getSource();
                        int modelRow = Integer.valueOf( e.getActionCommand() );
                        String user1=(String)table.getModel().getValueAt(modelRow, 0);
                        System.out.println(user1);
                        request r =new request(); r.command="acceptRequest";r.username=user1;r.user2=user;
                        clt.sendRequest(r);
                        ((DefaultTableModel)table.getModel()).removeRow(modelRow);
                    }
                };
                Action reject = new AbstractAction()
                {
                    public void actionPerformed(ActionEvent e)
                    {
                        JTable table = (JTable)e.getSource();
                        int modelRow = Integer.valueOf( e.getActionCommand() );
                        String user1=(String)table.getModel().getValueAt(modelRow, 0);
                        System.out.println(user1);
                        request r =new request(); r.command="rejectRequest";r.username=user1;r.user2=user;
                        clt.sendRequest(r);
                        ((DefaultTableModel)table.getModel()).removeRow(modelRow);
                    }
                };
                table_friends2.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
                if (table_friends2.getColumnModel().getColumnCount() > 0) {
                    table_friends2.getColumnModel().getColumn(0).setMinWidth(700);
                    table_friends2.getColumnModel().getColumn(0).setPreferredWidth(700);
                    table_friends2.getColumnModel().getColumn(0).setMaxWidth(700);
                    table_friends2.getColumnModel().getColumn(1).setMinWidth(170);
                    table_friends2.getColumnModel().getColumn(1).setPreferredWidth(170);
                    table_friends2.getColumnModel().getColumn(1).setMaxWidth(170);
                    table_friends2.getColumnModel().getColumn(2).setMinWidth(170);
                    table_friends2.getColumnModel().getColumn(2).setPreferredWidth(170);
                    table_friends2.getColumnModel().getColumn(2).setMaxWidth(170);
                }
                ButtonColumn buttonColumn = new ButtonColumn(table_friends2, accept, 1);
                ButtonColumn buttonColumn2 = new ButtonColumn(table_friends2, reject,2 );

                scrollpane_viewFriends2.getViewport().setBackground(new java.awt.Color(33, 39, 53));

                panel_friendrequests.add(scrollpane_viewFriends2, "card2");

                jInternalFrame1.getContentPane().add(panel_friendrequests, "card4");

                panel_editProfile.setBackground(new java.awt.Color(33, 39, 53));
                panel_editProfile.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

                jScrollPane5.setBorder(null);
                jScrollPane5.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

                edit_bio.setBackground(new java.awt.Color(66, 78, 106));
                edit_bio.setColumns(20);
                edit_bio.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
                edit_bio.setForeground(new java.awt.Color(204, 204, 204));
                edit_bio.setRows(5);
                edit_bio.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(51, 51, 51)));
                jScrollPane5.setViewportView(edit_bio);

                panel_editProfile.add(jScrollPane5, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 320, 890, 160));

                jLabel4.setFont(new java.awt.Font("Arial Black", 0, 18)); // NOI18N
                jLabel4.setForeground(new java.awt.Color(153, 153, 153));
                jLabel4.setText("First Name:");
                panel_editProfile.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 100, -1, -1));

                jLabel5.setFont(new java.awt.Font("Arial Black", 0, 18)); // NOI18N
                jLabel5.setForeground(new java.awt.Color(153, 153, 153));
                jLabel5.setText("Last Name:");
                panel_editProfile.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 160, -1, -1));

                jLabel8.setFont(new java.awt.Font("Arial Black", 0, 18)); // NOI18N
                jLabel8.setForeground(new java.awt.Color(153, 153, 153));
                jLabel8.setText("Country:");
                panel_editProfile.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 220, -1, -1));

                jLabel9.setFont(new java.awt.Font("Arial Black", 0, 18)); // NOI18N
                jLabel9.setForeground(new java.awt.Color(153, 153, 153));
                jLabel9.setText("Number:");
                panel_editProfile.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 280, -1, -1));

                jLabel10.setFont(new java.awt.Font("Arial Black", 0, 18)); // NOI18N
                jLabel10.setForeground(new java.awt.Color(153, 153, 153));
                jLabel10.setText("Avatar:");
                panel_editProfile.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 550, -1, -1));

                edit_firstName.setBackground(new java.awt.Color(66, 78, 106));
                edit_firstName.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
                edit_firstName.setForeground(new java.awt.Color(204, 204, 204));
                edit_firstName.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(51, 51, 51)));
                panel_editProfile.add(edit_firstName, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 100, 300, 30));

                edit_lastName.setBackground(new java.awt.Color(66, 78, 106));
                edit_lastName.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
                edit_lastName.setForeground(new java.awt.Color(204, 204, 204));
                edit_lastName.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(51, 51, 51)));
                edit_lastName.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        edit_lastNameActionPerformed(evt);
                    }
                });
                panel_editProfile.add(edit_lastName, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 160, 300, 30));

                edit_country.setBackground(new java.awt.Color(66, 78, 106));
                edit_country.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
                edit_country.setForeground(new java.awt.Color(204, 204, 204));
                edit_country.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(51, 51, 51)));
                panel_editProfile.add(edit_country, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 220, 300, 30));

                edit_number.setBackground(new java.awt.Color(66, 78, 106));
                edit_number.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
                edit_number.setForeground(new java.awt.Color(204, 204, 204));
                edit_number.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(51, 51, 51)));
                panel_editProfile.add(edit_number, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 270, 300, 30));

                jSeparator4.setBackground(new java.awt.Color(107, 92, 172));
                jSeparator4.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
                panel_editProfile.add(jSeparator4, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 80, 2087, 11));

                jSeparator5.setBackground(new java.awt.Color(107, 92, 172));
                jSeparator5.setOrientation(javax.swing.SwingConstants.VERTICAL);
                jSeparator5.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
                panel_editProfile.add(jSeparator5, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 0, 14, 80));

                jLabel11.setFont(new java.awt.Font("Arial Black", 0, 26)); // NOI18N
                jLabel11.setForeground(new java.awt.Color(107, 92, 172));
                jLabel11.setText("HUSSEINMZ ");
                panel_editProfile.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 20, -1, -1));

                jLabel12.setFont(new java.awt.Font("Arial Black", 2, 18)); // NOI18N
                jLabel12.setForeground(new java.awt.Color(107, 92, 172));
                jLabel12.setText(".Edit Profile");
                panel_editProfile.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 30, -1, -1));

                jLabel13.setText("AVATAR SMALL");
                panel_editProfile.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 30, -1, -1));

                jLabel14.setIcon(new javax.swing.ImageIcon("C:\\Users\\Hussein\\Downloads\\beetroot-02-02 (1).png")); // NOI18N
                panel_editProfile.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 0, -1, -1));

                pref_avatar.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
                pref_avatar.setForeground(new java.awt.Color(204, 204, 204));
                pref_avatar.setText("Viewable");
                pref_avatar.setContentAreaFilled(false);
                panel_editProfile.add(pref_avatar, new org.netbeans.lib.awtextra.AbsoluteConstraints(1150, 530, -1, -1));

                pref_lastName.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
                pref_lastName.setForeground(new java.awt.Color(204, 204, 204));
                pref_lastName.setText("Viewable");
                pref_lastName.setContentAreaFilled(false);
                panel_editProfile.add(pref_lastName, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 160, -1, -1));

                pref_country.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
                pref_country.setForeground(new java.awt.Color(204, 204, 204));
                pref_country.setText("Viewable");
                pref_country.setContentAreaFilled(false);
                panel_editProfile.add(pref_country, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 220, -1, -1));

                pref_number.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
                pref_number.setForeground(new java.awt.Color(204, 204, 204));
                pref_number.setText("Viewable");
                pref_number.setContentAreaFilled(false);
                panel_editProfile.add(pref_number, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 280, -1, -1));

                jLabel15.setFont(new java.awt.Font("Arial Black", 0, 18)); // NOI18N
                jLabel15.setForeground(new java.awt.Color(153, 153, 153));
                jLabel15.setText("Biography:");
                panel_editProfile.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 360, -1, -1));

                pref_firstName.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
                pref_firstName.setForeground(new java.awt.Color(204, 204, 204));
                pref_firstName.setText("Viewable");
                pref_firstName.setContentAreaFilled(false);
                panel_editProfile.add(pref_firstName, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 100, -1, -1));

                pref_bio.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
                pref_bio.setForeground(new java.awt.Color(204, 204, 204));
                pref_bio.setText("Viewable");
                pref_bio.setContentAreaFilled(false);
                panel_editProfile.add(pref_bio, new org.netbeans.lib.awtextra.AbsoluteConstraints(1150, 380, -1, -1));

                btn_submitChanges.setFont(new java.awt.Font("Arial Black", 0, 18)); // NOI18N
                btn_submitChanges.setForeground(new java.awt.Color(73, 61, 121));
                btn_submitChanges.setText("Submit Changes");
                btn_submitChanges.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        btn_submitChangesActionPerformed(evt);
                    }
                });
                panel_editProfile.add(btn_submitChanges, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 670, -1, -1));

                btn_goChangePassword.setFont(new java.awt.Font("Arial Black", 0, 18)); // NOI18N
                btn_goChangePassword.setForeground(new java.awt.Color(73, 61, 121));
                btn_goChangePassword.setText("Change Password");
                btn_goChangePassword.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        btn_goChangePasswordActionPerformed(evt);
                    }
                });
                panel_editProfile.add(btn_goChangePassword, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 670, -1, -1));

                jInternalFrame1.getContentPane().add(panel_editProfile, "card6");

                panel_viewprofile.setBackground(new java.awt.Color(33, 39, 53));
                panel_viewprofile.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

                jLabel3.setBackground(new java.awt.Color(204, 0, 51));
                jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/tv-smith-icon (1).png"))); // NOI18N
                jLabel3.setBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(107, 92, 172), new java.awt.Color(107, 92, 172)));
                panel_viewprofile.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 90, 270, 290));

                jLabel6.setFont(new java.awt.Font("Arial", 3, 36)); // NOI18N
                jLabel6.setForeground(new java.awt.Color(107, 92, 172));
                jLabel6.setText("BEATROOT Profile");
                panel_viewprofile.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 30, -1, -1));
                panel_viewprofile.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 0, -1, 80));

                view_username.setFont(new java.awt.Font("Arial Black", 1, 24)); // NOI18N
                view_username.setForeground(new java.awt.Color(107, 92, 172));
                view_username.setText("HSSEINMZ");
                panel_viewprofile.add(view_username, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 90, 390, 80));

                view_firstName.setFont(new java.awt.Font("Arial Black", 1, 24)); // NOI18N
                view_firstName.setForeground(new java.awt.Color(107, 92, 172));
                view_firstName.setText("First Name");
                panel_viewprofile.add(view_firstName, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 180, -1, -1));

                view_nationality.setFont(new java.awt.Font("Arial Black", 1, 24)); // NOI18N
                view_nationality.setForeground(new java.awt.Color(107, 92, 172));
                view_nationality.setText("Nationality");
                panel_viewprofile.add(view_nationality, new org.netbeans.lib.awtextra.AbsoluteConstraints(840, 180, -1, -1));

                jSeparator2.setBackground(new java.awt.Color(107, 92, 172));
                jSeparator2.setForeground(new java.awt.Color(107, 92, 172));
                panel_viewprofile.add(jSeparator2, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 170, 690, 20));

                jScrollPane4.setBorder(null);
                jScrollPane4.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

                view_bio.setBackground(new java.awt.Color(33, 39, 53));
                view_bio.setColumns(20);
                view_bio.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
                view_bio.setForeground(new java.awt.Color(107, 92, 172));
                view_bio.setRows(5);
                view_bio.setText("Richard Phillips Feynman (/ˈfaɪnmən/; May 11, 1918 – February 15, 1988) was an American theoretical \nphysicist known for his work in the path integral formulation of quantum mechanics, \nthe theory of quantum electrodynamics, and the physics of the superfluidity of supercooled liquid helium, \nas well as in particle physics for which he proposed the parton model. For his contributions to the development\n of quantum electrodynamics, Feynman, jointly with Julian Schwinger and Shin'ichirō Tomonaga, \nreceived the Nobel Prize in Physics in 1965.");
                jScrollPane4.setViewportView(view_bio);

                panel_viewprofile.add(jScrollPane4, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 240, 890, 160));

                jButton3.setFont(new java.awt.Font("Arial Black", 0, 24)); // NOI18N
                jButton3.setForeground(new java.awt.Color(107, 92, 172));
                jButton3.setText("Personal Info");
                jButton3.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, new java.awt.Color(107, 92, 172), new java.awt.Color(107, 92, 172), new java.awt.Color(107, 92, 172), new java.awt.Color(107, 92, 172)));
                jButton3.setContentAreaFilled(false);
                jButton3.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton3ActionPerformed(evt);
                    }
                });
                panel_viewprofile.add(jButton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 400, 220, 40));

                jButton4.setFont(new java.awt.Font("Arial Black", 0, 24)); // NOI18N
                jButton4.setForeground(new java.awt.Color(107, 92, 172));
                jButton4.setText("Statistics");
                jButton4.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, new java.awt.Color(107, 92, 172), new java.awt.Color(107, 92, 172), new java.awt.Color(107, 92, 172), new java.awt.Color(107, 92, 172)));
                jButton4.setContentAreaFilled(false);
                jButton4.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton4ActionPerformed(evt);
                    }
                });
                panel_viewprofile.add(jButton4, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 400, 160, 40));

                jButton5.setFont(new java.awt.Font("Arial Black", 0, 24)); // NOI18N
                jButton5.setForeground(new java.awt.Color(107, 92, 172));
                jButton5.setText("Games Played");
                jButton5.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, new java.awt.Color(107, 92, 172), new java.awt.Color(107, 92, 172), new java.awt.Color(107, 92, 172), new java.awt.Color(107, 92, 172)));
                jButton5.setContentAreaFilled(false);
                panel_viewprofile.add(jButton5, new org.netbeans.lib.awtextra.AbsoluteConstraints(760, 400, 230, 40));

                view_lastName.setFont(new java.awt.Font("Arial Black", 1, 24)); // NOI18N
                view_lastName.setForeground(new java.awt.Color(107, 92, 172));
                view_lastName.setText("Last Name");
                panel_viewprofile.add(view_lastName, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 180, -1, -1));

                jSeparator3.setBackground(new java.awt.Color(51, 0, 51));
                jSeparator3.setForeground(new java.awt.Color(107, 92, 172));
                panel_viewprofile.add(jSeparator3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 90, 2370, 10));

                jSeparator1.setBackground(new java.awt.Color(51, 0, 51));
                jSeparator1.setForeground(new java.awt.Color(107, 92, 172));
                panel_viewprofile.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 440, 1600, 10));

                jInternalFrame2.setVisible(true);
                ((javax.swing.plaf.basic.BasicInternalFrameUI)jInternalFrame2.getUI()).setNorthPane(null);
                jInternalFrame2.setBackground(new java.awt.Color(18, 19, 23));

                jInternalFrame2.setBorder(null);
                //jInternalFrame2.setBorder(javax.swing.BorderFactory.createRaisedBevelBorder());
                jInternalFrame2.getContentPane().setLayout(new java.awt.CardLayout());

                jPanel4.setBackground(new java.awt.Color(33, 39, 53));

                view_dof.setFont(new java.awt.Font("Arial", 3, 18)); // NOI18N
                view_dof.setForeground(new java.awt.Color(102, 77, 172));
                view_dof.setText("Date of birth");

                view_email.setFont(new java.awt.Font("Arial", 3, 18)); // NOI18N
                view_email.setForeground(new java.awt.Color(102, 77, 172));
                view_email.setText("Email:");

                view_number.setFont(new java.awt.Font("Arial", 3, 18)); // NOI18N
                view_number.setForeground(new java.awt.Color(102, 77, 172));
                view_number.setText("Phone number:");

                view_gender.setFont(new java.awt.Font("Arial", 3, 18)); // NOI18N
                view_gender.setForeground(new java.awt.Color(102, 77, 172));
                view_gender.setText("Gender:");

                javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
                jPanel4.setLayout(jPanel4Layout);
                jPanel4Layout.setHorizontalGroup(
                    jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(view_dof)
                            .addComponent(view_email))
                        .addGap(322, 322, 322)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(view_number)
                            .addComponent(view_gender))
                        .addContainerGap(416, Short.MAX_VALUE))
                );
                jPanel4Layout.setVerticalGroup(
                    jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(view_email)
                            .addComponent(view_number))
                        .addGap(36, 36, 36)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(view_dof)
                            .addComponent(view_gender))
                        .addContainerGap(591, Short.MAX_VALUE))
                );

                jInternalFrame2.getContentPane().add(jPanel4, "card2");

                jPanel5.setBackground(new java.awt.Color(33, 39, 53));

                view_matchesplayed.setFont(new java.awt.Font("Arial", 3, 18)); // NOI18N
                view_matchesplayed.setForeground(new java.awt.Color(102, 77, 172));
                view_matchesplayed.setText("Email:");

                view_wins.setFont(new java.awt.Font("Arial", 3, 18)); // NOI18N
                view_wins.setForeground(new java.awt.Color(102, 77, 172));
                view_wins.setText("Phone number:");

                view_ratio.setFont(new java.awt.Font("Arial", 3, 18)); // NOI18N
                view_ratio.setForeground(new java.awt.Color(102, 77, 172));
                view_ratio.setText("Gender:");

                view_losses.setFont(new java.awt.Font("Arial", 3, 18)); // NOI18N
                view_losses.setForeground(new java.awt.Color(102, 77, 172));
                view_losses.setText("Date of birth");

                javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
                jPanel5.setLayout(jPanel5Layout);
                jPanel5Layout.setHorizontalGroup(
                    jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(view_losses)
                            .addComponent(view_matchesplayed))
                        .addGap(322, 322, 322)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(view_wins)
                            .addComponent(view_ratio))
                        .addContainerGap(416, Short.MAX_VALUE))
                );
                jPanel5Layout.setVerticalGroup(
                    jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(view_matchesplayed)
                            .addComponent(view_wins))
                        .addGap(36, 36, 36)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(view_losses)
                            .addComponent(view_ratio))
                        .addContainerGap(591, Short.MAX_VALUE))
                );

                jInternalFrame2.getContentPane().add(jPanel5, "card3");

                panel_viewprofile.add(jInternalFrame2, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 440, -1, -1));

                btn_editProfile.setBackground(new java.awt.Color(153, 153, 153));
                btn_editProfile.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
                btn_editProfile.setForeground(new java.awt.Color(204, 204, 204));
                btn_editProfile.setText("Edit Profile");
                btn_editProfile.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
                btn_editProfile.setContentAreaFilled(false);
                panel_viewprofile.add(btn_editProfile, new org.netbeans.lib.awtextra.AbsoluteConstraints(931, 120, 110, -1));

                scrollpane_viewFriends3.setBackground(new java.awt.Color(51, 0, 255));

                table_friends3.setBackground(new java.awt.Color(33, 39, 53));
                table_friends3.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
                table_friends3.setFont(new java.awt.Font("Cambria Math", 1, 36)); // NOI18N
                table_friends3.setForeground(new java.awt.Color(107, 92, 172));
                table_friends3.setModel(new javax.swing.table.DefaultTableModel(
                    new Object [][] {

                    },
                    new String [] {
                        "Friends List"
                    }
                ) {
                    Class[] types = new Class [] {
                        java.lang.String.class
                    };
                    boolean[] canEdit = new boolean [] {
                        false
                    };

                    public Class getColumnClass(int columnIndex) {
                        return types [columnIndex];
                    }

                    public boolean isCellEditable(int rowIndex, int columnIndex) {
                        return canEdit [columnIndex];
                    }
                });
                table_friends3.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
                table_friends3.setAutoscrolls(false);
                table_friends3.setColumnSelectionAllowed(true);
                table_friends3.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
                table_friends3.setDropMode(javax.swing.DropMode.ON);
                table_friends3.setGridColor(new java.awt.Color(0, 0, 0));
                table_friends3.setRowHeight(50);
                table_friends3.setSelectionBackground(new java.awt.Color(204, 204, 204));
                table_friends3.setSelectionForeground(new java.awt.Color(33, 39, 53));
                table_friends3.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
                table_friends3.setShowHorizontalLines(false);
                table_friends3.getTableHeader().setResizingAllowed(false);
                table_friends3.getTableHeader().setReorderingAllowed(false);
                table_friends3.getTableHeader().setBackground(new java.awt.Color(33,39,53));
                table_friends3.getTableHeader().setForeground(new java.awt.Color(107,92,172));
                table_friends3.getTableHeader().setFont(new java.awt.Font("Arial", 1, 22));
                table_friends3.getTableHeader().setPreferredSize(new Dimension(380,40));
                table_friends3.getTableHeader().setBorder(null);
                scrollpane_viewFriends3.setViewportView(table_friends3);
                table_friends3.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                if (table_friends3.getColumnModel().getColumnCount() > 0) {
                    table_friends3.getColumnModel().getColumn(0).setMinWidth(380);
                    table_friends3.getColumnModel().getColumn(0).setPreferredWidth(380);
                    table_friends3.getColumnModel().getColumn(0).setMaxWidth(380);
                }

                scrollpane_viewFriends3.getViewport().setBackground(new java.awt.Color(33, 39, 53));

                panel_viewprofile.add(scrollpane_viewFriends3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 400, 380, 550));

                jInternalFrame1.getContentPane().add(panel_viewprofile, "card7");

                panel_changePassword.setBackground(new java.awt.Color(33, 39, 53));
                panel_changePassword.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

                jLabel16.setFont(new java.awt.Font("Arial Black", 0, 18)); // NOI18N
                jLabel16.setForeground(new java.awt.Color(153, 153, 153));
                jLabel16.setText("Enter Old Password:");
                panel_changePassword.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 170, -1, -1));

                jLabel17.setFont(new java.awt.Font("Arial Black", 0, 18)); // NOI18N
                jLabel17.setForeground(new java.awt.Color(153, 153, 153));
                jLabel17.setText("Confirm New Password");
                panel_changePassword.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 330, -1, -1));

                jSeparator6.setBackground(new java.awt.Color(107, 92, 172));
                jSeparator6.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
                panel_changePassword.add(jSeparator6, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 80, 2087, 11));

                jSeparator7.setBackground(new java.awt.Color(107, 92, 172));
                jSeparator7.setOrientation(javax.swing.SwingConstants.VERTICAL);
                jSeparator7.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
                panel_changePassword.add(jSeparator7, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 0, 14, 80));

                jLabel21.setFont(new java.awt.Font("Arial Black", 0, 26)); // NOI18N
                jLabel21.setForeground(new java.awt.Color(107, 92, 172));
                jLabel21.setText("HUSSEINMZ ");
                panel_changePassword.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 20, -1, -1));

                jLabel22.setFont(new java.awt.Font("Arial Black", 2, 18)); // NOI18N
                jLabel22.setForeground(new java.awt.Color(107, 92, 172));
                jLabel22.setText(".Edit Profile");
                panel_changePassword.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 30, -1, -1));

                jLabel23.setText("AVATAR SMALL");
                panel_changePassword.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 30, -1, -1));

                jLabel24.setIcon(new javax.swing.ImageIcon("C:\\Users\\Hussein\\Downloads\\beetroot-02-02 (1).png")); // NOI18N
                panel_changePassword.add(jLabel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 0, -1, -1));

                btn_changePasswordAction.setFont(new java.awt.Font("Arial Black", 0, 18)); // NOI18N
                btn_changePasswordAction.setForeground(new java.awt.Color(73, 61, 121));
                btn_changePasswordAction.setText("Change Password");
                btn_changePasswordAction.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        btn_changePasswordActionActionPerformed(evt);
                    }
                });
                panel_changePassword.add(btn_changePasswordAction, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 440, -1, -1));

                jLabel26.setFont(new java.awt.Font("Arial Black", 0, 18)); // NOI18N
                jLabel26.setForeground(new java.awt.Color(153, 153, 153));
                jLabel26.setText("New Password");
                panel_changePassword.add(jLabel26, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 250, -1, -1));

                edit_oldPassword.setBackground(new java.awt.Color(66, 78, 106));
                edit_oldPassword.setForeground(new java.awt.Color(204, 204, 204));
                edit_oldPassword.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(51, 51, 51)));
                panel_changePassword.add(edit_oldPassword, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 170, 300, 30));

                edit_newPassword.setBackground(new java.awt.Color(66, 78, 106));
                edit_newPassword.setForeground(new java.awt.Color(204, 204, 204));
                edit_newPassword.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(51, 51, 51)));
                panel_changePassword.add(edit_newPassword, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 250, 300, 30));

                edit_confirmnewPassword.setBackground(new java.awt.Color(66, 78, 106));
                edit_confirmnewPassword.setForeground(new java.awt.Color(204, 204, 204));
                edit_confirmnewPassword.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(51, 51, 51)));
                panel_changePassword.add(edit_confirmnewPassword, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 330, 300, 30));

                jButton7.setText("GO BACK");
                jButton7.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton7ActionPerformed(evt);
                    }
                });
                panel_changePassword.add(jButton7, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 120, -1, -1));

                label_changePassWarning.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
                label_changePassWarning.setForeground(new java.awt.Color(166, 0, 255));
                panel_changePassword.add(label_changePassWarning, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 440, 290, 40));

                jInternalFrame1.getContentPane().add(panel_changePassword, "card6");

                panel_modeSelection.setBackground(new java.awt.Color(33, 39, 53));

                jScrollPane8.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
                jScrollPane8.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

                jTextArea3.setBackground(new java.awt.Color(33, 39, 53));
                jTextArea3.setColumns(20);
                jTextArea3.setForeground(new java.awt.Color(204, 204, 204));
                jTextArea3.setRows(5);
                jTextArea3.setText("Automatic Matchmaking with a connection\nto a global chat-room\n");
                jTextArea3.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
                jScrollPane8.setViewportView(jTextArea3);

                jLabel25.setFont(new java.awt.Font("Arial Black", 0, 24)); // NOI18N
                jLabel25.setForeground(new java.awt.Color(204, 204, 204));
                jLabel25.setText("GLOBAL");

                jButton10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/global-network-icon.png"))); // NOI18N
                jButton10.setContentAreaFilled(false);
                jButton10.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton10ActionPerformed(evt);
                    }
                });

                jButton11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/man--1-.png"))); // NOI18N
                jButton11.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton11ActionPerformed(evt);
                    }
                });

                jLabel27.setFont(new java.awt.Font("Arial Black", 0, 24)); // NOI18N
                jLabel27.setForeground(new java.awt.Color(204, 204, 204));
                jLabel27.setText("FRIENDS");

                jScrollPane9.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
                jScrollPane9.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

                jTextArea4.setBackground(new java.awt.Color(33, 39, 53));
                jTextArea4.setColumns(20);
                jTextArea4.setForeground(new java.awt.Color(204, 204, 204));
                jTextArea4.setRows(5);
                jTextArea4.setText("Invite your friends to play and chat ");
                jTextArea4.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
                jScrollPane9.setViewportView(jTextArea4);

                javax.swing.GroupLayout panel_modeSelectionLayout = new javax.swing.GroupLayout(panel_modeSelection);
                panel_modeSelection.setLayout(panel_modeSelectionLayout);
                panel_modeSelectionLayout.setHorizontalGroup(
                    panel_modeSelectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel_modeSelectionLayout.createSequentialGroup()
                        .addGap(197, 197, 197)
                        .addGroup(panel_modeSelectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panel_modeSelectionLayout.createSequentialGroup()
                                .addGroup(panel_modeSelectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(panel_modeSelectionLayout.createSequentialGroup()
                                        .addGap(26, 26, 26)
                                        .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jButton11, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(77, 77, 77))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel_modeSelectionLayout.createSequentialGroup()
                                        .addGap(0, 0, Short.MAX_VALUE)
                                        .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 373, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(112, 112, 112)
                                        .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 373, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(195, 195, 195))
                            .addGroup(panel_modeSelectionLayout.createSequentialGroup()
                                .addGap(77, 77, 77)
                                .addComponent(jLabel25)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel27)
                                .addGap(348, 348, 348))))
                );
                panel_modeSelectionLayout.setVerticalGroup(
                    panel_modeSelectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel_modeSelectionLayout.createSequentialGroup()
                        .addGap(104, 104, 104)
                        .addGroup(panel_modeSelectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton11, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(29, 29, 29)
                        .addGroup(panel_modeSelectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel27)
                            .addComponent(jLabel25))
                        .addGap(28, 28, 28)
                        .addGroup(panel_modeSelectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(195, Short.MAX_VALUE))
                );

                jInternalFrame1.getContentPane().add(panel_modeSelection, "card8");

                scrollpane_viewFriends5.setBackground(new java.awt.Color(51, 0, 255));

                table_friends5.setBackground(new java.awt.Color(33, 39, 53));
                table_friends5.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
                table_friends5.setFont(new java.awt.Font("Cambria Math", 1, 36)); // NOI18N
                table_friends5.setForeground(new java.awt.Color(204, 204, 204));
                table_friends5.setModel(new javax.swing.table.DefaultTableModel(
                    new Object [][] {

                    },
                    new String [] {
                        "Username"
                    }
                ) {
                    Class[] types = new Class [] {
                        java.lang.String.class
                    };
                    boolean[] canEdit = new boolean [] {
                        false
                    };

                    public Class getColumnClass(int columnIndex) {
                        return types [columnIndex];
                    }

                    public boolean isCellEditable(int rowIndex, int columnIndex) {
                        return canEdit [columnIndex];
                    }
                });
                table_friends5.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
                table_friends5.setAutoscrolls(false);
                table_friends5.setColumnSelectionAllowed(true);
                table_friends5.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
                table_friends5.setDropMode(javax.swing.DropMode.ON);
                table_friends5.setGridColor(new java.awt.Color(0, 0, 0));
                table_friends5.setRowHeight(50);
                table_friends5.setSelectionBackground(new java.awt.Color(204, 204, 204));
                table_friends5.setSelectionForeground(new java.awt.Color(33, 39, 53));
                table_friends5.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
                table_friends5.setShowHorizontalLines(false);
                table_friends5.getTableHeader().setResizingAllowed(false);
                table_friends5.getTableHeader().setReorderingAllowed(false);
                table_friends5.getTableHeader().setBackground(new java.awt.Color(33,39,53));
                table_friends5.getTableHeader().setForeground(new java.awt.Color(204,204,204));
                table_friends5.getTableHeader().setBorder(javax.swing.BorderFactory.createEmptyBorder());
                table_friends5.getTableHeader().setFont(new java.awt.Font("Arial", 1, 22));
                scrollpane_viewFriends5.setViewportView(table_friends5);
                table_friends5.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
                table_friends5.addMouseListener( new MouseAdapter()
                    {
                        public void mousePressed(MouseEvent e)
                        {

                        }

                        public void mouseReleased(MouseEvent e)
                        {
                            if (e.isPopupTrigger())
                            {
                                JTable source = (JTable)e.getSource();
                                int row = source.rowAtPoint( e.getPoint() );
                                int column = source.columnAtPoint( e.getPoint() );

                                if (! source.isRowSelected(row))
                                source.changeSelection(row, column, false, false);

                                popmenu_inviteGame.show(e.getComponent(), e.getX(), e.getY());
                            }
                        }
                    });

                    scrollpane_viewFriends5.getViewport().setBackground(new java.awt.Color(33, 39, 53));

                    javax.swing.GroupLayout panel_selectFriendLayout = new javax.swing.GroupLayout(panel_selectFriend);
                    panel_selectFriend.setLayout(panel_selectFriendLayout);
                    panel_selectFriendLayout.setHorizontalGroup(
                        panel_selectFriendLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 1250, Short.MAX_VALUE)
                        .addGroup(panel_selectFriendLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(scrollpane_viewFriends5, javax.swing.GroupLayout.DEFAULT_SIZE, 1250, Short.MAX_VALUE))
                    );
                    panel_selectFriendLayout.setVerticalGroup(
                        panel_selectFriendLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 718, Short.MAX_VALUE)
                        .addGroup(panel_selectFriendLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(scrollpane_viewFriends5, javax.swing.GroupLayout.DEFAULT_SIZE, 718, Short.MAX_VALUE))
                    );

                    jInternalFrame1.getContentPane().add(panel_selectFriend, "card9");

                    scrollpane_viewFriends6.setBackground(new java.awt.Color(51, 0, 255));

                    table_friends6.setAutoCreateRowSorter(true);
                    table_friends6.setBackground(new java.awt.Color(33, 39, 53));
                    table_friends6.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
                    table_friends6.setFont(new java.awt.Font("Cambria Math", 1, 36)); // NOI18N
                    table_friends6.setForeground(new java.awt.Color(204, 204, 204));
                    table_friends6.setModel(new javax.swing.table.DefaultTableModel(
                        new Object [][] {

                        },
                        new String [] {
                            "Username", "Wins Ratio"
                        }
                    ) {
                        Class[] types = new Class [] {
                            java.lang.String.class, java.lang.String.class
                        };
                        boolean[] canEdit = new boolean [] {
                            false, false
                        };

                        public Class getColumnClass(int columnIndex) {
                            return types [columnIndex];
                        }

                        public boolean isCellEditable(int rowIndex, int columnIndex) {
                            return canEdit [columnIndex];
                        }
                    });
                    table_friends6.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
                    table_friends6.setAutoscrolls(false);
                    table_friends6.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
                    table_friends6.setDropMode(javax.swing.DropMode.ON);
                    table_friends6.setGridColor(new java.awt.Color(0, 0, 0));
                    table_friends6.setRowHeight(50);
                    table_friends6.setSelectionBackground(new java.awt.Color(204, 204, 204));
                    table_friends6.setSelectionForeground(new java.awt.Color(33, 39, 53));
                    table_friends6.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
                    table_friends6.setShowHorizontalLines(false);
                    table_friends6.getTableHeader().setResizingAllowed(false);
                    table_friends6.getTableHeader().setReorderingAllowed(false);
                    table_friends6.getTableHeader().setBackground(new java.awt.Color(33,39,53));
                    table_friends6.getTableHeader().setForeground(new java.awt.Color(204,204,204));
                    table_friends6.getTableHeader().setBorder(javax.swing.BorderFactory.createEmptyBorder());
                    table_friends6.getTableHeader().setFont(new java.awt.Font("Arial", 1, 22));
                    scrollpane_viewFriends6.setViewportView(table_friends6);
                    table_friends6.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
                    if (table_friends6.getColumnModel().getColumnCount() > 0) {
                        table_friends6.getColumnModel().getColumn(0).setMinWidth(600);
                        table_friends6.getColumnModel().getColumn(0).setPreferredWidth(600);
                        table_friends6.getColumnModel().getColumn(0).setMaxWidth(600);
                    }

                    scrollpane_viewFriends6.getViewport().setBackground(new java.awt.Color(33, 39, 53));

                    javax.swing.GroupLayout panel_scoresLayout = new javax.swing.GroupLayout(panel_scores);
                    panel_scores.setLayout(panel_scoresLayout);
                    panel_scoresLayout.setHorizontalGroup(
                        panel_scoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 1250, Short.MAX_VALUE)
                        .addGroup(panel_scoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(scrollpane_viewFriends6, javax.swing.GroupLayout.DEFAULT_SIZE, 1250, Short.MAX_VALUE))
                    );
                    panel_scoresLayout.setVerticalGroup(
                        panel_scoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 718, Short.MAX_VALUE)
                        .addGroup(panel_scoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(scrollpane_viewFriends6, javax.swing.GroupLayout.DEFAULT_SIZE, 718, Short.MAX_VALUE))
                    );

                    jInternalFrame1.getContentPane().add(panel_scores, "card10");

                    jSplitPane1.setRightComponent(jInternalFrame1);

                    jPanel2.setBackground(new java.awt.Color(33, 39, 53));
                    jPanel2.setBorder(javax.swing.BorderFactory.createRaisedBevelBorder());
                    jPanel2.setMaximumSize(new java.awt.Dimension(999999, 50));
                    jPanel2.setMinimumSize(new java.awt.Dimension(0, 50));
                    jPanel2.setPreferredSize(new java.awt.Dimension(1420, 50));
                    jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

                    jButton2.setBackground(new java.awt.Color(58, 58, 58));
                    jButton2.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
                    jButton2.setForeground(new java.awt.Color(204, 204, 204));
                    jButton2.setText("BEATROOT");
                    jButton2.setBorder(null);
                    jButton2.setContentAreaFilled(false);
                    jButton2.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                            jButton2ActionPerformed(evt);
                        }
                    });
                    jPanel2.add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 0, 150, 50));

                    btn_GAMES.setBackground(new java.awt.Color(58, 58, 58));
                    btn_GAMES.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
                    btn_GAMES.setForeground(new java.awt.Color(204, 204, 204));
                    btn_GAMES.setText("GAMES");
                    btn_GAMES.setBorder(null);
                    btn_GAMES.setContentAreaFilled(false);
                    btn_GAMES.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                            btn_GAMESActionPerformed(evt);
                        }
                    });
                    jPanel2.add(btn_GAMES, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 0, 140, 50));

                    btn_COMMUNITY.setBackground(new java.awt.Color(58, 58, 58));
                    btn_COMMUNITY.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
                    btn_COMMUNITY.setForeground(new java.awt.Color(204, 204, 204));
                    btn_COMMUNITY.setText("COMMUNITY");
                    btn_COMMUNITY.setBorder(null);
                    btn_COMMUNITY.setContentAreaFilled(false);
                    btn_COMMUNITY.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                            btn_COMMUNITYActionPerformed(evt);
                        }
                    });
                    jPanel2.add(btn_COMMUNITY, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 0, 210, 50));

                    btn_PROFILE.setBackground(new java.awt.Color(58, 58, 58));
                    btn_PROFILE.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
                    btn_PROFILE.setForeground(new java.awt.Color(204, 204, 204));
                    btn_PROFILE.setText("HSSEINMZ");
                    btn_PROFILE.setText(user);
                    btn_PROFILE.setBorder(null);
                    btn_PROFILE.setContentAreaFilled(false);
                    btn_PROFILE.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                            btn_PROFILEActionPerformed(evt);
                        }
                    });
                    jPanel2.add(btn_PROFILE, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 0, 250, 50));

                    jLabel1.setFont(new java.awt.Font("Cambria Math", 0, 18)); // NOI18N
                    jLabel1.setForeground(new java.awt.Color(204, 204, 204));
                    jLabel1.setText("HSSEINMZ");
                    jLabel1.setText(user);
                    jPanel2.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(920, 10, 120, 30));

                    jButton6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/friendrequest.png"))); // NOI18N
                    jButton6.setBorderPainted(false);
                    jButton6.setContentAreaFilled(false);
                    jButton6.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                            jButton6ActionPerformed(evt);
                        }
                    });
                    jPanel2.add(jButton6, new org.netbeans.lib.awtextra.AbsoluteConstraints(850, 0, 35, -1));

                    jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/beetroot-02-02.png"))); // NOI18N
                    jPanel2.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

                    jComboBox1.setBackground(new java.awt.Color(33, 39, 53));
                    jComboBox1.setEditable(true);
                    jComboBox1.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
                    jComboBox1.setForeground(new java.awt.Color(204, 204, 204));
                    jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Online", "Busy", "Away", "In Game", "Custom" }));
                    jComboBox1.setBorder(null);
                    jComboBox1.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                            jComboBox1ActionPerformed(evt);
                        }
                    });
                    jPanel2.add(jComboBox1, new org.netbeans.lib.awtextra.AbsoluteConstraints(1030, 10, 100, 30));

                    jSplitPane1.setLeftComponent(jPanel2);

                    jScrollPane1.setViewportView(jSplitPane1);

                    getContentPane().add(jScrollPane1);

                    jMenuBar1.setBackground(new java.awt.Color(33, 39, 53));
                    jMenuBar1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
                    jMenuBar1.setForeground(new java.awt.Color(255, 0, 255));
                    jMenuBar1.setBorderPainted(false);
                    jMenuBar1.setFont(new java.awt.Font("Segoe UI", 2, 12)); // NOI18N

                    jMenu1.setBackground(new java.awt.Color(33, 39, 53));
                    jMenu1.setForeground(new java.awt.Color(204, 204, 204));
                    jMenu1.setText("BEATROOT");
                    jMenu1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

                    menubar_changeUser.setBackground(new java.awt.Color(33, 39, 53));
                    menubar_changeUser.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
                    menubar_changeUser.setForeground(new java.awt.Color(204, 204, 204));
                    menubar_changeUser.setText("Change User");
                    menubar_changeUser.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                            menubar_changeUserActionPerformed(evt);
                        }
                    });
                    jMenu1.add(menubar_changeUser);

                    menubar_quit.setBackground(new java.awt.Color(33, 39, 53));
                    menubar_quit.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
                    menubar_quit.setForeground(new java.awt.Color(204, 204, 204));
                    menubar_quit.setText("Quit");
                    menubar_quit.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                            menubar_quitActionPerformed(evt);
                        }
                    });
                    jMenu1.add(menubar_quit);

                    jMenuBar1.add(jMenu1);

                    jMenu2.setBackground(new java.awt.Color(33, 39, 53));
                    jMenu2.setForeground(new java.awt.Color(204, 204, 204));
                    jMenu2.setText("Help");

                    menubar_about.setBackground(new java.awt.Color(33, 39, 53));
                    menubar_about.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
                    menubar_about.setForeground(new java.awt.Color(204, 204, 204));
                    menubar_about.setText("About Beatroot");
                    jMenu2.add(menubar_about);

                    jMenuBar1.add(jMenu2);

                    setJMenuBar(jMenuBar1);

                    pack();
                }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
        JFrame frame = (JFrame) evt.getSource();
        int result = JOptionPane.showConfirmDialog(
                frame,
                "Are you sure you want to exit the application?",
                "Exit Application",
                JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            if (clt.isConnected == true) {

                if (changeUser == true) {
                    timer.stop();
                    GUI gui1 = new GUI();
                    gui1.setVisible(true);
                }
                clt.terminate();
                chatclient cc = null;
                for (int i = 0; i < ct.size(); i++) {
                    cc = ct.get(i);
                    cc.terminate();
                    //useless here
                }
            }
            if (changeUser == true) {
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            }
            else {
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            }

        }

    }//GEN-LAST:event_formWindowClosing

    private void txt_chatsendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_chatsendActionPerformed
        // TODO add your handling code here:

//        chatclient ct2;
//        boolean exists = false;
//        int i = 0;
//        for (i = 0; i < ct.size(); i++) //can be valid for any chatroom, check if there is already a connection
//        {
//            if (ct.get(i).textarea.equals(chatarea)) {
//                exists = true;
//                break;
//            }
//        }
//        if (exists == false) {
//            chatarea.append("NO CONNECTION TO SERVER");
//        }
//        else {
//            ct2 = ct.get(i);
//            String message = txt_chatsend.getText();
//            String destination = txt_sendto.getText();
//            ct2.sendmessage("chat#" + message);
//        }

    }//GEN-LAST:event_txt_chatsendActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:

    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton2ActionPerformed

    private void fillTableFriends() {
        DefaultTableModel model = (DefaultTableModel) table_friends.getModel();
        model.setRowCount(0);
        request ok = clt.sendRequest(new request("getFriendsList", user, user));

        for (int i = 0; i < ok.friends.size(); i++) {

            model.addRow(new Object[]{ok.statuss.get(i), ok.friends.get(i)});

        }

    }
    private void popmenu_viewFriendsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_popmenu_viewFriendsActionPerformed
        // TODO add your handling code here:
        hidePanels();
        panel_community.setVisible(true);
        jSplitPane2.setVisible(false);
        scrollpane_viewFriends.setVisible(true);
        fillTableFriends();

    }//GEN-LAST:event_popmenu_viewFriendsActionPerformed

    private void btn_PROFILEActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_PROFILEActionPerformed
        // TODO add your handling code here:
        popmenu_Profile.show(btn_PROFILE, btn_PROFILE.getWidth() / 2, btn_PROFILE.getHeight() / 2);

    }//GEN-LAST:event_btn_PROFILEActionPerformed

    private void popmenu_searchFriendsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_popmenu_searchFriendsActionPerformed
        // TODO add your handling code here:
       hidePanels();
        panel_community.setVisible(true);
        scrollpane_viewFriends.setVisible(false);
        jSplitPane2.setVisible(true);
    }//GEN-LAST:event_popmenu_searchFriendsActionPerformed

    private void btn_searchusersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_searchusersActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) table_friends1.getModel();
        model.setRowCount(0);
        String search = txt_search.getText();
        request rq = new request();
        rq.command = "findUser";
        rq.username = search;
        request ok = clt.sendRequest(rq);

        if (!(ok.friends == null)) {

            for (int i = 0; i < ok.friends.size(); i++) {
                model.addRow(new Object[]{ok.friends.get(i)});

            }

        }

    }//GEN-LAST:event_btn_searchusersActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        // TODO add your handling code here:
      hidePanels();
        panel_community.setVisible(false);
        panel_friendrequests.setVisible(true);
        fillTableFriendRequests();


    }//GEN-LAST:event_jButton6ActionPerformed

    private void fillTableFriendRequests() {

        request ok = new request();
        ok.command = "getFriendRequests";
        ok.username = user;
        ok = clt.sendRequest(ok);
        DefaultTableModel model = (DefaultTableModel) table_friends2.getModel();
        model.setRowCount(0);
        for (int i = 0; i < ok.friends.size(); i++) {
            System.out.println("friend req " + ok.friends.get(i));
            model.addRow(new Object[]{ok.friends.get(i), "Accept", "Reject"});

        }

    }
    private void popitem_sendrequestActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_popitem_sendrequestActionPerformed
        // TODO add your handling code here:
        int row = table_friends1.getSelectedRow();

        String user1 = (String) table_friends1.getModel().getValueAt(row, 0); // fromuser
        request rqs = new request();
        rqs.command = "sendFriendRequest";
        rqs.username = user;
        rqs.user2 = user1;
        clt.sendRequest(rqs);
    }//GEN-LAST:event_popitem_sendrequestActionPerformed

    private void popitem_viewprofileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_popitem_viewprofileActionPerformed
        // TODO add your handling code here:
        int row = table_friends1.getSelectedRow();

        String user1 = (String) table_friends1.getModel().getValueAt(row, 0); // fromuser
        fillProfile(user1);
        hidePanels();
        panel_viewprofile.setVisible(true);

    }//GEN-LAST:event_popitem_viewprofileActionPerformed

    private void menuitem_viewProfileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuitem_viewProfileActionPerformed
        // TODO add your handling code here:
        hidePanels();
        fillProfile(user);
        //panelscroll_user.setVisible(true);
        panel_viewprofile.setVisible(true);

    }//GEN-LAST:event_menuitem_viewProfileActionPerformed

    private void btn_COMMUNITYActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_COMMUNITYActionPerformed
        // TODO add your handling code here:
        popmenu_community.show(btn_COMMUNITY, btn_COMMUNITY.getWidth() / 2, btn_COMMUNITY.getHeight() / 2);
    }//GEN-LAST:event_btn_COMMUNITYActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
          jPanel4.setVisible(true);
        jPanel5.setVisible(false);
    }//GEN-LAST:event_jButton3ActionPerformed

    private void menuitem_editProfileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuitem_editProfileActionPerformed
        // TODO add your handling code here:
        hidePanels();
        fillEditProfile(); // fill profile
        panel_editProfile.setVisible(true);
    }//GEN-LAST:event_menuitem_editProfileActionPerformed

    private void menuitem_removeFriendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuitem_removeFriendActionPerformed
        // TODO add your handling code here:

        int row = table_friends.getSelectedRow();
        String user1 = (String) table_friends.getModel().getValueAt(row, 1); // friend
        request rqs = new request();
        rqs.command = "removeFriend";
        rqs.username = user;
        rqs.user2 = user1;
        clt.sendRequest(rqs);
        fillTableFriends();

    }//GEN-LAST:event_menuitem_removeFriendActionPerformed

    private void menuitem_chatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuitem_chatActionPerformed
        // TODO add your handling code here:
        int row = table_friends.getSelectedRow();
        String user1 = (String) table_friends.getModel().getValueAt(row, 1);
        ArrayList<String> recipients = new ArrayList<>();
        recipients.add(user);
        recipients.add(user1);
        openChatClients.add(user1);
        ChatArea ca = new ChatArea(user, recipients, "chat"); // new chat area oppened
        ca.setVisible(true);
    }//GEN-LAST:event_menuitem_chatActionPerformed

    private void edit_lastNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_edit_lastNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_edit_lastNameActionPerformed

    private void btn_submitChangesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_submitChangesActionPerformed
        // TODO add your handling code here:
        JPanel panelpass = new JPanel();
        JLabel label = new JLabel("Please Enter your password to confirm changes:");
        JPasswordField pass = new JPasswordField(25);
        panelpass.add(label);
        panelpass.add(pass);
        String password = null;
        String[] options = new String[]{"OK", "Cancel"};
        int option = JOptionPane.showOptionDialog(null, panelpass, "EDIT PROFILE",
                JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE,
                null, options, options[1]);
        if (option == 0) // pressing OK button
        {
            password = String.valueOf(pass.getPassword());
            // password = pass.getPassword();

        }

        // String password = (String) JOptionPane.showInputDialog(panel_community, "Please Enter your password to confirm changes", "Edit Profile", JOptionPane.QUESTION_MESSAGE);
        while (true) {
            if (password == null) {
                break;
            }

            request r = clt.sendRequest(new request("authenticate", user, password));
            if (r.response.equals("true")) {
                System.out.println("authenticated");
                request preferencesUpdate = new request();
                preferencesUpdate.command = "setPreferences";
                preferencesUpdate.username = user; // set user for whom
                // now time to get preferences
                if (pref_bio.isSelected() == true) {
                    preferencesUpdate.bio = "public";
                }
                else if (pref_bio.isSelected() == false) {
                    preferencesUpdate.bio = "personal";
                }

                if (pref_firstName.isSelected() == true) {
                    preferencesUpdate.firstname = "public";
                }
                else if (pref_firstName.isSelected() == false) {
                    preferencesUpdate.firstname = "personal";
                }
                if (pref_lastName.isSelected() == true) {
                    preferencesUpdate.lastname = "public";
                }
                else if (pref_lastName.isSelected() == false) {
                    preferencesUpdate.lastname = "personal";
                }

                if (pref_country.isSelected() == true) {
                    preferencesUpdate.nationality = "public";
                }
                else if (pref_country.isSelected() == false) {
                    preferencesUpdate.nationality = "personal";
                }
                if (pref_number.isSelected() == true) {
                    preferencesUpdate.number = "public";
                }
                else if (pref_number.isSelected() == false) {
                    preferencesUpdate.number = "personal";
                }

                if (pref_avatar.isSelected() == true) {
                    preferencesUpdate.avatar = "public";
                }
                else if (pref_avatar.isSelected() == false) {
                    preferencesUpdate.avatar = "personal";
                }
                // now done setting preferences

                clt.sendRequest(preferencesUpdate);
                // now we update info
                request updateInfo = new request();
                updateInfo.username = user;
                updateInfo.command = "updateInfo";

                updateInfo.firstname = edit_firstName.getText();
                updateInfo.lastname = edit_lastName.getText();
                updateInfo.number = edit_number.getText();
                updateInfo.nationality = edit_country.getText();
                updateInfo.bio = edit_bio.getText();
                clt.sendRequest(updateInfo); // sent request to update preferences
                fillEditProfile(); // update screen
                break;
                //do other stuff and break + refresh screen 
            }
            else if (r.response.equals("false")) {
                label = new JLabel("Wrong Password! Please enter again to confirm changes:");
                option = JOptionPane.showOptionDialog(null, panelpass, "EDIT PROFILE",
                        JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE,
                        null, options, options[1]);
                if (option == 0) // pressing OK button
                {
                    password = String.valueOf(pass.getPassword());
                    // password = pass.getPassword();

                }
            }
            else {
                break; //something went wrong then
            }
        }
    }//GEN-LAST:event_btn_submitChangesActionPerformed

    private void menuitem_friendViewProfileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuitem_friendViewProfileActionPerformed
        // TODO add your handling code here:
        int row = table_friends.getSelectedRow();
        String user1 = (String) table_friends.getModel().getValueAt(row, 1); // friend
        fillProfile(user1);
        hidePanels();
        panel_viewprofile.setVisible(true);
    }//GEN-LAST:event_menuitem_friendViewProfileActionPerformed

    private void btn_goChangePasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_goChangePasswordActionPerformed
        // TODO add your handling code here:
        hidePanels();
        panel_changePassword.setVisible(true);
    }//GEN-LAST:event_btn_goChangePasswordActionPerformed

    private void btn_changePasswordActionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_changePasswordActionActionPerformed
        // TODO add your handling code here:
        String oldpass = String.valueOf(edit_oldPassword.getPassword());
        String newpass = String.valueOf(edit_newPassword.getPassword());
        String confirmpass = String.valueOf(edit_confirmnewPassword.getPassword());

        if (!newpass.equals(confirmpass)) {
            label_changePassWarning.setForeground(Color.RED);
            label_changePassWarning.setText("Password don't match!");
        }
        else if (newpass.equals(confirmpass)) {
            request newPass = new request();
            newPass.password = oldpass;
            newPass.username = user;
            newPass.firstname = newpass;
            newPass.command = "changePassword";
            request resp = clt.sendRequest(newPass);
            if (resp.response.equals("false")) {
                label_changePassWarning.setForeground(Color.RED);
                label_changePassWarning.setText("Old Password is wrong!");
            }
            else if (resp.response.equals("true")) {
                label_changePassWarning.setForeground(Color.green);
                label_changePassWarning.setText("Password Changed");
            }

        }


    }//GEN-LAST:event_btn_changePasswordActionActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        // TODO add your handling code here:
        hidePanels();
        fillEditProfile(); // fill profile
        panel_editProfile.setVisible(true);
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        // TODO add your handling code here:
        String status = jComboBox1.getSelectedItem().toString();
        request changeStatus = new request();
        changeStatus.username = user;
        changeStatus.command = "setStatus";
        changeStatus.statuss.add(status);
        clt.sendRequest(changeStatus);

    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void menubar_changeUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menubar_changeUserActionPerformed
        // TODO add your handling code here:

        changeUser = true;
        this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));


    }//GEN-LAST:event_menubar_changeUserActionPerformed
    private boolean changeUser = false;
    private void menubar_quitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menubar_quitActionPerformed
        // TODO add your handling code here:

        this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));

        // this.dispose(); //change pages

    }//GEN-LAST:event_menubar_quitActionPerformed
    private String gameSelected = "";
    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        // TODO add your handling code here:
        gameSelected = "DANK";
        hidePanels();
        panel_modeSelection.setVisible(true);


    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        gameSelected = "TTT";
        hidePanels();
        panel_modeSelection.setVisible(true);

    }//GEN-LAST:event_jButton9ActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        // TODO add your handling code here:
        if (gameSelected.equals("TTT")) {
            new Thread() {
                @Override
                public void run() {
                    try {
                        ArrayList<String> recipients = new ArrayList<>();
                        recipients.add(user);

                        ChatArea ca = new ChatArea(user, recipients, "connectTTT"); // new chat area oppened
                        ca.setVisible(true);
                        TTTclient tttc = new TTTclient(ipAdd);
                        tttc.Run(ipAdd, 8901,user);

                    } catch (Exception ex) {
                        Logger.getLogger(mainpage.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }.start();// MODIFY HEREEEEEEEEEEEEEEEEEEE

        }

        else if (gameSelected.equals("DANK")) {

            new Thread() {
                @Override
                public void run() {
                    try {

                        ArrayList<String> recipients = new ArrayList<>();
                        recipients.add(user);

                        ChatArea ca = new ChatArea(user, recipients, "connectDANK"); // new chat area oppened
                        ca.setVisible(true);

                        start play = new start(1600);
                        play.Run(play);
                    } catch (Exception ex) {
                        Logger.getLogger(mainpage.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }.start();// MODIFY HEREEEEEEEEE
        }
        // need to fill scores table with a timer that updates auto
        
      hidePanels();
        panel_scores.setVisible(true);
                ActionListener scoresUpdate = new ActionListener() {
            public void actionPerformed(ActionEvent event) {
          request r =new request();
          if (gameSelected.equals("TTT"))
          {
              r.username=" ";
              r.command="connectTTT";
              r =clt.sendRequest(r);
                

            }
          else if (gameSelected.equals("DANK"))
          {
               r.username=" ";
              r.command="connectDANK";
              r =clt.sendRequest(r);
              
          }
          
          // in friends
          DefaultTableModel model = (DefaultTableModel) table_friends6.getModel();
            model.setRowCount(0);
        for (int i = 0; i < r.friends.size(); i++) {
            request s= new request();s.command="getWinsLosses";s.username=r.friends.get(i);
            s= clt.sendRequest(s);
            double ratio = Double.valueOf(s.username) / Double.valueOf(s.user2);
            model.addRow(new Object[]{r.friends.get(i) ,String.valueOf(ratio)});

        }
         

        }
                };
                
        Timer scores=null;
        scores = new Timer(10000, scoresUpdate);
        scores.setInitialDelay(0);
        scores.start();
        hidePanels();
        panel_scores.setVisible(true);
         
        
        
    }//GEN-LAST:event_jButton10ActionPerformed
    private int TTTport = 8901;
    private int DANKport = 1600;
    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        // TODO add your handling code here:
        int row = table_friends5.getSelectedRow();
        String user1 = (String) table_friends5.getModel().getValueAt(row, 0);
        request r = new request();

        // now launch game and chat and wait 
        if (gameSelected.equals("TTT")) {
            r.command = "inviteGame";
            r.number = gameSelected; // inviteDANK OR inviteTTT
            r.username = user;
            r.user2 = user1;
            TTTport++;
            r.dof = String.valueOf(TTTport);
            clt.sendRequest(r);

            new Thread() {
                @Override
                public void run() {
                    try {
                        ArrayList<String> recipients = new ArrayList<>();
                        recipients.add(user);
                        recipients.add(user1);

                        ChatArea ca = new ChatArea(user, recipients, "group"); // new chat area oppened
                        ca.setVisible(true);
                        request ok = new request();
                        ok.command = "createTTTserver";
                        ok.dof = String.valueOf(TTTport);
                        clt.sendRequest(ok); //create new server 
                        TTTclient tttc = new TTTclient(ipAdd);
                        tttc.Run(ipAdd, TTTport,user);

                    } catch (Exception ex) {
                        Logger.getLogger(mainpage.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }.start();// MODIFY HEREEEEEEEEEEEEEEEEEEE

        }

        else if (gameSelected.equals("DANK")) {
            r.command = "inviteGame";
            r.number = gameSelected; // inviteDANK OR inviteTTT
            r.username = user;
            r.user2 = user1;
            DANKport++;
            r.dof = String.valueOf(DANKport);
            clt.sendRequest(r);

            new Thread() {
                @Override
                public void run() {
                    try {

                        ArrayList<String> recipients = new ArrayList<>();
                        recipients.add(user);
                        recipients.add(user1);
                        ChatArea ca = new ChatArea(user, recipients, "group"); // new chat area oppened
                        ca.setVisible(true);
                        request ok = new request();
                        ok.command = "createDANKserver";
                        ok.dof = String.valueOf(DANKport);
                        clt.sendRequest(ok); //create new server 
                        start play = new start(DANKport);
                        play.Run(play);
                    } catch (Exception ex) {
                        Logger.getLogger(mainpage.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }.start();// MODIFY HEREEEEEEEEEEEEEEEEEEE

        }
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        // TODO add your handling code here:
        hidePanels();
        panel_selectFriend.setVisible(true);
        DefaultTableModel model = (DefaultTableModel) table_friends5.getModel();
        model.setRowCount(0);
        request ok = clt.sendRequest(new request("getFriendsList", user, user));

        for (int i = 0; i < ok.friends.size(); i++) {

            model.addRow(new Object[]{ok.friends.get(i)});

        }
    }//GEN-LAST:event_jButton11ActionPerformed

    private void btn_GAMESActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_GAMESActionPerformed
        // TODO add your handling code here:

        hidePanels();
        panel_games.setVisible(true);
    }//GEN-LAST:event_btn_GAMESActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        jPanel4.setVisible(false);
        jPanel5.setVisible(true);
    }//GEN-LAST:event_jButton4ActionPerformed

    private void fillEditProfile() {
        request getInfo = new request();
        getInfo.username = user;
        getInfo.command = "getInfo"; // only for editProfile
        getInfo = clt.sendRequest(getInfo);
        edit_firstName.setText(getInfo.firstname);
        edit_lastName.setText(getInfo.lastname);
        edit_country.setText(getInfo.nationality);
        edit_number.setText(getInfo.number);
        edit_bio.setText(getInfo.bio);
        getInfo = new request();
        getInfo.command = "getPreferences"; //get preferences
        getInfo.username = user;
        getInfo = clt.sendRequest(getInfo);
        if (getInfo.firstname.equals("public")) {
            pref_firstName.setSelected(true);
        }
        else if (getInfo.firstname.equals("personal")) {
            pref_firstName.setSelected(false);
        }

        if (getInfo.lastname.equals("public")) {
            pref_lastName.setSelected(true);
        }
        else if (getInfo.lastname.equals("personal")) {
            pref_lastName.setSelected(false);
        }

        if (getInfo.number.equals("public")) {
            pref_number.setSelected(true);
        }
        else if (getInfo.number.equals("personal")) {
            pref_number.setSelected(false);
        }

        if (getInfo.nationality.equals("public")) {
            pref_country.setSelected(true);
        }
        else if (getInfo.nationality.equals("personal")) {
            pref_country.setSelected(false);
        }

        if (getInfo.bio.equals("public")) {
            pref_bio.setSelected(true);
        }
        else if (getInfo.bio.equals("personal")) {
            pref_bio.setSelected(false);
        }

    }

    /**
     * @param args the command line arguments
     */
    private String ipAdd = "192.168.1.73";

    private void fillProfile(String user1) {

        request rqs = clt.sendRequest(new request("getInfo", user1, user1));
        // LEFT TO DO IS: FIX PREFERENCES
        // POSSIBLY USER DOESNT WANT ALL INFO TO BE SELECTED
        // SET AVATAR: MAKE A BUNCH OF PICS TO USE.
        if (user1.equals(user)) // be able to edit
        {
            btn_editProfile.setVisible(true);
            btn_editProfile.setEnabled(true);
            view_username.setText(rqs.username);
            view_bio.setText("Bio: " + rqs.bio);
            view_dof.setText("Date of Birth:" + rqs.dof);
            view_firstName.setText(rqs.firstname);
            view_lastName.setText(rqs.lastname);
            view_number.setText("Number: " + rqs.number);
            view_nationality.setText(rqs.nationality);
            view_gender.setText("Gender: " + rqs.gender);
            view_email.setText("Email:" + rqs.email);
            
            rqs= new request(); rqs.command="getWinsLosses"; rqs.username=user1;
            rqs =clt.sendRequest(rqs);
            double wins=Double.valueOf(rqs.username);
            double losses=Double.valueOf(rqs.user2);
            view_matchesplayed.setText("Matches played: "+ String.valueOf(wins+losses));
            view_wins.setText("Total Wins: "+String.valueOf(wins));
            view_losses.setText("Total Losses: "+String.valueOf(losses));
            view_ratio.setText("Win/Loss Ratio: "+String.valueOf(wins/losses));
        }
        else { // set according to preferences // meaning get prefereces first
            btn_editProfile.setVisible(false);
            btn_editProfile.setEnabled(false);
            request getInfo = new request();
            getInfo.command = "getPreferences"; //get preferences
            getInfo.username = user1;
            getInfo = clt.sendRequest(getInfo);
            view_username.setText(rqs.username);

            if (getInfo.firstname.equals("public")) {
                view_firstName.setText(rqs.firstname);
            }
            else if (getInfo.firstname.equals("personal")) {
                view_firstName.setText("First Name");
            }

            if (getInfo.lastname.equals("public")) {
                view_lastName.setText(rqs.lastname);
            }
            else if (getInfo.lastname.equals("personal")) {
                view_lastName.setText("Last Name");
            }

            if (getInfo.number.equals("public")) {
                view_number.setText("Number: " + rqs.number);
            }
            else if (getInfo.number.equals("personal")) {
                view_number.setText("Number: ");
            }

            if (getInfo.nationality.equals("public")) {
                view_nationality.setText(rqs.nationality);
            }
            else if (getInfo.nationality.equals("personal")) {
                view_nationality.setText("BEATROOT LAND");
            }

            if (getInfo.bio.equals("public")) {
                view_bio.setText("Bio: " + rqs.bio);
            }
            else if (getInfo.bio.equals("personal")) {
                view_bio.setText("");
            }
            
            
            rqs= new request(); rqs.command="getMatchHistory"; rqs.username=user;rqs.user2=user1;
            rqs =clt.sendRequest(rqs);
            double wins=Double.valueOf(rqs.username);
            double losses=Double.valueOf(rqs.user2);
            view_matchesplayed.setText("Matches played against "+user1+": "+ String.valueOf(wins+losses));
            view_wins.setText("Total Wins against: "+String.valueOf(wins));
            view_losses.setText("Total Losses against: "+String.valueOf(losses));
            view_ratio.setText("Win/Loss Ratio against: "+String.valueOf(wins/losses));
            
            
            

        }

        DefaultTableModel model = (DefaultTableModel) table_friends3.getModel();
        model.setRowCount(0);
        request ok = clt.sendRequest(new request("getFriendsList", user1, user1));
        System.out.println(ok.friends.size());
        for (int i = 0; i < ok.friends.size(); i++) {
            System.out.println("itt " + ok.friends.get(i));
            model.addRow(new Object[]{ok.friends.get(i)});

        }

    }

    private void hidePanels() //hides all panels, so call this and set your panel to visible
    {
        panel_community.setVisible(false);
        panel_games.setVisible(false);
        panel_viewprofile.setVisible(false);
        panel_friendrequests.setVisible(false);
        panel_changePassword.setVisible(false);
        panel_editProfile.setVisible(false);
        panel_modeSelection.setVisible(false);
        panel_selectFriend.setVisible(false);
        panel_scores.setVisible(false);
    }

    private client clt = GUI.t; //SET IT TO THE GUI client :thus requires
    private ArrayList<chatclient> ct = new ArrayList<chatclient>();
    private String user = GUI.user;

    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Metal".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(mainpage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(mainpage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(mainpage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(mainpage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new mainpage().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_COMMUNITY;
    private javax.swing.JButton btn_GAMES;
    private javax.swing.JButton btn_PROFILE;
    private javax.swing.JButton btn_changePasswordAction;
    private javax.swing.JButton btn_editProfile;
    private javax.swing.JButton btn_goChangePassword;
    private javax.swing.JButton btn_searchusers;
    private javax.swing.JButton btn_submitChanges;
    private javax.swing.JTextArea chatarea;
    private javax.swing.JTextArea edit_bio;
    private javax.swing.JPasswordField edit_confirmnewPassword;
    private javax.swing.JTextField edit_country;
    private javax.swing.JTextField edit_firstName;
    private javax.swing.JTextField edit_lastName;
    private javax.swing.JPasswordField edit_newPassword;
    private javax.swing.JTextField edit_number;
    private javax.swing.JPasswordField edit_oldPassword;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JInternalFrame jInternalFrame1;
    private javax.swing.JInternalFrame jInternalFrame2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextArea jTextArea2;
    private javax.swing.JTextArea jTextArea3;
    private javax.swing.JTextArea jTextArea4;
    private javax.swing.JLabel label_changePassWarning;
    private javax.swing.JMenuItem menubar_about;
    private javax.swing.JMenuItem menubar_changeUser;
    private javax.swing.JMenuItem menubar_quit;
    private javax.swing.JMenuItem menuitem_chat;
    private javax.swing.JMenuItem menuitem_editProfile;
    private javax.swing.JMenuItem menuitem_friendViewProfile;
    private javax.swing.JMenuItem menuitem_removeFriend;
    private javax.swing.JMenuItem menuitem_viewProfile;
    private javax.swing.JPanel panel_changePassword;
    private javax.swing.JPanel panel_community;
    private javax.swing.JPanel panel_editProfile;
    private javax.swing.JPanel panel_friendrequests;
    private javax.swing.JPanel panel_games;
    private javax.swing.JPanel panel_modeSelection;
    private javax.swing.JPanel panel_scores;
    private javax.swing.JPanel panel_selectFriend;
    private javax.swing.JPanel panel_viewprofile;
    private javax.swing.JMenuItem popitem_sendrequest;
    private javax.swing.JMenuItem popitem_viewprofile;
    private javax.swing.JPopupMenu popmenu_Profile;
    private javax.swing.JPopupMenu popmenu_community;
    private javax.swing.JPopupMenu popmenu_friends;
    private javax.swing.JPopupMenu popmenu_inviteGame;
    private javax.swing.JMenuItem popmenu_searchFriends;
    private javax.swing.JPopupMenu popmenu_user;
    private javax.swing.JMenuItem popmenu_viewFriends;
    private javax.swing.JCheckBox pref_avatar;
    private javax.swing.JCheckBox pref_bio;
    private javax.swing.JCheckBox pref_country;
    private javax.swing.JCheckBox pref_firstName;
    private javax.swing.JCheckBox pref_lastName;
    private javax.swing.JCheckBox pref_number;
    private javax.swing.JScrollPane scrollpane_viewFriends;
    private javax.swing.JScrollPane scrollpane_viewFriends1;
    private javax.swing.JScrollPane scrollpane_viewFriends2;
    private javax.swing.JScrollPane scrollpane_viewFriends3;
    private javax.swing.JScrollPane scrollpane_viewFriends4;
    private javax.swing.JScrollPane scrollpane_viewFriends5;
    private javax.swing.JScrollPane scrollpane_viewFriends6;
    private javax.swing.JTable table_friends;
    private javax.swing.JTable table_friends1;
    private javax.swing.JTable table_friends2;
    private javax.swing.JTable table_friends3;
    private javax.swing.JTable table_friends4;
    private javax.swing.JTable table_friends5;
    private javax.swing.JTable table_friends6;
    private javax.swing.JTextField txt_chatsend;
    private javax.swing.JTextField txt_search;
    private javax.swing.JTextField txt_sendto;
    private javax.swing.JTextArea view_bio;
    private javax.swing.JLabel view_dof;
    private javax.swing.JLabel view_email;
    private javax.swing.JLabel view_firstName;
    private javax.swing.JLabel view_gender;
    private javax.swing.JLabel view_lastName;
    private javax.swing.JLabel view_losses;
    private javax.swing.JLabel view_matchesplayed;
    private javax.swing.JLabel view_nationality;
    private javax.swing.JLabel view_number;
    private javax.swing.JLabel view_ratio;
    private javax.swing.JLabel view_username;
    private javax.swing.JLabel view_wins;
    // End of variables declaration//GEN-END:variables
}
