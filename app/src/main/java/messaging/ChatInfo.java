package messaging;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by ChrisBehan on 5/12/2017.
 */

public class ChatInfo {
    private String chatID;
    private String myID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private String theirID;

    public ChatInfo(){

    }

    public ChatInfo(String chatID, String myID, String theirID) {
        this.chatID = chatID;
        this.myID = myID;
        this.theirID = theirID;
    }

    public String getChatID() {
        return chatID;
    }

    public String getMyID() {
        return myID;
    }

    public String getTheirID() {
        return theirID;
    }

    public void setChatID(String chatID) {
        this.chatID = chatID;
    }

    public void setMyID(String myID) {
        this.myID = myID;
    }

    public void setTheirID(String theirID) {
        this.theirID = theirID;
    }

    public void setProperName(String key){
        setChatID(key);
        String myName;
        String theirName;
        int middle = key.indexOf('_');
        String s1 = key.substring(0,middle);
        String s2 = key.substring(middle+1, key.length());
        if(s1.equalsIgnoreCase(myID)){
            setMyID(s1);
            setTheirID(s2);

        }else{
            setMyID(s2);
            setTheirID(s1);
        }

        //  return "";
    }
}
