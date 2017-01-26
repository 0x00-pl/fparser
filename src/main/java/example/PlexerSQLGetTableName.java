package example;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by pl on 1/26/17.
 */
public class PlexerSQLGetTableName extends PlexerSQL {
    public ArrayList<String> names = new ArrayList<>();
    private boolean[] next_may_be_a_table_name__stack = new boolean[100];
    private int stack_ptr = 0;
    private void push(){
        next_may_be_a_table_name__stack[++stack_ptr] = false;
    }
    private boolean pop(){
        return next_may_be_a_table_name__stack[stack_ptr--];
    }
    private void set_back(boolean b){
        next_may_be_a_table_name__stack[stack_ptr] = b;
    }
    private boolean back(){
        return next_may_be_a_table_name__stack[stack_ptr];
    }

    public PlexerSQLGetTableName() {
        set_back(false);
    }

    private boolean equal_substr(final byte[] a, int sa, final byte[] b, int sb, int length){
        for (int i = 0; i < length; i++) {
            if(a[sa]!=b[sb]){ return false; }
        }
        return true;
    }

    @Override
    protected void string_cb(byte[] text, int start, int end) {
        token_cb(text, start, end);
    }

    private boolean check_table_name_hint(final byte[] text, int start, int end, final byte[] keyword){
        return end-start == keyword.length && equal_substr(text, start, keyword, 0, keyword.length);
    }
    private boolean check_all_table_name_hint(final byte[] text, int start, int end){
        return check_table_name_hint(text, start, end, "from".getBytes()) ||
                check_table_name_hint(text, start, end, "join".getBytes()) ||
                (check_table_name_hint(text, start, end, ",".getBytes()) && back());
    }
    private boolean check_all_non_table_name_hint(final byte[] text, int start, int end){
        return check_table_name_hint(text, start, end, "where".getBytes()) ||
                check_table_name_hint(text, start, end, "order".getBytes()) ||
                check_table_name_hint(text, start, end, "limit".getBytes());
    }

    @Override
    protected void token_cb(byte[] text, int start, int end) {
        if(check_all_table_name_hint(text, start, end)){
            set_back(true);
        }else if(back()){
            this.names.add(new String(Arrays.copyOfRange(text, start, end)));
            set_back(false);
        }else if(check_all_non_table_name_hint(text, start, end)){
            set_back(false);
        }
    }

    @Override
    protected void bracket_l_cb() {
        push();
    }
    @Override
    protected void bracket_r_cb() {
        pop();
    }

    public static void main(String[] argv){
        PlexerSQLGetTableName gn = new PlexerSQLGetTableName();
        gn.lexer(new iter("select a from(select a,b from c),d join e".getBytes()));
        System.out.println(gn.names.toString());
    }
}
