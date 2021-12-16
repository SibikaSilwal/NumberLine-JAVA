package expression_evaluate;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;
import java.awt.Color;
import javax.swing.JButton;
import javax.swing.JOptionPane;

public class ClickListener extends NumberLine{
	
	// *********************************************************
    // ******************** Class Variables ********************
    // *********************************************************
	
	/*Color for clicked button*/
	Color mbtnClickedColor = Color.orange;
	
	/*Color for clicked button with wrong value*/
	Color mbtnWrongColor = Color.red;
	
	/*Color for clicked button with correct value*/
	Color mbtnRightColor = Color.green;
	
	/*Color for clicked button label*/
	Color mbtnLabelColor = Color.white;
	
	String mFeedBack;
	
	/*Expression received from NumberLine class*/
	//String mExpression;

	// *********************************************************
    // ******************** Constructor ************************
    // *********************************************************
    public ClickListener(){
    	mFeedBack = "";
    	//mExpression = GetExpression();
    }
	
    
    public void MarkBtn(JButton abtn, Expression a_expObject) {
    	abtn.setBackground(mbtnClickedColor);
    	abtn.setForeground(mbtnLabelColor);
    	System.out.println("calling mark btn....");
    	if(EvaluateExpValue(a_expObject, Integer.parseInt(abtn.getText())) == true) {
    		abtn.setBackground(mbtnRightColor);
    	}else {
    		abtn.setBackground(mbtnWrongColor);
    	}
    	
    }
    
    
    // Checks if the number selected by the student is a correct value that falls in the range or not
    public boolean EvaluateExpValue(Expression a_expObject, int a_chosenVAL) {
    	
    	boolean chosenVALCorrect = false;
    	
    	System.out.println("In evaluate exp value: condition count: " + a_expObject.GetConditionsCount());
    	
    	// if there is no logical operator (&&, ||, ==, !=) in the expression
    	
    	// if there is only one condition in the expression
    	if(a_expObject.GetConditionsCount() == 1) {
    		chosenVALCorrect = CheckValue(a_expObject.GetComparisonOperators().get(0), a_chosenVAL, a_expObject.GetConditionValues().get(0));
    	}
    	if(a_expObject.GetConditionsCount() == 2) {
    		chosenVALCorrect = CheckCondition(
    				a_expObject.GetLogicalOperators().get(0),
    				CheckValue(a_expObject.GetComparisonOperators().get(0), a_chosenVAL, a_expObject.GetConditionValues().get(0)),
    				CheckValue(a_expObject.GetComparisonOperators().get(1), a_chosenVAL, a_expObject.GetConditionValues().get(1))
    				);
    	}
    	
    	if(a_expObject.GetConditionsCount() > 2) {
    		chosenVALCorrect = CheckConditions(a_expObject.GetLogicalOperators(), GetTurthValues(a_expObject, a_chosenVAL));
    	}
    	
    	
    	return chosenVALCorrect;  	
    }
    
    // checks the selected value with "one" condition in the expression
    private boolean CheckValue(String a_compOperand, int a_chosenVAL, int a_comparingVAL) {
    	
    	boolean valCorrect = false;
    	
    	switch(a_compOperand) {
			case "<":
				valCorrect = a_chosenVAL < a_comparingVAL ? true : false;
				break;
			
			case "<=":
				valCorrect = a_chosenVAL <= a_comparingVAL ? true : false;
			    break; 
			    
			case ">":
				valCorrect = a_chosenVAL > a_comparingVAL ? true : false;
			    break;  
			case ">=":
				valCorrect = a_chosenVAL >= a_comparingVAL ? true : false;
			    break;
			    
			case "==":
				valCorrect = a_chosenVAL == a_comparingVAL ? true : false;
			    break;
			    
			case "!=":
				valCorrect = a_chosenVAL != a_comparingVAL ? true : false;
			    break; 
			
			default:
				valCorrect = false;
		}
    	
    	System.out.println("checking only value: "+valCorrect);
    	
    	return valCorrect;
    }
   
    // takes two truth values and the logical operator between them and returns true or false after evaluating them
    private boolean CheckCondition(String a_logicalOperator, boolean a_truthVAL1, boolean a_truthVAL2) {
    	
    	boolean conditionCorrect = false;
    	
    	switch(a_logicalOperator) {
    	
	    	case "&&":
	    		conditionCorrect = a_truthVAL1 && a_truthVAL2 ? true : false;
	    		break;
	    		
	    	case "||":
	    		conditionCorrect = a_truthVAL1 || a_truthVAL2 ? true : false;
	    		break;
	    		
	    	default:
	    		conditionCorrect = false;
	    		
    	}
    	
    	System.out.println("checking && || : " + (true && true) + " " + (false || true));
    	
    	return conditionCorrect;
    	
    }
    
    private boolean[] GetTurthValues(Expression a_expObject, int a_chosenVAL) {
    	boolean[] truthValues = new boolean[a_expObject.GetConditionsCount()];
    	
    	for(int i = 0; i<a_expObject.GetConditionsCount(); i++) {
    		truthValues[i] = CheckValue(a_expObject.GetComparisonOperators().get(i), a_chosenVAL, a_expObject.GetConditionValues().get(i));
    	}
    	
    	return truthValues;
    }
    
    // this function accepts an array of logical operators and truth values and returns the final boolean values after evluating all
    // this function is used for expression that have more then 2 conditions.
    private boolean CheckConditions(ArrayList<String> aLogicalOps, boolean[] aTruthValues) {
    	boolean conditionCorrect = CheckCondition(aLogicalOps.get(0), aTruthValues[0], aTruthValues[1]);
    	
    	for(int i = 1; i < aLogicalOps.size(); i++) {
    		switch(aLogicalOps.get(i)) {
        	
	    	case "&&":
	    		conditionCorrect = conditionCorrect && aTruthValues[i+1] ;
	    		break;
	    		
	    	case "||":
	    		conditionCorrect = conditionCorrect || aTruthValues[i+1] ;
	    		break;
	    		
	    	default:
	    		conditionCorrect = false;
	    		
    		}
    	}  	
    	return conditionCorrect;
    }
    
    
    //functions that checks if the user has selected all the boundary values and lets the student know about what values 
    // have already been selected
    // NOTE: this function has not been tested and was still under progress
    public boolean AllValuesSelected(Vector<Integer> aChosenVals, int aUpperRangeVal, int aLowerRangeVal) {
    	boolean allValSelected = false;
    	boolean[] allChosen = new boolean[5];
    	Arrays.fill(allChosen, Boolean.FALSE);
    	
    	for(int i =0; i<aChosenVals.size(); i++) {
    		if(aChosenVals.get(i)<aLowerRangeVal) {
    			allChosen[0] = true;
    			mFeedBack = mFeedBack + "\n a value less than " + aLowerRangeVal + " chosen.";
    		}
    		if(aChosenVals.get(i) < aLowerRangeVal && aChosenVals.get(i) > aUpperRangeVal) {
    			allChosen[1] = true;
    			mFeedBack = mFeedBack + "\n A value between " + aLowerRangeVal + " and " + aUpperRangeVal + " chosen.";
    		}
    		if(aChosenVals.get(i)<aLowerRangeVal) {
    			allChosen[3] = true;
    			mFeedBack = mFeedBack + "\n A value greater than " + aUpperRangeVal + " chosen.";
    		}
    		if(aChosenVals.get(i) == aLowerRangeVal) {
    			allChosen[4] = true;
    			mFeedBack = mFeedBack + "\n Lower boundry value chosen.";
    		}
    		if(aChosenVals.get(i) == aUpperRangeVal) {
    			allChosen[5] = true;
    			mFeedBack = mFeedBack + "\n Upper boundry value chosen.";
    		}
    		
    	}
    	
    	allValSelected = allChosen[0] && allChosen[1] && allChosen[2] && allChosen[3] && allChosen[4];
    	return allValSelected;
    }
	
	public static void main(String[] args) {}

}
