package Evaluation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Random;

/*
 * This class is to evaluate the performance of 
 * networked socialbots spam campaign by building 
 * the single retweeting tree.
 */

public class SpamCampaignTree {
	/* Input */
	private int n_max;	// The maximum number of socialbots;
	//private int[] nn;	// The number of socialbots;
	private int K;	// The number of levels of retweeting forest;
	private int KF;	// The actual number of levels of retweeting forest;
	private int M;	// All socialbots in the first M hops will be suspended;
	
	ArrayList<String> L; // The unused legitimate followers set;
	HashSet<String> LComp; // The complement of L;
	private int L_size;	// The size of L;
	private int[] Li;		// The number of legitimate followers for each socialbot
	int mu = 32;
	double sigma = 5;	// The Gaussian parameter for Li
	String[][] LiSetArray; // Each socialbot will have a legitimate followers set
	HashSet<String>[] LiSet; // Each socialbot will have a legitimate followers set
	
	
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
	
	SpamCampaignTree(int n_max, int K, int M, String l_file, 
			double[] tau, double r){
		this.n_max = n_max;
		this.K = K;
		this.M = M;
		this.tau = tau;
		this.r = r;
		//this.ss = ss;
		//this.nn = nn;
		
		loadLegitimateFollowers(l_file);
		generateFollowersSet();
		
		//this.Vk = new int[K][];
		//this.Vs = new int[s];
	}

	@SuppressWarnings("unchecked")
	private void generateFollowersSet() {
		// TODO Auto-generated method stub
		LiSet = new HashSet[n_max];
		Li = new int[n_max];
		
		Random  rand = new Random();
		for(int i = 0; i < n_max; i ++){
			Li[i] = (int) (mu + rand.nextGaussian() * sigma);
			if(Li[i] <= 0)
				Li[i] = 1;
			LiSet[i] = new HashSet<String>();
			int[] index_Li = chooseMfromN(Li[i], L.size());
			for (int j = 0; j < Li[i]; j ++){
				LiSet[i].add(L.get(index_Li[j]));
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
 * Greedy algorithm for max-k-cover problem, which is NP-hard.
 * vn : the set of unused socialbots
 * vnComp: the set of selected socialbots
 */
int[] MaxCover(int x, HashSet<Integer> vn, HashSet<Integer> vnComp){
	int[] ret = new int[x];
	
	// If vn is not enough, return the vn directly.
	/*if(x >= vn.size()){
		int i = 0;
		for(int v : vn){
			ret[i] = v;
			vn.remove(v);
			vnComp.add(v);
			i ++;
		}
		return ret;
	}*/
	/*
	 * Build the set for legitimate users
	 */
	//ArrayList<String> L = new ArrayList<String>();
	HashSet<String> LComp = new HashSet<String>();
	for (int v : vnComp){
		LComp.addAll(this.LiSet[v]);
	}
	
	for(int i = 0; i < x; i ++){
		int gain = -1;
		int v_max = -1;
		
		for(int v : vn){
			HashSet<String> temp = this.LiSet[v];
			temp.removeAll(LComp);
			if(gain < temp.size()){
				gain = temp.size();
				v_max = v;
			}
		}
		if(v_max == -1)
			continue;
		//this.L.removeAll(this.LiSet[v_max]);
		LComp.addAll(this.LiSet[v_max]);
		vn.remove(v_max);
		vnComp.add(v_max);
		ret[i] = v_max;
	}
	return ret;
}

/*
 * Greedy algorithm for min-k-cover problem, which is NP-hard. 
 * Min-k-cover is equivalent to max-(n-k)-cover problem.
 * 
 * vn : the set of unused socialbots
 * vnComp: the set of selected socialbots
 */
int[] MinCover(int x, HashSet<Integer> vn, HashSet<Integer> vnComp){
	int[] ret = new int[x];
	
	// If vn is not enough, return the vn directly.
	/*if(x >= vn.size()){
		int i = 0;
		for(int v : vn){
			ret[i] = v;
			vn.remove(v);
			vnComp.add(v);
			i ++;
		}
		return ret;
	}*/

	/*
	 * Build the set for legitimate users
	 */
	//ArrayList<String> L = new ArrayList<String>();
	HashSet<String> LComp = new HashSet<String>();
	//for (int v : vn)
	//	L.addAll(this.LiSet[v]);
	for (int v : vnComp){
		LComp.addAll(this.LiSet[v]);
	}
	
	for(int i = 0; i < x; i ++){
		int gain = 1000000000;
		int v_min = -1;
		
		for(int v : vn){
			HashSet<String> temp = this.LiSet[v];
			temp.removeAll(LComp);
			if(gain > temp.size()){
				gain = temp.size();
				v_min = v;
			}
		}
		if(v_min == -1)
			continue;

		//L.removeAll(this.LiSet[v_max]);
		LComp.addAll(this.LiSet[v_min]);
		vn.remove(v_min);
		vnComp.add(v_min);
		ret[i] = v_min;
	}
	return ret;
}

//@SuppressWarnings("unchecked")
public void greedyAppr(int n, int s1){

	if(n > this.n_max){
		System.out.println("Unsupported n.");
		return;
	}
	
	/*LiSet = new HashSet[n];
	for(int i = 0; i < n; i ++){
		LiSet[i] = new HashSet<String>();
		for (int j = 0; j < this.Li[i]; j++){
			this.LiSet[i].add(this.LiSetArray[i][j]);
		}
		if(this.LiSet[i].size() == 0)
			break;
	}*/

	HashSet<String> totalL = new HashSet<String>();
	int total = 0;
	for(int i = 0; i < n; i ++){
		totalL.addAll(this.LiSet[i]);
		total += this.LiSet[i].size();
	}
	this.L_size = totalL.size();
	System.out.println("Total followers: " + total);
	
	this.Vk = new int[K][];	// The output
	this.KF = 0;			// The output
	
	HashSet<Integer> vn;	// The unused soialbots
	HashSet<Integer> vnComp;	// The selected soialbots
	vn = new HashSet<Integer>();
	vnComp = new HashSet<Integer>();
	for (int i = 0; i < n; i ++)
		vn.add(i);

	if(s1 >= n){
		this.Vk[0] = new int[n];
		for (int i = 0; i < n; i ++)
			this.Vk[0][i] = i;
		this.Vs = this.Vk[0];
		this.KF = 1;
	}else
	{
		/* Firstly choose s minimum subset for the first M levels */
		this.Vs = this.MinCover(s1, vn, vnComp);
		HashSet<Integer> Vsn = new HashSet<Integer>();
		for (int v : this.Vs)
			Vsn.add(v);
		HashSet<Integer> VsnComp = new HashSet<Integer>();
		int num = s1 - this.M + 1;
		this.Vk[M - 1] = this.MaxCover(num, Vsn, VsnComp);
		//System.out.println(M + " level: " + this.Vk[M - 1].length);
		num = 1;	// The first M-1 levels are a straight line.
		for (int k = 0; k < this.M - 1; k ++){
			this.Vk[k] = this.MaxCover(num, Vsn, VsnComp);
			//System.out.println((k + 1) + " level: " + this.Vk[k].length);
		}
		
		/* Handle the indivisible for the first M levels */
		//handleIndivisible();
		
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
			
			if(x >= vn.size()){
				x = vn.size();
				// If the M-th level is surplus for the M+1 level, 
				// we will move the exceeded ones to the first level;
				if(k == M){
					adjustVMandV1(vn.size());
				}
			}
			
			this.Vk[k] = this.MaxCover(x, vn, vnComp);
			//System.out.println((k + 1) + " level: " + this.Vk[k].length);
		}
	}
	
	buildRTF();
	
	/* For statistics */
	// For coverage C
	HashSet<String> selectedL = new HashSet<String>();
	HashSet<String> selectedLSafe = new HashSet<String>();
	
	for (int k = 0; k < this.KF; k ++)
		for (int v : this.Vk[k]){
			selectedL.addAll(this.LiSet[v]);
			if(k >= M)
				selectedLSafe.addAll(this.LiSet[v]);
		}
	this.C = selectedL.size();
	
	// For cost S2
	/*HashSet<String> selectedLVs = new HashSet<String>();
	for (int k : this.Vs){
		selectedLVs.addAll(this.LiSet[k]);
	}*/
	//this.S2 = selectedLVs.size();
	selectedL.removeAll(selectedLSafe);
	this.S2 = selectedL.size();
	
	// For delay T
	HashSet<String> VkPast = new HashSet<String>();

	double tauTemp = 0;
	for (int k = 0; k < this.KF; k ++){
		tauTemp += this.tau[k];
		HashSet<String> VkTemp = new HashSet<String>();
		// Obtain the followers set for this level
		for (int v : this.Vk[k]){
			VkTemp.addAll(this.LiSet[v]);
		}
		HashSet<String> VkTempSub = VkTemp;
		VkTempSub.removeAll(VkPast);
		this.T += tauTemp * VkTempSub.size();
		
		// Sum up all the followers for level between 0 and K - 1
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

private void adjustVMandV1(int size) {
	// TODO Auto-generated method stub
	int cutoff = 0;
	int x = 0;
	int len_Vm = this.Vk[M - 1].length;
	for (int i = 0; i < len_Vm; i ++){
		int v = this.Vk[M - 1][i];
		x += Math.ceil(this.LiSet[v].size() * this.r / (1 - this.r));
		// If x is large enough, the rest of bots in Vm will be cutoff
		if(x >= size){
			cutoff = i + 1;
			break;
		}
	}
	
	// Build a new Vm and V1
	int[] newVm = new int[cutoff];
	int[] newV1 = new int[len_Vm - cutoff + 2];

	for (int i = 0; i < cutoff; i ++){
		newVm[i] = this.Vk[M - 1][i];
	}

	newV1[0] = this.Vk[0][0];
	for (int i = 1; i < len_Vm - cutoff + 1; i ++){
		newV1[i] = this.Vk[M - 1][i + cutoff - 1];
	}
	
	this.Vk[0] = newV1;
	this.Vk[M - 1] = newVm;
}

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
	for(int k = 1; k < K; k ++)
		tau[k] = 0.5;
	
	double r = 0.2;
	
	int n_num = 6;
	int s1_num = 20;
	int[][] KFMatrix = new int[n_num][s1_num];
	int[][] CMatrix = new int[n_num][s1_num];
	int[][] LSizeMatrix = new int[n_num][s1_num];
	int[][] S2Matrix = new int[n_num][s1_num];
	double[][] TMatrix = new double[n_num][s1_num];
	
	//SpamCampaign spam = new SpamCampaign(n_num * 100, K, M, l_file, tau, r);
	DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	Date date = new Date();
	System.out.println();
	System.out.println("----------------------------------------------");
	System.out.println("Start at: " + dateFormat.format(date));

	for (int i = 1; i <= n_num; i ++){
		for (int j = 1; j <= s1_num; j ++){
			int n = i * 100;
			int s1 = j * 5;
			System.out.println("n = " + n + ", S1 = " + s1);
			SpamCampaignTree spam = new SpamCampaignTree(n, K, M, l_file, tau, r);
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

	date = new Date();
	System.out.println("End at: " + dateFormat.format(date));
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
