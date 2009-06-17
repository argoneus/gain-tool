/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gain;

import org.sourceforge.jlibeps.epsgraphics.EpsGraphics2D;
import prefuse.Display;
import prefuse.Visualization;


/**
 *
 * @author DTian
 */
public class EPSDisplay extends Display {
    
    public EPSDisplay(){
        super();
    }
    
    public EPSDisplay(Visualization vis){
        super(vis);
    }

    public String getEPSText() {
		final EpsGraphics2D g = new EpsGraphics2D();
		g.setFont(getFont());
		invalidate();
		printComponent(g);
		// paintDisplay(g, d);
		return g.toString();
	}
}
