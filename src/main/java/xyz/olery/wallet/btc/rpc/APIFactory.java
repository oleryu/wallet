package xyz.olery.wallet.btc.rpc;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.params.MainNetParams;
import org.bouncycastle.util.encoders.Hex;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class APIFactory {
    static NetworkParameters NET_PARAMS = MainNetParams.get();

    private static HashMap<String,UTXO> utxos = null;

    public static void main(String[] args) {
//        String[] xpubs = new String[]{"aaa","bbb"};
//        String str = StringUtils.join(xpubs, "|");
//        System.out.println(str);

        utxos = new HashMap<String, UTXO>();

        new APIFactory().getUnspentOutputs();
    }
    public synchronized JSONObject getUnspentOutputs() {
//        String _url = "https://api.samouraiwallet.com/v2/";

        String _url = "https://blockchain.info/";
        JSONObject jsonObject  = null;
        String[] xpubs = new String[]{"39eV1GyTsfbdMc2JBseiATZ6S9eKToRADD"};
        try {
            String response = null;
            //statusFromBroadcast
             /*
            StringBuilder args = new StringBuilder();
            args.append("active=");
            args.append(StringUtils.join(xpubs, URLEncoder.encode("|", "UTF-8")));
            System.out.println("APIFactory UTXO args:" + args.toString());
            response = postURL(_url + "unspent?", args.toString());
            System.out.println("APIFactory UTXO:" + response);
            */

                HashMap<String,String> args = new HashMap<String,String>();
                args.put("active", StringUtils.join(xpubs, "|"));
                response = tor_postURL(_url + "unspent", args);
                System.out.println(response);
            parseUnspentOutputs(response);
        } catch(Exception e) {
            jsonObject = null;
            e.printStackTrace();
        }
        return jsonObject;
    }
    public String tor_postURL(String URL, HashMap<String,String> args) throws Exception {

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

    private synchronized boolean parseUnspentOutputs(String unspents)   {
        if(unspents != null)    {
            try {
                JSONObject jsonObj = new JSONObject(unspents);
                if(jsonObj == null || !jsonObj.has("unspent_outputs"))    {
                    return false;
                }
                JSONArray utxoArray = jsonObj.getJSONArray("unspent_outputs");
                if(utxoArray == null || utxoArray.length() == 0) {
                    return false;
                }
                for (int i = 0; i < utxoArray.length(); i++) {
                    JSONObject outDict = utxoArray.getJSONObject(i);
//                    byte[] hashBytes = Hex.decode((String)outDict.get("tx_hash"));
                    byte[] hashBytes = Hex.decode((String)outDict.get("tx_hash_big_endian"));
                    Sha256Hash txHash = Sha256Hash.wrap(hashBytes);
                    int txOutputN = ((Number)outDict.get("tx_output_n")).intValue();
                    BigInteger value = BigInteger.valueOf(((Number)outDict.get("value")).longValue());
                    String script = (String)outDict.get("script");
                    byte[] scriptBytes = Hex.decode(script);
                    int confirmations = ((Number)outDict.get("confirmations")).intValue();
                    try {
                        String address = null;
//                        if(Bech32Util.getInstance().isBech32Script(script))    {
//                            address = Bech32Util.getInstance().getAddressFromScript(script);
//                        } else    {
//                            address = new Script(scriptBytes).getToAddress(SamouraiWallet.getInstance().getCurrentNetworkParams()).toString();
//                        }
                        if(outDict.has("xpub"))    {
                            JSONObject xpubObj = (JSONObject)outDict.get("xpub");
                            String path = (String)xpubObj.get("path");
                            String m = (String)xpubObj.get("m");
//                            unspentPaths.put(address, path);
//                            if(m.equals(BIP49Util.getInstance(context).getWallet().getAccount(0).xpubstr()))    {
//                                unspentBIP49.put(address, 0);   // assume account 0
//                            } else if(m.equals(BIP84Util.getInstance(context).getWallet().getAccount(0).xpubstr()))    {
//                                unspentBIP84.put(address, 0);   // assume account 0
//                            } else    {
//                                unspentAccounts.put(address, AddressFactory.getInstance(context).xpub2account().get(m));
//                            }
                        } else if(outDict.has("pubkey"))    {
                            System.out.println("pubkey");
//                            int idx = BIP47Meta.getInstance().getIdx4AddrLookup().get(outDict.getString("pubkey"));
//                            BIP47Meta.getInstance().getIdx4AddrLookup().put(address, idx);
//                            String pcode = BIP47Meta.getInstance().getPCode4AddrLookup().get(outDict.getString("pubkey"));
//                            BIP47Meta.getInstance().getPCode4AddrLookup().put(address, pcode);
                        } else    {
                            System.out.println("no pubx");;
                        }
//                        // Construct the output
                        MyTransactionOutPoint outPoint = new MyTransactionOutPoint(NET_PARAMS,txHash, txOutputN, value, scriptBytes, address);
                        outPoint.setConfirmations(confirmations);
//
                        if(utxos.containsKey(script))    {
                            utxos.get(script).getOutpoints().add(outPoint);
                        }else    {
                            UTXO utxo = new UTXO();
                            utxo.getOutpoints().add(outPoint);
                            utxos.put(script, utxo);
                        }
//                        if(!BlockedUTXO.getInstance().contains(txHash.toString(), txOutputN))    {
//                            if(Bech32Util.getInstance().isBech32Script(script))    {
//                                UTXOFactory.getInstance().addP2WPKH(script, utxos.get(script));
//                            }
//                            else if(Address.fromBase58(SamouraiWallet.getInstance().getCurrentNetworkParams(), address).isP2SHAddress())    {
//                                UTXOFactory.getInstance().addP2SH_P2WPKH(script, utxos.get(script));
//                            }
//                            else    {
//                                UTXOFactory.getInstance().addP2PKH(script, utxos.get(script));
//                            }
//                        }
                    }
                    catch(Exception e) {
                        ;
                    }
                }
//                try {
//                    PayloadUtil.getInstance(context).serializeUTXO(jsonObj);
//                }catch(Exception e) {
////                    ;
//                }
                return true;
            }catch(Exception e) {
                ;
            }
        }
        return false;

    }



}


