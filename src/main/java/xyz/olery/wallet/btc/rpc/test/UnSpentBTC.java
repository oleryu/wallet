package xyz.olery.wallet.btc.rpc.test;

public class UnSpentBTC {
    private String txid;
    private long vout;
    private long satoshis;
    private int height;
    private String scriptPubKey;

    public UnSpentBTC(String txid, long vout, long satoshis, int height, String scriptPubKey) {
        this.txid = txid;
        this.vout = vout;
        this.satoshis = satoshis;
        this.height = height;
        this.scriptPubKey = scriptPubKey;
    }

    public String getTxid() {
        return txid;
    }

    public long getVout() {
        return vout;
    }

    public long getSatoshis() {
        return satoshis;
    }

    public int getHeight() {
        return height;
    }

    public String getScriptPubKey() {
        return scriptPubKey;
    }
}
