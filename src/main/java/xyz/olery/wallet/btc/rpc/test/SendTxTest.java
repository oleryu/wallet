package xyz.olery.wallet.btc.rpc.test;

/**
 *
 * 参考：https://blog.csdn.net/wypeng2010/article/details/81357265
 */

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.bitcoinj.core.*;
import org.bitcoinj.params.RegTestParams;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SendTxTest {




    public static void main(String[] args) throws Exception {
        String txSignedTxt = "010000000199954dbd04b0154d633e760109be9fec24d3965b019bfe4d33b53a348f2a9849010000006b483045022100a7b127481bcd066f891e25a8aea05a1caa3735e4987c2310dc181dc3f3cf80e202204089dea2a8253fc21f5bbb387046a57d17b8da06b69b33d7f45ccead6440bbf5812102d5e73c15e1cc8fad0703a150cb88863cfb7380093d0000eeeb7c5503f5c4756bffffffff0200e1f505000000001976a914b81a765c955b7085f7a6ff4fa9d1829ffcb98bb688ac30dde7a0000000001976a91449455df697aca0942bd7a32fd6547028c8b53a8a88ac00000000";

        sendtx(txSignedTxt);
    }

    public static void sendtx(String txSignedTxt) throws Exception {

//        NetworkParameters params = MainNetParams.get();
        NetworkParameters params = RegTestParams.get();
//        String seedCode = "tuna biology crawl bone bread chalk light there pattern borrow afraid inherit";

        /**
         [{
         "address": "mgcqjgfYxh4cVEzMqwDiUr8XRx1t8DhrGJ",
         "txid": "6ef4eadcff7f8fb628f4e9952e928b1954de31050a7163fc17bbcbee9bb56436",
         "vout": 0,
         "scriptPubKey": "76a9140c1525aaa4fb519301e7a477b64a932b20f7ff2f88ac",
         "amount": 31,
         "satoshis": 3100000000,
         "height": 552,
         "confirmations": 20
         }, {
         "address": "mgcqjgfYxh4cVEzMqwDiUr8XRx1t8DhrGJ",
         "txid": "b49d9d8a787670f589f5062313f2248cc4d28d2180dacdc3bd3579af3de6dbf9",
         "vout": 1,
         "scriptPubKey": "76a9140c1525aaa4fb519301e7a477b64a932b20f7ff2f88ac",
         "amount": 31,
         "satoshis": 3100000000,
         "height": 532,
         "confirmations": 40
         }, {
         "address": "mgcqjgfYxh4cVEzMqwDiUr8XRx1t8DhrGJ",
         "txid": "c41f4f8c7eaefcdde573d56a8ffbc888432bc770d97f0bab7b1bc8e311064c51",
         "vout": 0,
         "scriptPubKey": "76a9140c1525aaa4fb519301e7a477b64a932b20f7ff2f88ac",
         "amount": 10,
         "satoshis": 1000000000,
         "height": 502,
         "confirmations": 70
         }]
         */
        //String url = "http://192.168.10.104:3001/insight-api/tx/send";

        String url = "http://192.168.124.8:3001/insight-api/tx/send";
        SendTxTest sendTxTest = new SendTxTest();
        //String txSignedTxt = "0100000001d446c4f95212cdd130a73b96c2dde57b7c63214ab042fe79d6bfce3509ac13df000000006b483045022100f475baa84fddc1e98d376bd2acefec10d45d655b51ab0d0efdeac043a0c82f20022060fa7bcbd3a2e15c396f3b73b33e61085f3ed131a0015a31429c80a0d193909881210293042aa716ed785515e7cea87747e1b84f0428417ef4625995d5782458b43d60ffffffff0200c2eb0b000000001976a9147d7c05e76611b161509af321909fde1a2c9206c188ac10beac2f000000001976a9145f24b5e1626381ddcb8511fdd02ebd8f49a42dce88ac00000000";
        HashMap<String,String> inputArgs = new HashMap<String,String>();
        inputArgs.put("rawtx", txSignedTxt);
        String result = sendTxTest.txSend(url,inputArgs);

        System.out.println(result);
    }


    public String txSend(String URL, HashMap<String,String> args) throws Exception {

        HttpClient httpclient = new DefaultHttpClient();


        HttpPost httppost = new HttpPost(new URI(URL));
        httppost.setHeader("Content-Type", "application/x-www-form-urlencoded");
        httppost.setHeader("charset", "utf-8");
        httppost.setHeader("Accept", "application/json");
        httppost.setHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.57 Safari/537.36");

        if(args != null)    {
            List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
            for(String key : args.keySet())   {
                urlParameters.add(new BasicNameValuePair(key, args.get(key)));
            }
            httppost.setEntity(new UrlEncodedFormEntity(urlParameters));
        }

        HttpResponse response = httpclient.execute(httppost);

        StringBuffer sb = new StringBuffer();
        sb.append(response.getStatusLine()).append("\n\n");

        InputStream is = response.getEntity().getContent();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line = null;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }

        String result = sb.toString();
//        Log.d("WebUtil", "POST result via Tor:" + result);
        int idx = result.indexOf("{");
        if(idx != -1)    {
            return result.substring(idx);
        }
        else    {
            return result;
        }

    }
}
