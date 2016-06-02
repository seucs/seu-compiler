
/**
 * Created by Jch on 2016/5/12.
 */

public class StackNode {
    //存储一个状态机的开始和结尾
    public Vertex start =null;
    public Vertex end =null;
    public StackNode(Vertex s, Vertex e) {
        start = s;
        end = e;
    }
    public StackNode(){

    }
    public Vertex getStart() {
        return start;
    }
    public void setStart(Vertex start) {
        this.start = start;
    }
    public Vertex getEnd() {
        return end;
    }
    public void setEnd(Vertex end) {
        this.end = end;
    }

}
