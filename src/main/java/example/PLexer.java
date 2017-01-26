package example;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Created by pl on 1/26/17.
 */
public class PLexer {
    public static class iter{
        public final byte[] text;
        public int pos;
        public byte ch = '\0';

        public iter(byte[] text) {
            this.text = text;
            this.pos = 0;
        }
        public int get(){
            try {
                return ch = text[pos++];
            }catch(Exception e){
                pos = text.length;
                return '\0';
            }
        }
        public boolean startsWith(final byte[] text){
            try {
                for (int i = 0; i < text.length; i++) {
                    if(this.text[pos+i] != text[i]){
                        return false;
                    }
                }
            }catch(Exception e){
                pos = text.length;
                return false;
            }
            return true;
        }
    }
    protected boolean try_read_comment(iter it){
        throw new NotImplementedException();
    }
    protected boolean try_read_string(iter it){
        throw new NotImplementedException();
    }
    protected boolean try_read_blacks(iter it){
        throw new NotImplementedException();
    }
    protected boolean try_read_sep(iter it){
        throw new NotImplementedException();
    }
    protected boolean try_read_bracket_l(iter it){
        throw new NotImplementedException();
    }
    protected boolean try_read_bracket_r(iter it) {
        throw new NotImplementedException();
    }
    private int token_nextpos = 0;
    private int token_startpos = 0;
    private void read_token_iter(iter it){
        if(it.pos != token_nextpos) {
            flush_token(it);
            token_startpos = it.pos;
        }
        it.get();
        token_nextpos = it.pos;
    }
    public void flush_token(iter it){
        if(token_startpos == token_nextpos){ return; }
        token_cb(it.text, token_startpos, token_nextpos);
        token_startpos = token_nextpos;
    }

    protected void token_cb(final byte[] text, int start, int end){}


    public void lexer(iter it){
        while(true){
            if(it.get() == '\0'){ return; }else{ it.pos--; }
            if(try_read_blacks(it)){ continue; }
            if(try_read_sep(it)){ continue; }
            if(try_read_comment(it)){ continue; }
            if(try_read_string(it)){ continue; }
            if(try_read_bracket_l(it)){ continue; }
            if(try_read_bracket_r(it)){ continue; }
            read_token_iter(it);
        }
    }
}
