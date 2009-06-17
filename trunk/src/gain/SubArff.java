/**
 * this class is to get a sub file from the original arff file
 * 
 * @author DTian
 * 
 */
package gain;

import java.awt.HeadlessException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JOptionPane;

public class SubArff {

    private ArrayList arrListAttrName; // for attribute names in output file
    private ArrayList arrListData;// for data part in output file
    private ArrayList attrNameList;// for keep name list

    // constructors
    
    public SubArff(ArrayList arrNameList, String oriFileName) {
        arrListAttrName = new ArrayList();
        arrListData = new ArrayList();
        
        //initialize attrNameList
        attrNameList = arrNameList;
//tdh        attrNameList.add("Class"); //could be deleted, but put here for sure
        
        //initialize arrListAttrName and arrListData
        init(oriFileName);

    }// end
    
    public SubArff(String nameFile, String oriFileName) {
        arrListAttrName = new ArrayList();
        arrListData = new ArrayList();
        attrNameList = new ArrayList();

        try {
            // read the attr names from the name list file
            FileReader frFirst = new FileReader(nameFile);
            BufferedReader brFirst = new BufferedReader(frFirst);

            String strTemp = "";
            while ((strTemp = brFirst.readLine()) != null) {
                String strName = strTemp.trim().split("\\s")[0];
                attrNameList.add(strName);
            }
//tdh            attrNameList.add("Class");
            frFirst.close();
            brFirst.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e.toString());
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        
        init(oriFileName);

    }// end
    
    
    /**
     * 
     * @param nameList
     *            the name of attribures in new file
     * @param fileName
     *            new file name
     * @return A tab delimited File contains only the attributes in the nameList
     */
    public void toTabFile(String[] nameList, String fileName) {
        try {
            FileWriter fw = new FileWriter(fileName);
            BufferedWriter writer = new BufferedWriter(fw);
            ArrayList vecIndex = new ArrayList();
            for (int i = 0; i < getVecAttrName().size(); i++) {
                // get the name of the current attribute

                String attrName = getVecAttrName().get(i).toString().trim();

                // if the attr is in the name list, then remember the index
                if (isMember(attrName, nameList)) {
                    vecIndex.add(i + "");
                    // write the attribute name first
                    writer.write(attrName + "\t");
                }
            }
            writer.write("\n");
            // write to the output tab file
            for (int i = 0; i < getVecData().size(); i++) {
                String[] strArr = (String[]) getVecData().get(i);
                for (int j = 0; j < vecIndex.size(); j++) {
                    writer.write(strArr[Integer.parseInt((String) vecIndex.get(j))] + "\t");
                }
                writer.write("\n");

            }

            writer.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

    // return resultFile;
    }

   
    /**
     * test a string is or is not in a array
     * 
     * @parameter: str, the string to be tested
     * @parameter: strArr, the string array
     * 
     * @return: ture if the string is in the array; false if the string is not
     *          in the array
     * 
     */
    private static boolean isMember(String str, String[] strArr) {
        boolean result = false;
        for (int i = 0; i < strArr.length; i++) {
            if (str.compareToIgnoreCase(strArr[i]) == 0) {
                result = true;
                break;
            }
        }

        return result;
    }

    /**
     * @return the arrListAttrName
     */
    public ArrayList getVecAttrName() {
        return arrListAttrName;
    }

    /**
     * @param arrListAttrName
     *            the arrListAttrName to set
     */
    public void setVecAttrName(ArrayList vecAttrName) {
        this.arrListAttrName = vecAttrName;
    }

    /**
     * @return the arrListData
     */
    public ArrayList getVecData() {
        return arrListData;
    }

    /**
     * @param arrListData
     *            the arrListData to set
     */
    public void setVecData(ArrayList vecData) {
        this.arrListData = vecData;
    }

    /**
     * @return the attrNameList as a ArrayList
     */
    public ArrayList getAttrNameList() {
        return attrNameList;
    }

    /**
     * @param attrNameList
     *            the attrNameList to set
     */
    public void setAttrNameList(ArrayList attrNameList) {
        this.attrNameList = attrNameList;
    }

    /**
     * 
     * @return the keeping name list from the file as a string array
     */
    public String[] getNameList() {

        int intTopAttr = getAttrNameList().size();
        String[] nameList = new String[intTopAttr];
        for (int i = 0; i < intTopAttr; i++) {
            nameList[i] = getAttrNameList().get(i).toString();
        }
        return nameList;
    }

    /**
     *
     * @return the all name list from the data file as a string array
     */
    public String[] getAllNameList() {

        int intTopAttr = getVecAttrName().size();
        String[] nameList = new String[intTopAttr];
        for (int i = 0; i < intTopAttr; i++) {
            nameList[i] = getVecAttrName().get(i).toString();
        }
        return nameList;
    }

    private void init(String oriFileName) throws HeadlessException {

        try {
            //**************************************************
            //**************************************************
            FileReader frSecond = new FileReader(oriFileName);
            BufferedReader br = new BufferedReader(frSecond);
            // read the first line of the file
            String strLine = br.readLine();
            String className = "";

            if (oriFileName.toLowerCase().endsWith(".arff")) {
                //for .arff file
                // this while loop is to get the attributes name part
                while (((strLine = br.readLine().trim()) != null) && (strLine.compareToIgnoreCase("@DATA") != 0)) {
                    String[] strArray = strLine.split("\\s");
                    if (strArray[0].compareToIgnoreCase("@attribute") == 0) {
                        String attrName = strArray[1].trim();
                        int index = 1;
                        while (attrName.compareTo("") == 0) {
                            attrName = strArray[++index];
                        }
                        arrListAttrName.add(attrName);
                        className = attrName;
                    }
                } // end of while loop
                // this while loop is to get the data part
                while ((strLine = br.readLine()) != null) {
                    if (strLine.indexOf(",") >= 0) {
                        String[] strArray = strLine.split("\\,");
                        if (strArray.length > 0) {
                            arrListData.add(strArray);
                        }
                    } else {
                        String[] strArray = strLine.split("\\s");
                        if (strArray.length > 0) {
                            arrListData.add(strArray);
                        }
                    }
                    System.out.println();
                } // end of while loop
            } else if (oriFileName.toLowerCase().endsWith(".tab")) {
                //for .tab file(tab delimited file)
                //get the name part of the file
                String[] strArray = strLine.trim().split("\\t");
                for (int i = 0; i < strArray.length; i++) {
                    arrListAttrName.add(strArray[i]);
                }
                className = strArray[strArray.length-1];//class attribute is the last one
                //get the data part of the file
                while ((strLine = br.readLine()) != null) {
                    strArray = strLine.split("\\t");
                    if (strArray.length > 0) {
                        arrListData.add(strArray);
                    }
                } // end of while loop
            } else {
                //error,
                JOptionPane.showMessageDialog(null, "File name must end with .arff or .tab!", "File Name Error", JOptionPane.ERROR_MESSAGE);
            } //end of else
            attrNameList.add(className);
            frSecond.close();
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e.toString());
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }//end of init()

} //end of class SubArff
