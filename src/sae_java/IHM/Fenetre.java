/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sae_java.IHM;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
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
    JButton butalea, butgenerer;
    JLabel labNB1, labNB2, labNB3, labGraph;

    public Fenetre() {
        super("MonAppli");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        initComponent();

        this.setContentPane(pan);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    public void initComponent() {
        pan = new JPanel();
        panoutil = new JPanel();
        panGraph = new JPanel();
        panLabStat = new JPanel();
        panChangeGraph = new JPanel();

        pan.setBackground(Color.black);

        pan.setLayout(new GridBagLayout());
        GridBagConstraints gcpan = new GridBagConstraints();

        initPanOutil();
        initPanGraphe();
        initPanChangeGraph();
        initEcouteur();

        gcpan.gridx = 1;
        gcpan.gridy = 1;
        pan.add(panGraph, gcpan);

        gcpan.gridy = 0;
        gcpan.anchor = GridBagConstraints.WEST;
        pan.add(panChangeGraph, gcpan);

        gcpan.gridx = 0;
        gcpan.gridy = 0;
        gcpan.gridheight = 2;
        pan.add(panoutil, gcpan);
    }

    public void initPanOutil() {

        tfalea = new JTextField("Saisir un nombre de sommet");

        tfalea.setPreferredSize(new Dimension(170, 25));
        tfalea.addFocusListener(new ResetTF());

        panoutil.setLayout(new GridBagLayout());
        GridBagConstraints gcpo = new GridBagConstraints();

        initPanLabStat();

        gcpo.gridx = 0;
        gcpo.gridy = 0;
        panoutil.add(panLabStat, gcpo);
        panoutil.setVisible(true);
    }

    public void initPanGraphe() {
        panGraph.setVisible(true);
        panGraph.setPreferredSize(new Dimension(400, 400));
        panGraph.setBackground(Color.decode("#1e1e1e"));
    }

    public void initPanLabStat() {
        labNB1 = new JLabel("Maternités : ");
        labNB2 = new JLabel("Blocs opératoire : ");
        labNB3 = new JLabel("Centres de nutrition : ");

        panLabStat.setLayout(new BorderLayout());

        panLabStat.add(labNB1, BorderLayout.NORTH);
        panLabStat.add(labNB2, BorderLayout.CENTER);
        panLabStat.add(labNB3, BorderLayout.SOUTH);

        panLabStat.setVisible(true);
    }

    public void initPanChangeGraph() {
        labGraph = new JLabel("Nom du graphe");
        butalea = new JButton("Générer Graphe");
        butgenerer = new JButton("Charger graphe");
        butalea.setPreferredSize(new Dimension(125, 25));
        butgenerer.setPreferredSize(new Dimension(125, 25));

        panChangeGraph.add(labGraph);
        panChangeGraph.add(butgenerer);
        panChangeGraph.add(butalea);

        panChangeGraph.setVisible(true);
    }

    public void initEcouteur() {

        butalea.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String input = JOptionPane.showInputDialog(null, "Saisir un nombre de sommets :", "Générer Graphe Aléatoire", JOptionPane.QUESTION_MESSAGE);
                if (input != null && !input.isEmpty()) {
                    int nb = Integer.parseInt(input);

                    Graph g = MonGraphe.construireGrapheAleatoire(nb);
                    afficherGraph(g, null);
                }

            }
        });

        butgenerer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.showOpenDialog(null);
                String chemin = chooser.getSelectedFile().getAbsolutePath();
                try {
                    Graph g = MonGraphe.chargerGraphe(chemin);
                    afficherGraph(g, chooser.getSelectedFile().getName());
                } catch (FileNotFoundException ex) {
                    System.getLogger(Fenetre.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
                }
            }
        });

    }

    class ResetTF implements FocusListener {

        public void focusGained(FocusEvent e) {
            if (e.getSource() == tfalea) {
                tfalea.setText("");
            }

        }

        @Override
        public void focusLost(FocusEvent e) {
            if (e.getSource() == tfalea && tfalea.getText().equals("")) {
                tfalea.setText("Saisir un nombre de sommet");
            }
        }
    }

    public void afficherGraph(Graph g, String name) {
        panGraph.removeAll();
        int nbOperatoire = 0, nbMaternite = 0, nbNutrition = 0;

        for (Node node : g) {
            String type = (String) node.getAttribute("type");

            if (type.equals("Maternite")) {
                node.setAttribute("ui.style", "fill-color: red;");
                nbMaternite++;
            } else if (type.equals("Bloc operatoire")) {
                node.setAttribute("ui.style", "fill-color: blue;");
                nbOperatoire++;
            } else if (type.equals("Centre nutrition")) {
                node.setAttribute("ui.style", "fill-color: green;");
                nbNutrition++;
            }
        }

        g.setAttribute("ui.stylesheet", "graph { fill-color: #1e1e1e; }"
                + "node { size: 15px; text-color: white; }"
                + "edge { fill-color: #ffffff; }"
        );

        Viewer viewer = new Viewer(g, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
        viewer.enableAutoLayout();

        DefaultView view = (DefaultView) viewer.addDefaultView(false);
        view.setPreferredSize(new Dimension(400, 400));

        if (name != null) {
            labGraph.setText(name);
        } else {
            labGraph.setText("Graphe Aléa");
        }

        labNB1.setText("Maternités : " + nbMaternite);
        labNB2.setText("Blocs opératoire : " + nbNutrition);
        labNB3.setText("Centres de nutrition : " + nbOperatoire);

        panGraph.add(view);
        panGraph.revalidate(); // rafraichissement de la position des composants du panel.
        panGraph.repaint(); // réactualisation du panGraph.

    }

}
