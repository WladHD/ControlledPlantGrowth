package de.wladtheninja.controlledplantgrowth.data.dao.utils;

import de.wladtheninja.controlledplantgrowth.ControlledPlantGrowth;

import java.util.Arrays;
import java.util.Random;

public class RandomArrayFiller {
    public static int[] createRandomArrayWithSum(int numberOfElements, int desiredSum) {
        if (numberOfElements > desiredSum) {

            ControlledPlantGrowth.handleException(new IllegalArgumentException(
                    "It's not possible to create an array with each element at least 1 and the total sum less than n" +
                            "."));
            return null;
        }

        int[] arr = new int[numberOfElements];
        Arrays.fill(arr, 1);

        int remainingSum = desiredSum - numberOfElements;
        Random random = new Random();

        while (remainingSum > 0) {
            int index = random.nextInt(numberOfElements);
            arr[index]++;
            remainingSum--;
        }

        return arr;
    }
}