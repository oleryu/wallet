package xyz.olery.wallet.btc.rpc;

import org.json.JSONObject;
import xyz.olery.wallet.util.CharSequenceX;

public class JSONRPC_TEST {
    public static void main(String[] args) {
        String user = "admin2";
        CharSequenceX password = new CharSequenceX("123");
        String node = "192.168.124.2";
        int port = 19011;
        JSONRPC jsonrpc = new JSONRPC(user, password, node, port);
        JSONObject jsonobj = jsonrpc.getSth();
        System.out.println(jsonobj);
    }
}
