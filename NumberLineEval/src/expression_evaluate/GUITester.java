package expression_evaluate;


import java.awt.Container;
import java.awt.Frame;
import javax.swing.JFrame;
import javax.swing.JPanel;
import expression_evaluate.NumberLine;


public class GUITester extends JFrame{

	// *********************************************************
    // ******************** Constructor ************************
    // *********************************************************

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

    public GUITester()
    {
      setTitle( "Tester" );
      setContentPane( createContentPane() );
      show();
    }
	
	
	// *********************************************************
    // ******************** Utility Methods ********************
    // *********************************************************

    private Container createContentPane()
    {
        JPanel mainPanel = new JPanel();
        NumberLine numberLine = new NumberLine(0, 10);
        mainPanel.add(numberLine);
        mainPanel.add(numberLine.ChosenNumPanel());
        //mainPanel.add(numberLine.getNumberlinePanel());
        //Expression expObj = new Expression();
        //expObj.ParseExpression(numberLine.GetExpression());
	    return mainPanel;
    }
    
    
    
	public static void main(String[] args) {
		GUITester test = new GUITester();
		test.setDefaultCloseOperation( EXIT_ON_CLOSE );
		test.setExtendedState( Frame.MAXIMIZED_BOTH );
		test.show();
	    System.out.println("gui main...");
	}

}
