package example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Created by pl on 1/26/17.
 */
public class PlexerSQLTree extends PlexerSQL {
    public static class node{
        public byte[] text;
        public ArrayList<node> children = new ArrayList<>();

        public node() {
            this.text = "undefined".getBytes();
        }
        public node(byte[] text) {
            this.text = text;
        }

        @Override
        public String toString() {
            if(children.size()==0){
                String s = new String(text);
                return s;
            }else{
                String s = children.stream().map(x->x.toString()).collect(Collectors.joining(" "));
                return "(" + s + ")";
            }
        }
    }

    public ArrayList<node> t_stack;

    public PlexerSQLTree() {
        this.t_stack = new ArrayList<>();
        this.t_stack.add(new node());
    }

    @Override
    protected void string_cb(byte[] text, int start, int end){
        node cur = t_stack.get(t_stack.size()-1);
        if(!cur.children.add(new node(Arrays.copyOfRange(text, start, end)))){
            return;
        }
    }

    @Override
    protected void token_cb(byte[] text, int start, int end) {
        node cur = t_stack.get(t_stack.size()-1);
        if(!cur.children.add(new node(Arrays.copyOfRange(text, start, end)))){
            return;
        }
    }

    @Override
    protected void sep_cb() {
        node cur = t_stack.get(t_stack.size()-1);
        if(!cur.children.add(new node(",".getBytes()))){
            return;
        }
    }
    @Override
    protected void bracket_l_cb() {
        t_stack.add(new node());
    }
    @Override
    protected void bracket_r_cb() {
        node p = t_stack.get(t_stack.size()-2);
        node c = t_stack.get(t_stack.size()-1);
        p.children.add(c);
        t_stack.remove(t_stack.size()-1);
    }

    public static void main(String[] argv){
        PlexerSQLTree l = new PlexerSQLTree();
        l.lexer(new iter("select a from(select a,b from c)".getBytes()));
        System.out.println(l.t_stack.get(0).toString());
    }
}
