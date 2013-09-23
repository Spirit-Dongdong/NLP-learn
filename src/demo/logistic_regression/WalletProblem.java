package demo.logistic_regression;

import com.aliasi.matrix.DenseVector;
import com.aliasi.matrix.Vector;

import com.aliasi.stats.AnnealingSchedule;
import com.aliasi.stats.LogisticRegression;
import com.aliasi.stats.RegressionPrior;

public class WalletProblem {

    public static void main(String[] args) {
        System.out.println("Computing Wallet Problem Logistic Regression");
        LogisticRegression regression
            = LogisticRegression.estimate(INPUTS,
                                          OUTPUTS,
                                          RegressionPrior.noninformative(),
                                          AnnealingSchedule.inverse(.05,100),
                                          null, // reporter with no feedback
                                          0.000000001, // min improve
                                          1, // min epochs
                                          5000); // max epochs
        
        Vector[] betas = regression.weightVectors();
        for (int outcome = 0; outcome < betas.length; ++outcome) {
            System.out.print("Outcome=" + outcome);
            for (int i = 0; i < betas[outcome].numDimensions(); ++i)
                System.out.printf(" %6.2f",betas[outcome].value(i));
            System.out.println();
        }

	System.out.println("\nInput Vector         Outcome Conditional Probabilities");
        for (Vector testCase : TEST_INPUTS) {
            double[] conditionalProbs = regression.classify(testCase);
            for (int i = 0; i < testCase.numDimensions(); ++i) {
                System.out.printf("%3.1f ",testCase.value(i));
            }
            for (int k = 0; k < conditionalProbs.length; ++k) {
                System.out.printf(" p(%d|input)=%4.2f ",k,conditionalProbs[k]);
            }
            System.out.println();
        }

    }


    // parallel to inputs
    public static final int[] OUTPUTS = new int[] {
        1,
        1,
        2,
        2,
        0,
        2,
        2,
        2,
        2,
        2,
        1,
        2,
        2,
        2,
        2,
        2,
        2,
        1,
        0,
        1,
        1,
        2,
        2,
        2,
        2,
        1,
        1,
        0,
        2,
        2,
        2,
        2,
        0,
        2,
        2,
        2,
        2,
        2,
        2,
        2,
        2,
        2,
        2,
        2,
        2,
        2,
        2,
        2,
        2,
        2,
        1,
        2,
        2,
        2,
        2,
        2,
        2,
        1,
        2,
        2,
        2,
        2,
        2,
        0,
        2,
        2,
        0,
        2,
        1,
        0,
        0,
        2,
        2,
        1,
        1,
        1,
        2,
        2,
        2,
        2,
        2,
        2,
        2,
        2,
        1,
        2,
        2,
        1,
        2,
        2,
        2,
        2,
        2,
        2,
        2,
        0,
        0,
        1,
        0,
        1,
        0,
        1,
        0,
        2,
        2,
        1,
        2,
        0,
        2,
        1,
        2,
        2,
        1,
        2,
        2,
        0,
        1,
        1,
        0,
        0,
        2,
        2,
        2,
        2,
        2,
        2,
        2,
        2,
        2,
        1,
        1,
        2,
        1,
        2,
        1,
        2,
        2,
        0,
        2,
        2,
        2,
        2,
        1,
        2,
        1,
        2,
        1,
        2,
        2,
        2,
        2,
        1,
        2,
        2,
        1,
        2,
        2,
        1,
        2,
        1,
        2,
        0,
        2,
        1,
        0,
        1,
        2,
        1,
        2,
        1,
        1,
        0,
        1,
        1,
        0,
        1,
        1,
        2,
        2,
        1,
        0,
        1,
        2,
        1,
        2,
        0,
        1,
        2,
        1,
        2,
        2,
        2,
        2,
        2,
        1, 
    };

    // parallel to outputs
    public static final Vector[] INPUTS = new Vector[] {
        new DenseVector(new double[] { 1, 0, 0, 2, 0 }),
        new DenseVector(new double[] { 1, 0, 0, 2, 1 }),
        new DenseVector(new double[] { 1, 0, 0, 1, 1 }),
        new DenseVector(new double[] { 1, 0, 0, 2, 0 }),
        new DenseVector(new double[] { 1, 1, 0, 1, 1 }),
        new DenseVector(new double[] { 1, 0, 0, 1, 1 }),
        new DenseVector(new double[] { 1, 0, 0, 1, 1 }),
        new DenseVector(new double[] { 1, 1, 0, 1, 1 }),
        new DenseVector(new double[] { 1, 1, 0, 1, 1 }),
        new DenseVector(new double[] { 1, 0, 0, 2, 1 }),
        new DenseVector(new double[] { 1, 0, 1, 1, 1 }),
        new DenseVector(new double[] { 1, 1, 1, 1, 1 }),
        new DenseVector(new double[] { 1, 1, 0, 1, 1 }),
        new DenseVector(new double[] { 1, 1, 0, 1, 1 }),
        new DenseVector(new double[] { 1, 0, 0, 1, 1 }),
        new DenseVector(new double[] { 1, 0, 0, 1, 0 }),
        new DenseVector(new double[] { 1, 0, 0, 2, 1 }),
        new DenseVector(new double[] { 1, 0, 0, 3, 0 }),
        new DenseVector(new double[] { 1, 1, 1, 3, 0 }),
        new DenseVector(new double[] { 1, 0, 0, 1, 1 }),
        new DenseVector(new double[] { 1, 1, 0, 2, 0 }),
        new DenseVector(new double[] { 1, 0, 0, 1, 1 }),
        new DenseVector(new double[] { 1, 1, 0, 1, 1 }),
        new DenseVector(new double[] { 1, 1, 1, 1, 0 }),
        new DenseVector(new double[] { 1, 1, 0, 1, 1 }),
        new DenseVector(new double[] { 1, 0, 0, 1, 0 }),
        new DenseVector(new double[] { 1, 1, 0, 3, 0 }),
        new DenseVector(new double[] { 1, 1, 0, 2, 0 }),
        new DenseVector(new double[] { 1, 1, 0, 2, 0 }),
        new DenseVector(new double[] { 1, 1, 0, 1, 1 }),
        new DenseVector(new double[] { 1, 0, 0, 1, 1 }),
        new DenseVector(new double[] { 1, 1, 0, 2, 1 }),
        new DenseVector(new double[] { 1, 1, 0, 1, 1 }),
        new DenseVector(new double[] { 1, 0, 0, 2, 0 }),
        new DenseVector(new double[] { 1, 1, 0, 1, 0 }),
        new DenseVector(new double[] { 1, 1, 1, 2, 1 }),
        new DenseVector(new double[] { 1, 0, 0, 2, 0 }),
        new DenseVector(new double[] { 1, 0, 0, 1, 1 }),
        new DenseVector(new double[] { 1, 1, 0, 1, 1 }),
        new DenseVector(new double[] { 1, 0, 0, 1, 1 }),
        new DenseVector(new double[] { 1, 0, 0, 1, 1 }),
        new DenseVector(new double[] { 1, 0, 0, 1, 1 }),
        new DenseVector(new double[] { 1, 0, 0, 2, 0 }),
        new DenseVector(new double[] { 1, 1, 1, 1, 1 }),
        new DenseVector(new double[] { 1, 1, 0, 1, 1 }),
        new DenseVector(new double[] { 1, 0, 0, 1, 1 }),
        new DenseVector(new double[] { 1, 0, 0, 1, 1 }),
        new DenseVector(new double[] { 1, 0, 0, 1, 0 }),
        new DenseVector(new double[] { 1, 0, 1, 3, 0 }),
        new DenseVector(new double[] { 1, 1, 0, 2, 0 }),
        new DenseVector(new double[] { 1, 0, 0, 2, 1 }),
        new DenseVector(new double[] { 1, 0, 0, 1, 1 }),
        new DenseVector(new double[] { 1, 1, 0, 1, 1 }),
        new DenseVector(new double[] { 1, 1, 0, 1, 1 }),
        new DenseVector(new double[] { 1, 1, 0, 1, 0 }),
        new DenseVector(new double[] { 1, 1, 1, 1, 0 }),
        new DenseVector(new double[] { 1, 1, 0, 1, 0 }),
        new DenseVector(new double[] { 1, 1, 1, 3, 1 }),
        new DenseVector(new double[] { 1, 0, 0, 1, 1 }),
        new DenseVector(new double[] { 1, 0, 0, 3, 1 }),
        new DenseVector(new double[] { 1, 0, 0, 1, 1 }),
        new DenseVector(new double[] { 1, 0, 0, 1, 0 }),
        new DenseVector(new double[] { 1, 0, 0, 1, 1 }),
        new DenseVector(new double[] { 1, 0, 1, 1, 1 }),
        new DenseVector(new double[] { 1, 0, 0, 1, 0 }),
        new DenseVector(new double[] { 1, 1, 0, 1, 1 }),
        new DenseVector(new double[] { 1, 1, 0, 1, 0 }),
        new DenseVector(new double[] { 1, 1, 0, 3, 1 }),
        new DenseVector(new double[] { 1, 1, 0, 3, 1 }),
        new DenseVector(new double[] { 1, 1, 1, 2, 1 }),
        new DenseVector(new double[] { 1, 1, 0, 2, 1 }),
        new DenseVector(new double[] { 1, 0, 0, 1, 1 }),
        new DenseVector(new double[] { 1, 0, 0, 3, 0 }),
        new DenseVector(new double[] { 1, 1, 0, 1, 1 }),
        new DenseVector(new double[] { 1, 0, 0, 1, 1 }),
        new DenseVector(new double[] { 1, 1, 0, 1, 1 }),
        new DenseVector(new double[] { 1, 0, 0, 1, 0 }),
        new DenseVector(new double[] { 1, 1, 1, 1, 1 }),
        new DenseVector(new double[] { 1, 1, 0, 1, 1 }),
        new DenseVector(new double[] { 1, 0, 0, 1, 1 }),
        new DenseVector(new double[] { 1, 0, 1, 1, 1 }),
        new DenseVector(new double[] { 1, 0, 0, 1, 1 }),
        new DenseVector(new double[] { 1, 0, 0, 1, 1 }),
        new DenseVector(new double[] { 1, 0, 0, 2, 1 }),
        new DenseVector(new double[] { 1, 1, 1, 1, 0 }),
        new DenseVector(new double[] { 1, 1, 0, 1, 1 }),
        new DenseVector(new double[] { 1, 0, 0, 1, 1 }),
        new DenseVector(new double[] { 1, 1, 1, 1, 1 }),
        new DenseVector(new double[] { 1, 0, 0, 1, 1 }),
        new DenseVector(new double[] { 1, 1, 1, 1, 1 }),
        new DenseVector(new double[] { 1, 0, 0, 1, 1 }),
        new DenseVector(new double[] { 1, 0, 0, 1, 1 }),
        new DenseVector(new double[] { 1, 0, 0, 1, 1 }),
        new DenseVector(new double[] { 1, 0, 1, 1, 1 }),
        new DenseVector(new double[] { 1, 0, 0, 2, 1 }),
        new DenseVector(new double[] { 1, 1, 1, 1, 1 }),
        new DenseVector(new double[] { 1, 1, 0, 2, 0 }),
        new DenseVector(new double[] { 1, 1, 0, 1, 1 }),
        new DenseVector(new double[] { 1, 1, 1, 1, 1 }),
        new DenseVector(new double[] { 1, 1, 0, 1, 1 }),
        new DenseVector(new double[] { 1, 1, 1, 3, 0 }),
        new DenseVector(new double[] { 1, 1, 1, 1, 1 }),
        new DenseVector(new double[] { 1, 1, 1, 3, 1 }),
        new DenseVector(new double[] { 1, 0, 0, 3, 1 }),
        new DenseVector(new double[] { 1, 0, 0, 1, 1 }),
        new DenseVector(new double[] { 1, 0, 0, 1, 1 }),
        new DenseVector(new double[] { 1, 0, 0, 1, 1 }),
        new DenseVector(new double[] { 1, 0, 1, 3, 0 }),
        new DenseVector(new double[] { 1, 0, 0, 1, 1 }),
        new DenseVector(new double[] { 1, 0, 0, 1, 0 }),
        new DenseVector(new double[] { 1, 0, 0, 1, 1 }),
        new DenseVector(new double[] { 1, 0, 0, 1, 1 }),
        new DenseVector(new double[] { 1, 1, 0, 1, 1 }),
        new DenseVector(new double[] { 1, 1, 0, 1, 1 }),
        new DenseVector(new double[] { 1, 0, 0, 1, 1 }),
        new DenseVector(new double[] { 1, 1, 0, 3, 0 }),
        new DenseVector(new double[] { 1, 1, 0, 1, 1 }),
        new DenseVector(new double[] { 1, 0, 1, 1, 1 }),
        new DenseVector(new double[] { 1, 0, 0, 3, 0 }),
        new DenseVector(new double[] { 1, 0, 1, 2, 0 }),
        new DenseVector(new double[] { 1, 0, 0, 1, 1 }),
        new DenseVector(new double[] { 1, 0, 0, 1, 1 }),
        new DenseVector(new double[] { 1, 0, 0, 1, 1 }),
        new DenseVector(new double[] { 1, 1, 1, 1, 1 }),
        new DenseVector(new double[] { 1, 0, 0, 1, 0 }),
        new DenseVector(new double[] { 1, 0, 0, 1, 1 }),
        new DenseVector(new double[] { 1, 1, 0, 1, 1 }),
        new DenseVector(new double[] { 1, 1, 1, 1, 1 }),
        new DenseVector(new double[] { 1, 1, 0, 1, 1 }),
        new DenseVector(new double[] { 1, 1, 1, 1, 1 }),
        new DenseVector(new double[] { 1, 0, 0, 1, 0 }),
        new DenseVector(new double[] { 1, 0, 0, 1, 1 }),
        new DenseVector(new double[] { 1, 1, 1, 2, 0 }),
        new DenseVector(new double[] { 1, 1, 0, 1, 0 }),
        new DenseVector(new double[] { 1, 1, 0, 1, 0 }),
        new DenseVector(new double[] { 1, 0, 0, 2, 1 }),
        new DenseVector(new double[] { 1, 1, 1, 1, 1 }),
        new DenseVector(new double[] { 1, 0, 0, 3, 0 }),
        new DenseVector(new double[] { 1, 0, 0, 1, 1 }),
        new DenseVector(new double[] { 1, 1, 1, 1, 1 }),
        new DenseVector(new double[] { 1, 0, 0, 1, 0 }),
        new DenseVector(new double[] { 1, 0, 1, 1, 0 }),
        new DenseVector(new double[] { 1, 0, 0, 1, 1 }),
        new DenseVector(new double[] { 1, 1, 0, 1, 1 }),
        new DenseVector(new double[] { 1, 1, 0, 1, 0 }),
        new DenseVector(new double[] { 1, 0, 0, 1, 0 }),
        new DenseVector(new double[] { 1, 1, 0, 1, 1 }),
        new DenseVector(new double[] { 1, 1, 0, 1, 1 }),
        new DenseVector(new double[] { 1, 1, 0, 2, 0 }),
        new DenseVector(new double[] { 1, 1, 1, 2, 1 }),
        new DenseVector(new double[] { 1, 1, 0, 1, 1 }),
        new DenseVector(new double[] { 1, 1, 0, 1, 0 }),
        new DenseVector(new double[] { 1, 0, 0, 1, 1 }),
        new DenseVector(new double[] { 1, 1, 0, 1, 1 }),
        new DenseVector(new double[] { 1, 1, 1, 1, 0 }),
        new DenseVector(new double[] { 1, 0, 0, 1, 1 }),
        new DenseVector(new double[] { 1, 1, 0, 1, 1 }),
        new DenseVector(new double[] { 1, 1, 0, 1, 0 }),
        new DenseVector(new double[] { 1, 0, 1, 2, 1 }),
        new DenseVector(new double[] { 1, 1, 1, 2, 1 }),
        new DenseVector(new double[] { 1, 0, 0, 1, 1 }),
        new DenseVector(new double[] { 1, 1, 0, 1, 1 }),
        new DenseVector(new double[] { 1, 1, 1, 3, 1 }),
        new DenseVector(new double[] { 1, 1, 0, 1, 1 }),
        new DenseVector(new double[] { 1, 0, 0, 1, 0 }),
        new DenseVector(new double[] { 1, 1, 0, 3, 0 }),
        new DenseVector(new double[] { 1, 0, 0, 1, 1 }),
        new DenseVector(new double[] { 1, 1, 1, 1, 1 }),
        new DenseVector(new double[] { 1, 0, 0, 1, 1 }),
        new DenseVector(new double[] { 1, 0, 0, 2, 0 }),
        new DenseVector(new double[] { 1, 1, 0, 1, 1 }),
        new DenseVector(new double[] { 1, 1, 1, 2, 0 }),
        new DenseVector(new double[] { 1, 1, 0, 2, 0 }),
        new DenseVector(new double[] { 1, 0, 0, 1, 1 }),
        new DenseVector(new double[] { 1, 0, 1, 3, 0 }),
        new DenseVector(new double[] { 1, 1, 0, 1, 1 }),
        new DenseVector(new double[] { 1, 1, 0, 1, 1 }),
        new DenseVector(new double[] { 1, 1, 0, 1, 1 }),
        new DenseVector(new double[] { 1, 0, 0, 1, 1 }),
        new DenseVector(new double[] { 1, 1, 0, 1, 1 }),
        new DenseVector(new double[] { 1, 1, 0, 2, 0 }),
        new DenseVector(new double[] { 1, 0, 1, 2, 1 }),
        new DenseVector(new double[] { 1, 0, 0, 2, 0 }),
        new DenseVector(new double[] { 1, 1, 1, 1, 1 }),
        new DenseVector(new double[] { 1, 0, 1, 2, 1 }),
        new DenseVector(new double[] { 1, 0, 0, 3, 0 }),
        new DenseVector(new double[] { 1, 1, 1, 1, 0 }),
        new DenseVector(new double[] { 1, 0, 0, 3, 1 }),
        new DenseVector(new double[] { 1, 1, 0, 2, 1 }),
        new DenseVector(new double[] { 1, 0, 0, 1, 1 }),
        new DenseVector(new double[] { 1, 1, 0, 3, 1 }),
        new DenseVector(new double[] { 1, 0, 0, 1, 1 }),
        new DenseVector(new double[] { 1, 1, 1, 1, 1 }),
        new DenseVector(new double[] { 1, 1, 0, 1, 1 }),
        new DenseVector(new double[] { 1, 1, 0, 1, 1 }),
    };

    public static final Vector[] TEST_INPUTS = new Vector[] {
        new DenseVector(new double[] { 1, 0, 0, 1, 1 }),
        new DenseVector(new double[] { 1, 0, 1, 0, 0 }),        
        new DenseVector(new double[] { 1, 0, 1, 3, 1 }),        
    };

}
