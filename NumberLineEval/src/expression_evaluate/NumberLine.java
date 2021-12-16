package expression_evaluate;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.util.*;

/*******************************************************************
Base class: draws a numberline based on user's input for expression
@author Sibika Silwal
@since 6/30/2021
******************************************************************** */

public class NumberLine extends JPanel implements ActionListener{
	
	//*********************************************************
	// ******************** Class Variables ********************
	// *********************************************************
	
	/*Main panel that hold the input panel and numberline panel*/
	//public JPanel mMainPanel;
	
	/* Text field for user to enter the expression */
    private JTextField mInputExpression = new JTextField("");
    
	/*SetExpression button to evaluate the expression that user gives*/
    private JButton mSetExpressionBtn = new JButton("Set Expression");
    
    /*Number line number buttons */
    JButton[] mNumLineBtns = new JButton[14];
    
    /*Default color for buttons*/
	Color mbtnDefaultColor = new Color(135,206,235);
	
    /* (x1,y1) and (x2,y2) points to draw the number line's line */
    private static int x1 = 10, x2 = 685, y1 = 205, y2 = 205;
    
    /* Height of the number marker in number line */
    private static final int VERT_LINE_HEIGHT = 15;
    
    /* Start value for the number line */
    private double START = 0.0;

    /* End value for the number line */
    private double END = 10.0;
    
    /* Interval between the numbers plotted in the numberline*/
    private double INTERVAL = 1.0;
    
    /* Gap between the numbers in number line */
    public static double GAP = (x2 -x1)/15.0;
    
    /* Expression accepted from the user */
    String mExpression = "";
    
    
    /** An object of Expression class to parse expression and get its data */
    Expression mExpressionObject;// = new Expression();
    
    /** An object of ClickListener class to parse expression and get its data */
    ClickListener mClickListenerObject;
    
    /*Numbers chosen by the student for the expression*/
    private Vector<Integer> mChosenNumbers;
    
    /*Panel that displays the numbers chosen by the student*/
    private JPanel mChosenNumPanel;
    
    /*Label for the chosen line panel*/
    JLabel mChosenNumLabel = new JLabel("The numbers chosen: ");
    
    
	// *********************************************************
    // ******************** Constructor ************************
    // *********************************************************
    
    /*Empty constructor*/
    public NumberLine() {}
    
	public NumberLine(int  a_startValue, int a_endValue) {
		//mMainPanel = new JPanel(new GridBagLayout());
		mExpressionObject = new Expression();
		mClickListenerObject = new ClickListener();
		mChosenNumPanel = new JPanel(new GridLayout(8, 1));
		mChosenNumPanel.add(mChosenNumLabel);
		mChosenNumbers = new Vector<>();
		
		for(int i =0; i<14; i++) {
        	mNumLineBtns[i] = new JButton();
		}
		
		GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(0,0,15,15);
        
        constraints.gridx =5;
        constraints.gridy =5; //3
        
        this.setPreferredSize(new Dimension(700, 300));
	    this.setBorder(BorderFactory.createTitledBorder(
	          BorderFactory.createEtchedBorder(), "NumberLine "));
	    
        GridLayout layout = new GridLayout(3,3);
        layout.setVgap(10); //NEED THIS
        this.setLayout(layout);
        
		//mMainPanel.add(inputGUI(), constraints);
        this.add(inputGUI(), constraints);
		constraints.gridy = GridBagConstraints.RELATIVE;//8;
		this.add(Box.createHorizontalStrut(1)); //NEED THIS
		
		this.add(SetUpNumLine(), constraints);
		
	}
	
	
	// *********************************************************
    // ******************** Paint - View ***********************
    // *********************************************************
	
    /** 
    This function overrides the paintComponent() method to draw the number line on our frame
    @param a_graphics_obj is a Graphics class instance which is used to call all the drawing methods of the Graphics class
    */
    @Override
    public void paintComponent(Graphics a_graphics_obj) {
        // call super method
        super.paintComponent(a_graphics_obj);
        //setOpaque(true);
        a_graphics_obj.setColor(Color.red);
        a_graphics_obj.drawString("<", x1-2, y1+4);
        a_graphics_obj.drawLine(x1, y1, x2, y2);
        a_graphics_obj.drawString(">", x2-2, y2+4);
        
        int x = x1, number = (int)START;
        double number2 = START;
        
        for(int i = 0 ; i<14; i++) {
        	x = (int) (x + GAP);
        	drawNumberAndLine(a_graphics_obj, mExpressionObject.round(number2), x, y1, mNumLineBtns[i]);
        	number2 = number2 +INTERVAL;//INTERVAL;
        	
        }
        
        boolean[] shade = mExpressionObject.NeedToShade(mExpressionObject.GetConditionValues(), mExpressionObject.GetComparisonOperators());
        for(int i = 0; i < mExpressionObject.GetConditionsCount(); i++) {
        	mExpressionObject.DrawRange(a_graphics_obj, mExpressionObject.GetConditionValues().get(i), mExpressionObject.GetComparisonOperators().get(i), shade[i]);
        }
        //mExpressionObject.ResetVariables();
    }
    
    
    // *********************************************************
    // ******************** actionPerformed - Controller *******
    // *********************************************************

    /**
    Overriding actionPerformed() method to define the click listeners for clickable elements on this class.
    @param ActionEvent class instance
    */
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == mSetExpressionBtn){
            try{
            	mExpressionObject.ResetVariables();
                mExpression = mInputExpression.getText();
                mExpressionObject.ParseExpression(mExpression);
                double numLineData[] = mExpressionObject.GetNumLineData();
            	END= numLineData[0];          //upperRangeValue
                START= numLineData[1];       //lowerRangeValue
                INTERVAL = numLineData[2];  //interval
                System.out.println("expression: " + mExpression);
                System.out.println("start, end, interval, gap: " + START + " , "+END+" , "+INTERVAL+" , "+GAP);
            }catch(Exception ex){
            	System.out.println("in catch block...");
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Please enter all integer values",
               "Type Error", JOptionPane.ERROR_MESSAGE);
                //System.out.println("Please enter an integer value");
                //throw;
            }
            mChosenNumPanel.removeAll();
            mChosenNumPanel.revalidate();
            mChosenNumPanel.repaint();
            mChosenNumPanel.add(mChosenNumLabel);
            this.repaint();
        }
        
        switch (e.getActionCommand()) {
        	case "0":
	        	 mClickListenerObject.MarkBtn(mNumLineBtns[0], mExpressionObject);
	        	 mChosenNumbers.add(Integer.parseInt(mNumLineBtns[0].getText()));
	        	 mChosenNumPanel.add(new JLabel(mNumLineBtns[0].getText()));	 
	        	 break;
        	case "1":
	        	 mClickListenerObject.MarkBtn(mNumLineBtns[1], mExpressionObject);
	        	 mChosenNumbers.add(Integer.parseInt(mNumLineBtns[1].getText()));
	        	 mChosenNumPanel.add(new JLabel(mNumLineBtns[1].getText()));
	        	 break;
        	case "2":
        		 mClickListenerObject.MarkBtn(mNumLineBtns[2], mExpressionObject);
        		 mChosenNumbers.add(Integer.parseInt(mNumLineBtns[2].getText()));
        		 mChosenNumPanel.add(new JLabel(mNumLineBtns[2].getText()));
        		 break;
        	case "3":
        		 mClickListenerObject.MarkBtn(mNumLineBtns[3], mExpressionObject);
        		 mChosenNumbers.add(Integer.parseInt(mNumLineBtns[3].getText()));
        		 mChosenNumPanel.add(new JLabel(mNumLineBtns[3].getText()));
        		 break;
        	case "4":
        		 mClickListenerObject.MarkBtn(mNumLineBtns[4], mExpressionObject);
        		 mChosenNumbers.add(Integer.parseInt(mNumLineBtns[4].getText()));
        		 mChosenNumPanel.add(new JLabel(mNumLineBtns[4].getText()));
        		 break;
        	case "5":
        		 mClickListenerObject.MarkBtn(mNumLineBtns[5], mExpressionObject);
        		 mChosenNumbers.add(Integer.parseInt(mNumLineBtns[5].getText()));
        		 mChosenNumPanel.add(new JLabel(mNumLineBtns[5].getText()));
        		 break;
        	case "6":
        		 mClickListenerObject.MarkBtn(mNumLineBtns[6], mExpressionObject);
        		 mChosenNumbers.add(Integer.parseInt(mNumLineBtns[6].getText()));
        		 mChosenNumPanel.add(new JLabel(mNumLineBtns[6].getText()));
        		 break;
        	case "7":
        		 mClickListenerObject.MarkBtn(mNumLineBtns[7], mExpressionObject);
        		 mChosenNumbers.add(Integer.parseInt(mNumLineBtns[7].getText()));
        		 mChosenNumPanel.add(new JLabel(mNumLineBtns[7].getText()));
        		 break;
        	case "8":
	       		 mClickListenerObject.MarkBtn(mNumLineBtns[8], mExpressionObject);
	       		 mChosenNumbers.add(Integer.parseInt(mNumLineBtns[8].getText()));
	       		 mChosenNumPanel.add(new JLabel(mNumLineBtns[8].getText()));
	       		 break;
        	case "9":
	       		 mClickListenerObject.MarkBtn(mNumLineBtns[9], mExpressionObject);
	       		 mChosenNumbers.add(Integer.parseInt(mNumLineBtns[9].getText()));
	       		 mChosenNumPanel.add(new JLabel(mNumLineBtns[9].getText()));
	       		 break;
        	case "10":
	       		 mClickListenerObject.MarkBtn(mNumLineBtns[10], mExpressionObject);
	       		 mChosenNumbers.add(Integer.parseInt(mNumLineBtns[10].getText()));
	       		 mChosenNumPanel.add(new JLabel(mNumLineBtns[10].getText()));
	       		 break;
        	case "11":
	       		 mClickListenerObject.MarkBtn(mNumLineBtns[11], mExpressionObject);
	       		 mChosenNumbers.add(Integer.parseInt(mNumLineBtns[11].getText()));
	       		 mChosenNumPanel.add(new JLabel(mNumLineBtns[11].getText()));
	       		 break;
        	case "12":
       		 	mClickListenerObject.MarkBtn(mNumLineBtns[12], mExpressionObject);
       		    mChosenNumbers.add(Integer.parseInt(mNumLineBtns[12].getText()));
       		    mChosenNumPanel.add(new JLabel(mNumLineBtns[12].getText()));
       		    break;	            
        	case "13":
       		 	mClickListenerObject.MarkBtn(mNumLineBtns[13], mExpressionObject);
       		    mChosenNumbers.add(Integer.parseInt(mNumLineBtns[13].getText()));
       		    mChosenNumPanel.add(new JLabel(mNumLineBtns[13].getText()));
       		    break;
        	
        }
        mChosenNumPanel.revalidate();
    }
	
	// *********************************************************
    // ******************** Selectors **************************
    // *********************************************************
	public JPanel getNumberlinePanel() {
		return this;
	}
	
	public String GetExpression() {return mExpression;}
	
	// *********************************************************
    // ******************** Utility Methods ********************
    // *********************************************************
	private JPanel inputGUI() {
		JPanel getExpressionPanel = new JPanel(new GridBagLayout());
		
		GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(0,0,15,15);

        constraints.gridx =0;
        constraints.gridy =0;
        
		JLabel expressionLabel = new JLabel("Please enter the expression to evaluate: ");	
		getExpressionPanel.add(expressionLabel, constraints);
		
		constraints.gridx = 1;
		mInputExpression.setPreferredSize( new Dimension( 200, 24 ));
		getExpressionPanel.add(mInputExpression, constraints);
		
		constraints.gridx = 0;
		constraints.gridy = 1;
		getExpressionPanel.add(mSetExpressionBtn, constraints);
		mSetExpressionBtn.addActionListener(this);
		
		return getExpressionPanel;
	}
	
	private JPanel SetUpNumLine() {
		JPanel numLine = new JPanel();//(new GridLayout(1, 14));
		
		
		FlowLayout layout = new FlowLayout();
        layout.setHgap(20); //NEED THIS
        layout.setVgap(4);
        numLine.setLayout(layout);
		
        int number = (int)START;
        for(int i =0; i<14; i++) {
        	mNumLineBtns[i].setActionCommand(Integer.toString(i));
        	mNumLineBtns[i].setMargin(new Insets(0,0,0,0));
        	mNumLineBtns[i].setPreferredSize( new Dimension( 25, 25 ));
        	number = number  + (int)INTERVAL;
        	mNumLineBtns[i].setText(Integer.toString(number));
        	numLine.add(mNumLineBtns[i]);//, constraints);
        	mNumLineBtns[i].addActionListener(this);
        	//constraints.gridx = constraints.gridx + 5;
        }
		return numLine;
	}
	
	public JPanel ChosenNumPanel() {
		return mChosenNumPanel;
	}
	
	/** 
    This function draws the actual numbers and the vertical dividers in the number line
    @param a_graphics_obj is a Graphics class instance which is used to call all the drawing methods given by the Graphics class
    @param number is an integer which represents the number on number line to be drawn
    @param x is an integer which is the x position for the vertical divider line in the number line
    @param y is an integer which is the y position for the vertical divider line in the number line
    */
    private void drawNumberAndLine(Graphics a_graphics_obj, int number, int x, int y, JButton aButton) {
        int y0 = y - (VERT_LINE_HEIGHT/2);
        int y1 = y + (VERT_LINE_HEIGHT/2);
        
        a_graphics_obj.drawLine(x, y0, x, y1);
        
        aButton.setText(String.valueOf(number));
        aButton.setFont(aButton.getFont().deriveFont(10.0f));
        aButton.setBackground(mbtnDefaultColor);
        aButton.setForeground(Color.black);        
   }
	
	public static void main( String args[] ) {}
	
}
