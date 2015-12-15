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

public class FoundUsersAdd {
	
	HashMap<Integer, int[]> cands;
	HashMap<Integer, Integer> candRo;
	HashMap<Integer, Integer> candFriends;
	HashSet<Integer> seeds;
	HashMap<Integer, HashSet<Integer>> addEdges;
	PrintWriter  outf = null;
	
	int t, k, threshold;
	
	int addE;
	
	FoundUsersAdd(String seedFile, String candIDFile, String candFile, int thre, int kk, int tt, String ff, int ad){
		threshold = thre;
		k = kk;
		t = tt;
		cands = new HashMap<Integer, int[]>();
		candRo = new HashMap<Integer, Integer>();
		candFriends = new HashMap<Integer, Integer>();
		seeds = new HashSet<Integer>();
		addEdges = new HashMap<Integer, HashSet<Integer>> ();;

		loadSeed(seedFile);
		loadCandID(candIDFile);
		
		loadAdd(ad);
		addE = ad;

		loadCand(candFile);
		//outf = buildOutFile(candFile.split("\\.")[0]);
		outf = buildOutFile("foundUsers" + threshold + "-" + ff + "-k" + k + "-t" + t + "-add" + ad + "-map.txt");
	}

	/*
	 * Add the additional neighbors to cands
	 */
	private void loadAdd(int add) {
		// TODO Auto-generated method stub
		String addFile = "add" + add + ".txt.gz";
		File inFile = new File(addFile);

		if (!inFile.isFile()) {
			System.out.println(addFile + " is not an existing file");
			return;
		}

		BufferedReader br = null;
		try {
			//br = new BufferedReader(new FileReader(candFile));
			// For gzip file
			br = new BufferedReader(new InputStreamReader(
			        new GZIPInputStream(new FileInputStream(addFile))));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		String line = null;

		//Read from the original file and write to the new
		//unless content matches data to be removed.
		int src = 0;
		try {
			while ((line = br.readLine()) != null){
				// For the user file with ID,screenName format
				String[] items = line.split(",");
				src = Integer.parseInt(items[0]);
				
				HashSet<Integer> adds = new HashSet<Integer>();
				for (int ii = 1; ii <= add; ii++)
					adds.add(Integer.parseInt(items[ii]));
				
				addEdges.put(src, adds);
			}
			br.close();
		} catch (IOException e) {
		// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("We will" + (addEdges.size() * add)+ " edges." );
	}

	private void loadCand(String candFile) {
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
					if( candRo.containsKey(cand) == false || friends > threshold || localFriends == 0){
						skipLine = true;
						continue;
					} else
						skipLine = false;
					
					/*
					 * Add the edges from addEdges
					 */
					int[] friendList = null;
					if (this.addEdges.containsKey(cand)){
						friendList = new int[localFriends + addE];
						friends += addE;
					}
					else
						friendList = new int[localFriends];
					cands.put(cand, friendList);
					//candRo.put(cand, 0);
					candFriends.put(cand, friends);
					localFriendsIndex = 0;
					
					/*
					 * We need to add the egdes here
					 * If there is a repeat edge, we add it anyway; The impact should be limited
					 */
					if (this.addEdges.containsKey(cand)){
						HashSet<Integer> addF = this.addEdges.get(cand);
						for (int fr : addF){
							friendList[localFriendsIndex ++] = fr;
						}
					}

					if (++users % 1000 == 0)
						System.out.println(users + " users have been added.");
				} else if (!skipLine){
					int[] fl = cands.get(cand);
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
			while ((line = br.readLine()) != null)
				// For the user file with ID,screenName format
				candRo.put(Integer.parseInt(line.split("\\s+")[0]), 0);
			br.close();
		} catch (IOException e) {
		// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("We load " + candRo.size() + " candidates." );
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
	
	
	private HashSet<Integer> findUsers_initial(){
		int roundI = 1;
		HashSet<Integer> topkSet = new HashSet<Integer>();
		
		MinPriorityQueue<Candidate> kheap = new HeapMinPriorityQueue<Candidate>(); 
		int heap_n = 0;
		
		for (int cand : cands.keySet()){
			
			// We first obtain the friends in seed set
			int s2 = commonFriends(seeds, cands.get(cand));
			
			this.candRo.put(cand, s2);
			int s1 = this.candFriends.get(cand);
			
			// We then build the heap with k elements
			if (heap_n < k){
				Candidate ca = new Candidate(1.0 * s2 / s1, s2, cand);
				kheap.insert(ca);
				heap_n ++;
			} else{
				Candidate cmin = kheap.minimum();
				if (cmin.ro < 1.0 * s2 / s1){
					Candidate newCand = new Candidate(1.0 * s2 / s1, s2, cand);
					kheap.ReplaceMin(newCand);
				}
			}
		}
		
		int ii = 0;
		while(!kheap.isEmpty()){
			Candidate cmin = kheap.extractMin();
			int id = cmin.id;
			System.out.println("Find the candidate in round " + roundI + " " +  ii + "/" + 
					cands.size() + " " + id + " " + cmin.localFriends + " " + 
					this.candFriends.get(id) + " " + cmin.ro);
			
			outf.println(roundI + " " +  ii + " " + id + " " + cmin.localFriends + " " + 
					this.candFriends.get(id) + " " + cmin.ro);
			
			//this.seeds.add(id);
			this.cands.remove(id);
			//this.candRo.remove(id);
			//this.candFriends.remove(id);
			topkSet.add(id);
			ii ++;
		}
		
		return topkSet;
	}
	
	public void findUsers() {
		// TODO Auto-generated method stub
		int users = cands.size();
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
			
			for (int cand : cands.keySet()){
				
				// We first obtain the friends in seed set
				int s2 = this.commonFriends(topks, cands.get(cand));
				this.candRo.put(cand, this.candRo.get(cand) + s2);
				s2 = this.candRo.get(cand);
				
				int s1 = this.candFriends.get(cand);
				
				// We then build the heap with k elements
				if (heap_n < k){
					Candidate ca = new Candidate(1.0 * s2 / s1, s2, cand);
					kheap.insert(ca);
					heap_n ++;
					//System.out.println(kheap.getSize());
				} else{
					Candidate cmin = kheap.minimum();
					if (cmin.ro < 1.0 * s2 / s1){
						Candidate newCand = new Candidate(1.0 * s2 / s1, s2, cand);
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
						this.candFriends.get(id) + " " + cmin.ro);
				outf.println(roundI + " " +  ((roundI - 1) * k + ii) + " " + id + " " + cmin.localFriends + " " + 
						this.candFriends.get(id) + " " + cmin.ro);
				
				//this.seeds.add(id);
				this.cands.remove(id);
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
		if (args.length < 8) {
			System.out
					.println("Usage: java foundUsers"
							+ "[seed File] [candidate ID] [candidate File] [threshold] [k] [t] [followers|friends] [adds]");
			System.exit(-1);
		}

		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		System.out.println();
		System.out.println("----------------------------------------------");
		System.out.println("Start at: " + dateFormat.format(date));

		FoundUsersAdd fu = new FoundUsersAdd(args[0], args[1], args[2], Integer.parseInt(args[3]),
								Integer.parseInt(args[4]), Integer.parseInt(args[5]), args[6], Integer.parseInt(args[7]));
		
		fu.findUsers();
		
		date = new Date();
		System.out.println("End at: " + dateFormat.format(date));
	}
}
