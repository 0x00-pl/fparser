package example;

import java.nio.charset.StandardCharsets;

/**
 * Created by pl on 1/27/17.
 */
public class SQLFast {
    public static class collector{
        public int select(final byte[] text, int pos){
            return 2;
        }
        public int join(final byte[] text, int pos){
            return 2;
        }
    }

    private static final int _lect = 'l'<<24 | 'e'<<16 | 'c'<<8 | 't';
    private static final int _join = 'j'<<24 | 'o'<<16 | 'i'<<8 | 'n';

    public static void lexer(final byte[] text, int pos, collector c){
        int cache = 0;
        int len = text.length;
        while(pos < len){
            cache = cache<<8 | text[pos]; // TODO: this line
            switch(cache) {
                case _lect:
                    pos += c.select(text, pos);
                    continue;
                case _join:
                    pos += c.join(text, pos);
                    continue;
                default:
                    pos++;
            }
        }
    }

    static long RunBench() {

        //byte[] src = "SELECT a FROM ab             , ee.ff AS f,(SELECT a FROM `schema_bb`.`tbl_bb`,(SELECT a FROM ccc AS c, `dddd`));".getBytes(StandardCharsets.UTF_8);//20794
        byte[] src = "select a,b,c,d,e,f,g from h".getBytes(StandardCharsets.UTF_8);//20794
        int count = 0;
        SQLFast.collector c = new SQLFast.collector();
        long start = System.currentTimeMillis();
        do {
            SQLFast.lexer(src, 0, c);
        } while (count++ < 10_000_000);
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
            System.out.println("min time F: " + cur);
        }
    }
}
