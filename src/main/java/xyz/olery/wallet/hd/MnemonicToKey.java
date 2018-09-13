package xyz.olery.wallet.hd;

import net.sf.json.JSONObject;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Utils;
import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDUtils;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.script.Script;
import org.bitcoinj.wallet.DeterministicKeyChain;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.Wallet;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.crypto.WalletFile;
//import org.web3j.crypto.Credentials;

import java.math.BigInteger;
import java.util.List;

public class MnemonicToKey {

    public static Script segWitRedeemScript(ECKey ecKey)    {
        //
        // The P2SH segwit redeemScript is always 22 bytes. It starts with a OP_0, followed by a canonical push of the keyhash (i.e. 0x0014{20-byte keyhash})
        //
        byte[] hash = Utils.sha256hash160(ecKey.getPubKey());
        byte[] buf = new byte[2 + hash.length];
        buf[0] = (byte)0x00;  // OP_0
        buf[1] = (byte)0x14;  // push 20 bytes
        System.arraycopy(hash, 0, buf, 2, hash.length); // keyhash

        return new Script(buf);
    }

    public static Script segWitOutputScript(ECKey ecKey)    {
        //
        // OP_HASH160 hash160(redeemScript) OP_EQUAL
        //
        byte[] hash = Utils.sha256hash160(segWitRedeemScript(ecKey).getProgram());
        byte[] buf = new byte[3 + hash.length];
        buf[0] = (byte)0xa9;    // HASH160
        buf[1] = (byte)0x14;    // push 20 bytes
        System.arraycopy(hash, 0, buf, 2, hash.length); // keyhash
        buf[22] = (byte)0x87;   // OP_EQUAL

        return new Script(buf);
    }

    public static String getAddressAsString(ECKey ecKey,NetworkParameters params)    {

        return Address.fromP2SHScript(params, segWitOutputScript(ecKey)).toString();

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

    public static String btc49Address(String seedCode,String passphrase,NetworkParameters params,String strKeypath) throws Exception {
        DeterministicKey key = getDeterministicKey(seedCode,passphrase,strKeypath);
        BigInteger privKey = key.getPrivKey();

        ECKey ecKey = ECKey.fromPrivate(privKey);

        return getAddressAsString(ecKey,params);
    }

    public static String btc44Address(String seedCode,String passphrase,NetworkParameters params,String strKeypath) throws Exception {
        DeterministicKey key = getDeterministicKey(seedCode,passphrase,strKeypath);
        BigInteger privKey = key.getPrivKey();

        ECKey ecKey = ECKey.fromPrivate(privKey);
        Address address = ecKey.toAddress(params);

        return address.toBase58();
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

    /**
     *  导出私钥
     * @param seedCode    助记词
     * @param passphrase  密码
     * @param strKeypath  KEYPATH
     * @return
     * @throws Exception
     */
    public static String ethPrivateKey(String seedCode,String passphrase,String strKeypath) throws Exception {

        //参见 ：http://nmgfrank.com/2016/03/%E4%BD%BF%E7%94%A8%E5%BC%80%E6%BA%90%E5%BA%93bitcoinj%E5%AE%9E%E7%8E%B0%E7%AE%80%E6%98%93%E5%8C%BA%E5%9D%97%E9%93%BE%E9%92%B1%E5%8C%85/
        //参见 https://lhalcyon.com/blockchain-eth-wallet-android/
        DeterministicKey key = getDeterministicKey(seedCode,passphrase,strKeypath);

        //BigInteger privKey = key.getPrivKey();
        System.out.println(key.getPrivateKeyAsHex());

//

        return "";

    }

    /**
     *
     * @param seedCode    助记词
     * @param passphrase  密码
     * @param strKeypath  BIP32 Derivation Path
     * @return
     * @throws Exception
     */
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

    public static void main(String[] args) throws Exception {
        String seedCode = "tuna biology crawl bone bread chalk light there pattern borrow afraid inherit";
        String btcKeyath = "M/44H/0H/0H/0/0";
        String ethKeyath = "M/44H/60H/0H/0/0";
        //TestNet3Params
        //MainNetParams
        NetworkParameters params = MainNetParams.get();
        String bip44Address = MnemonicToKey.btc44Address(seedCode,"",params,btcKeyath);
        String bip49Address = MnemonicToKey.btc49Address(seedCode,"",params,btcKeyath);

        System.out.println("BIP44 Address：" +bip44Address);
        System.out.println("BIP49 Address：" +bip49Address);

//mgcqjgfYxh4cVEzMqwDiUr8XRx1t8DhrGJ

//        String ethAddrss = MnemonicToKey.ethAddress(seedCode,"",ethKeyath);
        String ethAddrss = MnemonicToKey.ethPrivateKey(seedCode,"",ethKeyath);

        MnemonicToKey.ethKeystore(seedCode,"hongliang",ethKeyath);

    }



//    public String getBech32AsString(ECKey ecKey)    {
//
//        String address = null;
//
//        try {
//            address = Bech32Segwit.encode(NetworkParameters.prodNet()
//                    instanceof TestNet3Params ? "tb" : "bc", (byte)0x00, Utils.sha256hash160(ecKey.getPubKey()));
//        }
//        catch(Exception e) {
//            ;
//        }
//
//        return address;
//    }


}
