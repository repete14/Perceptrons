/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package perceptrons;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

/**
 *
 * @author Peter
 */
public final class LearningModule {
    
//    data as read from the csv
    Instance[] rawData = new Instance[20000];
//    data after being sorted by letter. [letter starting with A][instance]
    Instance[][] sortedData = new Instance[26][813];
//    sorted data split in half into training and test data [letter][instance]
    Instance[][] trainingData = new Instance[26][407];
    Instance[][] testData = new Instance[26][407];
//    the 25 perceptrons for letter identifications
    Perceptron[] perceptrons = new Perceptron[25];
    
//    rate of weight change per iteration
    float learningRate = (float) 0.2;
//    hard limit of number of epochs before halting learning meathod
    int epochLimit = 100000;
    
    
//    main run method. called with the string location of data
    public LearningModule(String location){
        
//        create a new perceptron for each letter differenciation, starting with B (vs.A)
        for(int i=0;i<perceptrons.length;i++){
            perceptrons[i]= new Perceptron(i+1);
        }
        
//        take in dataset, shuffle it randomly, then sort by classification
        SetData(rawData, location);
        rawData = ShuffleInstances(rawData);
        SortRawData();
        
//        devide sorted data into training data sets and test data sets. 
//        remove null arrays (because i'm really bad at this and don't remember lists).
        for(int i=0;i<sortedData.length;i++){
            SplitData(sortedData[i], trainingData[i], testData[i]);
            trainingData[i] = RemoveNulls(trainingData[i]);
            testData[i] = RemoveNulls(testData[i]);
        }      
        
//        run data through perceptrons, and get accuracy type data;
//        print heading for testing results as printed to output
        System.out.println("Perceptron Learning (at rate: " + learningRate + ")");
        System.out.println("\nPercep\ttarget\tEpochs\tTP\tFN\tFP\tTN\tAccuracy\tPrecision\tRecal");
//        train and test each perceptron on it's data sets, then print out results
        for(int i=0;i<perceptrons.length;i++){
            
            System.out.println((i+1) + "\t" + (char)(perceptrons[i].classification2 + 65) +
                                   "\t" + Train(perceptrons[i], MergeInstances(trainingData[0],trainingData[i+1]), learningRate) +
                                   "\t" + Test(perceptrons[i], MergeInstances(testData[0],testData[i+1])));
        }
        
//        generate a ROC curve for each of the Perceptrons
        System.out.println("\nPerceptron\tThreshhold\tTPR\tFPR");
        for(int i=0;i<perceptrons.length;i++){
            perceptrons[i].GenerateROC(MergeInstances(testData[0],testData[i+1]));
        }
    }
    
//    Reads in data by parsing the CSV, storing data in an "Instance" and filing into an array
    private void SetData(Instance[] instanceSet, String location) {
        BufferedReader br;
        String line;
        
        try{           
            br = new BufferedReader (new FileReader (location));           
            int i=0;
            while ((line = br.readLine()) != null){                
                instanceSet[i] = new Instance(line.split(","));                
                i++;                
            }
            
        }
        catch(IOException e){
            System.out.println(e);
        }
    }
    
//    takes in raw set of Instances and places each into the next available slot 
//    in it's letter's specific array
    public void SortRawData (){
        
        for(int i=0;i<sortedData.length;i++){
            int next = 0;
            for(int j=0;j<rawData.length;j++){
                if(rawData[j].GetValueInt() == i){
                    sortedData[i][next]=rawData[j];
                    next++;
                }
            } 
        }
        
        for(int i=0;i<sortedData.length;i++){
            sortedData[i] = RemoveNulls(sortedData[i]);
        }
              
    }
    
//    takes a given set of instances and slits into two equal sets of instances.
//    should be run after shuffleing data for best results.
//    results are separated every other one, as if dealing to two hands.
    public void SplitData (Instance[] raw, Instance[] training,Instance[] test){
        int trainingIndex = 0;
        int testIndex = 0;
        
        for(int i=0;i<raw.length;i++){
            if((i & 1) == 0){
                training[trainingIndex] = raw[i];
                trainingIndex++;
            }else{
                test[testIndex] = raw[i];
                testIndex++;
            }
        }
            
    }
    
//    prints out the data in a set of instances. Used for debugging purposes.
    public void PrintData (Instance[] instanceSet){
        for(int i=0; i<instanceSet.length; i++){
            System.out.println(instanceSet[i].StringOutput());
        }
    }
    
//    takes a set of instances and determines the frequency for each letter.
//    used for debugging purposes to ensure even data.
    public int[] AnalyseData(Instance[] instanceSet){
        int[] freq = new int[instanceSet.length];
        int total = 0;
        
        for(int i=0;i<freq.length;i++){
           freq[i] = 0; 
        }
        
        for (Instance instanceSet1 : instanceSet) {
            if(instanceSet1!=null){
                freq[instanceSet1.GetValueInt()]++;
                total++;
            } 
        }
        
        for(int i=0;i<freq.length;i++){
           System.out.println((char) (i+65) + ": " + freq[i] + ": %" + ( (float) freq[i] / total) * 100);
        }
        
        return freq;
        
    }
       
//    randomize a set of instances by random swapping of instances
    public Instance[] ShuffleInstances(Instance[] instances){
        Random rnd = new Random();
        Instance temp;
        
        for (int i = 0; i < instances.length; i++)
        {
            int index = rnd.nextInt(i + 1);
//            Simple swap
            temp = instances[index];
            instances[index] = instances[i];
            instances[i] = temp;
        }
        
        return instances;
    }
    
//    becuase it's been far to long, and i've forgotten how to do lists in a
//    nice and easy way, i had to make a method that goes through a set of
//    instances and returns a new array without any null instances.
    public Instance[] RemoveNulls(Instance[] instances){       
        int size = 0;  
        
//        determine total non null instances
        for(int i=0;i<instances.length;i++){
            if(instances[i]!=null){
                size++;
            }
        }
        
//        create new instance set of proper size
        Instance[] reducedSet = new Instance[size];
        
//        assign non nulls to new set and return
        int next=0;
        for(int i=0;i<instances.length;i++){
            if(instances[i]!=null){
                reducedSet[next] = instances[i];
                next++;
            }
        }
    
        return reducedSet;
    }
    
//    takes two sets of instances and returns a combined set
    public Instance[] MergeInstances(Instance[] instA, Instance[] instB){
        Instance[] set = new Instance[instA.length+instB.length]; 
        
        int next = 0;
        for(int i=0;i<instA.length;i++){
            set[next] = instA[i]; 
            next++;
        }
        for(int i=0;i<instB.length && instB[i]!=null;i++){
            set[next] = instB[i];
            next++;
        }
        
        return ShuffleInstances(RemoveNulls(set));
    }
    
//    the real meat and potatoes. the training of the perceptrons
//    takes in the perceptron to be trained, the traing data, and learning rate
    public int Train(Perceptron perceptron, Instance[] instances,float learningRate){
        float[] deltaWs = new float[perceptron.weight.length];
        float totalDeltaW = 1;
        
        float trueValue;
        float output;
        
        int epoch;
        
//        runs for a number of epochs, until the total change in all weights is
//        no more than .1 (absolute values), or until 100,000 epochs have passed
        for(epoch=0;epoch<epochLimit && totalDeltaW >= .1;epoch++){
            totalDeltaW = 0;
            
//            runs through each of the instances in sequential order.
            for(int i=0;i<instances.length;i++){
                
//                determine the true and output values.
                trueValue = perceptron.TrueClassify(instances[i]);
                output = perceptron.Classify(instances[i]);
                
//                for each weight determine the change in weights as the
//                learning weight * T-O * input.
                for(int w=0;w<perceptron.weight.length;w++){
                    if(w==0){
                        deltaWs[w] = learningRate * (trueValue-output) * 1;
                    }else{
                        deltaWs[w] = learningRate * (trueValue-output) * instances[i].feature[w-1];              
                    }
                    
//                    add to total change in weights for determining end of run.
                    totalDeltaW+=java.lang.Math.abs(deltaWs[w]);
                }
                
//                actually change the wieghts and start over.
                for(int w = 0;w<perceptron.weight.length;w++){
                    perceptron.weight[w] += deltaWs[w];
                }
            }
            
            
        }
        return epoch;
    }
    
//    test data. Takes in the perceptron to be tested and the test data
    public String Test(Perceptron perceptron, Instance[] instances){
//        [Actual Pos,Neg][Outcome Pos,Neg]    
        float[][] confusionMatrix = perceptron.GetConfusionMatrix(instances);
        
        float TP = confusionMatrix[0][0];
        float FN = confusionMatrix[0][1];
        float FP = confusionMatrix[1][0];
        float TN = confusionMatrix[1][1];
        
//        determing output analysis based on confusion matrix.
        float accuracy = (TP+TN)/(TP+TN+FP+FN);
        float precision = TP /(TP + FP);
        float recall = TP /(TP + FN);
        
//        export output format
        return((int) TP + "\t" + (int) FN + "\t" + (int) FP + "\t" + (int) TN +
                "\t" + accuracy + "\t" + precision + "\t" + recall);
    }
     
}
