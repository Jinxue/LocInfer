package Evaluation;

import java.util.Arrays;
import java.util.Random;

public class IntersectionTest {

	public static int commonFriends(int[] a, int[] b){
		int comm = 0;
	
		int indexA = 0, indexB = 0;
		
		while (indexA < a.length && indexB < b.length)
			if (a[indexA] == b[indexB]){
				comm ++;
				indexA ++;
				indexB ++;
			} else if (a[indexA] < b[indexB]){
				do 
					indexA ++;
				while ((indexA < a.length) && (a[indexA] < b[indexB]));
			} else{
				do
					indexB ++;
				while ((indexB < b.length) && (a[indexA] > b[indexB]));
			}
		
		return comm;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		int len = 10;
		int[] a = new int[len];
		Random rand = new Random();
		for (int i = 0; i < a.length; i++)
		    a[i] = rand.nextInt(100) + 1;
		System.out.println("Before sorting: " + Arrays.toString(a));
		Arrays.sort(a);
		System.out.println("After sorting: " + Arrays.toString(a));
		
		
		int[] b = new int[len];
		for (int i = 0; i < b.length; i++)
		    b[i] = rand.nextInt(100) + 1;
		System.out.println("Before sorting: " + Arrays.toString(b));
		Arrays.sort(b);
		System.out.println("After sorting: " + Arrays.toString(b));
	
		System.out.println("Common friends: " + commonFriends(a, b));
	}

}
