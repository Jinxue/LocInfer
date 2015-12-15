package Evaluation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/*
 * This class is to evaluate the performance of 
 * networked socialbots spam campaign.
 */

public class SpamCampaign {
	
	/* Input */
	private int n_max;	// The maximum number of socialbots;
	//private int[] nn;	// The number of socialbots;
	private int K;	// The number of levels of retweeting forest;
	private int KF;	// The actual number of levels of retweeting forest;
	private int M;	// All socialbots in the first M hops will be suspended;
	
	ArrayList<String> L; // The unused legitimate followers set;
	ArrayList<String> LComp; // The complement of L;
	private int L_size;	// The size of L;
	private int[] Li;		// The number of legitimate followers for each socialbot
	int mu = 20;
	double sigma = 5;	// The Gaussian parameter for Li
	String[][] LiSetArray; // Each socialbot will have a legitimate followers set
	ArrayList<String>[] LiSet; // Each socialbot will have a legitimate followers set
	
	
	//private ArrayList<Integer> vn;	// The unused soialbots
	//private ArrayList<Integer> vnComp;	// The selected soialbots
	double[] tau; // The delay for each level;
	
	private double r; // The retweeting rate for each socialbots;
	
	//private int[] ss;	// The budget of suspended accounts, S_1;
	
	/* Output */
	int[][] Vk;	// The socialbots set for each level;
	private int C;	// The audience coverage
	private int S2; // The lost legitimate followers
	private int[] Vs; // The set for suspended socialbots
	private double T; // The overall delay for the spam campaign.
	
	SpamCampaign(int n_max, int K, int M, String l_file, 
			double[] tau, double r){
		this.n_max = n_max;
		this.K = K;
		this.M = M;
		this.tau = tau;
		this.r = r;
		//this.ss = ss;
		//this.nn = nn;
		
		loadLegitimateFollowers(l_file);
		generateFollowers();
		
		//this.Vk = new int[K][];
		//this.Vs = new int[s];
	}

	/*SpamCampaign(int n, int K, int M, String l_file, 
			double[] tau, double r){
		this.n_max = n;
		this.K = K;
		this.M = M;
		this.tau = tau;
		this.r = r;
		
		loadLegitimateFollowers(l_file);
		generateFollowers();
		
		//this.Vk = new int[K][];
		//this.Vs = new int[s];
	}*/

	private void generateFollowers() {
		// TODO Auto-generated method stub
		LiSetArray = new String[n_max][];
		Li = new int[n_max];
		
		Random  rand = new Random();
		for(int i = 0; i < n_max; i ++){
			Li[i] = (int) (mu + rand.nextGaussian() * sigma);
			if(Li[i] <= 0)
				Li[i] = 1;
			LiSetArray[i] = new String[Li[i]];
			int[] index_Li = chooseMfromN(Li[i], L.size());
			for (int j = 0; j < Li[i]; j ++){
				LiSetArray[i][j]= L.get(index_Li[j]);
			}
			/*if(this.LiSetArray[i].length == 0)
				break;*/
		}
	}

	private void loadLegitimateFollowers(String l_file) {
		// TODO Auto-generated method stub
		L = new ArrayList<String>();
		
		File inFile = new File(l_file);

		if (!inFile.isFile()) {
			System.out.println("Parameter is not an existing file");
			return;
		}

		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(l_file));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		String line = null;

		//Read from the original file and write to the new
		//unless content matches data to be removed.
		try {
			while ((line = br.readLine()) != null) {
				String[] followers = line.split(",");
				this.L.add(followers[0]);
			}
			br.close();
		} catch (IOException e) {
		// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*void setBudget(int[] s){
	this.s = s;
	this.KF = 0;
}*/
/*
 * Get the maximum subset union 
 * vn : the set of unused socialbots
 * vnComp: the set of selected socialbots
 */
int[] MaxSubUnion(int x, ArrayList<Integer> vn, ArrayList<Integer> vnComp){
	int[] ret = new int[x];
	
	/*
	 * Build the set for legitimate users
	 */
	//ArrayList<String> L = new ArrayList<String>();
	ArrayList<String> LComp = new ArrayList<String>();
	for (int v : vnComp){
		LComp.removeAll(this.LiSet[v]);
		LComp.addAll(this.LiSet[v]);
	}
	
	for(int i = 0; i < x; i ++){
		int gain = -1;
		int v_max = -1;
		
		for(int v : vn){
			ArrayList<String> temp = this.LiSet[v];
			temp.removeAll(LComp);
			if(gain < temp.size()){
				gain = temp.size();
				v_max = v;
			}
		}
		if(v_max == -1)
			continue;
		//this.L.removeAll(this.LiSet[v_max]);
		LComp.removeAll(this.LiSet[v_max]);
		LComp.addAll(this.LiSet[v_max]);
		vn.remove(vn.indexOf(v_max));
		vnComp.add(v_max);
		ret[i] = v_max;
	}
	return ret;
}

/*
 * Get the minimum subset union 
 * vn : the set of unused socialbots
 * vnComp: the set of selected socialbots
 */
int[] MinSubUnion(int x, ArrayList<Integer> vn, ArrayList<Integer> vnComp){
	int[] ret = new int[x];
	
	/*
	 * Build the set for legitimate users
	 */
	//ArrayList<String> L = new ArrayList<String>();
	ArrayList<String> LComp = new ArrayList<String>();
	//for (int v : vn)
	//	L.addAll(this.LiSet[v]);
	for (int v : vnComp){
		LComp.removeAll(this.LiSet[v]);
		LComp.addAll(this.LiSet[v]);
	}
	
	for(int i = 0; i < x; i ++){
		int gain = 1000000000;
		int v_min = -1;
		
		for(int v : vn){
			ArrayList<String> temp = this.LiSet[v];
			temp.removeAll(LComp);
			if(gain > temp.size()){
				gain = temp.size();
				v_min = v;
			}
		}
		if(v_min == -1)
			continue;

		//L.removeAll(this.LiSet[v_max]);
		LComp.removeAll(this.LiSet[v_min]);
		LComp.addAll(this.LiSet[v_min]);
		vn.remove(vn.indexOf(v_min));
		vnComp.add(v_min);
		ret[i] = v_min;
	}
	return ret;
}

@SuppressWarnings("unchecked")
public void greedyAppr(int n, int s1){

	if(n > this.n_max){
		System.out.println("Unsupported n.");
		return;
	}
		
	LiSet = new ArrayList[n];
	for(int i = 0; i < n; i ++){
		LiSet[i] = new ArrayList<String>();
		for (int j = 0; j < this.Li[i]; j++){
			this.LiSet[i].add(this.LiSetArray[i][j]);
		}
		/*if(this.LiSet[i].size() == 0)
			break;*/
	}

	ArrayList<String> totalL = new ArrayList<String>();
	int total = 0;
	for(int i = 0; i < n; i ++){
		totalL.removeAll(this.LiSet[i]);
		totalL.addAll(this.LiSet[i]);
		total += this.LiSet[i].size();
	}
	this.L_size = totalL.size();
	System.out.println("Total followers: " + total);
	
	this.Vk = new int[K][];	// The output
	this.KF = 0;			// The output
	
	ArrayList<Integer> vn;	// The unused soialbots
	ArrayList<Integer> vnComp;	// The selected soialbots
	vn = new ArrayList<Integer>();
	vnComp = new ArrayList<Integer>();
	for (int i = 0; i < n; i ++)
		vn.add(i);

	/* Firstly choose s minimum subset for the first M levels */
	this.Vs = this.MinSubUnion(s1, vn, vnComp);
	ArrayList<Integer> Vsn = new ArrayList<Integer>();
	for (int v : this.Vs)
		Vsn.add(v);
	ArrayList<Integer> VsnComp = new ArrayList<Integer>();
	int num = s1 / this.M;
	this.Vk[M - 1] = this.MaxSubUnion(num, Vsn, VsnComp);
	//System.out.println(M + " level: " + this.Vk[M - 1].length);
	for (int k = 0; k < this.M - 1; k ++){
		this.Vk[k] = this.MaxSubUnion(num, Vsn, VsnComp);
		//System.out.println((k + 1) + " level: " + this.Vk[k].length);
	}
	
	/* Handle the indivisible for the first M levels */
	handleIndivisible();
	
	/* Then determine the levels from M+1 to K */
	for (int k = M; k < this.K; k ++){
		this.KF = k + 1;
		if(vn.isEmpty()){
			this.KF -= 1;
			break;
		}
		
		int x = 0;
		for (int v : this.Vk[k - 1]){
			x += Math.ceil(this.LiSet[v].size() * this.r / (1 - this.r));
			if(x == 0){
				break;
			}
		}
		if(x > vn.size())
			x = vn.size();
		this.Vk[k] = this.MaxSubUnion(x, vn, vnComp);
		//System.out.println((k + 1) + " level: " + this.Vk[k].length);
	}
	
	buildRTF();
	
	/* For statistics */
	// For coverage C
	ArrayList<String> selectedL = new ArrayList<String>();
	for (int k = 0; k < this.KF; k ++)
		for (int v : this.Vk[k]){
			selectedL.removeAll(this.LiSet[v]);
			selectedL.addAll(this.LiSet[v]);
		}
	this.C = selectedL.size();
	
	// For cost S2
	ArrayList<String> selectedLVs = new ArrayList<String>();
	for (int k : this.Vs){
		selectedLVs.removeAll(this.LiSet[k]);
		selectedLVs.addAll(this.LiSet[k]);
	}
	this.S2 = selectedLVs.size();
	
	// For delay T
	ArrayList<String> VkPast = new ArrayList<String>();

	double tauTemp = 0;
	for (int k = 0; k < this.KF; k ++){
		tauTemp += this.tau[k];
		ArrayList<String> VkTemp = new ArrayList<String>();
		// Obtain the followers set for this level
		for (int v : this.Vk[k]){
			VkTemp.removeAll(this.LiSet[v]);
			VkTemp.addAll(this.LiSet[v]);
		}
		ArrayList<String> VkTempSub = VkTemp;
		VkTempSub.removeAll(VkPast);
		this.T += tauTemp * VkTempSub.size();
		
		// Sum up all the followers for level between 0 and K - 1
		VkPast.removeAll(VkTemp);
		VkPast.addAll(VkTemp);
	}
	this.T = this.T / this.C;

	System.out.println("KF=" + this.KF + ", C=" + this.C + ", S1=" + s1
			+ ", S2=" + this.S2  + ", T=" + this.T);
}

/*void greedyApproBatch(){
	for (int n : this.nn){
		for(int s1 : this.ss){
			this.greedyAppr(n, s1);
		}
	}
}*/

public int getKF(){
	return this.KF;
}

public int getC(){
	return this.C;
}

public int getS2(){
	return this.S2;
}

public double getT(){
	return this.T;
}

public int getLSize(){
	return this.L_size;
}

public static void main(String[] args){
	//SpamCampaign(int n, int K, int M, String l_file, 
	//		double[] tau, double r, int s){

	//int n = 400;
	int K = 10;
	int M = 3;
	String l_file = "Lfollowers.txt";
	//String l_file = "sampleID-complete.txt";
	double[] tau = new double[K];
	for(int k = 0; k < K; k ++)
		tau[k] = 0.5;
	
	double r = 0.2;
	
	int n_num = 6;
	int s1_num = 10;
	int[][] KFMatrix = new int[n_num][s1_num];
	int[][] CMatrix = new int[n_num][s1_num];
	int[][] LSizeMatrix = new int[n_num][s1_num];
	int[][] S2Matrix = new int[n_num][s1_num];
	double[][] TMatrix = new double[n_num][s1_num];
	
	//SpamCampaign spam = new SpamCampaign(n_num * 100, K, M, l_file, tau, r);
	
	for (int i = 1; i <= n_num; i ++){
		for (int j = 1; j <= s1_num; j ++){
			int n = i * 100;
			int s1 = j * M;
			System.out.println("n = " + n + ", S1 = " + s1);
			SpamCampaign spam = new SpamCampaign(n, K, M, l_file, tau, r);
			spam.greedyAppr(n, s1);
			KFMatrix[i - 1][j - 1] = spam.getKF();
			CMatrix[i - 1][j - 1] = spam.getC();
			LSizeMatrix[i - 1][j - 1] = spam.getLSize();
			S2Matrix[i - 1][j - 1] = spam.getS2();
			TMatrix[i - 1][j - 1] = spam.getT();
			//ss[i - 1] = i * M;
		}
	}

	// Output the matrix;
	System.out.println("KF matrix.");
	for (int i = 0; i < n_num; i ++){
		for (int j = 0; j < s1_num; j ++){
			System.out.print(KFMatrix[i][j] + " ");
		}
		System.out.println(";");
	}

	System.out.println("C matrix.");
	for (int i = 0; i < n_num; i ++){
		for (int j = 0; j < s1_num; j ++){
			System.out.print(CMatrix[i][j] + " ");
		}
		System.out.println(";");
	}

	System.out.println("LSize matrix.");
	for (int i = 0; i < n_num; i ++){
		for (int j = 0; j < s1_num; j ++){
			System.out.print(LSizeMatrix[i][j] + " ");
		}
		System.out.println(";");
	}

	System.out.println("S2 matrix.");
	for (int i = 0; i < n_num; i ++){
		for (int j = 0; j < s1_num; j ++){
			System.out.print(S2Matrix[i][j] + " ");
		}
		System.out.println(";");
	}

	System.out.println("T matrix.");
	for (int i = 0; i < n_num; i ++){
		for (int j = 0; j < s1_num; j ++){
			System.out.print(TMatrix[i][j] + " ");
		}
		System.out.println(";");
	}

	/*int[] ss = new int[10];
	for (int i = 1; i < 11; i ++){
		ss[i - 1] = i * M;
	}

	SpamCampaign spam = new SpamCampaign(n, K, M, l_file, tau, r, ss);
	spam.greedyApproBatch();*/
}

private void buildRTF() {
	// TODO Auto-generated method stub
	
}

private void handleIndivisible() {
	// TODO Auto-generated method stub
	
}

int[] chooseMfromN(int m, int n) {

	int[] ret = new int[m];

	int[] orig = new int[n];
	for (int i = 0; i < n; i++)
		orig[i] = i;

	Random rand = new Random();
	for (int i = n - 1; i >= n - m; i--) {
		int p = rand.nextInt(i + 1);
		/*
		 * Swap the p and i
		 */
		int swap = orig[p];
		orig[p] = orig[i];
		orig[i] = swap;

		ret[n - 1 - i] = orig[i];
	}
	// intentionally for this setting
	//ret[m - 1] = orig[0];
	return ret;
}
}
