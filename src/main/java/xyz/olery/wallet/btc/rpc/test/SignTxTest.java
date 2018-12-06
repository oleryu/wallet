package xyz.olery.wallet.btc.rpc.test;

/**
 *
 * 参考：https://blog.csdn.net/wypeng2010/article/details/81357265
 */

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.bitcoinj.core.*;
import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDUtils;
import org.bitcoinj.params.RegTestParams;
import org.bitcoinj.script.Script;
import org.bitcoinj.wallet.DeterministicKeyChain;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bouncycastle.util.encoders.Hex;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bitcoinj.core.UTXO;
import org.json.JSONArray;
import org.json.JSONObject;

public class SignTxTest {




    public static void main(String[] args) throws Exception {
//        NetworkParameters params = MainNetParams.get();
        NetworkParameters params = RegTestParams.get();
        //String seedCode = "tuna biology crawl bone bread chalk light there pattern borrow afraid inherit";
        String seedCode = "faith assist joy kidney climb clerk legend hover before budget consider lobster";

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


        //72 BTC
        //tuna biology crawl bone bread chalk light there pattern borrow afraid inherit
        String from = "mst1TjPdd3ZRe845R8iiBHdps9Kaoi8yz4";

        //datadir=2
        String to = "n1j7r9bTxo9d6iLPYtQSj2KQBm3AxpyCYe";

        String url = "http://192.168.124.8:3001/insight-api/addr/"+from+"/utxo";

        System.out.println(url);
        SignTxTest signDataTest = new SignTxTest();
        //JSONArray 字符串
        String unspents = signDataTest.getResponse(url);

        System.out.println(unspents);

        JSONArray utxoArray = new JSONArray(unspents.trim() );

        List unSpentBTCList = new ArrayList();
        for (int i = 0; i < utxoArray.length(); i++) {
            JSONObject utxoObject = utxoArray.getJSONObject(i);
            int confirmations = utxoObject.getInt("confirmations");
            if(confirmations == 0) {
                continue;
            }


            String address = utxoObject.getString("address");
            String txid = utxoObject.getString("txid");
            long vout = utxoObject.getLong("vout");
            String scriptPubKey = utxoObject.getString("scriptPubKey");
            long amount =  utxoObject.getLong("amount");
            long satoshis = utxoObject.getLong("satoshis");
            int height = utxoObject.getInt("height");




            unSpentBTCList.add(new UnSpentBTC(txid, vout, satoshis, height, scriptPubKey));

        }

        String privateKey = signDataTest.eckey(params,seedCode);

        long value = 200000000l;
        long fee =      1150000l;
        String signDateString = signDataTest.signTxData(params,unSpentBTCList,from,to,privateKey,value,fee);

        System.out.println(signDateString);

        //SendTxTest.sendtx(signDateString);

    }

    public String eckey(NetworkParameters params,String seedCode) throws Exception {

        String passphrase = "12345678";
        String strKeypath = "M/44H/0H/0H/0/0";
        DeterministicKey key = getDeterministicKey(seedCode,passphrase,strKeypath);
        BigInteger privKey = key.getPrivKey();

        ECKey ecKey = ECKey.fromPrivate(privKey);
        String publicKey = ecKey.getPublicKeyAsHex();
//        System.out.println("publicKey: " + publicKey);
        String privateKey = ecKey.getPrivateKeyEncoded(params).toString();
//        System.out.println("privateKey: " + privateKey);
        Address address = ecKey.toAddress(params);
        System.out.println(address.toBase58());
        return  privateKey;
    }


    public static DeterministicKey getDeterministicKey(String seedCode,String passphrase,String strKeypath) throws Exception{
        //String seedCode = "tuna biology crawl bone bread chalk light there pattern borrow afraid inherit";

        // BitcoinJ
        DeterministicSeed seed = new DeterministicSeed(seedCode, null, "", 1409478661L);
        DeterministicKeyChain chain = DeterministicKeyChain.builder().seed(seed).build();
        //"M/44H/0H/0H/0/0"
        //"M/44H/60H/0H/0/0"
        List<ChildNumber> keyPath = HDUtils.parsePath(strKeypath);

        DeterministicKey key = chain.getKeyByPath(keyPath, true);

        return key;
    }

    public String getResponse(String url) throws Exception {

        HttpClient httpclient = new DefaultHttpClient();


        HttpGet httpget = new HttpGet(new URI(url));

        HttpResponse response = httpclient.execute(httpget);

        StringBuffer sb = new StringBuffer();
        sb.append(response.getStatusLine()).append("\n\n");

        InputStream is = response.getEntity().getContent();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line = null;

        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        String result = sb.toString();

        //
        int iStartWith = result.indexOf("[");
        result = "[" + result.substring(iStartWith+1);
        int iEndWith = result.lastIndexOf("]");
        result = result.substring(0,iEndWith+1);

        return result;

    }

    public String signTxData(
            NetworkParameters params,
            List<UnSpentBTC> unSpentBTCList,
            String from,
            String to,
            String privateKey,
            long value, long fee) throws Exception {

        Transaction transaction = new Transaction(params);
        DumpedPrivateKey dumpedPrivateKey = DumpedPrivateKey.fromBase58(params, privateKey);

        ECKey ecKey = dumpedPrivateKey.getKey();

        long totalMoney = 0;
        List<UTXO> utxos = new ArrayList<>();
        //遍历未花费列表，组装合适的item
        for (UnSpentBTC us : unSpentBTCList) {
            if (totalMoney >= (value + fee))
                break;
            UTXO utxo = new UTXO(Sha256Hash.wrap(us.getTxid()), us.getVout(), Coin.valueOf(us.getSatoshis()),
                    us.getHeight(), false, new Script(Hex.decode(us.getScriptPubKey())));
            utxos.add(utxo);
            totalMoney += us.getSatoshis();
        }

        transaction.addOutput(Coin.valueOf(value), Address.fromBase58(params, to));
        // transaction.

        //消费列表总金额 - 已经转账的金额 - 手续费 就等于需要返回给自己的金额了
        long balance = totalMoney - value - fee;
        //输出-转给自己
        if (balance > 0) {
            transaction.addOutput(Coin.valueOf(balance), Address.fromBase58(params, from));
        }
        //输入未消费列表项
        for (UTXO utxo : utxos) {


            TransactionOutPoint outPoint = new TransactionOutPoint(params, utxo.getIndex(), utxo.getHash());
            transaction.addSignedInput(outPoint, utxo.getScript(), ecKey, Transaction.SigHash.ALL, true);
        }

        return Hex.toHexString(transaction.bitcoinSerialize());
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
