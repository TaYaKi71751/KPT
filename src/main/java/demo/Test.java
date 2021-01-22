package demo;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

class KoreaPost {
    /**
     * <tbody> -> Information
     */
    private Object info;

    /**
     * 
     * @param trackNo
     * @throws Throwable
     */
    public KoreaPost(String trackNo) throws Throwable {
        this.info = trackNo.matches(".*[A-Z]+.*") ? new EMS(trackNo) {
            {
                parseInfo();
            }
        } : new Domestic(trackNo) {
            {
                parseInfo();
            }
        }.rtnObject();
    }

    /**
     * 
     * @return
     */
    public Object getInfo() {
        return this.info;
    }

    class EMS extends Info {
        protected Elements elements;

        /**
         * 
         * @param POST_CODE
         * @throws IOException
         */
        public EMS(String POST_CODE) throws IOException {
            Elements elements = Jsoup.connect(
                    "https://trace.epost.go.kr/ipl.tts.tt.epost.web.OrderEpostEventMgmt.retrieveEmsTraceEngC.do")
                    .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:5.0) Gecko/20100101 Firefox/5.0")
                    .data(new HashMap<String, String>() {
                        {
                            put("POST_CODE", POST_CODE);
                            put("JspURI", "/xtts/tt/epost/ems/EmsSearchResultEng.jsp");
                            put("target_command", "kpl.tts.tt.epost.cmd.RetrieveEmsTraceEngCmd");
                        }
                    }).maxBodySize(0).post().getElementsByTag("tbody");
            elements.trimToSize();
            this.elements = elements;
        }

        /***
         * dd-MMM-YYYY to yyyy-MM-dd
         * 
         * @throws ParseException
         */
        public void parseInfo() throws ParseException {
            int length;
            Element info;

            /**
             * TrackInfo 0->Date 1->Status 2->Post office/Airport 3->Details
             */
            info = this.elements.last();
            this.trackList = new ArrayList<HashMap<String, String>>();
            for (int i = 0; i < info.childrenSize(); i++) {
                String text;
                HashMap<String, String> trackMap = new HashMap<>();
                trackMap.put("Postal", (text = info.child(i).child(2).text().trim()).length() > 0 ? text : null);
                trackMap.put("Date",
                        (text = info.child(i).child(0).text().trim()).length() > 0 ? new SimpleDateFormat("yyyy-MM-dd")
                                .format((new SimpleDateFormat("dd-MMM-yyyy")).parse(text.split(" ")[1])) : null);
                trackMap.put("Time", text.length() > 0 ? text.split(" ")[0] : null);
                trackMap.put("Status", (text = info.child(i).child(1).text().trim()).length() > 0 ? text : null);
                trackMap.put("Details", (text = info.child(i).child(3).text().trim()).length() > 0 ? text : null);
                trackList.add(trackMap);
            }

            return;
        }
    }

    class Domestic extends Info {
        protected Elements elements;

        /**
         * 
         * @throws Throwable
         */
        public void parseInfo() throws Throwable {
            int length;
            Element info;
            length = (info = this.elements.first().child(0)).childrenSize();

            this.dlvrd = info.children().last().text().contains("배달");
            this.senderName = (senderName = info.child(1).toString().trim()).contains("*")
                    ? (senderName = senderName.substring(senderName.indexOf("<td>") + 4, senderName.indexOf("<br>")))
                            .length() > 0 ? senderName : null
                    : null;
            this.sendDate = (sendDate = info.child(1).toString().trim()).contains("*")
                    ? (sendDate = sendDate.substring(sendDate.indexOf("<br>") + 4, sendDate.lastIndexOf("<")))
                            .length() > 0 ? sendDate : null
                    : null;
            this.recieverName = (recieverName = info.child(2).toString().trim()).contains("*")
                    ? (recieverName = recieverName.substring(recieverName.indexOf("<td>") + 4,
                            recieverName.indexOf("<br>"))).length() > 0 ? senderName : null
                    : null;
            this.recieveDate = this.dlvrd
                    ? (recieveDate = (recieveDate = (recieveDate = info.child(3 - length % 2).toString())
                            .replace(recieveDate.substring(0, recieveDate.indexOf("<br>") + 4), "")).replace(
                                    recieveDate.substring(recieveDate.indexOf("<"), recieveDate.indexOf(">") + 1), ""))
                                            .contains(".") ? recieveDate : null
                    : null;
            this.hClass = (hClass = info.child(4).text().trim()).length() > 0 ? hClass : null;

            length = (info = this.elements.last()).childrenSize();
            this.trackList = new ArrayList<HashMap<String, String>>();

            for (int i = 0; i < length; i++) {
                String text;
                HashMap<String, String> trackMap = new HashMap<>();
                trackMap.put("Postal", (text = info.child(i).child(2).text().trim()).length() > 0 ? text : null);
                trackMap.put("Date", (text = info.child(i).child(0).text().trim()).length() > 0 ? text : null);
                trackMap.put("Time", (text = info.child(i).child(1).text().trim()).length() > 0 ? text : null);
                trackMap.put("Status", (text = info.child(i).child(3).text().trim()).length() > 0 ? text : null);
                trackList.add(trackMap);
            }
            return;
        }

        /**
         * 
         * @param trackNo
         * @throws IOException
         */
        public Domestic(String trackNo) throws IOException {
            Elements elements = Jsoup.connect("https://service.epost.go.kr/trace.RetrieveDomRigiTraceList.comm")
                    .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:5.0) Gecko/20100101 Firefox/5.0")
                    .data(new HashMap<String, String>() {
                        {
                            put("sid1", trackNo);
                            put("displayHeader", "");
                        }
                    }).maxBodySize(0).post().getElementsByTag("tbody");
            elements.trimToSize();
            this.elements = elements;
        }

    }
}

class Test {
    /**
     * 
     * @param args
     * @throws Throwable
     */
    public static void main(String[] args) throws Throwable {
        Object a = new KoreaPost("EM123456789KR");
        Object aa= new KoreaPost("1234567890123");
    }
}
