package dongdong;

import com.aliasi.tokenizer.Tokenizer;
import com.aliasi.tokenizer.TokenizerFactory;
import com.aliasi.util.Strings;

public class MyTokenizerFactory implements TokenizerFactory {
	
	private MyTokenizerFactory() {
		
	}
	
	public static final TokenizerFactory INSTANCE = new MyTokenizerFactory();

	@Override
	public Tokenizer tokenizer(char[] ch, int start, int length) {
		// TODO Auto-generated method stub
		String input = new String(ch, start, length);
		Tokenizer tokenizer = new MyTokenizer(input, false);
		return tokenizer;
	}

	public static void main(String[] args) {
		TokenizerFactory mTokenizerFactory = MyTokenizerFactory.INSTANCE;
		CharSequence cSeq = "会计";
        char[] cs = Strings.toCharArray(cSeq);
        Tokenizer tokenizer = mTokenizerFactory.tokenizer(cs,0,cs.length);
        String nextToken;
        while ((nextToken = tokenizer.nextToken()) != null) {
//            mTokenCounter.increment(nextToken);
//            sb.append(nextToken);
//            sb.append(' ');
        	System.out.println(nextToken);
        }
    
	}

}
