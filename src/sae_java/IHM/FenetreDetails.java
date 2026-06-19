/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sae_java.IHM;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.graphstream.graph.*;

/**
 *
 * @author p2501775
 */
public class FenetreDetails extends JFrame {

    JLabel labDistanceM, labDistanceN, labDistanceO, labIdCategorie, labId1, labId2, labId3, labDistanceCategorie, labDistanceKm1, labDistanceKm2, labDistanceKm3;
    JPanel panneau;
    Node n;

    public FenetreDetails(Node n, Graph graphActuel) {
        // Attribution d'un titre à la fenêtre.
        super("Détails");
        this.n = n;

        // Création de tout les composants de la fenêtre (Panneaux + Labels + Buttons etc).
        init(graphActuel);

        // Remplacement du contentPane par pan (panneau principal).
        this.setContentPane(panneau);

        // Redimension de la fenêtre pour s'adapter à tout les composants du panneau principal.
        this.pack();

        // Positionne la fenêtre en "null".
        this.setLocationRelativeTo(null);
        this.setVisible(true);

    }

    public void init(Graph graphActuel) {
        panneau = new JPanel();
        panneau.setLayout(new GridBagLayout());
        panneau.setBackground(Color.decode("#122438"));

        initLabel();

        labDistanceKm1.setForeground(Color.white);
        labDistanceKm2.setForeground(Color.white);
        labDistanceKm3.setForeground(Color.white);
        labDistanceM.setForeground(Color.white);
        labDistanceN.setForeground(Color.white);
        labDistanceO.setForeground(Color.white);
        labId1.setForeground(Color.white);
        labId2.setForeground(Color.white);
        labId3.setForeground(Color.white);
        labDistanceCategorie.setForeground(Color.white);
        labIdCategorie.setForeground(Color.white);

        GridBagConstraints gcpan = new GridBagConstraints();

        gcpan.gridx = 0;
        gcpan.gridy = 1;
        gcpan.fill = GridBagConstraints.HORIZONTAL;
        gcpan.anchor = GridBagConstraints.WEST;
        gcpan.insets = new Insets(3, 2, 3, 2);
        panneau.add(labDistanceM, gcpan);

        gcpan.insets = new Insets(3, 4, 3, 6);

        gcpan.gridx = 1;
        gcpan.gridy = 0;
        panneau.add(labIdCategorie, gcpan);

        gcpan.gridx = 2;
        panneau.add(labDistanceCategorie, gcpan);

        List<JLabel> lstMaternite = creerLabelMaternite(graphActuel);
        List<JLabel> lstNutrition = creerLabelNutrition(graphActuel);
        List<JLabel> lstOperatoire = creerLabelOperatoire(graphActuel);

        gcpan.gridx = 1;
        gcpan.gridy = 1;
        gcpan.insets = new Insets(0, 5, 0, 0);
        for (JLabel jLabel : lstMaternite) {
            panneau.add(jLabel, gcpan);
            jLabel.setForeground(Color.white);
            gcpan.gridy++;
        }

        gcpan.gridx = 0;
        gcpan.gridy++;
        gcpan.insets = new Insets(10, 0, 0, 0);
        panneau.add(labDistanceN, gcpan);

        gcpan.gridx = 1;
        int cptTempPositionnement = 0;
        for (JLabel jLabel : lstNutrition) {
            if (cptTempPositionnement == 0) {
                gcpan.insets = new Insets(10, 5, 0, 0);
            }
            else {
                gcpan.insets = new Insets(0, 5, 0, 0);
            }
            cptTempPositionnement++;
            panneau.add(jLabel, gcpan);
            jLabel.setForeground(Color.white);
            gcpan.gridy++;
        }
        cptTempPositionnement = 0;

        gcpan.gridx = 0;
        gcpan.gridy++;
        gcpan.insets = new Insets(10, 0, 0, 0);
        panneau.add(labDistanceO, gcpan);

        gcpan.gridx = 1;
        for (JLabel jLabel : lstOperatoire) {
            if (cptTempPositionnement == 0) {
                gcpan.insets = new Insets(10, 5, 0, 0);
            }
            else {
                gcpan.insets = new Insets(0, 5, 0, 0);
            }
            cptTempPositionnement++;
            panneau.add(jLabel, gcpan);
            jLabel.setForeground(Color.white);
            gcpan.gridy++;
        }
    }

    private void initLabel() {
        labDistanceKm1 = new JLabel("Distance (km)");
        labDistanceKm2 = new JLabel("Distance (km)");
        labDistanceKm3 = new JLabel("Distance (km)");
        labDistanceO = new JLabel("Distance aux blocs opératoires");
        labDistanceM = new JLabel("Distance aux maternités");
        labDistanceN = new JLabel("Distances aux centres de nutrition");
        labId1 = new JLabel("id ");
        labId2 = new JLabel("id ");
        labId3 = new JLabel("id ");
        labIdCategorie = new JLabel("Id");
        labDistanceCategorie = new JLabel("Distances");
    }

    private List<JLabel> creerLabelMaternite(Graph graph) {
        List<JLabel> lstLab = new ArrayList();
        for (Node node : graph) {
            String type = (String) node.getAttribute("type");
            if (type.equals("Maternite") && node != n) {
                JLabel temp = new JLabel("" + node);
                lstLab.add(temp);
            }

        }
        return lstLab;
    }

    private List<JLabel> creerLabelNutrition(Graph graph) {
        List<JLabel> lstLab = new ArrayList();
        for (Node node : graph) {
            String type = (String) node.getAttribute("type");
            if (type.equals("Centre nutrition") && node != n) {
                JLabel temp = new JLabel("" + node);
                lstLab.add(temp);
            }

        }
        return lstLab;
    }

    private List<JLabel> creerLabelOperatoire(Graph graph) {
        List<JLabel> lstLab = new ArrayList();
        for (Node node : graph) {
            String type = (String) node.getAttribute("type");
            if (type.equals("Bloc operatoire") && node != n) {
                JLabel temp = new JLabel("" + node);
                lstLab.add(temp);
            }

        }
        return lstLab;
    }

}
