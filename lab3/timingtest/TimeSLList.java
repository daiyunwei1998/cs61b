package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeSLList {
    private static void printTimingTable(AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }

    public static void main(String[] args) {
        timeGetLast();
    }

    public static void timeGetLast() {
        // TODO: YOUR CODE HERE

        // Step1: the size N of the data structure
        AList<Integer> nList = new AList<Integer>();
        nList.addLast(1000);
        for(int i = 0; i < 7; i += 1){
            // Use left shift expression to calculate power of integers
            // https://stackoverflow.com/questions/8071363/calculating-powers-of-integers
            nList.addLast(1000 * 2 << i);
        }

        // Step2:
        AList<Double> timeList = new AList<Double>();
        AList<Integer> opsList = new AList<Integer>();
        for(int i = 0; i < 8; i += 1){
            int n = nList.get(i);
            int ops = 10000;

            SLList<Integer> tempList = new SLList<Integer>();

            for(int e = 0; e < n; e++){
                tempList.addLast(1);
            }


            Stopwatch sw = new Stopwatch();
            for(int e = 0; e < ops; e++){
                tempList.getLast();
            }
            double timeInSeconds = sw.elapsedTime();
            timeList.addLast(timeInSeconds);
            opsList.addLast(ops);
            System.out.print("The time it took to getlast for an sLList of size "+n+" is "+timeInSeconds+'\n');
        }


        // Step3: create the table
        printTimingTable(nList, timeList, opsList);
    }


}
