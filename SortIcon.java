/*
 * this class is used to generate an up/down arrow Icon to show the sort order;
 * Being called only in SortManager.java in this project
 * 
 */

package gain;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import javax.swing.Icon;
import javax.swing.UIManager;


public class SortIcon implements Icon {
    
    //local variables
    private  int size = 12;
    public static final int UP = 0;
    public static final int DOWN = 1;
    private int direction;
    
    //constructor
    public SortIcon(int i) {
        direction = i;
    }
    
    //get the Icon height
    public int getIconHeight() {
        return size;
    }
    //get the Icon width
    public int getIconWidth() {
        return size;
    }
    
    //generate the arrow Icon
    public void paintIcon(Component component, Graphics g, int i, int j) {
        int k = i + size / 2;
        int l = i + 1;
        int i1 = (i + size) - 2;
        int j1 = j + 1;
        int k1 = (j + size) - 2;
        Color color = (Color) UIManager.get("controlDkShadow");
        if (direction == 0) {
            g.setColor(Color.white);
            g.drawLine(l, k1, i1, k1);
            g.drawLine(i1, k1, k, j1);
            g.setColor(color);
            g.drawLine(l, k1, k, j1);
        } else {
            g.setColor(color);
            g.drawLine(l, j1, i1, j1);
            g.drawLine(l, j1, k, k1);
            g.setColor(Color.white);
            g.drawLine(i1, j1, k, k1);
        }//end else
    }//end of paintIcon
}
