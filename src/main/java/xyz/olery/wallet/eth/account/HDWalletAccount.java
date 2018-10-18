package xyz.olery.wallet.eth.account;

import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDUtils;
import org.bitcoinj.wallet.DeterministicKeyChain;
import org.bitcoinj.wallet.DeterministicSeed;
import org.web3j.crypto.Credentials;

import java.math.BigInteger;
import java.util.List;

/**
 *  oleryu 2018/10/17
 */
public class HDWalletAccount {

    private String seedCode;
    /* M/44H/60H/0H/0/0 */
    private String keypath;
    private String passphrase;


    private Credentials credentials;
    public Credentials getCredentials() {
        return credentials;
    }

    public Credentials loadCredentials(String seedCode,String passphrase,String strKeypath) throws Exception {

        DeterministicKey key = getDeterministicKey();

        BigInteger privKey = key.getPrivKey();

        // Web3j
        Credentials credentials = Credentials.create(privKey.toString(16));

        String address = credentials.getAddress();
        String privateKey = privKey.toString(16);

        return credentials;

    }


    public HDWalletAccount(String seedCode, String keypath, String passphrase) {
        this.seedCode = seedCode;
        this.keypath = keypath;
        this.passphrase = passphrase;
        try {
            DeterministicKey key = getDeterministicKey();
            BigInteger privKey = key.getPrivKey();

            // Web3j
            this.credentials = Credentials.create(privKey.toString(16));
            String address = credentials.getAddress();
            String privateKey = privKey.toString(16);
        } catch (Exception e) {}

    }


    public  BigInteger getPrivateKey()  {
        BigInteger privateKey = credentials.getEcKeyPair().getPrivateKey();

        return privateKey;
    }

    public  String ethAddress() throws Exception {
        return credentials.getAddress();

    }

    public  DeterministicKey getDeterministicKey() throws Exception{
        //String seedCode = "tuna biology crawl bone bread chalk light there pattern borrow afraid inherit";

        // BitcoinJ
        DeterministicSeed seed = new DeterministicSeed(seedCode, null, "", 1409478661L);
        DeterministicKeyChain chain = DeterministicKeyChain.builder().seed(seed).build();
        //"M/44H/0H/0H/0/0"
        //"M/44H/60H/0H/0/0"
        List<ChildNumber> keyPath = HDUtils.parsePath(keypath);

        DeterministicKey key = chain.getKeyByPath(keyPath, true);

        com.google.protobuf.ByteString bytes;

        return key;
    }
}
