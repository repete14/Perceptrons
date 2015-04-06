/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package perceptrons;

/**
 *
 * @author Peter
 */
public class Instance {
    
    char Classification;
    float[] feature = new float[16];
    
    public Instance(){
        
    }
    
    public Instance(String[] input){
        SetData(input);
    }
    
//    takes in location of data csv and parses into the feature set and class.
    private void SetData(String[] input){
        Classification = input[0].charAt(0);
        for(int i=1;i<input.length;i++){
            feature[i-1] = (Float.parseFloat(input[i])/15);
        }
    }
    
//    returns the set of features of the instance as a string, for debugging
    public String StringOutput(){
        String output =  Character.toString(Classification);
        
        for(int i=0;i<feature.length;i++){
            output = output + ",";
            output = output + Float.toString(feature[i]);
        }
        return(output);
    }
    
//    as above, but for the classification.
    public char GetValueChar(){
        return Classification;
    }
    
//    returns the classification as an int value.
    public int GetValueInt(){
        return ((int)Classification - 65);
        
    }
    
//    accessor method for getting the features.
    public float GetFeatureAt(int f){
        return feature[f];
    }
    
}
