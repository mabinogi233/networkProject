package main.java.agreement;

import java.io.Serializable;

//应用层消息协议 (POJO)
public class MessageAgreement implements Serializable {
    public MessageAgreement(){}
    public MessageAgreement(int type,int fromID,int lenToID,int[] toID,int datatype,byte[] data){
        this.type=type;
        this.fromID=fromID;
        this.lenToID=lenToID;
        this.toID=toID;
        this.datatype=String.valueOf(datatype);
        this.data=data;
    }
    public MessageAgreement(int type,int fromID,int lenToID,int[] toID,String datatype,byte[] data){
        this.type=type;
        this.fromID=fromID;
        this.lenToID=lenToID;
        this.toID=toID;
        this.datatype=datatype;
        this.data=data;
    }
    //协议类型
    //0 为登录请求      1 为注销请求   2 为注册请求         3,4,5为服务端回复，登录/注销/注册成功or失败
    //10 为发送消息请求 11 为消息回复(客户端收到的消息type)  12为获取在线用户请求  13 为在线用户状态回复
    //100 为异常回复
    private int type;

    //发送请求的ID，若是服务端回复则此为 0
    private int fromID;

    //消息接收方的个数，一对一为1，一对多为具体个数
    private int lenToID;

    //接收方们的ID数组
    private int[] toID;

    //数据类型 0为文本
    //文件类型
    private String datatype;

    //具体的数据
    private byte[] data;

    public byte[] getData() {
        return data;
    }

    public String getDatatype() {
        return datatype;
    }

    public int getFromID() {
        return fromID;
    }

    public int getLenToID() {
        return lenToID;
    }

    public int getType() {
        return type;
    }

    public int[] getToID() {
        return toID;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public void setDatatype(int datatype) {
        this.datatype = String.valueOf(datatype);
    }

    public void setDatatype(String datatype) {
        this.datatype = datatype;
    }

    public void setFromID(int fromID) {
        this.fromID = fromID;
    }

    public void setLenToID(int lenToID) {
        this.lenToID = lenToID;
    }

    public void setToID(int[] toID) {
        this.toID = toID;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDataTypeString(){
        return datatype;
    }

}
