package dongdong;

import java.io.IOException;

import com.aliasi.lm.CompiledNGramProcessLM;
import com.aliasi.spell.CompiledSpellChecker;
import com.aliasi.spell.WeightedEditDistance;

import demo.querySpellChecker.QuerySpellCheck;

public class SpellCheckerTest {
	
	static CompiledSpellChecker sc;
	static CompiledNGramProcessLM lm;
	static WeightedEditDistance wed;
	public static void init() {
		try {
			sc = QuerySpellCheck.readModel(BuildIndex.MODEL_FILE);
			lm = sc.languageModel();
			wed = sc.editDistance();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void editDis() {
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String input = "sougo";
		String o1 = "sou guo";
		String o2 = "sougou";
		
		init();
		double d1 = wed.distance(input, o1);
		double d2 = wed.distance(input, o2);
		System.out.println(d1);
		System.out.println(d2);
	}

}
