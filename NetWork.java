/**
 * This class is a new version from network2 class. Below is the difference:
 *  
 * I would like to represent the information in getCytoscapeSifNumric as a 
 * matrix.  Let's call it weightedAdjacencyMatrix.  We will need another args
 * value for the name of the output file.  
 * (By the way, soon I want to create a GUI for this.)
 * The first row will have SNP variable names.  Let's make it look like (for Matlab reason):
 * {"SNP1","SNP2"...}
 * After the first row comes the square matrix of mutual informations and interaction gains.  The diagonal will be mutualInformationGain*100.
 * The off-diagonal elements will be interactionGain*100.
 * 
 */
package gain;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;
import javax.swing.JOptionPane;

public class NetWork extends Thread{

    private InformationTheoretica dataSource; // for data information
//	private double cutoff; // for cutoff value
    private HashMap numberOfConnections; // for node degree
    private Vector nodeEdgeNode; // for sif file(node1 edge node2)
    private Vector nodeEdgeNodeNumric; // for numric sif file(node1 numric
    // node2)
    private Vector interactionGain; // for interaction gain result table
    private Vector filteredNamelist; // for filtered attributes name(name1 +
    // name2)
    private Vector vecFilteredNamelist; // for filtered attributes name(name1
    // only)
    private Vector vecAdjacencyMatrix; // for weightedAdjacencyMatrix
    private double minInteractionGain; // the min value of interaction gain
    private double maxInteractionGain; // the max value of interaction gain
    private boolean stop; // flag to end the thread
    private String choose; // to choose which method will be executed
    private double threshold;
    private double cutoff;

    public NetWork() {
         // *******initialize variables***********
//        dataSource = new InformationTheoretica("");
//        minInteractionGain = 0;
//        maxInteractionGain = 1;
//
//        numberOfConnections = new HashMap();
//        nodeEdgeNode = new Vector();
//        interactionGain = new Vector();
//        nodeEdgeNodeNumric = new Vector();
//        filteredNamelist = new Vector();
//        vecFilteredNamelist = new Vector();
//        vecAdjacencyMatrix = new Vector();

    }

    public String getChoose() {
        return choose;
    }

    public void setChoose(String choose) {
        this.choose = choose;
    }

    public double getCutoff() {
        return cutoff;
    }

    public void setCutoff(double cutoff) {
        this.cutoff = cutoff;
    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    public boolean isStop() {
        return stop;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }

    public double getMaxInteractionGain() {
        return maxInteractionGain;
    }

    public void setMaxInteractionGain(double maxInteractionGain) {
        this.maxInteractionGain = maxInteractionGain;
    }

    public double getMinInteractionGain() {
        return minInteractionGain;
    }

    public void setMinInteractionGain(double minInteractionGain) {
        this.minInteractionGain = minInteractionGain;
    }

    //-- constructors --
    public NetWork(InformationTheoretica data, String choose) {
        this(data,choose,0.8,0.05);
    }

    public NetWork(InformationTheoretica data, String choose, double threshold, double cutoff) {

        // *******initialize variables***********
        dataSource = data;
        this.choose = choose;
        minInteractionGain = 10;
        maxInteractionGain = 0;
        stop = false;
        this.threshold = threshold;
        this.cutoff = cutoff;
        
        numberOfConnections = new HashMap();
        nodeEdgeNode = new Vector();
        interactionGain = new Vector();
        nodeEdgeNodeNumric = new Vector();
        filteredNamelist = new Vector();
        vecFilteredNamelist = new Vector();
        vecAdjacencyMatrix = new Vector();
        
    }
    //-- end of constructors --


    // -- get the original data variable information --
    public ArrayList getSnpNames(){
        return dataSource.getSnpNames();
    }

    /**
     *  thread body
     */
    public void run(){
        if(choose.compareToIgnoreCase("calculate") == 0){
            this.executeCorrelations(threshold);
        } else if (choose.compareToIgnoreCase("run") == 0){
            this.executeInteractionGain(cutoff);
        }else{
            JOptionPane.showMessageDialog(null, "please choose which one should be executed");
            
        }
    }
    
    public void shutDown(){
        this.stop = true;
    }
    
    /**
     * to get the correlations which greater than threshold
     * 
     * @param threshold, 
     */
    private void executeCorrelations(double threshold) {

        String name1 = "";
        String name2 = "";
        filteredNamelist.clear();
        vecFilteredNamelist.clear();

        // ***********get the name set of the data hashmap***********
        Vector nameList = new Vector(); // contains the name set of the Hashmap
        Iterator it = this.getDataSource().mutualInformationGain().keySet().iterator();
        while (it.hasNext()) {
            Object key = it.next();
            nameList.addElement(key);
        }

        // ***********local variables***********
        int size = nameList.size();

        Vector removedNameList = new Vector();

        // first line of the filteredNamelist
        filteredNamelist.addElement("SNP1" + "\t" + "Score" + "\t" + "SNP2");

        for (int i = 0; i < size && !stop; i++) {

            name1 = (String) nameList.elementAt(i);
            for (int j = 0; j < size && !stop; j++) {
                name2 = (String) nameList.elementAt(j);

                // ********** get the filtered name list *************
                double correlationScore = this.getDataSource().symmetricMutualInfo(name1, name2);
                if (j > i && correlationScore >= threshold) {
                    removedNameList.addElement(name2);
                    if (!this.checkExist(name1, removedNameList)) {
                        vecFilteredNamelist.addElement(name1);
                    }
                    filteredNamelist.addElement(name1 + "\t" + correlationScore + "\t" + name2);
                }
            }//end of inner for loop
        }//end of outter for loop
    }//end of executeCorrelations

    
    /**
     * to get the interaction gains which is greater than threshold
     * 
     * @param cutoffValue, the value act as the threshold
     */
    private void executeInteractionGain(double cutoffValue) {
        // ***********define local variable***********
        double zero = 0.0;
        String name1 = "";
        String name2 = "";
        vecAdjacencyMatrix.clear();
        interactionGain.clear();
        numberOfConnections.clear();
        nodeEdgeNode.clear();
        nodeEdgeNodeNumric.clear();


        // ***********get the name set of the data hashmap***********
        Vector nameList = new Vector(); // contains the name set of the Hashmap
        Iterator it = this.getDataSource().mutualInformationGain().keySet().iterator();
        while (it.hasNext()) {
            Object key = it.next();
            nameList.addElement(key);
        }
        vecAdjacencyMatrix.addElement(nameList);

        // ***********local variables***********
        int size = nameList.size();
        double interactInfoScore = 0.0;
        double mi1 = 0.0;
        double mi2 = 0.0;
        double interact = 0.0;

        // first line of the interaction gain result table
        interactionGain.addElement("SNP1	IG1	SNP2	IG2	Interaction_Gain	Total");


        for (int i = 0; i < size && !stop; i++) {

            name1 = (String) nameList.elementAt(i);
            int numConnection = 0;
            Vector vecMatrixRow = new Vector();
            for (int j = 0; j < size && !stop; j++) {
                name2 = (String) nameList.elementAt(j);

                // *******calculate the interact information score********
                if (j == i) { // same node
                    interactInfoScore = 0.0;
                    vecMatrixRow.addElement(Double.parseDouble((String) (this.getDataSource().mutualInformationGain().get(name1))) * 100 + "");
                } else {
                    interactInfoScore = this.getDataSource().interactionInformation(name1,
                            name2);
                    // if the score is too small, kill it
                    if (Math.abs(interactInfoScore) < cutoffValue) {
                        interactInfoScore = 0.0;
                    }
                    vecMatrixRow.addElement(interactInfoScore * 100 + "");

                }
                
                //set min interactionGain score
                if(minInteractionGain > interactInfoScore*100){
                    minInteractionGain = interactInfoScore*100;
                }
                //set max interactionGain score
                if(maxInteractionGain < interactInfoScore*100){
                    maxInteractionGain = interactInfoScore*100;
                }


                // ***********output to the data containers***************
                DecimalFormat df = new DecimalFormat("###,##0.00"); // number
                // format
                if (interactInfoScore > zero) {
                    if (j > i) {
                        nodeEdgeNode.addElement(name1 + "\tpp\t" + name2);
                        mi1 = Double.parseDouble((String) (this.getDataSource().mutualInformationGain().get(name1))) * 100;
                        mi2 = Double.parseDouble((String) (this.getDataSource().mutualInformationGain().get(name2))) * 100;
                        interact = interactInfoScore * 100;
                        nodeEdgeNodeNumric.addElement(name1 + "\t" + df.format(interact) + "\t" + name2);
                        interactionGain.addElement(name1 + "\t" + df.format(mi1) + "\t" + name2 + "\t" + df.format(mi2) + "\t" + df.format(interact) + "\t" + df.format(mi1 + mi2 + interact));
                    }
                    numConnection++;
                }
                if (interactInfoScore < zero) {
                    if (j > i) {
                        nodeEdgeNode.addElement(name1 + "\tpd\t" + name2);
                        mi1 = Double.parseDouble((String) this.getDataSource().mutualInformationGain().get(name1)) * 100;
                        mi2 = Double.parseDouble((String) this.getDataSource().mutualInformationGain().get(name2)) * 100;
                        interact = interactInfoScore * 100;
                        nodeEdgeNodeNumric.addElement(name1 + "\t" + df.format(interact) + "\t" + name2);
                        interactionGain.addElement(name1 + "\t" + df.format(mi1) + "\t" + name2 + "\t" + df.format(mi2) + "\t" + df.format(interact) + "\t" + df.format(mi1 + mi2 + interact));
                    }
                    numConnection++;
                }
            }// end of inner for loop
            numberOfConnections.put(name1, numConnection + "");
            vecAdjacencyMatrix.addElement(vecMatrixRow);

        }// end of outer for loop
    }// end of constructor
    /**
     * 
     * @param vec
     *            the original hashMap
     * @param ascending
     *            boolean variable control the order of the sort
     * @return
     */
    private HashMap insertionSort(HashMap vec, boolean ascending) {
        int in = 0;
        int out = 0;
        int nElems = vec.size();
        HashMap result = new LinkedHashMap();
        double[] sa = new double[nElems];
        Object[] keyName = new Object[nElems];

        // ************* assignment to double array***************
        Iterator it = vec.keySet().iterator();
        Object key = null;
        int index = 0;
        while (it.hasNext()) {
            key = it.next();
            double value = Double.parseDouble(vec.get(key).toString());
            sa[index] = value;
            keyName[index] = key;
            index++;
        }

        // *************** sort **********************
        for (out = 1; out < nElems; out++) { // out loop
            double temp = sa[out]; // get the one want to be in the ordered
            // list
            Object keyTemp = keyName[out];
            in = out; // compare to its left one
            if (ascending) {
                while (in > 0 && (sa[in - 1] >= temp)) { // condition
                    sa[in] = sa[in - 1]; // if less than temp, move to its
                    // right
                    keyName[in] = keyName[in - 1];
                    --in; // continue to compare to the left one
                }
            } else {
                while (in > 0 && (sa[in - 1] <= temp)) { // condition
                    sa[in] = sa[in - 1]; // if less than temp, move to its
                    // right
                    keyName[in] = keyName[in - 1];
                    --in; // continue to compare to the left one
                }
            }
            sa[in] = temp; // find the right position
            keyName[in] = keyTemp;
        } // end for loop

        for (int i = 0; i < sa.length; i++) {
            // result.addElement(sa[i]);
            result.put(keyName[i], (int) sa[i]);
        }

        return result;
    } // end insertionSort()

    /**
     * this method will output a .sif file For cytoscape
     * 
     * @param sifFilename :
     *            the output .sif filename
     */
    public void getCytoscapeSif(String sifFilename) {
        try {
            FileWriter fw = new FileWriter(sifFilename);
            BufferedWriter writer = new BufferedWriter(fw);
            // System.out.println("node1" + "\tedge\t" + "node2");
            for (int i = 0; i < this.getNodeEdgeNode().size(); i++) {
                // System.out.println(this.getNodeEdgeNode().elementAt(i));
                writer.write(this.getNodeEdgeNode().elementAt(i) + "\n");
            }
            writer.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }// end of getCytoscapeSif
    /**
     * this method will output a numeric .sif file For cytoscape
     * 
     * @param sifFilename :
     *            the output .sif filename
     */
    public void getCytoscapeSifNumric(String sifFilename) {
        try {
            FileWriter fw = new FileWriter(sifFilename);
            BufferedWriter writer = new BufferedWriter(fw);
            // System.out.println("node1" + "\tedge\t" + "node2");
            for (int i = 0; i < this.getNodeEdgeNodeNumric().size(); i++) {
                // System.out.println(this.getNodeEdgeNodeNumric().elementAt(i));
                writer.write(this.getNodeEdgeNodeNumric().elementAt(i) + "\n");
            }
            writer.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }// end of getCytoscapeSif
    /**
     * this method will output a file of degree result table
     * 
     * @param outputFilename :
     *            the output .sif filename
     */
    public void getDegreeResultsTable(String outputFilename) {
        try {
            FileWriter fw = new FileWriter(outputFilename);
            BufferedWriter writer = new BufferedWriter(fw);

            // ***********output the number of connections for each
            // node***********
            Map sortedMap = insertionSort(this.getNumberOfConnections(), false);
            // System.out.println("Node" + "\t" + "Degree");
            writer.write("Node" + "\t" + "Degree" + "\n");
            Iterator it = sortedMap.keySet().iterator();
            while (it.hasNext()) {
                Object key = it.next();
                // System.out.println(key.toString() + "\t\t" +
                // sortedMap.get(key));
                writer.write(key.toString() + "\t" + sortedMap.get(key) + "\n");
            }
            writer.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }// end of getDegreeResultstable
    /**
     * this method will output a file of interaction gain result table
     * 
     * @param outputFilename
     */
    public void getSortedInteractionGainResultsTable(String outputFilename) {
        try {
            FileWriter fw = new FileWriter(outputFilename);
            BufferedWriter writer = new BufferedWriter(fw);
            for (int i = 0; i < this.getSortedInteractionGain(false).size(); i++) {
                // System.out.println(this.getInteractionGain().elementAt(i));
                writer.write(this.getSortedInteractionGain(false).elementAt(i) + "\n");
            }
            writer.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }// end
   /**
     * output a file of filtered name list(name1 , name2)
     * 
     * @param outputFilename
     */
    public void getFilteredNameList(String outputFilename) {
        try {
            FileWriter fw = new FileWriter(outputFilename);
            BufferedWriter writer = new BufferedWriter(fw);
            for (int i = 0; i < this.getFilteredNamelist().size(); i++) {
                // System.out.println(this.getInteractionGain().elementAt(i));
                writer.write(this.getFilteredNamelist().elementAt(i) + "\n");
            }
            writer.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }// end
    /**
     * 
     * 
     * @param outputFilename
     */
    public void getAdjacencyMatrix(String outputFilename) {
        try {
            FileWriter fw = new FileWriter(outputFilename);
            BufferedWriter writer = new BufferedWriter(fw);
            for (int i = 0; i < this.getVecAdjacencyMatrix().size(); i++) {
                for (int j = 0; j < ((Vector) (this.getVecAdjacencyMatrix().elementAt(i))).size(); j++) {
                    writer.write(((Vector) (this.getVecAdjacencyMatrix().elementAt(i))).elementAt(j) + "\t");
                }
                writer.write("\n");
            }
            writer.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }// end
    /**
     * @return the cutoff
     */
//	public double getCutoff() {
//		return cutoff;
//	}
    /**
     * @param cutoff
     *            the cutoff to set
     */
//	public void setCutoff(double cutoff) {
//		this.cutoff = cutoff;
//	}
    /**
     * @return the numberOfConnections
     */
    public HashMap getNumberOfConnections() {
        return numberOfConnections;
    }

    /**
     * @param numberOfConnections
     *            the numberOfConnections to set
     */
    public void setNumberOfConnections(HashMap numberOfConnections) {
        this.numberOfConnections = numberOfConnections;
    }

    /**
     * @return the nodeEdgeNode
     */
    public Vector getNodeEdgeNode() {
        return nodeEdgeNode;
    }

    /**
     * @param nodeEdgeNode
     *            the nodeEdgeNode to set
     */
    public void setNodeEdgeNode(Vector nodeEdgeNode) {
        this.nodeEdgeNode = nodeEdgeNode;
    }

    /**
     * @return the interactionGain
     */
    public Vector getInteractionGain() {

        return interactionGain;
    }

    /**
     * @return the interactionGain sorted by total
     */
    public Vector getSortedInteractionGain(boolean ascending) {
        Vector result = new Vector();
        Vector vecGains = getInteractionGain();
        HashMap hmGains = new HashMap();

        //get the column name
        result.addElement(vecGains.elementAt(0).toString());
        //get the data part
        for (int i = 1; i < vecGains.size(); i++) {
            String[] strArray = ((String) vecGains.elementAt(i)).trim().split("\\t");
            hmGains.put(vecGains.elementAt(i), strArray[strArray.length - 1]);
        }
        //sort by the total desending
        Map sortedMap = insertionSort(hmGains, ascending);
        Iterator it = sortedMap.keySet().iterator();
        while (it.hasNext()) {
            Object key = it.next();
            result.addElement(key);
        }
        return result;
    }

    /**
     * @param interactionGain
     *            the interactionGain to set
     */
    public void setInteractionGain(Vector interactionGain) {
        this.interactionGain = interactionGain;
    }

    /**
     * @return the nodeEdgeNodeNumric
     */
    public Vector getNodeEdgeNodeNumric() {
        return nodeEdgeNodeNumric;
    }

    /**
     * @param nodeEdgeNodeNumric
     *            the nodeEdgeNodeNumric to set
     */
    public void setNodeEdgeNodeNumric(Vector nodeEdgeNodeNumric) {
        this.nodeEdgeNodeNumric = nodeEdgeNodeNumric;
    }

    /**
     * @return the filteredNamelist
     */
    public Vector getFilteredNamelist() {
        return filteredNamelist;
    }

    /**
     * @param filteredNamelist
     *            the filteredNamelist to set
     */
    public void setFilteredNamelist(Vector filteredNamelist) {
        this.filteredNamelist = filteredNamelist;
    }

   /**
     * @return the vecFilteredNamelist
     */
    public Vector getVecFilteredNamelist() {
        return vecFilteredNamelist;
    }

    /**
     * @param vecFilteredNamelist
     *            the vecFilteredNamelist to set
     */
    public void setVecFilteredNamelist(Vector vecFilteredNamelist) {
        this.vecFilteredNamelist = vecFilteredNamelist;
    }

    /**
     * @return the dataSource
     */
    public InformationTheoretica getDataSource() {
        return dataSource;
    }

    /**
     * @param dataSource
     *            the dataSource to set
     */
    public void setDataSource(InformationTheoretica dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * add the new string into the vector. 
     * arguments: value, the new input value need to check 
     * existValue: the exist value set
     */
    private boolean checkExist(String value, Vector existValue) {
        boolean exist = false;
        for (int i = 0; i < existValue.size(); i++) {
            if (((String) existValue.elementAt(i)).compareToIgnoreCase(value) == 0) {
                // if exist then flag it and quit loop
                exist = true;
                break;
            }
        }
        if (!exist) {
            // if not exist then add it into the vector
            existValue.addElement(value);
        }
        return exist;
    }

    /**
     * @return the vecAdjacencyMatrix
     */
    public Vector getVecAdjacencyMatrix() {
        return vecAdjacencyMatrix;
    }

    /**
     * @param vecAdjacencyMatrix the vecAdjacencyMatrix to set
     */
    public void setVecAdjacencyMatrix(Vector vecAdjacencyMatrix) {
        this.vecAdjacencyMatrix = vecAdjacencyMatrix;
    }

    public int getNumbeOfAttributes(){
        return this.dataSource.getNumAttributes();
    }
    
    public int getNumbeOfSamples(){
        return this.dataSource.getNumInstances();
    }

   
}
