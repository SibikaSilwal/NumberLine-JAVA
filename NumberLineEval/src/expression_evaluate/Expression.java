package expression_evaluate;

import java.awt.Graphics;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*******************************************************
This class parses the expression string recived from number 
line and provides the numberline graph information back to
NumberLine class to draw.
@author Sibika Silwal
@since 09/12/2021
******************************************************** */

public class Expression extends NumberLine{

	
	// *********************************************************
    // ******************** Class Variables ********************
    // *********************************************************

    /** Expression accepted from the user */
    String mExpression;
    
    /** Map to store the information about the input expression */
    private Map<String, String> mExpressionData = new HashMap<>();
    private ArrayList<String> mlogicalOperators = new ArrayList<>();
    private ArrayList<String> mVariables = new ArrayList<>();
    private ArrayList<String> mComparisonOperators = new ArrayList<>();
    private ArrayList<Integer> mValues = new ArrayList<>();
    
    private Color mLTarrow = new Color(34,139,34);
    private Color mGTarrow = new Color(123,104,238);
    private Color mEqual = new Color(0,255,0);
    private Color mNotEqual = new Color(139,0,0);
    
    /** count of logical operators in the expression*/
    Integer mLogicalOperatorCount = 0;
    /** Count of conditions in the expression*/
    Integer mConditionCount = 0;   
    
    /** upper range value in the expression*/
    Double mUpperValue = 0.0;
    /** lower range value in the expression*/
    Double mLowerValue = 0.0;
    /** Interval calculated from upper and lower range values*/
    Double mInterval = 1.0;
    
    public int initial =0;
    public int last =0;
    // *********************************************************
    // ******************** Constructor ************************
    // *********************************************************
    public Expression(){
    	mExpression = "";   
    }
	
    // *********************************************************
    // ******************** Selectors **************************
    // *********************************************************
    public int GetConditionsCount() {return mConditionCount;}
    public int GetLogicalOperatorCount() {return mLogicalOperatorCount;}
    
    public ArrayList<Integer> GetConditionValues(){ return mValues; }
    
    public ArrayList<String> GetComparisonOperators(){return mComparisonOperators;}
    
    public ArrayList<String> GetLogicalOperators(){return mlogicalOperators;}
    
    // *********************************************************
    // ******************** Mutators = setter methods***************************
    // *********************************************************
    void ResetVariables() {
    	mExpressionData.clear();
        mVariables.clear();
        mComparisonOperators.clear();
  	  	mValues.clear();
  	  	mlogicalOperators.clear();
        mConditionCount = 0;
        mLogicalOperatorCount = 0;
        mUpperValue = 0.0;
        mLowerValue = 0.0;
    }
    
    // *********************************************************
    // ******************** Utility Methods ********************
    // *********************************************************
    
    // this function parses the expression received from the student and stores different parts of the expression in
    // different arrays
    void ParseExpression(String a_expression){
      
      a_expression = a_expression.replaceAll("\\s", "");
      

      String logicalOperatorsPattern = "(&&)|([|][|])" ;
      
      String conditionPattern = "(\\w+)(([>]=?)|([<]=?)|([=][=])|([!][=]))((-?)\\d+)";
      
      Pattern logicalPatternObj = Pattern.compile(logicalOperatorsPattern);
      Pattern conditionPatternObj = Pattern.compile(conditionPattern);
      
      Matcher logicalMatcher = logicalPatternObj.matcher(a_expression);
      Matcher conditionMatcher = conditionPatternObj.matcher(a_expression);
      
      while(logicalMatcher.find()) {
    	  mlogicalOperators.add(logicalMatcher.group());
    	  
    	  mLogicalOperatorCount++;
    	  mExpressionData.put("logicalOperator", logicalMatcher.group());   	  
      }
     
      while(conditionMatcher.find()) {
    	  mVariables.add(conditionMatcher.group(1));
    	  mComparisonOperators.add(conditionMatcher.group(2));
    	  mValues.add(Integer.parseInt(conditionMatcher.group(7)));
    	  
    	  mConditionCount++;
    	  mExpressionData.put("variable", conditionMatcher.group(1));
    	  mExpressionData.put("operators", conditionMatcher.group(2));
    	  mExpressionData.put("value", conditionMatcher.group(7));  	  
      } 
      
      initial = mValues.get(0);
      last = mValues.get(mValues.size()-1);
      //REGEX: (!?)([(])(\w+)(([>]=?)|([<]=?))(\d+)((&&)|([|][|]))(\w+)(([>]=?)|([<]=?))(\d+)([)])
    }
    
    /*round to the nearest integer*/
    public int round(double d){
        double dAbs = Math.abs(d);
        int i = (int) dAbs;
        double result = dAbs - (double) i;
        if(result<0.5){
            return d<0 ? -i : i;            
        }else{
            return d<0 ? -(i+1) : i+1;          
        }
    }
    
    // sets the upper and lower range for the number line after parsing the expression
    private void SetRangeValues(int aValue1, int aValue2) {
    	int range = 0, addRange = 0;
    	if(aValue1 > aValue2) {
			range = aValue1 - aValue2;
			if(range<9) {
				addRange = (11 - range)/2;
				aValue1 = aValue1 + addRange;
				aValue2 = aValue2 - addRange;
			}
			mUpperValue = (double) round(aValue1 + (0.2000 * (aValue1 - aValue2)));
			mLowerValue = (double) round(aValue2 - (0.2000 * (aValue1 - aValue2)));
		}else {
			range = aValue2 - aValue1;
			if(range<9) {
				addRange = (11 - range)/2;
				aValue2 = aValue2 + addRange;
				aValue1 = aValue1 - addRange;
			}
			mUpperValue = (double) round(aValue2 + (0.2000 * (aValue2 - aValue1)));
			mLowerValue = (double) round(Math.ceil(aValue1 - (0.2000 * (aValue2 - aValue1))));
		}
    	//compute the interval based on the upper value and lower value
		mInterval = (double) ((mUpperValue - mLowerValue)/14.00000000); // Math.ceil = round
		// if the interval is 0, make the interval 1
		if((int)(mInterval*1)==0) {
			mInterval = 1.0;
		}
		System.out.println("range: "+range+" value1: "+aValue1+" value2: "+aValue2);
    }
    
    /*********** Gets ranges for number line ************************************/
    public double[] GetNumLineData(){
 	
    	if(mComparisonOperators.contains("<")||mComparisonOperators.contains(">")
    		||mComparisonOperators.contains("<=")||mComparisonOperators.contains(">=")
    		||mComparisonOperators.contains("==")||mComparisonOperators.contains("!=")) {
    		
    		//if there is only one condition in the expression
    		if(mConditionCount.equals(1)) {
    			int value = mValues.get(0);
    			mUpperValue = value + 6.0;
    			mLowerValue = value - 6.0;
    		}
    		
    		//if there are two conditions in the expression
    		if(mConditionCount.equals(2)) {
    			int value1 = mValues.get(0);
    			int value2 = mValues.get(1);
    			
    			SetRangeValues(value1, value2);
    		}
    		
    		if(mConditionCount>2) {
    			//ArrayList<Integer> values = mValues;
    			ArrayList<Integer> values = new ArrayList<Integer>();
    			for( int i =0; i<mValues.size(); i++){
    				values.add(mValues.get(i));
    			}
    			Collections.sort(values);
    			int smallestValue = values.get(0);
    			int largestValue = values.get((values.size()-1));
    			
    			SetRangeValues(smallestValue, largestValue);    			
    		}
    		
			System.out.println("interval: "+ mInterval + " UR: " + mUpperValue + " LR: "+mLowerValue);
			
    	}
    	
    	double numlineRangeData[] = {mUpperValue, mLowerValue, mInterval};
    	return numlineRangeData;
    }
    
    /*populates the mShade array with T or F depending on the need to shade for a value*/
    boolean[] NeedToShade(ArrayList<Integer> aValues, ArrayList<String> aComparisonSymbols) {
    	int maxIndexLT = -1;
    	int minIndexGT = -1;
    	int maxValueLT = -1000; //maxValue for less than
    	int minValueGT = 10000; //minValue for greater than
    	for(int i =0; i<aValues.size();i++) {
    		if(aComparisonSymbols.get(i).equals("<") || aComparisonSymbols.get(i).equals("<=")) {
    			if(aValues.get(i)>maxValueLT) {
    				maxValueLT = aValues.get(i);
    				maxIndexLT = i;
    			}
    		}
    		if(aComparisonSymbols.get(i).equals(">") || aComparisonSymbols.get(i).equals(">=")) {
    			if(aValues.get(i)<minValueGT) {
    				minValueGT = aValues.get(i);
    				minIndexGT = i;
    			}
    		}
    	}   	
    	boolean[] shade = new boolean[aValues.size()];
    	if(maxIndexLT!=-1)shade[maxIndexLT]=true;
    	if(minIndexGT!=-1)shade[minIndexGT]=true;
    	
    	return shade;
    }
    
    // shades the range as provided in the function parameter
    void ShadeRange(Graphics a_graphics_obj, int a_shadeWidth, int x1, int y1, int y2, int a_loop) {
    	for (int i = 0; i < a_loop; i++) {
        	a_graphics_obj.drawLine((x1+(a_shadeWidth*i)), y1, (x1+(a_shadeWidth*(i+1))), y2);
        }
    }
    
    // draws the arrow diagram in the numberline for each condition in the expression
    void DrawRange(Graphics a_graphics_obj, int a_value, String a_Symbol, boolean a_shade) {
    	int loop = 0; 
    	double m = 1.0; 
    	double numOfIntervals = (((a_value-mLowerValue)/mInterval) + 1);
    	int a1 = (int) (NumberLine.GAP * numOfIntervals) + 10; //+4 for precision on number line
    	int a2 = a1;
    	int b1 = 205; int b2 = 185; // a=x, b=y
    	double secWidth = (b1-b2)/m;
    	int shadex1;
    	
    	if(a_Symbol.equals("<")||a_Symbol.equals("<=")||a_Symbol.equals(">")||a_Symbol.equals(">=")) {
    		if(a_Symbol.equals("<")) {
        		a1 = a1 - 8;
        		a2 = 10;
        		b2 = b2 + (mValues.indexOf(a_value))*4;
        		a_graphics_obj.setColor(mLTarrow);
        		a_graphics_obj.drawString("<", a2-2, b2+4);
        		secWidth = (b1-b2)/m;
        		loop = (int) ((a1-a2)/secWidth);
        		// to draw the shades under the arrows 
        		if(mLogicalOperatorCount.equals(1) && mlogicalOperators.get(0).equals("&&")) {
        			//shade for &&
        			secWidth= -secWidth;
        			shadex1 = a1;
        		}else {
        			shadex1=a2;
        		}       		        
        	}
        	else if(a_Symbol.equals("<=")) {
        		a2 = 10;
        		b2 = b2 + (mValues.indexOf(a_value))*4;
        		a_graphics_obj.setColor(mLTarrow);
        		a_graphics_obj.drawString("<", a2-2, b2+4);
        		secWidth = (b1-b2)/m;
        		loop = (int) ((a1-a2)/secWidth);
        		
        		// to draw the shades under the arrows 
        		if(mLogicalOperatorCount.equals(1) && mlogicalOperators.get(0).equals("&&")) {
        			//shade for &&
        			secWidth= -secWidth; // slope = -slope to change the direction of the shades
        			shadex1 = a1;
        		}else {
        			//shade for ||       			
        			shadex1= a2;
        		} 
        	}
        	else if(a_Symbol.equals(">")) {
        		a1 = a1 + 8;
        		a2 = 680;
        		b2 = b2 - 15 - (mValues.indexOf(a_value))*5;
        		a_graphics_obj.setColor(mGTarrow);
        		a_graphics_obj.drawString(">", a2-2, b2+4);
        		secWidth = (b1-b2)/m;
        		loop = (int) ((a2-a1)/secWidth);
        		shadex1 = a1;
        	}
        	else { //symbol >=
        		a2 = 680;
        		b2 = b2 - 15 - (mValues.indexOf(a_value))*5;
        		a_graphics_obj.setColor(mGTarrow);
        		a_graphics_obj.drawString(">", a2-2, b2+4);
        		secWidth = (b1-b2)/m;
        		loop = (int) ((a2-a1)/secWidth);
        		shadex1 = a1;
        	}
    		a_graphics_obj.drawLine(a1, b1, a1, b2);
        	a_graphics_obj.drawLine(a1, b2, a2, b2);
        	
        	if (a_shade==true) ShadeRange(a_graphics_obj, (int)secWidth, shadex1, b1, b2, loop);
        	System.out.println("index is: "+ mValues.indexOf(a_value));
        	
    	}
    	
    	
    	if(a_Symbol.equals("==")||a_Symbol.equals("!=")) {
    		if(a_Symbol.equals("==")) {
        		a_graphics_obj.setColor(mEqual);
        		a_graphics_obj.drawString("X", a1-3, b1+3);
        	}
        	else {
        		a_graphics_obj.setColor(mNotEqual);
        		a_graphics_obj.drawString("X", a1-3, b1+3);
        	}
    	}
    	System.out.println("a1: "+a1+" a2: "+a2+" b1: "+ b1+" b2: "+b2);
 	
    }
    
	public static void main(String[] args) {}

}
