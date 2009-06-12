/**
 * this class and NetWork.java together to generate the network by being
 * called in GainGui.java
 *
 */
package gain;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;

public class InformationTheoretica {

    //*** local variables ****************************************
    //private Vector arrListData;
    private HashMap hmAttributeDict;
    private int numInstances;
    private int numAttributes;
    private String statusKey;

    //*** constructors ****************************************
    public InformationTheoretica(String infilename) {
//		***********initialize the variables ***********
        ArrayList arrListData = new ArrayList();
        numInstances = 0;
        numAttributes = 0;
        hmAttributeDict = new HashMap();
        statusKey = "";
        String finalFilename = "";


        boolean isTempFile = false;
        if (infilename.toLowerCase().endsWith("arff")) {
            finalFilename = infilename + ".tab";
            SubArff subArff = new SubArff(arrListData, infilename);
            subArff.toTabFile(subArff.getAllNameList(), finalFilename);
            isTempFile = true;
        } else {
            finalFilename = infilename;
        }

        try {
            FileReader fr = new FileReader(finalFilename);
            BufferedReader br = new BufferedReader(fr);

            // strRow is used to read line from file
            String strRow = new String();

            // read the file and store into ArrayList
            arrListData.clear();
            while ((strRow = br.readLine()) != null) {
                String[] strArrayRow = strRow.split("\\t");
                arrListData.add(strArrayRow);
            }

            //set the number of attributes and instances
            numAttributes = ((String[]) arrListData.get(0)).length - 1;
            numInstances = arrListData.size() - 1;

            //***********set the hashmap***********
            String[] keyset = new String[numAttributes + 1];
            ArrayList valueSet = new ArrayList();

            //set the keyset
            for (int i = 0; i <= numAttributes; i++) {
                keyset[i] = ((String[]) arrListData.get(0))[i];
            }
            statusKey = ((String[]) arrListData.get(0))[numAttributes];
            //set the value set
            for (int i = 0; i <= numAttributes; i++) {
                String[] value = new String[numInstances];
                for (int j = 1; j < arrListData.size(); j++) {
                    value[j - 1] = ((String[]) arrListData.get(j))[i];
                }
                valueSet.add(value);
            }

            for (int i = 0; i <= numAttributes; i++) {
                hmAttributeDict.put(keyset[i], ((String[]) valueSet.get(i)));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        }//end of if-else

        if (isTempFile) {
            new File(finalFilename).deleteOnExit();
        }

    }

    // -- get the snpNames --
    public ArrayList getSnpNames() {
        ArrayList result = new ArrayList();
        Iterator it = hmAttributeDict.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next().toString();
            if (key.compareToIgnoreCase(statusKey) != 0) {
                result.add(key);
            }
        }

        return result;
    }

    /**
     * @return the numInstances
     */
    public int getNumInstances() {
        return numInstances;
    }

    /**
     * @param numInstances the numInstances to set
     */
    public void setNumInstances(int numInstances) {
        this.numInstances = numInstances;
    }

    /**
     * @return the numAttributes
     */
    public int getNumAttributes() {
        return numAttributes;
    }

    /**
     * @param numAttributes the numAttributes to set
     */
    public void setNumAttributes(int numAttributes) {
        this.numAttributes = numAttributes;
    }

    /**
     * @return the statusKey
     */
    public String getStatusKey() {
        return statusKey;
    }

    /**
     * @param statusKey the statusKey to set
     */
    public void setStatusKey(String statusKey) {
        this.statusKey = statusKey;
    }

    /**
     * @return the hmAttributeDict
     */
    public HashMap getHmAttributeDict() {
        return hmAttributeDict;
    }

    /**
     * @param hmAttributeDict the hmAttributeDict to set
     */
    public void setHmAttributeDict(HashMap hmAttributeDict) {
        this.hmAttributeDict = hmAttributeDict;
    }

    /**
     * 
     * @param key  the key  of the dictionary
     * @return the entropy 
     */
    public double entropy(String key) {
        double entropy = 0;
//		get the entropy of attribute with given its key
        String[] strData = (String[]) this.getHmAttributeDict().get(key);
        //***********frequency table***********
        HashMap freqDict = new HashMap();
        int one = 1;
        for (int i = 0; i < strData.length; i++) {
            String newkey = (String) strData[i];
            if (freqDict.containsKey(newkey)) {
                int val = Integer.parseInt((String) freqDict.get(newkey));
                freqDict.remove(newkey);
                val = val + 1;
                freqDict.put(newkey, val + "");

            } else {
                freqDict.put(newkey, (one + ""));
            }
        }

        // ***********probability table***********
        HashMap probDict = new HashMap();
        Iterator it = freqDict.keySet().iterator();
        String newkey = "";
        while (it.hasNext()) {
            newkey = (String) it.next();
            double value = 0;
            value = Double.parseDouble((String) freqDict.get(newkey)) / this.getNumInstances();
            probDict.put(newkey, value + "");
        }

        //***********calculate the entropy***********
        it = probDict.keySet().iterator();
        while (it.hasNext()) {
            newkey = (String) it.next();
            double value = 0;
            value = Double.parseDouble((String) probDict.get(newkey));
            entropy = entropy - value * (Math.log(value) / Math.log(2));
        }
        return entropy;

    }//end of entropy

    /**
     * 
     * @param firstKey  the key of the first node
     * @param secondKey the key of the second node
     * @return the joint entropy of the two nodes
     */
    public double jointEntropy(String firstKey, String secondKey) {
        double result = 0;
//		get the entropy of attribute with given its key
        String[] firstData = (String[]) this.getHmAttributeDict().get(firstKey);
        String[] secondData = (String[]) this.getHmAttributeDict().get(secondKey);

        //***********frequency table***********
        HashMap freqDict = new HashMap();
        int one = 1;
        int smallLength = 0;
        if (firstData.length > secondData.length) {
            smallLength = secondData.length;
        } else {
            smallLength = firstData.length;
        }
        for (int i = 0; i < smallLength; i++) {
            String newkey = (String) firstData[i] + "," + (String) secondData[i];
            if (freqDict.containsKey(newkey)) {
                int val = Integer.parseInt((String) freqDict.get(newkey));
                freqDict.remove(newkey);
                val = val + 1;
                freqDict.put(newkey, val + "");

            } else {
                freqDict.put(newkey, (one + ""));
            }
        }

        //*********** probability table***********
        HashMap probDict = new HashMap();
        Iterator it = freqDict.keySet().iterator();
        String newkey = "";
        while (it.hasNext()) {
            newkey = (String) it.next();
            double value = 0;
            value = Double.parseDouble((String) freqDict.get(newkey)) / this.getNumInstances();
            probDict.put(newkey, value + "");
        }

        //***********calculate the joint entropy***********
        it = probDict.keySet().iterator();
        while (it.hasNext()) {
            newkey = (String) it.next();
            double value = 0;
            value = Double.parseDouble((String) probDict.get(newkey));
            result = result - value * (Math.log(value) / Math.log(2));
        }

        return result;
    }//end of jointEntropy

    /**
     * 
     * @return the dictionary of mutual information 
     */
    public HashMap mutualInformationGain() {
        ArrayList attrMinusClassKey = new ArrayList();
        Iterator it = this.getHmAttributeDict().keySet().iterator();
        String newkey = "";
        while (it.hasNext()) {
            newkey = (String) it.next();
            if (newkey.compareToIgnoreCase(this.getStatusKey()) != 0) {
                attrMinusClassKey.add(newkey);
            }
        }

        HashMap mutualInfoDict = new HashMap();
        for (int i = 0; i < attrMinusClassKey.size(); i++) {
            String key = (String) attrMinusClassKey.get(i);
            double attrEntropy = this.entropy(key);
            double classEntropy = this.entropy(this.getStatusKey());
            double jointEntropy = this.jointEntropy(key, this.getStatusKey());
            double value = attrEntropy + classEntropy - jointEntropy;
            mutualInfoDict.put(key, value + "");
        }

        return mutualInfoDict;
    }

    /**
     * 
     * @param name1  the name of the attribute1 
     * @param name2	 the name of the attribute2
     * @return a double value
     */
    public double mutualInformation(String name1, String name2) {
        double attrEntropy1 = this.entropy(name1);
        double attrEntropy2 = this.entropy(name2);
        double jointEntropy = this.jointEntropy(name1, name2);
        return attrEntropy1 + attrEntropy2 - jointEntropy;

    }

    /**
     * 
     * @param name1  the name of the attribute1 
     * @param name2	 the name of the attribute2
     * @return a double value
     */
    public double symmetricMutualInfo(String name1, String name2) {
        double attrEntropy1 = this.entropy(name1);
        double attrEntropy2 = this.entropy(name2);
        double jointEntropy = this.jointEntropy(name1, name2);
        return 2 * (attrEntropy1 + attrEntropy2 - jointEntropy) / (attrEntropy1 + attrEntropy2);

    }

    /**
     * 
     * @param firstKey the key of the first node
     * @param secondKey the key of the second node
     * @return the interaction information of the two node and the class node
     */
    public double interactionInformation(String firstKey, String secondKey) {
        double result = 0;
        String classKey = this.getStatusKey();
        String[] firstData = (String[]) this.getHmAttributeDict().get(firstKey);
        String[] secondData = (String[]) this.getHmAttributeDict().get(secondKey);
        String[] statusData = (String[]) this.getHmAttributeDict().get(classKey);

        //***********frequency table***********
        HashMap comboFreqDict = new HashMap();
        int one = 1;
        int smallLength = 0;
        if (firstData.length > secondData.length && statusData.length > secondData.length) {
            smallLength = secondData.length;
        } else if (firstData.length < secondData.length && firstData.length < statusData.length) {
            smallLength = firstData.length;
        } else {
            smallLength = statusData.length;
        }
        for (int i = 0; i < smallLength; i++) {
            String newkey = (String) firstData[i] + "," + (String) secondData[i] + "," + (String) statusData[i];
            if (comboFreqDict.containsKey(newkey)) {
                int val = Integer.parseInt((String) comboFreqDict.get(newkey));
                comboFreqDict.remove(newkey);
                val = val + 1;
                comboFreqDict.put(newkey, val + "");

            } else {
                comboFreqDict.put(newkey, (one + ""));
            }
        }

        //*********** probability table***********
        HashMap comboProbDict = new HashMap();
        Iterator it = comboFreqDict.keySet().iterator();
        String newkey = "";
        while (it.hasNext()) {
            newkey = (String) it.next();
            double value = 0;
            value = Double.parseDouble((String) comboFreqDict.get(newkey)) / this.getNumInstances();
            comboProbDict.put(newkey, value + "");
        }

        //***********calculate the interaction information***********
        double H_ABC = 0.0;
        double H_AB = this.jointEntropy(firstKey, secondKey);
        double H_AC = this.jointEntropy(firstKey, classKey);
        double H_BC = this.jointEntropy(secondKey, classKey);
        double H_A = this.entropy(firstKey);
        double H_B = this.entropy(secondKey);
        double H_C = this.entropy(classKey);

        it = comboProbDict.keySet().iterator();
        while (it.hasNext()) {
            newkey = (String) it.next();
            double value = 0;
            value = Double.parseDouble((String) comboProbDict.get(newkey));
            H_ABC = H_ABC - value * (Math.log(value) / Math.log(2));
        }

        result = H_AB + H_BC + H_AC - H_A - H_B - H_C - H_ABC;

        return result;
    }
}//end of the class

