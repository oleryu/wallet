package xyz.olery.wallet;

import net.sf.json.JSONObject;

public class MainTest {
    public static void main(String[] args) {
        String msg = "{\"txid\":\"68afe99c9642874ffb67c6c8e05de1876b25fd1a416f2525db549188506edeff\"}";

        JSONObject jsonObject = JSONObject.fromObject(msg);
        System.out.println(jsonObject.getString("txid"));
    }
}
