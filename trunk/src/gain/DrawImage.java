/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gain;

//import com.sun.corba.se.impl.util.Utility;
import java.awt.*;
import javax.swing.*;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.metal.MetalFileChooserUI;
import prefuse.Constants;
import prefuse.data.Node;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.assignment.ColorAction;
import prefuse.action.layout.graph.ForceDirectedLayout;
import prefuse.activity.Activity;
import prefuse.controls.DragControl;
import prefuse.controls.PanControl;
import prefuse.controls.ZoomControl;
//import prefuse.data.Graph;
//import prefuse.data.io.GraphMLReader;
//import prefuse.render.DefaultRendererFactory;
import prefuse.render.LabelRenderer;
//import prefuse.util.ColorLib;
import prefuse.visual.VisualItem;
import prefuse.controls.FocusControl;
import prefuse.controls.NeighborHighlightControl;
import prefuse.controls.WheelZoomControl;
import prefuse.controls.ZoomToFitControl;
import prefuse.data.Table;
import prefuse.data.Tuple;
import prefuse.data.event.TupleSetListener;
import prefuse.data.query.SearchQueryBinding;
import prefuse.data.tuple.TupleSet;
import prefuse.render.DefaultRendererFactory;
import prefuse.util.PrefuseLib;
import prefuse.util.force.ForceSimulator;
import prefuse.util.ui.JForcePanel;
import prefuse.util.ui.UILib;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualGraph;
import prefuse.controls.ControlAdapter;
import prefuse.data.Graph;
import prefuse.data.io.GraphMLReader;
import prefuse.data.search.PrefixSearchTupleSet;
import prefuse.data.search.SearchTupleSet;
import prefuse.data.tuple.DefaultTupleSet;
import prefuse.render.EdgeRenderer;
import prefuse.util.ColorLib;
import prefuse.util.FontLib;
import prefuse.util.ui.JFastLabel;
import prefuse.util.ui.JSearchPanel;

/**
 *
 * @author DTian
 */
public class DrawImage {

    private EPSDisplay display;
    private String imageName;

    public DrawImage() {
        display = new EPSDisplay();
        this.imageName = new String();
    }

    public DrawImage(String picName) {
        display = new EPSDisplay();
        this.imageName = picName;
    }

    public Display getDisplay() {
        return display;
    }

    public String getImageName() {
        return imageName;
    }

    /**
     * to draw the picture based on the data file
     * @param datafile  the source data
     * @param label     the name
     * @return      a JComponent with the image on it.
     */
    public JComponent drawPic(String datafile, final String label) {

        Graph g = null;
        try {
            g = new GraphMLReader().readGraph(datafile);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        return drawPic(g, label);
    }

     /**
     * to draw the picture based on the data file
     * @param g       the graph
     * @param label   the name
     * @return      a JComponent with the image on it.
     */
    private JComponent drawPic(Graph g, final String label) {

        // -- local variables --
        final String graph = "graph";
        final String nodes = "graph.nodes";
        final String edges = "graph.edges";

        // -- create a new, empty visualization for our data --
        final Visualization vis = new Visualization();
        final VisualGraph vg = vis.addGraph(graph, g);
        vis.setInteractive(edges, null, false);
        DefaultRendererFactory rf = new DefaultRendererFactory();
        rf.setDefaultEdgeRenderer(new EdgeRenderer(Constants.EDGE_TYPE_CURVE));
        rf.add("INGROUP('data')", new LabelRenderer("label"));
        vis.setRendererFactory(rf);


        // -- set up the focus group to show --
        final TupleSet focusGroup = vis.getGroup(Visualization.FOCUS_ITEMS);
        focusGroup.addTupleSetListener(new TupleSetListener() {

            public void tupleSetChanged(TupleSet ts, Tuple[] add, Tuple[] rem) {
                for (int i = 0; i < rem.length; ++i) {
                    ((VisualItem) rem[i]).setFixed(false);
                }
                for (int i = 0; i < add.length; ++i) {
                    ((VisualItem) add[i]).setFixed(false);
                    ((VisualItem) add[i]).setFixed(true);
                }
                vis.run("draw");
            }
        });

        // -- set up the renderers --
        LabelRenderer tr = new LabelRenderer(label);
        tr.setRoundedCorner(8, 8);
        vis.setRendererFactory(new DefaultRendererFactory(tr));

        // -- set up the actions ----------------------------------------------
        ActionList draw = new ActionList(Activity.INFINITY);
        draw.add(new ColorAction(nodes, VisualItem.FILLCOLOR, ColorLib.rgb(200, 200, 255)));
        draw.add(new ColorAction(nodes, VisualItem.STROKECOLOR, 0));
        draw.add(new ColorAction(nodes, VisualItem.TEXTCOLOR, ColorLib.rgb(0, 0, 0)));
        draw.add(new ColorAction(edges, VisualItem.FILLCOLOR, ColorLib.gray(200)));
        draw.add(new ColorAction(edges, VisualItem.STROKECOLOR, ColorLib.gray(200)));

        // -- set up the color action for focus group node --
        ColorAction fill = new ColorAction(nodes,
                VisualItem.FILLCOLOR, ColorLib.rgb(200, 200, 255));
        fill.add("_fixed", ColorLib.rgb(255, 100, 100));
        fill.add("_highlight", ColorLib.rgb(255, 200, 125));

        //----------------------------------------------------
//        -- set up the color action for focus group edge --
        ColorAction fillEdge = new ColorAction(edges,
                VisualItem.STROKECOLOR, ColorLib.gray(200));
        fillEdge.add("_highlight", ColorLib.rgb(255, 100, 100));
        //----------------------------------------------------

        // -- set up the control panal --
        ForceDirectedLayout fdl = new ForceDirectedLayout(graph);
        ForceSimulator fsim = fdl.getForceSimulator();
        fsim.getForces()[0].setParameter(0, -1.2f);

        ActionList animate = new ActionList(Activity.INFINITY);
        animate.add(fdl);
        animate.add(fill);//for node
        animate.add(fillEdge);//for edge
        animate.add(new RepaintAction());

        // finally, we register our ActionList with the Visualization.
        // we can later execute our Actions by invoking a method on our
        // Visualization, using the name we've chosen below.
        vis.putAction("draw", draw);
        vis.putAction("layout", animate);

        // -- set up a display to show the visualization --
//        final Display display = new Display(vis);
        display = new EPSDisplay(vis);
        display.setSize(520, 300);
        display.setForeground(Color.GRAY);
        display.setBackground(Color.WHITE);

        // main display controls
        display.addControlListener(new FocusControl(1));
        display.addControlListener(new DragControl());
        display.addControlListener(new PanControl());
        display.addControlListener(new ZoomControl());
        display.addControlListener(new WheelZoomControl());
        display.addControlListener(new ZoomToFitControl());
        display.addControlListener(new NeighborHighlightControl());

        display.setForeground(Color.GRAY);
        display.setBackground(Color.WHITE);

        // maintain a set of items that should be interpolated linearly
        // this isn't absolutely necessary, but makes the animations nicer
        // the PolarLocationAnimator should read this set and act accordingly
        vis.addFocusGroup("linear", new DefaultTupleSet());
        vis.getGroup(Visualization.FOCUS_ITEMS).addTupleSetListener(
                new TupleSetListener() {

                    public void tupleSetChanged(TupleSet t, Tuple[] add, Tuple[] rem) {
                        TupleSet linearInterp = vis.getGroup("linear");
                        if (add.length < 1) {
                            return;
                        }
                        linearInterp.clear();
                        for (Node n = (Node) add[0]; n != null; n = n.getParent()) {
                            linearInterp.addTuple(n);
                        }
                    }//end of tupleSetChanged()
                });

        SearchTupleSet search = new PrefixSearchTupleSet();
        vis.addFocusGroup(Visualization.SEARCH_ITEMS, search);
        search.addTupleSetListener(new TupleSetListener() {

            public void tupleSetChanged(TupleSet t, Tuple[] add, Tuple[] rem) {
                // position and fix the default focusedNode node
                NodeItem focusedNode = (NodeItem) add[0];
                PrefuseLib.setX(focusedNode, null, 400);
                PrefuseLib.setY(focusedNode, null, 250);
                focusGroup.setTuple(focusedNode);
            }
        });

        // create a searchPanel panel for the graph map
        SearchQueryBinding sq = new SearchQueryBinding(
                (Table) vis.getGroup(nodes), label,
                (SearchTupleSet) vis.getGroup(Visualization.SEARCH_ITEMS));
        JSearchPanel searchPanel = sq.createSearchPanel();
        searchPanel.setShowResultCount(true);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 4, 0));
        searchPanel.setFont(FontLib.getFont("Tahoma", Font.PLAIN, 11));

        // -- create a label to show the node name while mouse over --
        final JFastLabel title = new JFastLabel("                 ");
        title.setPreferredSize(new Dimension(350, 20));
        title.setVerticalAlignment(SwingConstants.BOTTOM);
        title.setBorder(BorderFactory.createEmptyBorder(3, 0, 0, 0));
        title.setFont(FontLib.getFont("Tahoma", Font.PLAIN, 16));

        display.addControlListener(new ControlAdapter() {

            public void itemEntered(VisualItem item, MouseEvent e) {
                if (item.canGetString(label)) {
                    title.setText(item.getString(label));
                }
            }

            public void itemExited(VisualItem item, MouseEvent e) {
                title.setText(null);
            }
        });

        Box box = new Box(BoxLayout.X_AXIS);
        box.add(Box.createHorizontalStrut(10));
        box.add(title);
        box.add(Box.createHorizontalGlue());
        box.add(searchPanel);
        box.add(Box.createHorizontalStrut(3));

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(display, BorderLayout.CENTER);
        panel.add(box, BorderLayout.SOUTH);

        Color BACKGROUND = Color.WHITE;
        Color FOREGROUND = Color.DARK_GRAY;
        UILib.setColor(panel, BACKGROUND, FOREGROUND);

//        JLabel jLabelSaveImage=new JLabel("Save Image:");

        //-- create a button to save image
        JButton jButtonSaveImage = new JButton("Save Image");
        jButtonSaveImage.setEnabled(true);
        jButtonSaveImage.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveImage(display);
            }
        });

        // -- create a panel for editing force values --
        final JForcePanel fpanel = new JForcePanel(fsim);
        fpanel.add(Box.createVerticalGlue());
        fpanel.add(jButtonSaveImage, BorderLayout.SOUTH);

        // -- create a new JSplitPane to present the interface --
        JSplitPane split = new JSplitPane();
        split.setLeftComponent(panel);
        split.setRightComponent(fpanel);
        split.setOneTouchExpandable(true);
        split.setContinuousLayout(false);
        split.setDividerLocation(530);
        split.setDividerLocation(800);


        // position and fix the default focusedNode node
        NodeItem focusedNode = (NodeItem) vg.getNode(0);
        PrefuseLib.setX(focusedNode, null, 400);
        PrefuseLib.setY(focusedNode, null, 250);
        focusGroup.setTuple(focusedNode);

        vis.run("draw");
        vis.run("layout");

        // now we run our action list and return
        return split;
    }

    /**
     * to save the image as a eps format
     * @param display   
     */
    private void saveImage(EPSDisplay display) {

        //get the file
        JFileChooser jc = createJFileChooser();
        SuffixFileFilter jpgFilter = new SuffixFileFilter("jpg");
        SuffixFileFilter pngFilter = new SuffixFileFilter("png");
        SuffixFileFilter epsFilter = new SuffixFileFilter("eps");
        jc.setCurrentDirectory(new File("."));
        jc.setAcceptAllFileFilterUsed(false);
        jc.setFileFilter(jpgFilter);
        jc.addChoosableFileFilter(pngFilter);
        jc.addChoosableFileFilter(epsFilter);

        int fileOpenReturnValue = jc.showSaveDialog(new java.awt.Component() {
        });

        if (fileOpenReturnValue == JFileChooser.APPROVE_OPTION) {
            String suffix = "";
            if (jc.getFileFilter().equals(epsFilter)) {
                suffix = "eps";
            } else if (jc.getFileFilter().equals(jpgFilter)) {
                suffix = "jpg";
            } else {
                suffix = "png";
            }

            String filename = jc.getSelectedFile().getAbsolutePath();
            //add the suffix to the end of the file name
            if (!filename.endsWith("." + suffix)) {
                filename += "." + suffix;
            }

            // -- save the image --
            saveImage(filename, suffix, display);

        } else if (fileOpenReturnValue == JFileChooser.CANCEL_OPTION) {
            //do nothing
        }

    }//end of save image

    /**
     * 
     * @param jpgname   the image file name 
     * @param display   the image to be saved
     */
    private void saveImage(String jpgname, String suffix, EPSDisplay display) {

        if (suffix.compareToIgnoreCase("eps") == 0) {
            try {
                final Writer w = new FileWriter(jpgname);
                w.write(display.getEPSText());
                w.flush();
                w.close();
            } catch (final IOException ex) {
//                Utility.logException(ex);
                JOptionPane.showMessageDialog(null, ex.getMessage(), "I/O Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            try {
                FileOutputStream out = new FileOutputStream(jpgname); //for output file name
                if (display.saveImage(out, suffix, 3)) {
                } else {
                    JOptionPane.showMessageDialog(null, "Saving image failed!");
                }
                out.flush();
                out.close();
            } catch (Exception e) {
                System.out.println("creating image file Failed!");
                e.printStackTrace();
            }
        }
    }

    /**
     * to solve a java bug for JFileChooser
     * @return  a JFileChooser
     */
    public JFileChooser createJFileChooser() {
        class JF extends JFileChooser {

            public JF(String name) {
                super(name);
            }

            protected void setUI(ComponentUI newUI) {
                try {
                    super.setUI(newUI);
                } catch (NullPointerException e) {
                    if (newUI.getClass().getName().equals("com.sun.java.swing.plaf.windows.WindowsFileChooserUI")) {
                        try {
                            super.setUI(null);
                        } catch (NullPointerException ee) {
                            ui = null;
                        }
                        super.setUI(new MetalFileChooserUI(this));
                    } else {
                        throw e;
                    }
                }
            }
        }
        return new JF(".");
    }//end of filechooser
}//end of class

/**
 *  this class is to create a file filter to decide the accepted file type
 *  based on the suffix of the file name
 */
class SuffixFileFilter extends FileFilter {

    private String suffix;

    public SuffixFileFilter(String suffix) {

        super();

        this.suffix = suffix;

    }

    public boolean accept(File pathname) {

        if (pathname.isDirectory()) {

            return true;

        }

        return (pathname.getName().toLowerCase().endsWith(suffix.toLowerCase()));

    }

    public String getDescription() {

        return "*." + suffix;

    }
}