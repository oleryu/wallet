package xyz.olery.wallet.hd;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Utils;
import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDUtils;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.script.Script;
import org.bitcoinj.wallet.DeterministicKeyChain;
import org.bitcoinj.wallet.DeterministicSeed;
import org.web3j.crypto.Credentials;

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
        Address address = ecKey.toAddress(NetworkParameters.prodNet());

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


    public static void main(String[] args) throws Exception {
        String seedCode = "tuna biology crawl bone bread chalk light there pattern borrow afraid inherit";
        String btcKeyath = "M/44H/0H/0H/0/0";
        String ethKeyath = "M/44H/60H/0H/0/0";
        //TestNet3Params
        //MainNetParams
        NetworkParameters params = MainNetParams.get();
        String bip44Address = MnemonicToKey.btc44Address(seedCode,"",params,btcKeyath);
        String bip49Address = MnemonicToKey.btc44Address(seedCode,"",params,btcKeyath);

        System.out.println("BIP44 Address" +bip44Address);
        System.out.println("BIP49 Address" +bip49Address);


        String ethAddrss = MnemonicToKey.ethAddress(seedCode,"",ethKeyath);

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
