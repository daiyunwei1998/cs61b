package tester;

import static org.junit.Assert.*;

import java.util.Random;
import edu.princeton.cs.introcs.StdRandom;
import org.junit.Test;
import student.StudentArrayDeque;
public class TestArrayDequeEC {
    @Test
    public void test1() {
        StringBuilder record = new StringBuilder();
        StudentArrayDeque<Integer> sad1 = new StudentArrayDeque<>();
        ArrayDequeSolution<Integer> GroundTruth = new ArrayDequeSolution<Integer>();

        for (int i = 0; i < 100; i += 1) {
            Random random = new Random();
            int randomNumber = random.nextInt(4);

            if (randomNumber == 0) {
                sad1.addLast(i);
                GroundTruth.addLast(i);
                record.append("addLast(");
                record.append(String.valueOf(i));
                record.append(")\n");
            } else if (randomNumber == 1) {
                sad1.addFirst(i);
                GroundTruth.addFirst(i);
                record.append("addFirst(");
                record.append(String.valueOf(i));
                record.append(")\n");
            } else if (randomNumber == 2 & sad1.size() > 2) {
                Integer sadFirst = sad1.removeFirst();
                Integer gtFirst = GroundTruth.removeFirst();
                record.append("removeFirst()\n");
                String message = record.toString();
                assertEquals(message, gtFirst, sadFirst);


            } else if (randomNumber == 3  & sad1.size() > 2) {
                Integer sadLast = sad1.removeLast();
                Integer gtLast = GroundTruth.removeLast();
                record.append("removeLast()\n");
                String message = record.toString();
                assertEquals(message, gtLast, sadLast);


            }

      /*      for (int j = 0; j < sad1.size(); j += 1) {
                record.append("get(");
                record.append(String.valueOf(i));
                record.append(")\n");
                String message = record.toString();
                assertEquals(message, GroundTruth.get(j), sad1.get(j));
            }*/

            assertEquals(sad1.size(), GroundTruth.size());
        }

        //sad1.printDeque();
    }
}
