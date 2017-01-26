package example;

/**
 * Created by pl on 1/26/17.
 */
public class PlexerSQL extends PLexer {
    @Override
    protected boolean try_read_comment(iter it){
        return false;
    }
    @Override
    protected boolean try_read_string(iter it){
        final byte[] string_sym = {'\"'};
        if(!it.startsWith(string_sym)){ return false; }
        flush_token(it);
        int pos_start = it.pos;
        it.get();
        while(it.get() != '\"'){
            if(it.ch == '\\'){ it.get(); }
        }
        it.get();
        string_cb(it.text, pos_start, it.pos);
        return true;
    }
    @Override
    protected boolean try_read_blacks(iter it){
        int p = it.pos;
        it.get();
        while(it.ch==' ' || it.ch=='\n' || it.ch=='\t' || it.ch=='\r'){
            it.get();
        }
        it.pos--;
        if(p == it.pos){
            return false;
        }else{
            flush_token(it);
            return true;
        }
    }
    @Override
    protected boolean try_read_sep(iter it){
        if(it.get()==','){ flush_token(it); sep_cb(); return true; }
        else{ it.pos--; return false; }
    }
    @Override
    protected boolean try_read_bracket_l(iter it){
        if(it.get()=='('){ flush_token(it); bracket_l_cb(); return true; }
        else{ it.pos--; return false; }
    }
    @Override
    protected boolean try_read_bracket_r(iter it){
        if(it.get()==')'){ flush_token(it); bracket_r_cb(); return true; }
        else{ it.pos--; return false; }
    }

    protected void string_cb(final byte[] text, int start, int end){}
    protected void sep_cb(){}
    protected void bracket_l_cb(){}
    protected void bracket_r_cb(){}
}
