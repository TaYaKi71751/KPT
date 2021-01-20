package demo;

import java.io.IOException;
import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

class Test {
    protected Elements elements;

    protected Test(String trackNo) throws IOException {
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

    public Elements getElements() {
        return this.elements.clone();
    }
}
