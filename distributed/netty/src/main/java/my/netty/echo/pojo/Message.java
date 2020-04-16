package my.netty.echo.pojo;


public class Message {

    //内容长度
    private int length;
    //内容
    private String content;

    public Message(int length, String content) {
        this.length = length;
        this.content = content;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
