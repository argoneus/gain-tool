/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gain;

import java.util.Vector;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author DTian
 */
public class RemainCorrelationTableModel extends DefaultTableModel{

     //*** local variables ****************************************
     public static final int SNP_INDEX = 1;
     public static final int SNPSELECTED_INDEX = 0;
     
     //*** constructors ******************************************
     public RemainCorrelationTableModel() {
        super();
     }
     
     public RemainCorrelationTableModel(Vector columnName,Vector data) {
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
             case SNP_INDEX:
                return false;
             case SNPSELECTED_INDEX:
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
             case SNP_INDEX:
                 return String.class;
             case SNPSELECTED_INDEX:
                 return Boolean.class;
             default:
                 return Object.class;
         }
     }

}
