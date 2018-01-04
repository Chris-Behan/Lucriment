package Messaging;

/**
 * Created by ChrisBehan on 5/17/2017.
 */

public class Chat {
    public String senderName;
    public String receiverName;
    public String senderId;
    public String receiverId;
    public String text;
    public long timestamp;

    public Chat() {}

    public Chat(String sender, String receiver, String senderUid, String receiverUid, String message, long timestamp) {
        this.senderName = sender;
        this.receiverName = receiver;
        this.senderId = senderUid;
        this.receiverId = receiverUid;
        this.text = message;
        this.timestamp = timestamp;
    }
}
