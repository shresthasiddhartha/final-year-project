package Zest.gym.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OtpCodeGenerator {
	 public static void main(String[] args) {
	        int uniqueNumber = generateUniqueNumber();
	        System.out.println(uniqueNumber);
	    }

	    public static int generateUniqueNumber() {
	        List<Integer> digits = new ArrayList<>();
	        for (int i = 0; i < 10; i++) {
	            digits.add(i);
	        }

	        Collections.shuffle(digits);

	        // Make sure the first digit is not 0
	        if (digits.get(0) == 0) {
	            Collections.swap(digits, 0, 1);
	        }

	        int uniqueNumber = 0;
	        for (int i = 0; i < 4; i++) {
	            uniqueNumber = uniqueNumber * 10 + digits.get(i);
	        }

	        return uniqueNumber;
	    }
}
