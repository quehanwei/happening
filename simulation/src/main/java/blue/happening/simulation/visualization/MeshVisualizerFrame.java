package blue.happening.simulation.visualization;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;

import blue.happening.simulation.graph.NetworkGraph;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;


public class MeshVisualizerFrame<V, E> extends JFrame {

    private final NetworkGraph<V, E> graph;
    private final MeshVisualizationViewer visualizerPanel;

    public MeshVisualizerFrame(NetworkGraph<V, E> graph) {
        super("Graph " + graph.getName());
        this.graph = graph;

        // add blue.happening.bla.visualization panel
        Dimension preferredSize = new Dimension(10000, 10000);
        this.visualizerPanel = new MeshVisualizationViewer(graph, preferredSize);
        getContentPane().add(new GraphZoomScrollPane(visualizerPanel));
        // new JComponentRepaintAction(blue.happening.bla.graph, "test", visualizerPanel, 0.01);
        new TimedJComponenetRepainter(visualizerPanel, 15);

        // add buttons panel
        ButtonsPanel<V, E> buttonsPanel = new ButtonsPanel<V, E>(visualizerPanel);
        add(buttonsPanel, BorderLayout.SOUTH);
        add(new DevicePanel(), BorderLayout.EAST);

        // pack and view
        pack();
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public NetworkGraph<V, E> getGraph() {
        return graph;
    }

    public MeshVisualizationViewer getVisualizerPanel() {
        return visualizerPanel;
    }

}