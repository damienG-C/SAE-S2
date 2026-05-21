/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sae_java.IHM;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.io.FileNotFoundException;
import javax.swing.*;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.swingViewer.DefaultView;
import org.graphstream.ui.view.Viewer;
import sae_java_24.MonGraphe;

/**
 *
 * @author p2501262
 */
public class Fenetre extends JFrame {

    JPanel pan, panalea, pangenerer, panGraph;
    JTextField tfalea;
    JButton butalea, butgenerer;

    public Fenetre() {
        super("MonAppli");
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
        pangenerer = new JPanel();
        panGraph = new JPanel();
        butgenerer = new JButton("Charger graphe");

        pan.setBackground(Color.black);
        
        pan.setLayout(new GridBagLayout());
        GridBagConstraints gcpan = new GridBagConstraints();

        initPanAlea();
        initPanGenerer();
        initPanGraphe();
        initEcouteur();

        gcpan.gridx = 0;
        gcpan.gridy = 0;
        pan.add(panalea, gcpan);

        gcpan.gridx = 1;
        gcpan.gridheight = 2;
        pan.add(panGraph, gcpan);
        gcpan.gridheight = 1;

        gcpan.gridx = 0;
        gcpan.gridy = 1;
        gcpan.gridwidth = 2;
        gcpan.anchor = GridBagConstraints.NORTHWEST;
        pan.add(pangenerer, gcpan);
    }

    public void initPanAlea() {
        butalea = new JButton("Générer Graphe");
        tfalea = new JTextField("Saisir un nombre de sommet");

        butalea.setPreferredSize(new Dimension(125, 25));

        tfalea.setPreferredSize(new Dimension(170, 25));
        tfalea.addFocusListener(new ResetTF());
        panalea.add(butalea);
        panalea.add(tfalea);

        panalea.setVisible(true);
    }

    public void initPanGenerer() {
        butgenerer.setPreferredSize(new Dimension(125, 25));

        pangenerer.add(butgenerer);

    }

    public void initPanGraphe() {
        panGraph.setVisible(true);
        panGraph.setPreferredSize(new Dimension(400, 400));
    }

    public void initEcouteur() {
        butalea.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int nb = Integer.parseInt(tfalea.getText());

                Graph g = MonGraphe.construireGrapheAleatoire(nb);
                afficherGraph(g);
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
                    afficherGraph(g);
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

    public void afficherGraph(Graph g) {
        panGraph.removeAll();
        Viewer viewer = new Viewer(g, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
        viewer.enableAutoLayout();

        DefaultView view = (DefaultView) viewer.addDefaultView(false);
        view.setPreferredSize(new Dimension(300, 400));

        panGraph.add(view);
        panGraph.revalidate(); // rafraichissement de la position des composants du panel.
        panGraph.repaint(); // réactualisation du panGraph.
    }

}
