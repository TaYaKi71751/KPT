package demo;

import java.util.HashMap;

import org.jsoup.select.Elements;

class Info extends Object {
    HashMap<String,String> track;
    String senderName, recieverName, dlvrDate, hClass, trackNo;
    Boolean dlvrd;
}

public class Testt extends Info {

    public Testt(String trackNo) throws Throwable {
        Elements elements = new Test(trackNo).getElements();
        byte length;
        String[] infoArr;
        String tagName, info;
        info = new String(elements.first().toString());
        tagName = null;
        info = info.replace("<br><", "<");
        while (true) {
            try {
                info = info.replaceAll((tagName = info.substring((info).indexOf("<"), info.indexOf(">") + 1)),
                        (tagName).contains("<br>") ? ";" : "");
            } catch (Exception e) {
                info = info.trim();
                break;
            }
        }
        infoArr = info.split("\n");
        this.dlvrd = infoArr.length % 2 == 0;//배달완료 = length == 6 , 미완료시  trim()에서 length == 5
        this.senderName = (senderName = infoArr[1].trim()).contains("*") ? senderName : null;
        this.dlvrDate = this.dlvrd ? ((dlvrDate = infoArr[3].trim()).contains(";") ? dlvrDate.split(";")[dlvrDate.split(";").length-1] : null): null;
        this.recieverName = (recieverName = infoArr[2].trim()).contains(";")?recieverName.replaceAll(";",""):null;
        this.hClass = ((length = (byte) (hClass = infoArr[4].trim()).length()) >> 1 != length) ? hClass : null;
        this.trackNo = trackNo;
        
        info = elements.last().toString();
        while (true) {
            try {
                info = info.replace((tagName = info.substring((info).indexOf("<"), info.indexOf(">") + 1)),tagName.equals("</tr>")?"/tr/":"");
            } catch (Exception e) {
                info = info.trim();
                break;
            }
        }
        infoArr = info.split("/tr/");
        for(int i = 0; i < infoArr.length;infoArr[i] = infoArr[i++].trim().replaceAll("&nbsp;", ""));
        this.track = new HashMap<String,String>();
        track.put("first", infoArr[0]);
        track.put("last", infoArr[infoArr.length-1]);
        info = null;
        return;
    }

    public Object rtnObject() {
        return this;
    }
}
