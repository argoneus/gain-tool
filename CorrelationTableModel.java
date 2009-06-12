/*
 *  this class is used to be a JTableModel.
 *  Being called in GainGUI as the table model of the correlations 
 *  result table.
 * 
 */

package gain;

 import java.util.Vector;
 import javax.swing.table.DefaultTableModel;

 public class CorrelationTableModel extends DefaultTableModel {

     //*** local variables ****************************************
     public static final int SNP1_INDEX = 1;
     public static final int SCORE_INDEX = 2;
     public static final int SNP2_INDEX = 4;
     public static final int SNP1Selected_INDEX = 0;
     public static final int SNP2Selected_INDEX = 3;
     public static final int HIDDEN_INDEX = 5;
     
     //*** constructors ******************************************
     public CorrelationTableModel() {
        super();
     }
     
     public CorrelationTableModel(Vector columnName,Vector data) {
        super(data,columnName);
      
     }

     //*** function methods **************************************
     
     /**
      * 
      * @param row      the row number of the table
      * @param column   the column number of the table
      * @return     true if it is editable, false if not
      */
     public boolean isCellEditable(int row, int column) {
          switch (column) {
             case SNP1_INDEX:
             case SCORE_INDEX:
             case SNP2_INDEX:
                return false;
             case SNP1Selected_INDEX:
             case SNP2Selected_INDEX:
                 return true;
             default:
                return false;
         }
     }

     /**
      * 
      * @param column   the column number of the table
      * @return     the Class of the column
      */
     public Class getColumnClass(int column) {
         switch (column) {
             case SNP1_INDEX:
             case SCORE_INDEX:
             case SNP2_INDEX:
                 return String.class;
             case SNP1Selected_INDEX:
             case SNP2Selected_INDEX:
                 return Boolean.class;
             default:
                 return Object.class;
         }
     }

 }
