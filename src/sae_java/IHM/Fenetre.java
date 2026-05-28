/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sae_java.IHM;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.io.FileNotFoundException;
import javax.swing.*;
import javax.swing.border.Border;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.swingViewer.DefaultView;
import org.graphstream.ui.view.Viewer;
import sae_java_24.MonGraphe;

/**
 *
 * @author p2501262
 */
public class Fenetre extends JFrame {

    JPanel pan, panoutil, panGraph, panLabStat, panChangeGraph;
    JTextField tfalea;
    JButton butcroix;
    JLabel labNB1, labNB2, labNB3, labGraph;

    public Fenetre() {
        // Attribution d'un titre à la fenêtre.
        super("MonAppli");
        
        // Ferme la fenêtre lorsqu'on clique sur la croix de la fenêtre.
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Création de tout les composants de la fenêtre (Panneaux + Labels + Buttons etc).
        initComponent();

        // Remplacement du contentPane par pan (panneau principal).
        this.setContentPane(pan);
        
        // Redimension de la fenêtre pour s'adapter à tout les composants du panneau principal.
        this.pack();
        
        // Positionne la fenêtre en "null".
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    public void initComponent() {
        pan = new JPanel();
        panoutil = new JPanel();
        panGraph = new JPanel();
        panLabStat = new JPanel();
        panChangeGraph = new JPanel();

        // Changement de couleur du pan (panneau principal).
        pan.setBackground(Color.decode("#1a2b3c"));

        pan.setLayout(new GridBagLayout());
        GridBagConstraints gcpan = new GridBagConstraints();

        // Initialisation de tout les panneaux pour les positionner.
        initPanOutil();
        initPanGraphe();
        initPanChangeGraph();
        initEcouteur();

        // Espacement réalisé sur tout les composants : Top / Left / Bottom / Right.
        gcpan.insets = new Insets(10, 5, 3, 5);

        // Positionnement du panGraph.
        gcpan.gridx = 1;
        gcpan.gridy = 1;
        gcpan.fill = GridBagConstraints.HORIZONTAL;
        gcpan.fill = GridBagConstraints.VERTICAL;
        pan.add(panGraph, gcpan);

        // Positionnement du panChangeGraph.
        gcpan.gridy = 0;
        gcpan.anchor = GridBagConstraints.WEST;
        pan.add(panChangeGraph, gcpan);
        gcpan.anchor = GridBagConstraints.CENTER;

        // Positionnement du panoutil.
        gcpan.gridx = 0;
        gcpan.gridy = 0;
        gcpan.gridheight = 2;
        pan.add(panoutil, gcpan);
    }

    public void initPanOutil() {
        panoutil.setLayout(new BorderLayout());
        panoutil.setPreferredSize(new Dimension(200, 200));

        // Changement de couleur du fond de panoutil.
        panoutil.setBackground(Color.decode("#1e3a5f"));

        initPanLabStat();

        panoutil.add(panLabStat, BorderLayout.NORTH);
        panoutil.setVisible(true);
    }

    public void initPanGraphe() {
        panGraph.setLayout(new BorderLayout());
        panGraph.setPreferredSize(new Dimension(400, 400));
        panGraph.setVisible(true);

        // Changement de couleur du fond du panGraph.
        panGraph.setBackground(Color.decode("#0d1f2d"));

        // Création d'une bordure blanche sur le panGraph.
        panGraph.setBorder(BorderFactory.createLineBorder(Color.white, 1, true));
    }

    public void initPanLabStat() {
        labNB1 = new JLabel("Maternités : ");
        labNB2 = new JLabel("Blocs opératoire : ");
        labNB3 = new JLabel("Centres de nutrition : ");

        // Changement de couleur du fond du panLabStat.
        panLabStat.setBackground(Color.decode("#122438"));

        // Création d'une bordure blanche sur le panLabStat.
        panLabStat.setBorder(BorderFactory.createLineBorder(Color.white, 1, true));

        panLabStat.setLayout(new BorderLayout());

        // Création d'une bordure invisible en guise de padding et changement de couleur du texte.
        labNB1.setBorder(BorderFactory.createEmptyBorder(3, 7, 3, 0));
        labNB1.setForeground(Color.white);
        labNB2.setBorder(BorderFactory.createEmptyBorder(3, 7, 3, 0));
        labNB2.setForeground(Color.white);
        labNB3.setBorder(BorderFactory.createEmptyBorder(3, 7, 3, 0));
        labNB3.setForeground(Color.white);

        panLabStat.add(labNB3, BorderLayout.NORTH);
        panLabStat.add(labNB2, BorderLayout.CENTER);
        panLabStat.add(labNB1, BorderLayout.SOUTH);

        panLabStat.setVisible(true);
    }

    public void initPanChangeGraph() {
        labGraph = new JLabel("Nom du graphe");
        butcroix = new JButton("+");

        butcroix.setPreferredSize(new Dimension(25, 25));

        // Changement du style du bouton "croix".
        setStyleBouton(butcroix);
        butcroix.setFont(new Font("Arial", Font.BOLD, 18));

        // Changement de la couleur du texte de labGraphe.
        labGraph.setForeground(Color.white);

        // Ajout dans le panChangeGraph.
        panChangeGraph.add(labGraph);
        panChangeGraph.add(butcroix);

        // Changement de la couleur du fond du panChangeGraph.
        panChangeGraph.setBackground(Color.decode("#122438"));

        // Création d'une bordure sur le panChangeGraph.
        panChangeGraph.setBorder(BorderFactory.createLineBorder(Color.white, 1, true));

        // Le panneau est rendu visible.
        panChangeGraph.setVisible(true);
    }

    public void initEcouteur() {
        butcroix.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] options = {"Charger graphe", "Générer Graphe"};
                int choix = JOptionPane.showOptionDialog(
                        null,
                        "Que voulez-vous faire ?",
                        "MonAppli",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        options,
                        options[0]
                );

                // Si le choix est le premier bouton.
                if (choix == 0) {
                    try {
                        ChargerGraphBoutonPressé();
                    } catch (FileNotFoundException ex) {
                        System.getLogger(Fenetre.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
                    }

                    // Si le choix est le deuxième bouton.
                } else if (choix == 1) {
                    GénérerGraphBoutonPressé();
                }
            }
        });

    }

    public void GénérerGraphBoutonPressé() {
        String input = JOptionPane.showInputDialog(null, "Saisir un nombre de sommets :", "Générer Graphe Aléatoire", JOptionPane.QUESTION_MESSAGE);

        // Vérification de la valeur d'entrée.
        if (input != null && !input.isEmpty()) {
            // Conversion du String en int.
            int nb = Integer.parseInt(input);

            Graph g = MonGraphe.construireGrapheAleatoire(nb);

            // null est le nom du graphe qui signifie "graphe alea".
            afficherGraph(g, null);
        }
    }

    public void ChargerGraphBoutonPressé() throws FileNotFoundException {
        JFileChooser chooser = new JFileChooser();
        chooser.showOpenDialog(null);

        // Récupération du chemin absolut.
        String chemin = chooser.getSelectedFile().getAbsolutePath();
        Graph g = MonGraphe.chargerGraphe(chemin);

        // Affichage du graphe avec le nom du graphe.
        afficherGraph(g, chooser.getSelectedFile().getName());
    }

    public void afficherGraph(Graph g, String name) {
        // Suppression de l'ancien graphe.
        panGraph.removeAll();

        // Création des compteurs de statistiques.
        int nbOperatoire = 0, nbMaternite = 0, nbNutrition = 0;

        // Changement de couleur des sommets.
        for (Node node : g) {
            String type = (String) node.getAttribute("type");

            if (type.equals("Maternite")) {
                node.setAttribute("ui.style", "fill-color: red;");
                nbMaternite++;
            } else if (type.equals("Bloc operatoire")) {
                node.setAttribute("ui.style", "fill-color: #7EB8F7 ;");
                nbOperatoire++;
            } else if (type.equals("Centre nutrition")) {
                node.setAttribute("ui.style", "fill-color: green;");
                nbNutrition++;
            }
        }

        // Changement des couleurs du graphe, textes des sommets et arrêtes.
        g.setAttribute("ui.stylesheet", "graph { fill-color: #122438; }"
                + "node { size: 15px; text-color: #ffffff;}"
                + "edge { fill-color: #ffffff; }"
        );
        // Rend les arrêtes plus nettes.
        g.setAttribute("ui.antialias");

        Viewer viewer = new Viewer(g, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
        viewer.enableAutoLayout();

        DefaultView view = (DefaultView) viewer.addDefaultView(false);
        view.setPreferredSize(new Dimension(400, 400));

        // Changement du nom du Graphe.
        if (name != null) {
            labGraph.setText(name);
        } else {
            labGraph.setText("Graphe Aléa");
        }

        // Actualisation des statistiques du graphe.
        labNB1.setText("Maternités : " + nbMaternite);
        labNB2.setText("Blocs opératoire : " + nbNutrition);
        labNB3.setText("Centres de nutrition : " + nbOperatoire);

        // Ajout du viewer au panneau.
        panGraph.add(view);
        panGraph.revalidate(); // Rafraichissement de la position des composants du panel.
        panGraph.repaint(); // Réactualisation du panGraph.

    }

    public void setStyleBouton(JButton but) {
        // Changement de couleur du fond du bouton passé en paramètre.
        but.setBackground(Color.decode("#2e86c1"));
        
        // Changement de couleur du texte du bouton passé en paramètre.
        but.setForeground(Color.white);
        
        // Enlève le margin du bouton.
        but.setMargin(new Insets(2, 0, 0, 0));
        
        // Enlève le contour de focus qui apparaît autour du bouton quand on clique dessus.
        but.setFocusPainted(false);
    }

}
