package xyz.olery.wallet.eth.util;

import net.sf.json.JSONObject;
import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDUtils;
import org.bitcoinj.wallet.DeterministicKeyChain;
import org.bitcoinj.wallet.DeterministicSeed;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.crypto.WalletFile;

import java.math.BigInteger;
import java.util.List;

public class EthKeystore {

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

    public static String ethKeystore(String seedCode,String passphrase,String strKeypath) throws Exception {

        //参见 https://lhalcyon.com/blockchain-eth-wallet-android/
        DeterministicKey child = getDeterministicKey(seedCode,passphrase,strKeypath);

        //私钥&公钥
        String childPrivateKey = child.getPrivateKeyAsHex();
        String childPublicKey = child.getPublicKeyAsHex();

        ECKeyPair childEcKeyPair = ECKeyPair.create(child.getPrivKeyBytes());

        //钱包地址
        String childAddress = Keys.getAddress(childEcKeyPair);
        //String fullAddress = Constant.PREFIX_16 + childAddress;
        //Logger.w("child privateKey:" + childPrivateKey + "\n" + "child publicKey:" + childPublicKey + "\n" + "address:" + fullAddress);

        //
        WalletFile walletFile = org.web3j.crypto.Wallet.createStandard(passphrase, childEcKeyPair);

        //String keystore = Singleton.get().gson.toJson(walletFile);
        JSONObject jsonObject = JSONObject.fromObject(walletFile);

        return jsonObject.toString();

    }

    public void exptBySeed() throws Exception {
        String strKeypath = "M/44H/60H/0H/0/0";
        String seedCode = "beach off supreme nut route glide busy beef grass solve crater dry";
        String passphrase = "";
        ethKeystore(seedCode,passphrase,strKeypath);
    }


    public static String ethKeystoreByKey(String key,String passphrase) throws Exception {

        BigInteger privKey = new BigInteger(key,16);

        System.out.println(privKey);

        BigInteger privKey1 = new BigInteger("77943784685715847311651170011835116147109339488172869420205546856112813020228");
        // Web3j
        Credentials credentials = Credentials.create(privKey1.toString(16));
        ECKeyPair childEcKeyPair = credentials.getEcKeyPair();


        //钱包地址
        String childAddress = Keys.getAddress(childEcKeyPair);
        //String fullAddress = Constant.PREFIX_16 + childAddress;
        //Logger.w("child privateKey:" + childPrivateKey + "\n" + "child publicKey:" + childPublicKey + "\n" + "address:" + fullAddress);

        //
        WalletFile walletFile = org.web3j.crypto.Wallet.createStandard(passphrase, childEcKeyPair);

        //String keystore = Singleton.get().gson.toJson(walletFile);
        JSONObject jsonObject = JSONObject.fromObject(walletFile);

        return jsonObject.toString();

    }

    public static void main(String[] args) throws Exception{
        String key = "ac529e67a4214487db814ab077921e3bcfc07c82bce9baf89118aee26a0c9444";
        String passphrase = "12345678";
        String keystore = ethKeystoreByKey(key,passphrase);
        System.out.println(keystore);
    }
}
