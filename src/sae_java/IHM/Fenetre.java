/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sae_java.IHM;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.FileNotFoundException;
import javax.swing.*;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import sae_java_24.MonGraphe;

/**
 *
 * @author p2501262
 */
public class Fenetre extends JFrame {
    JPanel pan;
    JPanel panalea;
    JPanel pangenerer;
    JTextField tfalea, tfgenerer;
    JButton butalea, butgenerer;
    public Fenetre() {
        super("MaFenetreGoatesque");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        
        initComponent();
        
        this.setContentPane(pan);
        this.pack();
    }
    
    public void initComponent() {
        pan = new JPanel();
        panalea = new JPanel();
        
        
        
        initPanAlea();
        initPanGenerer();
        initEcouteur();

        pan.add(panalea);
        pan.add(pangenerer);
    }
    
    public void initPanAlea() {
        butalea = new JButton("Generer Graphique");
        tfalea = new JTextField("Saisir un nombre de sommet");
        tfalea.setPreferredSize(new Dimension(170, 25));
        
        tfalea.addFocusListener(new ResetTF());
        panalea.add(tfalea);
        panalea.add(butalea);
        
        panalea.setVisible(true);
    }
    
    public void initPanGenerer() {
        pangenerer = new JPanel();
        
        tfgenerer = new JTextField("Saisir le nom du fichier");
        butgenerer = new JButton("Charger graphe");
        
        tfgenerer.setPreferredSize(new Dimension(170, 25));
        tfgenerer.addFocusListener(new ResetTF());
        
        pangenerer.add(tfgenerer);
        pangenerer.add(butgenerer);
        
        
        
    }
    
    public void initEcouteur() {
        butalea.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int nb = Integer.parseInt(tfalea.getText());
                
                MonGraphe.construireGrapheAleatoire(nb);
            }
        });
        
        butgenerer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nomfichier = "data/" + tfgenerer.getText();
                try {
                    MonGraphe.chargerGraphe(nomfichier).display();
                } catch (FileNotFoundException ex) {
                    System.getLogger(Fenetre.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
                }
            }
        });
        
    }
    
    class ResetTF implements FocusListener{
        public void focusGained(FocusEvent e) {
            if (e.getSource() == tfalea) {
                tfalea.setText("");
            }
            else if (e.getSource() == tfgenerer) {
                tfgenerer.setText("");
            }
            
        }

        @Override
        public void focusLost(FocusEvent e) {
            if (e.getSource() == tfalea && tfalea.getText().equals("")) {
                tfalea.setText("Saisir un nombre de sommet");
            }
            else if (e.getSource() == tfgenerer && tfgenerer.getText().equals("")) {
                tfgenerer.setText("Saisir le nom du fichier");
            }
        }
    }
}
