package xyz.olery.wallet.eth.erc20;

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
public class WalletAccount {

    private String seedCode;
    /* M/44H/60H/0H/0/0 */
    private String keypath;
    private String passphrase;

    public WalletAccount(String seedCode,String keypath,String passphrase) {
        this.seedCode = seedCode;
        this.keypath = keypath;
        this.passphrase = passphrase;
    }

    public static String getPrivateKey(String seedCode,String ethKeyath) throws Exception {
        DeterministicSeed seed = new DeterministicSeed(seedCode, null, "", 1409478661L);
        DeterministicKeyChain chain = DeterministicKeyChain.builder().seed(seed).build();
        List<ChildNumber> keyPath = HDUtils.parsePath(ethKeyath);
        DeterministicKey key = chain.getKeyByPath(keyPath, true);
        BigInteger privKey = key.getPrivKey();

        // Web3j
        Credentials credentials = Credentials.create(privKey.toString(16));
        String address = credentials.getAddress();
        String privateKey = privKey.toString(16);

        return privateKey;
    }

    public static String ethAddress(String seedCode,String passphrase,String strKeypath) throws Exception {

        DeterministicKey key = getDeterministicKey(seedCode,passphrase,strKeypath);

        BigInteger privKey = key.getPrivKey();

        // Web3j
        Credentials credentials = Credentials.create(privKey.toString(16));
        String address = credentials.getAddress();
        String privateKey = privKey.toString(16);
        return credentials.getAddress();

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

        com.google.protobuf.ByteString bytes;

        return key;
    }
}
