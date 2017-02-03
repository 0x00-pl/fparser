package example;

import java.nio.charset.StandardCharsets;

/**
 * Created by pl on 2/3/17.
 */
public class FC {

    public static final class collector{
        public int select(final byte[] text, int pos){
            return 0;
        }
        public int join(final byte[] text, int pos){
            return 0;
        }
    }

    private int pos = 0;
    public byte[] text;
    public collector c;
    public void lexer(){
        //final byte[] text = bytes;
        pos = 0;
        while(pos+4 < text.length){
            switch(text[pos+3]){
                case 'f':
                    pos += 3;
                    break;
                case 'r':
                    pos += 2;
                    break;
                case 'o':
                    pos += 1;
                    break;
                case 'm':
                    pos += 4;
                    if(text[pos]=='l' && text[pos+1]=='e' && text[pos+2]=='c'){
                        pos += c.select(text, pos);
                    }
                    break;
                case 'i':
                    pos += 3;
                    break;
//                case 'o':
//                    pos += 2;
//                    break;
                case 'j':
                    pos += 1;
                    break;
                case 'n':
                    pos += 4;
                    if(text[pos]=='j' && text[pos+1]=='o' && text[pos+2]=='i'){
                        pos += c.join(text, pos);
                    }
                    break;
                default:
                    pos += 4;
            }
        }
    }
    static long RunBench(FC fc, FC.collector c) {

        //byte[] src = "SELECT a FROM ab             , ee.ff AS f,(SELECT a FROM `schema_bb`.`tbl_bb`,(SELECT a FROM ccc AS c, `dddd`));".getBytes(StandardCharsets.UTF_8);//20794
        byte[] src = "select a,b,c,d,e,f,g from h".getBytes(StandardCharsets.UTF_8);//20794
        fc.text = src;
        int count = 0;
        long start = System.currentTimeMillis();
        do {
            fc.lexer();
        } while (count++ < 10_000_000);
        return System.currentTimeMillis() - start;
    }
    public static void main(String[] args) {
        long min = 0;
        FC.collector c = new FC.collector();
        FC fc = new FC();
        fc.c = c;
        for (int i = 0; i < 10; i++) {
            //System.out.print("Loop "+i+" : ");
            long cur = RunBench(fc, c);//无参数优化：7510，加server参数7657，因为是默认就是server jvm，优化流程后6531
            //System.out.println(cur);
            if (cur < min || min == 0) {
                min = cur;
            }
            System.out.println("min time G: " + cur);
        }
    }
}
