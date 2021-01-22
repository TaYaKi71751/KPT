package demo;

import java.util.ArrayList;
import java.util.HashMap;

class Info extends Object {
    ArrayList<HashMap<String, String>> trackList;
    String senderName, recieverName, recieveDate, sendDate, hClass, trackNo;
    Boolean dlvrd;

    Object rtnObject() {
        return this;
    }
}
