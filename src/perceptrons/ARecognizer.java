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
public class ARecognizer {

    /**
     * @param args the command line arguments
     */
            
    
    public static void main(String[] args) {
        /* 
         *enter the path of the file to be red in here. default is sample given with package
         */
        LearningModule learner = new LearningModule(System.getProperty("user.dir") + "\\content\\letter-recognition.data");
        
    }
    
}
