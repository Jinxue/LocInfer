package Evaluation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.zip.GZIPInputStream;

public class FoundUsersCombined {
	
	HashMap<Integer, int[]> candsFO, candsFI, candsIO;
	HashMap<Integer, Integer> candRoFO, candRoFI, candRoIO;
	HashMap<Integer, Integer> candFriendsFO, candFriendsFI, candFriendsIO;
	// For the interaction, we add a weight for each interaction edge; we assume the edge is sparse
	HashMap<String, Integer> candWeights;  
	HashSet<Integer> seeds;
	PrintWriter  outf = null;
	
	int t, k, threshold;
	int ctype = 1;
	String location;
	double [] epsilon;
	
	FoundUsersCombined(String seedFile, String candIDFile, String candFileFO, String candFileFI, String candFileIO, int thre, int kk, int tt, int combinType, String loc, float rr){
		threshold = thre;
		k = kk;
		t = tt;
		ctype = combinType;
		
		candsFO = new HashMap<Integer, int[]>();
		candsFI = new HashMap<Integer, int[]>();
		candsIO = new HashMap<Integer, int[]>();
		
		candRoFO = new HashMap<Integer, Integer>();
		candRoFI = new HashMap<Integer, Integer>();
		candRoIO = new HashMap<Integer, Integer>();
		
		candFriendsFO = new HashMap<Integer, Integer>();
		candFriendsFI = new HashMap<Integer, Integer>();
		candFriendsIO = new HashMap<Integer, Integer>();
		
		candWeights = new HashMap<String, Integer>();
		seeds = new HashSet<Integer>();
		location = loc;
		
		loadEpsilon();
		loadSeed(seedFile);
		loadCandID(candIDFile);
		loadCand1(candFileFO);
		loadCand2(candFileFI);
		loadCand3(candFileIO);
		//outf = buildOutFile(candFile.split("\\.")[0]);
		outf = buildOutFile("foundUsers" + threshold + "-combined" + combinType + "-k" + k + "-t" + t  + "-r" + rr + "-map.txt");
	}

	private void loadEpsilon() {
		// TODO Auto-generated method stub
		epsilon = new double[3];
		switch(location.toLowerCase()){
		case "ts":
			epsilon[0] = 9.2 / 30.1; epsilon[1] = 8.1 / 30.1; epsilon[2] = 12.8 / 30.1;
			break;
		case "pi":
			epsilon[0] = 8.4 / 28.2; epsilon[1] = 4.9 / 28.2; epsilon[2] = 14.9 / 28.2;
		case "la":
			epsilon[0] = 8.4 / 26.9; epsilon[1] = 1.5 / 26.9; epsilon[2] = 17 / 26.9;
		}
		
		//l = (9.2 * l1 +  8.1 * l2 + 12.8 * l3) / 30.1 ;	// For Tucson
		//l = (8.4 * l1 +  4.9 * l2 + 14.9 *l3) / 30.1 ;	// For PI
		//l = (10.3 * l1 +  6.9 * l2 + 16.9 * l3) / 30.1 ;	// For LA
	}

	private void loadCand1(String candFile) {
		// TODO Auto-generated method stub
		File inFile = new File(candFile);

		if (!inFile.isFile()) {
			System.out.println(candFile + " is not an existing file");
			return;
		}

		BufferedReader br = null;
		try {
			//br = new BufferedReader(new FileReader(candFile));
			// For gzip file
			br = new BufferedReader(new InputStreamReader(
			        new GZIPInputStream(new FileInputStream(candFile))));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		String line = null;
		boolean skipLine = false;
		int users = 0, lines = 0;
		int cand = 0;
		int friends = 0, localFriends = 0, localFriendsIndex = 0;
		//Read from the original file and write to the new
		//unless content matches data to be removed.
		try {
			while ((line = br.readLine()) != null){
				// The starting line for a candidate user
				if(line.startsWith("%")){
					
					// For debugging
					/*if (users > 10000){
						System.out.println(users + " users have been analyzed. We stop for debug");
						break;
					}*/

					String[] items = line.split(",");
					cand = Integer.parseInt(items[0].substring(1));
					friends = Integer.parseInt(items[1]);
					localFriends = Integer.parseInt(items[2]);
					if( candRoFO.containsKey(cand) == false || friends > threshold || localFriends == 0){
						skipLine = true;
						continue;
					} else
						skipLine = false;
					
					int[] friendList = new int[localFriends];
					candsFO.put(cand, friendList);
					//candRo.put(cand, 0);
					candFriendsFO.put(cand, friends);
					localFriendsIndex = 0;
					
					if (++users % 10000 == 0)
						System.out.println(users + " users have been added.");
				} else if (!skipLine){
					int[] fl = candsFO.get(cand);
					fl[localFriendsIndex ++] = Integer.parseInt(line);
					if (localFriendsIndex == localFriends)
						Arrays.sort(fl);
				}
				if (++lines % 1000000 == 0)
					System.out.println(lines + " lines have been analyzed.");
			}
			br.close();
		} catch (IOException e) {
		// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("We load " + users + " candidates with friends less than " + threshold );
	}

	private void loadCand2(String candFile) {
		// TODO Auto-generated method stub
		File inFile = new File(candFile);

		if (!inFile.isFile()) {
			System.out.println(candFile + " is not an existing file");
			return;
		}

		BufferedReader br = null;
		try {
			//br = new BufferedReader(new FileReader(candFile));
			// For gzip file
			br = new BufferedReader(new InputStreamReader(
			        new GZIPInputStream(new FileInputStream(candFile))));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		String line = null;
		boolean skipLine = false;
		int users = 0, lines = 0;
		int cand = 0;
		int friends = 0, localFriends = 0, localFriendsIndex = 0;
		//Read from the original file and write to the new
		//unless content matches data to be removed.
		try {
			while ((line = br.readLine()) != null){
				// The starting line for a candidate user
				if(line.startsWith("%")){
					
					// For debugging
					/*if (users > 10000){
						System.out.println(users + " users have been analyzed. We stop for debug");
						break;
					}*/

					String[] items = line.split(",");
					cand = Integer.parseInt(items[0].substring(1));
					friends = Integer.parseInt(items[1]);
					localFriends = Integer.parseInt(items[2]);
					if( candRoFI.containsKey(cand) == false || friends > threshold || localFriends == 0){
						skipLine = true;
						continue;
					} else
						skipLine = false;
					
					int[] friendList = new int[localFriends];
					candsFI.put(cand, friendList);
					//candRo.put(cand, 0);
					candFriendsFI.put(cand, friends);
					localFriendsIndex = 0;
					
					if (++users % 10000 == 0)
						System.out.println(users + " users have been added.");
				} else if (!skipLine){
					int[] fl = candsFI.get(cand);
					fl[localFriendsIndex ++] = Integer.parseInt(line);
					if (localFriendsIndex == localFriends)
						Arrays.sort(fl);
				}
				if (++lines % 1000000 == 0)
					System.out.println(lines + " lines have been analyzed.");
			}
			br.close();
		} catch (IOException e) {
		// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("We load " + users + " candidates with friends less than " + threshold );
	}

	private void loadCand3(String candFile) {
		// TODO Auto-generated method stub
		File inFile = new File(candFile);

		if (!inFile.isFile()) {
			System.out.println(candFile + " is not an existing file");
			return;
		}

		BufferedReader br = null;
		try {
			//br = new BufferedReader(new FileReader(candFile));
			// For gzip file
			br = new BufferedReader(new InputStreamReader(
			        new GZIPInputStream(new FileInputStream(candFile))));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		String line = null;
		boolean skipLine = false;
		int users = 0, lines = 0;
		int cand = 0;
		int friends = 0, localFriends = 0, localFriendsIndex = 0;
		//Read from the original file and write to the new
		//unless content matches data to be removed.
		try {
			while ((line = br.readLine()) != null){
				// The starting line for a candidate user
				if(line.startsWith("%")){
					
					// For debugging
					/*if (users > 10000){
						System.out.println(users + " users have been analyzed. We stop for debug");
						break;
					}*/

					String[] items = line.split(",");
					cand = Integer.parseInt(items[0].substring(1));
					friends = Integer.parseInt(items[1]);
					localFriends = Integer.parseInt(items[2]);
					if( candRoIO.containsKey(cand) == false || friends > threshold || localFriends == 0){
						skipLine = true;
						continue;
					} else
						skipLine = false;
					
					int[] friendList = new int[localFriends];
					candsIO.put(cand, friendList);
					//candRo.put(cand, 0);
					candFriendsIO.put(cand, friends);
					localFriendsIndex = 0;
					
					if (++users % 10000 == 0)
						System.out.println(users + " users have been added.");
				} else if (!skipLine){
					int[] fl = candsIO.get(cand);
					String[] items = line.split(",");
					fl[localFriendsIndex ++] = Integer.parseInt(items[0]);
					if (localFriendsIndex == localFriends)
						Arrays.sort(fl);
					// Insert the weight
					candWeights.put(cand + "," + items[0], Integer.parseInt(items[1]));
				}
				if (++lines % 1000000 == 0)
					System.out.println(lines + " lines have been analyzed.");
			}
			br.close();
		} catch (IOException e) {
		// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("We load " + users + " candidates with friends less than " + threshold );
	}

	private void loadSeed(String seedFile2) {
		// TODO Auto-generated method stub
		File inFile = new File(seedFile2);

		if (!inFile.isFile()) {
			System.out.println(seedFile2 + " is not an existing file");
			return;
		}

		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(seedFile2));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		String line = null;

		//Read from the original file and write to the new
		//unless content matches data to be removed.
		try {
			while ((line = br.readLine()) != null)
				// For the user file with ID,screenName format
				seeds.add(Integer.parseInt(line));
			br.close();
		} catch (IOException e) {
		// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("We load " + seeds.size() + " seeds." );
	}

	private void loadCandID(String candIDFile) {
		// TODO Auto-generated method stub
		File inFile = new File(candIDFile);

		if (!inFile.isFile()) {
			System.out.println(candIDFile + " is not an existing file");
			return;
		}

		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(candIDFile));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		String line = null;

		//Read from the original file and write to the new
		//unless content matches data to be removed.
		try {
			while ((line = br.readLine()) != null){
				// For the user file with ID,screenName format
				candRoFO.put(Integer.parseInt(line.split("\\s+")[0]), 0);
				candRoFI.put(Integer.parseInt(line.split("\\s+")[0]), 0);
				candRoIO.put(Integer.parseInt(line.split("\\s+")[0]), 0);
			}
			br.close();
		} catch (IOException e) {
		// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("We load " + candRoFO.size() + " candidates." );
	}

	private PrintWriter buildOutFile(String fileName) {
		PrintWriter writer = null;
		// TODO Auto-generated method stub
		try {
			//FileWriter outFile = new FileWriter(fileName + "-targeted.txt", false);
			FileWriter outFile = new FileWriter(fileName, false);
			writer = new PrintWriter (outFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return writer;
	}


	/*
	 * Compute the intersection of a hashset and a sorted list (n) for the first iterations
	 * Usually the seed set is huge, so we traverse the sorted list
	 */
	private int commonFriends(HashSet<Integer> a, int[] b){
		int comm = 0;
	
		for (int friend : b)
			if (a.contains(friend))
				comm ++;
	
		return comm;
	}

	/*
	 * Compute the intersection of a two sorted list (n) for the following iterations
	 */
	private int commonFriends(int[] a, int[] b){
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
	

	/*
	 * Compute the intersection of a hashset and a sorted list (n) for the first iterations
	 * Usually the seed set is huge, so we traverse the sorted list
	 */
	private int commonFriendsWeight(HashSet<Integer> a, int[] b, int cand){
		int comm = 0;
	
		for (int friend : b)
			if (a.contains(friend)){
				//comm ++;
				// The interaction is from B to A;
				comm += this.candWeights.get(cand +"," + friend);
			}
	
		return comm;
	}

	/*
	 * Compute the intersection of a two sorted list (n) for the following iterations
	 */
	private int commonFriendsWeight(int[] a, int[] b, int cand){
		int comm = 0;
	
		int indexA = 0, indexB = 0;
		
		while (indexA < a.length && indexB < b.length)
			if (a[indexA] == b[indexB]){
				//comm ++;
				// The interaction is from B to A;
				comm += this.candWeights.get(cand +"," + a[indexA]);
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
	
	double max(double a, double b, double c){
		double ret = 0;
		ret = (a > b) ? a : b;
		ret = (ret > c) ? ret : c;
		return ret;
	}
	
	private HashSet<Integer> findUsers_initial(){
		int roundI = 1;
		HashSet<Integer> topkSet = new HashSet<Integer>();
		
		MinPriorityQueue<Candidate> kheap = new HeapMinPriorityQueue<Candidate>(); 
		int heap_n = 0;
		
		for (int cand : candsFO.keySet()){
			
			// We first obtain the friends in seed set
			double l1 = 0.0;
			
			//if (candsFO.containsKey(cand)){
				int s2FO = commonFriends(seeds, candsFO.get(cand));
				this.candRoFO.put(cand, s2FO);
				int s1FO = this.candFriendsFO.get(cand);
				l1 = 1.0 * s2FO / s1FO;
			//}
			
			double l2 = 0.0;
			if (candsFI.containsKey(cand)){
				int s2FI = commonFriends(seeds, candsFI.get(cand));
				this.candRoFI.put(cand, s2FI);
				int s1FI = this.candFriendsFI.get(cand);
				l2 = 1.0 * s2FI / s1FI;
			}			

			double l3 = 0.0;
			if (candsIO.containsKey(cand)){
				int s2IO = commonFriendsWeight(seeds, candsIO.get(cand), cand);
				this.candRoIO.put(cand, s2IO);
				int s1IO = this.candFriendsIO.get(cand);
				l3 = 1.0 * s2IO / s1IO;
			}
			double l = 0;
			if (this.ctype == 1) 
				l = max(l1, l2, l3);
			else{
				l = epsilon[0] * l1 +  epsilon[1] * l2 + epsilon[2] * l3;
			}
			// We then build the heap with k elements
			if (heap_n < k){
				Candidate ca = new Candidate(l, s2FO, cand);
				kheap.insert(ca);
				heap_n ++;
			} else{
				Candidate cmin = kheap.minimum();
				if (cmin.ro < l){
					Candidate newCand = new Candidate(l, s2FO, cand);
					kheap.ReplaceMin(newCand);
				}
			}
		}
		
		int ii = 0;
		while(!kheap.isEmpty()){
			Candidate cmin = kheap.extractMin();
			int id = cmin.id;
			System.out.println("Find the candidate in round " + roundI + " " +  ii + "/" + 
					candsFO.size() + " " + id + " " + cmin.localFriends + " " + 
					this.candFriendsFO.get(id) + " " + cmin.ro);
			
			outf.println(roundI + " " +  ii + " " + id + " " + cmin.localFriends + " " + 
					this.candFriendsFO.get(id) + " " + cmin.ro);
			
			//this.seeds.add(id);
			this.candsFO.remove(id);
			this.candsFI.remove(id);
			this.candsIO.remove(id);
			//this.candRo.remove(id);
			//this.candFriends.remove(id);
			topkSet.add(id);
			ii ++;
		}
		
		return topkSet;
	}
	
	public void findUsers() {
		// TODO Auto-generated method stub
		int users = candsFI.size();
		int rounds = (int) Math.ceil(1.0 * users / k);
		
		System.out.println("We will analyze users: " + users + " in #rounds: " + rounds);
		
		HashSet<Integer> topkSet = findUsers_initial();
		
		for (int roundI = 2; roundI <= rounds; roundI ++){
			MinPriorityQueue<Candidate> kheap = new HeapMinPriorityQueue<Candidate>(); 
			int heap_n = 0;

			// Obtain the sorted topk list
			int [] topks = new int[k];
			int ii = 0;
			for (Integer topk : topkSet)
				topks[ ii ++] = topk;
			Arrays.sort(topks);
			
			for (int cand : candsFO.keySet()){
				
				// We first obtain the friends in seed set
				/*int s2 = this.commonFriendsWeight(topks, cands.get(cand), cand);
				this.candRo.put(cand, this.candRo.get(cand) + s2);
				s2 = this.candRo.get(cand);
				int s1 = this.candFriends.get(cand);*/
				
				double l1 = 0.0;
				//int s2FO = 0;
				//if (candsFO.containsKey(cand)){
					int s2FO = commonFriends(topks, candsFO.get(cand));
					this.candRoFO.put(cand, this.candRoFO.get(cand) + s2FO);
					s2FO = this.candRoFO.get(cand);
					int s1FO = this.candFriendsFO.get(cand);
					l1 = 1.0 * s2FO / s1FO;
				//}
				
				double l2 = 0.0;
				if (candsFI.containsKey(cand)){
					int s2FI = commonFriends(topks, candsFI.get(cand));
					this.candRoFI.put(cand, this.candRoFI.get(cand) + s2FI);
					s2FI = this.candRoFO.get(cand);
					int s1FI = this.candFriendsFI.get(cand);
					l2 = 1.0 * s2FI / s1FI;
				}			

				double l3 = 0.0;
				if (candsIO.containsKey(cand)){
					int s2IO = commonFriendsWeight(topks, candsIO.get(cand), cand);
					this.candRoIO.put(cand, this.candRoIO.get(cand)  + s2IO);
					int s1IO = this.candFriendsIO.get(cand);
					l3 = 1.0 * s2IO / s1IO;
				}
				double l = 0;
				if (this.ctype == 1) 
					l = max(l1, l2, l3);
				else{
					l = epsilon[0] * l1 +  epsilon[1] * l2 + epsilon[2] * l3;
				}


				// We then build the heap with k elements
				if (heap_n < k){
					Candidate ca = new Candidate(l, s2FO, cand);
					kheap.insert(ca);
					heap_n ++;
					//System.out.println(kheap.getSize());
				} else{
					Candidate cmin = kheap.minimum();
					if (cmin.ro < l){
						Candidate newCand = new Candidate(l, s2FO, cand);
						//kheap.ReplaceMin(newCand);
						kheap.extractMin();
						kheap.insert(newCand);
					}
					//System.out.println(kheap.getSize());
				}
			}
			
			ii = 0;
			topkSet.clear();
			while(!kheap.isEmpty()){
				Candidate cmin = kheap.extractMin();
				int id = cmin.id;
				System.out.println("Find the candidate in round " + roundI + " " +  ((roundI - 1) * k + ii) + "/" + 
						users + " " + id + " " + cmin.localFriends + " " + 
						this.candFriendsFO.get(id) + " " + cmin.ro);
				outf.println(roundI + " " +  ((roundI - 1) * k + ii) + " " + id + " " + cmin.localFriends + " " + 
						this.candFriendsFO.get(id) + " " + cmin.ro);
				
				//this.seeds.add(id);
				this.candsFO.remove(id);
				this.candsFI.remove(id);
				this.candsIO.remove(id);
				//this.candRo.remove(id);
				//this.candFriends.remove(id);
				topkSet.add(id);
				ii ++;
			}
		}
		outf.close();
	}
	
	class Candidate implements Comparable<Candidate>{
	    double ro;
	    int localFriends;
	    int id;

	    Candidate(double ro1, int lf, int id1){
	    	ro = ro1;
	    	localFriends = lf;
	    	id = id1;
	    }
	    
		@Override
		public int compareTo(Candidate other) {
			// TODO Auto-generated method stub
			return (this.ro < other.ro) ? -1 : 1;
		}
	}
	
	public static void main(String[] args) {
		if (args.length < 11) {
			System.out
					.println("Usage: java foundUsersInteractions"
							+ "[seed File] [candidate ID] [candidate File-friends] [candidate File-followers] [candidate File-interactions] [threshold] [k] [t] [combinedType 1|2] [loc]");
			System.exit(-1);
		}

		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		System.out.println();
		System.out.println("----------------------------------------------");
		System.out.println("Start at: " + dateFormat.format(date));

		FoundUsersCombined fu = new FoundUsersCombined(args[0], args[1], args[2],args[3], args[4], Integer.parseInt(args[5]),
								Integer.parseInt(args[6]), Integer.parseInt(args[7]), Integer.parseInt(args[8]), args[9], Float.parseFloat(args[10]));
		
		fu.findUsers();
		
		date = new Date();
		System.out.println("End at: " + dateFormat.format(date));
	}
}
