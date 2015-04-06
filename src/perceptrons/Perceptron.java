/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package perceptrons;

import java.util.Random;
/**
 *
 * @author Peter
 */
public class Perceptron {
 
    float classification1;
    float classification2;
    float[] weight;
    
    
    public Perceptron(){
        
    }
       
//    initialization.
    public Perceptron(int target){
        classification1 = 0;
        classification2 = target;
        weight = new float[17];
        
//        sets random weights between -1 and +1
        Random rng = new Random();
        for(int i=0;i<weight.length;i++){
            weight[i] = (rng.nextFloat() * 2) - 1;
        }
               
    }
    
//    returns the classification of the input features
    public float Classify(float[] input){
        
        float result = 0;
                
        for(int i=0;i<input.length+1;i++){
            if(i==0){
//                threshold value
                result += weight[0];
            }else{
//                adding Weights * input
                result += (input[i-1]*weight[i]);
            }
        }
        
//        return signum function of the summation
        return java.lang.Math.signum(result);
        
    }
    
//    runs classify but with an instance as the input
    public float Classify(Instance instance){
        return Classify(instance.feature);
    }
    
//    returns what the actual classification should be, given an instance.
//    +1 if it is A, -1 if it is the target
    public float TrueClassify(Instance instance){
            if (instance.GetValueInt() == classification1){
                return 1;
            }else{
                return -1;
            }
    }
    
//    given a set of instnaces, it returns the confusion matrix for that set.
    public float[][] GetConfusionMatrix(Instance[] sets){
        float actual;
        float result;
        float[][] confusionMatrix = new float[2][2];
//        [Actual Pos or Neg][Outcome Pos or Neg]
//        in decending order: TP,FP,TN,FN
        confusionMatrix[0][0]=0;
        confusionMatrix[1][0]=0;
        confusionMatrix[1][1]=0;
        confusionMatrix[0][1]=0;
        
//        for each of the instances, compares the true result to the ouput
//        and adds it to the total for each matrix spot.
        for(int i=0;i<sets.length;i++){          
            actual = TrueClassify(sets[i]);           
            result = Classify(sets[i]);
            
            if(actual == 1){
                if(actual == result){
                    confusionMatrix[0][0]++;
                }else{
                    confusionMatrix[0][1]++;
                }
            }else{
                if(actual == result){
                    confusionMatrix[1][1]++;
                }else{
                    confusionMatrix[1][0]++;
                }
            }
        }        

        return confusionMatrix;
    }
    
//    generates and prints out the ROC curve data, given an input set.
//    due to not knowing where the weights will fall, it runs the threshold from
//    -100 to 100 with .01 intervals.
    public void GenerateROC(Instance[] sets){
        float TPR;
        float FPR;
        float[][] cf;
        
        for(float t=-100;t<=100;t+=0.01){ 
            weight[0]=t;
            
            cf = GetConfusionMatrix(sets);
            
            TPR = cf[0][0]/(cf[0][0]+cf[0][1]);
            FPR = cf[1][0]/(cf[1][1]+cf[1][0]);
            
            System.out.println(((char) (classification2 +65)) + "\t" + t + "\t" + TPR + "\t" + FPR);
        }
        
    }
    
    
 
}