/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sae_java.IHM;

import javax.swing.*;

/**
 *
 * @author p2501262
 */
public class Fenetre extends JFrame {
    JPanel pan;
    JButton but1;
    JLabel lab1;
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
        lab1 = new JLabel("Voici un test");
        but1 = new JButton("Le Bouton magique");
        
        
        pan.add(lab1);
        pan.add(but1);
    }
}
