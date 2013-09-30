package dongdong.didUMean;

import java.io.IOException;
import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.lucene.IKAnalyzer;



import com.aliasi.tokenizer.Tokenizer;

import dongdong.util.AnalyzerUtil;

public class MyTokenizer extends Tokenizer {
	// IK分词器实现
	private IKSegmenter _IKImplement;
	private TokenStream ts;
	private Analyzer analyzer;

	// 词元文本属性
	private final CharTermAttribute termAtt;
	// 词元位移属性
	private final OffsetAttribute offsetAtt;
	// 词元分类属性（该属性分类参考org.wltea.analyzer.core.Lexeme中的分类常量）
	private final TypeAttribute typeAtt;
	// 记录最后一个词元的结束位置
	private int endPosition;

	public MyTokenizer(String input, boolean useSmart) {
		try {
			analyzer = new IKAnalyzer(useSmart);
			ts = analyzer.tokenStream(null, new StringReader(input));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		offsetAtt = ts.addAttribute(OffsetAttribute.class);
	    termAtt = ts.addAttribute(CharTermAttribute.class);
	    typeAtt = ts.addAttribute(TypeAttribute.class);
		_IKImplement = new IKSegmenter(new StringReader(input), useSmart);
	}
	

	@Override
	public String nextToken() {
		try {
			if (ts.incrementToken()) {
				return termAtt.toString();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static void main(String[] args) {
		String text = "会";
		MyTokenizer tokenizer = new MyTokenizer(text, false);
		System.out.println(tokenizer.nextToken());
		System.out.println(tokenizer.nextToken());
	}
}
