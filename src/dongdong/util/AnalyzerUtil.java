package dongdong.util;

import org.apache.lucene.analysis.Analyzer;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class AnalyzerUtil {
	
	private static final Analyzer smart = new IKAnalyzer(true);
	private static final Analyzer noSmart = new IKAnalyzer(false);

	public static Analyzer getAnalyzer(boolean useSmart) {
		if (useSmart) {
			return smart;
		} else {
			return noSmart;
		}
	}

}
