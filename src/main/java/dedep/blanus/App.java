package dedep.blanus;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class App {
    public static void main(String[] args) {
        Condition leftLeg = new Condition("Left leg");
        Condition rightLeg = new Condition("Right leg");
        Condition leftSock = new Condition("Left sock");
        Condition rightSock = new Condition("Right sock");
        Condition leftBoot = new Condition("Left boot");
        Condition rightBoot = new Condition("Right boot");

        MovementOperator pullLeftSock = new MovementOperator(Collections.singletonList(leftLeg), Collections.singletonList(leftSock), "Pull left Sock");
        MovementOperator pullRightSock = new MovementOperator(Collections.singletonList(rightLeg), Collections.singletonList(rightSock), "Pull right Sock");
        MovementOperator pullLeftBoot = new MovementOperator(Collections.singletonList(leftSock), Collections.singletonList(leftBoot), "Pull left boot");
        MovementOperator pullRightBoot = new MovementOperator(Collections.singletonList(rightSock), Collections.singletonList(rightBoot), "Pull right boot");

        List<MovementOperator> operators = Arrays.asList(pullLeftBoot, pullLeftSock, pullRightBoot, pullRightSock);

        System.out.println("OK");
    }
}
