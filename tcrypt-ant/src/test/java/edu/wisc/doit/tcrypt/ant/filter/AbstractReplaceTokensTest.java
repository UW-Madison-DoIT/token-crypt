package edu.wisc.doit.tcrypt.ant.filter;

import static org.junit.Assert.assertEquals;

import java.io.Reader;

import org.junit.Before;
import org.junit.Test;

import edu.wisc.doit.tcrypt.TokenEncrypter;
import edu.wisc.doit.tcrypt.ant.filter.AbstractReplaceTokens;

public class AbstractReplaceTokensTest {
    private AbstractReplaceTokens prt;
    
    @Before
    public void setup() {
        prt = new AbstractReplaceTokens() {
            @Override
            CharSequence replaceToken(String token) {
                return token;
            }
            @Override
            Reader createChainedReader(Reader rdr) {
                return null;
            }
            @Override
            void initialize() {
            }
        };
        
        prt.setBeginToken(TokenEncrypter.TOKEN_PREFIX);
        prt.setEndToken(TokenEncrypter.TOKEN_SUFFIX);
    }
    
    @Test
    public void testSingleTokenReplacement() {
        String actual;
        
        actual = prt.replaceTokensOnLine("this is a line with no tokens");
        assertEquals("this is a line with no tokens", actual);
        
        actual = prt.replaceTokensOnLine(" * @author Eric Dalquist\n");
        assertEquals(" * @author Eric Dalquist\n", actual);
        
        actual = prt.replaceTokensOnLine("this is a line with one token ENC(token1)");
        assertEquals("this is a line with one token token1", actual);
        
        actual = prt.replaceTokensOnLine("ENC(token1) this is a line with one token");
        assertEquals("token1 this is a line with one token", actual);
        
        actual = prt.replaceTokensOnLine("this is a line ENC(token1) with one token");
        assertEquals("this is a line token1 with one token", actual);
    }
    
    @Test
    public void testBrokenTokenReplacement() {
        String actual;
        
        actual = prt.replaceTokensOnLine("ENC(this is a line with no token and a start");
        assertEquals("ENC(this is a line with no token and a start", actual);
        
        actual = prt.replaceTokensOnLine("ENC(this is aENC( line )with no token )and a start");
        assertEquals("this is aENC( line with no token )and a start", actual);
        
        actual = prt.replaceTokensOnLine("this is a line with no token and a startENC(");
        assertEquals("this is a line with no token and a startENC(", actual);
    }
    
    @Test
    public void testMultiTokenReplacement() {
        String actual;
        
        actual = prt.replaceTokensOnLine("ENC(badger)ENC(badger)ENC(badger)ENC(badger)ENC(badger)ENC(badger)");
        assertEquals("badgerbadgerbadgerbadgerbadgerbadger", actual);
        
        actual = prt.replaceTokensOnLine(" ENC(badger) ENC(badger) ENC(badger) ENC(badger) ENC(badger) ENC(badger) ");
        assertEquals(" badger badger badger badger badger badger ", actual);
    }
}
