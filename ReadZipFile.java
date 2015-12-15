package Evaluation;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

public class ReadZipFile {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		BufferedReader in;
		try {
			in = new BufferedReader(new InputStreamReader(
			        new GZIPInputStream(new FileInputStream("E:\\UserDiscover\\TS\\candidates-filter-map.txt.gz"))));
			String content;
			
			int count = 0;
			while ((content = in.readLine()) != null)
				count ++;
			System.out.println(count);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
