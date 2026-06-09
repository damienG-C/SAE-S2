/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sae_java.IHM;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author p2501775
 */
public class FenetreDetails extends JFrame {
    JLabel distanceM, distanceN, distanceO, id1, id2, id3, distanceKm1, distanceKm2, distanceKm3;
    JPanel panneau;
    
    
    public FenetreDetails(float x, float y){
        // Attribution d'un titre à la fenêtre.
        super("Détails");


        // Création de tout les composants de la fenêtre (Panneaux + Labels + Buttons etc).
        init(x, y);

        // Remplacement du contentPane par pan (panneau principal).
        this.setContentPane(panneau);

        // Redimension de la fenêtre pour s'adapter à tout les composants du panneau principal.
        this.pack();

        // Positionne la fenêtre en "null".
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        
    }
    public void init(float x, float y){
        panneau = new JPanel();
        panneau.setLayout(new GridBagLayout());
        GridBagConstraints gcpan = new GridBagConstraints();
        initLabel();
        gcpan.gridx = 0;
        gcpan.gridy = 0;
        gcpan.fill = GridBagConstraints.HORIZONTAL;
        gcpan.fill = GridBagConstraints.VERTICAL;
        panneau.add(distanceM, gcpan);
        

        gcpan.gridx = 1;
        
        panneau.add(id1, gcpan);
        
        gcpan.gridx = 2;
        panneau.add(distanceKm1, gcpan);
        
        gcpan.gridy = 1;
        panneau.add(distanceKm2, gcpan);
        
        gcpan.gridx = 1;
        panneau.add(id2, gcpan);
        
        gcpan.gridx = 0;
        panneau.add(distanceO, gcpan);
        
        gcpan.gridy = 11;
        panneau.add(distanceN, gcpan);
        
        gcpan.gridx = 1;
        panneau.add(id3, gcpan);
        
        gcpan.gridx = 2;
        panneau.add(distanceKm3, gcpan);

        
    }
    
    private void initLabel(){
        distanceKm1= new JLabel("Distance (km)");
        distanceKm2= new JLabel("Distance (km)");
        distanceKm3= new JLabel("Distance (km)");
        distanceO = new JLabel("Distance aux blocs opératoires");
        distanceM = new JLabel("Distance aux maternités");
        distanceN = new JLabel("Distances aux centres de nutrition");
        id1 = new JLabel("id ");
        id2 = new JLabel("id ");
        id3 = new JLabel("id ");
    }
}
