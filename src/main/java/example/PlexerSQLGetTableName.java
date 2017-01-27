package example;

import java.nio.charset.StandardCharsets;
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
        final byte[] _from = "from".getBytes();
        final byte[] _join = "join".getBytes();
        final byte[] _comma = ",".getBytes();
        return check_table_name_hint(text, start, end, _from) ||
                check_table_name_hint(text, start, end, _join) ||
                (check_table_name_hint(text, start, end, _comma) && back());
    }
    private boolean check_all_non_table_name_hint(final byte[] text, int start, int end){
        final byte[] _where = "where".getBytes();
        final byte[] _order = "order".getBytes();
        final byte[] _limit = "limit".getBytes();
        return check_table_name_hint(text, start, end, _where) ||
                check_table_name_hint(text, start, end, _order) ||
                check_table_name_hint(text, start, end, _limit);
    }

    @Override
    protected void token_cb(byte[] text, int start, int end) {
        if(check_all_table_name_hint(text, start, end)){
            set_back(true);
        }else if(back()){
            //this.names.add(new Strselect a,b,c,d,e,f,g from hing(Arrays.copyOfRange(text, start, end)));
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

    public static void main1(String[] argv){
        PlexerSQLGetTableName gn = new PlexerSQLGetTableName();
        gn.lexer(new iter("select a from(select a,b from c),d join e".getBytes()));
        System.out.println(gn.names.toString());
    }

    static long RunBench() {
        //  todo:动态解析代码生成器
        //  todo:函数调用
        //  tip:sql越长时间越长
        //  tip:递归好像消耗有点大

        //byte[] src = "SELECT a FROM ab             , ee.ff AS f,(SELECT a FROM `schema_bb`.`tbl_bb`,(SELECT a FROM ccc AS c, `dddd`));".getBytes(StandardCharsets.UTF_8);//20794
        byte[] src = "select a,b,c,d,e,f,g from h".getBytes(StandardCharsets.UTF_8);//20794
        int count = 0;
        long start = System.currentTimeMillis();
        do {
            PlexerSQLGetTableName l = new PlexerSQLGetTableName();
            l.lexer(new PLexer.iter(src));  //parser.parse(src, context);
        } while (count++ < 10_000_0);
        return System.currentTimeMillis() - start;
    }
    public static void main(String[] args) {
        long min = 0;
        for (int i = 0; i < 10; i++) {
            //System.out.print("Loop "+i+" : ");
            long cur = RunBench();//无参数优化：7510，加server参数7657，因为是默认就是server jvm，优化流程后6531
            //System.out.println(cur);
            if (cur < min || min == 0) {
                min = cur;
            }
            System.out.println("min time P: " + cur);
        }
    }
}
