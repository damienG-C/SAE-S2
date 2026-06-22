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
import Algo.MonGraphe;
import java.awt.Dimension;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

/**
 * FenetreDetails est une fenetre secondaire affichant les distances depuis un
 * sommet donne vers tous les autres dispensaires du graphe.
 * <p>
 * Elle est ouverte par {@link Fenetre} lors d'un clic sur un sommet. Pour
 * chaque autre sommet (regroupe par categorie : maternites, centres de
 * nutrition, blocs operatoires), elle calcule la distance via le plus court
 * chemin ({@link MonGraphe#calculerItinerairePlusCourt}) et l'affiche dans un
 * tableau de labels organise en GridBagLayout.
 *
 * @author p2501775
 */
public class FenetreDetails extends JFrame {

    /**
     * Labels d'en-tete et titres de sections (distances et identifiants).
     */
    JLabel labDistanceM, labDistanceN, labDistanceO, labIdCategorie, labId1, labId2, labId3, labDistanceCategorie, labDistanceKm1, labDistanceKm2, labDistanceKm3;
    /**
     * Panneau principal de la fenetre.
     */
    JPanel panneau;
    /**
     * Panneau de scrolling pour le panneau principal de la fenetre.
     */
    JScrollPane scrollPanePanneau;
    /**
     * Sommet de reference dont on calcule les distances vers les autres.
     */
    Node n;

    /**
     * Construit la fenetre de details pour un sommet donne.
     * <p>
     * Initialise les composants, calcule et affiche les distances vers les
     * autres dispensaires, ajuste la taille de la fenetre a son contenu, la
     * centre et l'affiche.
     *
     * @param n le sommet de reference
     * @param graphActuel le graphe contenant tous les sommets
     */
    public FenetreDetails(Node n, Graph graphActuel) {
        // Attribution d'un titre à la fenêtre.
        super("Détails");
        this.n = n;

        // Création de tout les composants de la fenêtre (Panneaux + Labels + Buttons etc).
        init(graphActuel);

        // Remplacement du contentPane par pan (panneau principal).
        this.setContentPane(scrollPanePanneau);

        // Redimension de la fenêtre pour s'adapter à tout les composants du panneau principal.
        this.pack();

        // Positionne la fenêtre en "null".
        this.setLocationRelativeTo(null);
        this.setVisible(true);

    }

    /**
     * Construit et remplit le panneau de details.
     * <p>
     * Cree les labels d'en-tete, puis pour chacune des trois categories
     * (maternites, centres de nutrition, blocs operatoires) genere une ligne
     * par sommet contenant son identifiant et la distance (plus court chemin)
     * depuis le sommet de reference {@link #n}. Le tout est dispose en
     * GridBagLayout.
     *
     * @param graphActuel le graphe contenant tous les sommets
     */
    public void init(Graph graphActuel) {

        panneau = new JPanel();
        scrollPanePanneau = new JScrollPane(panneau);
        panneau.setLayout(new GridBagLayout());
        panneau.setBackground(Color.decode("#122438"));
        scrollPanePanneau.setPreferredSize(new Dimension(400, 500));

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

        // Recuperation des labels (id + distance) pour chaque categorie de dispensaire.
        List<JLabel> lstMaternite = creerLabelMaternite(graphActuel);
        List<JLabel> lstNutrition = creerLabelNutrition(graphActuel);
        List<JLabel> lstOperatoire = creerLabelOperatoire(graphActuel);

        gcpan.gridx = 1;
        gcpan.gridy = 1;
        gcpan.insets = new Insets(0, 5, 0, 0);

        // Affichage des maternites : la liste alterne label id puis label distance (pas de 2).
        for (int i = 0; i < lstMaternite.size(); i = i + 2) {
            JLabel jLabel = lstMaternite.get(i);
            JLabel jLabelsuiv = lstMaternite.get(i + 1);
            panneau.add(jLabel, gcpan);
            jLabel.setForeground(Color.white);
            gcpan.gridx++;

            int distanceNode = 0;

            // Calcul du plus court chemin entre le sommet de reference et le sommet courant.
            Node nodeChercher = graphActuel.getNode(jLabel.getText());
            List lstReturnPC = MonGraphe.calculerItinerairePlusCourt(graphActuel, "" + n, "" + nodeChercher);
            Path chemin = (Path) lstReturnPC.get(0);

            // calcule de la Distance
            for (Edge edge : chemin.getEachEdge()) {
                distanceNode = distanceNode + (int) edge.getAttribute("distance");
            }

            // préparation du label qui affiche la distance
            jLabelsuiv.setText("" + distanceNode);
            jLabelsuiv.setForeground(Color.white);

            panneau.add(jLabelsuiv, gcpan);

            // Incrémentation de la position des labels
            gcpan.gridx--;
            gcpan.gridy++;
        }

        gcpan.gridx = 0;
        gcpan.gridy++;
        gcpan.insets = new Insets(10, 0, 0, 0);
        panneau.add(labDistanceN, gcpan);

        gcpan.gridx = 1;
        int cptTempPositionnement = 0;
        // Affichage des centres de nutrition (meme principe d'alternance id/distance).
        for (int i = 0; i < lstNutrition.size(); i = i + 2) {
            // Marge superieure plus grande sur la premiere ligne de la categorie.
            if (cptTempPositionnement == 0) {
                gcpan.insets = new Insets(10, 5, 0, 0);
            } else {
                gcpan.insets = new Insets(0, 5, 0, 0);
            }
            cptTempPositionnement++;

            JLabel jLabel = lstNutrition.get(i);
            JLabel jLabelsuiv = lstNutrition.get(i + 1);
            panneau.add(jLabel, gcpan);
            jLabel.setForeground(Color.white);
            gcpan.gridx++;

            int distanceNode = 0;

            // Calcul du plus court chemin entre le sommet de reference et le sommet courant.
            Node nodeChercher = graphActuel.getNode(jLabel.getText());
            List lstReturnPC = MonGraphe.calculerItinerairePlusCourt(graphActuel, "" + n, "" + nodeChercher);
            Path chemin = (Path) lstReturnPC.get(0);

            // calcule de la Distance
            for (Edge edge : chemin.getEachEdge()) {
                distanceNode = distanceNode + (int) edge.getAttribute("distance");
            }

            // préparation du label qui affiche la distance
            jLabelsuiv.setText("" + distanceNode);
            jLabelsuiv.setForeground(Color.white);

            panneau.add(jLabelsuiv, gcpan);

            // Incrémentation de la position des labels
            gcpan.gridx--;
            gcpan.gridy++;
        }
        cptTempPositionnement = 0;

        gcpan.gridx = 0;
        gcpan.gridy++;
        gcpan.insets = new Insets(10, 0, 0, 0);
        panneau.add(labDistanceO, gcpan);

        gcpan.gridx = 1;
        // Affichage des blocs operatoires (meme principe d'alternance id/distance).
        for (int i = 0; i < lstOperatoire.size(); i = i + 2) {
            // Marge superieure plus grande sur la premiere ligne de la categorie.
            if (cptTempPositionnement == 0) {
                gcpan.insets = new Insets(10, 5, 0, 0);
            } else {
                gcpan.insets = new Insets(0, 5, 0, 0);
            }
            cptTempPositionnement++;

            JLabel jLabel = lstOperatoire.get(i);
            JLabel jLabelsuiv = lstOperatoire.get(i + 1);
            panneau.add(jLabel, gcpan);
            jLabel.setForeground(Color.white);
            gcpan.gridx++;

            int distanceNode = 0;

            // Calcul du plus court chemin entre le sommet de reference et le sommet courant.
            Node nodeChercher = graphActuel.getNode(jLabel.getText());
            List lstReturnPC = MonGraphe.calculerItinerairePlusCourt(graphActuel, "" + n, "" + nodeChercher);
            Path chemin = (Path) lstReturnPC.get(0);

            // calcule de la Distance
            for (Edge edge : chemin.getEachEdge()) {
                distanceNode = distanceNode + (int) edge.getAttribute("distance");
            }

            // préparation du label qui affiche la distance
            jLabelsuiv.setText("" + distanceNode);
            jLabelsuiv.setForeground(Color.white);

            panneau.add(jLabelsuiv, gcpan);

            // Incrémentation de la position des labels
            gcpan.gridx--;
            gcpan.gridy++;
        }
    }

    /**
     * Initialise les labels d'en-tete et de titres de sections.
     * <p>
     * Note : {@code labDistanceKm1/2/3} et {@code labId1/2/3} sont crees mais
     * ne sont jamais ajoutes au panneau (code mort, voir remarque en
     * conversation).
     */
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

    /**
     * Cree la liste des labels des maternites (hors sommet de reference).
     * <p>
     * Pour chaque maternite differente de {@link #n}, ajoute deux labels : un
     * pour l'identifiant et un (provisoire) destine a recevoir la distance.
     *
     * @param graph le graphe a parcourir
     * @return une liste de labels alternant id / distance pour chaque maternite
     */
    private List<JLabel> creerLabelMaternite(Graph graph) {
        List<JLabel> lstLab = new ArrayList();
        for (Node node : graph) {
            String type = (String) node.getAttribute("type");
            if (type.equals("Maternite") && node != n) {
                JLabel tempId = new JLabel("" + node);
                JLabel tempDistance = new JLabel("" + node);
                lstLab.add(tempId);
                lstLab.add(tempDistance);
            }

        }
        return lstLab;
    }

    /**
     * Cree la liste des labels des centres de nutrition (hors sommet de
     * reference).
     * <p>
     * Pour chaque centre de nutrition different de {@link #n}, ajoute deux
     * labels : un pour l'identifiant et un (provisoire) destine a recevoir la
     * distance.
     *
     * @param graph le graphe a parcourir
     * @return une liste de labels alternant id / distance pour chaque centre de
     * nutrition
     */
    private List<JLabel> creerLabelNutrition(Graph graph) {
        List<JLabel> lstLab = new ArrayList();
        for (Node node : graph) {
            String type = (String) node.getAttribute("type");
            if (type.equals("Centre nutrition") && node != n) {
                JLabel tempId = new JLabel("" + node);
                JLabel tempDistance = new JLabel("" + node);
                lstLab.add(tempId);
                lstLab.add(tempDistance);
            }

        }
        return lstLab;
    }

    /**
     * Cree la liste des labels des blocs operatoires (hors sommet de
     * reference).
     * <p>
     * Pour chaque bloc operatoire different de {@link #n}, ajoute deux labels :
     * un pour l'identifiant et un (provisoire) destine a recevoir la distance.
     *
     * @param graph le graphe a parcourir
     * @return une liste de labels alternant id / distance pour chaque bloc
     * operatoire
     */
    private List<JLabel> creerLabelOperatoire(Graph graph) {
        List<JLabel> lstLab = new ArrayList();
        for (Node node : graph) {
            String type = (String) node.getAttribute("type");
            if (type.equals("Bloc operatoire") && node != n) {
                JLabel tempId = new JLabel("" + node);
                JLabel tempDistance = new JLabel("" + node);
                lstLab.add(tempId);
                lstLab.add(tempDistance);
            }

        }
        return lstLab;
    }

}
