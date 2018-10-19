package xyz.olery.wallet.btc;



import com.google.common.base.Joiner;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import org.bitcoinj.core.*;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.RegTestParams;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.store.BlockStoreException;
import org.bitcoinj.store.SPVBlockStore;
import org.bitcoinj.wallet.*;
import org.bitcoinj.wallet.listeners.WalletCoinsReceivedEventListener;
import org.web3j.utils.Numeric;
import xyz.olery.wallet.hd.MnemonicToKey;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class BitcoinUtil {
    public static final String passphrase = "";


    /**
     * 通过私钥获取ECKey
     * @param priKey
     * @return
     */
    public static ECKey getECKeyFromPriKey(String priKey){
        ECKey ecKey = ECKey.fromPrivate(Numeric.toBigInt(priKey));
        return ecKey;
    }
    public  static String getPubKeyFrom(ECKey ecKey){
        //MainNetParams.get()
        //TestNet3Params.get();
        NetworkParameters params =TestNet3Params.get();
        return ecKey.toAddress(params).toBase58().toString();
    }


    //通过speed 获取钱包
    public static Wallet getFromSpeed(String seedCode){
        //MainNetParams.get()
        //TestNet3Params.get();
        NetworkParameters params =TestNet3Params.get();
        DeterministicSeed seed;
        try {
            seed = new DeterministicSeed(seedCode, null, passphrase, Utils.currentTimeSeconds() );

            Wallet restoredWallet = Wallet.fromSeed(params, seed);

            return  restoredWallet;
        } catch (UnreadableWalletException e) {
            e.printStackTrace();
        }
        return  null;
    }

//
//
    //通过本地文件获取Wallet
    public static  Wallet getWalletFromFile(String filePath){
        try {
            return  Wallet.loadFromFile(new File(filePath));
        } catch (UnreadableWalletException e) {
            e.printStackTrace();
        }
        return null;
    }
//





//
//
    public static File getBLockFile(){
        File file = new File("/tmp/bitcoin-blocks");
        if(!file.exists()){
            try {
                boolean newFile = file.createNewFile();
                if(newFile){
                    return file;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }
//
    public static void getWallet(){
        //MainNetParams.get()
        //TestNet3Params.get();
        NetworkParameters params =TestNet3Params.get();
        Wallet wallet = new Wallet(params);

        List<ECKey> keys = new ArrayList<ECKey>();
        ECKey ecKey = new ECKey();
        //加密eckey
        ecKey.encrypt(wallet.getKeyCrypter(),wallet.getKeyCrypter().deriveKey("123456"));
        keys.add(ecKey);


        wallet.importKeysAndEncrypt(keys,"123456");
        try {
            SPVBlockStore blockStore = new SPVBlockStore(params, getBLockFile());
            BlockChain chain = new BlockChain(params, wallet,blockStore);
            PeerGroup peerGroup = new PeerGroup(params, chain);
            peerGroup.addWallet(wallet);
            peerGroup.startAsync();
            peerGroup.downloadBlockChain();
            //startAndWait()
        } catch (BlockStoreException e) {
            e.printStackTrace();
        }
    }
//
//
    public static void testAddress(Wallet wallet){
        Address a = wallet.currentReceiveAddress();
        ECKey b = wallet.currentReceiveKey();
        Address c = wallet.freshReceiveAddress();
    }
//
//
//
//
    public static void userSPeed(Wallet wallet){
        NetworkParameters params = TestNet3Params.get();

        DeterministicSeed seed = wallet.getKeyChainSeed();
        System.out.println("Seed words are: " + Joiner.on(" ").join(seed.getMnemonicCode()));
        System.out.println("Seed birthday is: " + seed.getCreationTimeSeconds());


        //通过speed 获取Wallet
        String seedCode = "yard impulse luxury drive today throw farm pepper survey wreck glass federal";
        String seedCode2 = "liberty identify erase shuffle dignity armed produce mention actual you top vendor";
        long creationtime = 1409478661L;
        DeterministicSeed seed2;
        try {
            seed2 = new DeterministicSeed(seedCode, null, "", creationtime);
            Wallet restoredWallet = Wallet.fromSeed(params, seed2);
        } catch (UnreadableWalletException e) {
            e.printStackTrace();
        }

    }
//
//
    public static void t(Wallet wallet,String recipientAddress,String password,String mount){
        //MainNetParams.get()
        //TestNet3Params.get();

        Address a =Address.fromBase58(TestNet3Params.get(), recipientAddress);
        SendRequest req = SendRequest.to(a, Coin.parseCoin(mount));
        req.aesKey = wallet.getKeyCrypter().deriveKey(password);
        try {
            wallet.sendCoins(req);
        } catch (InsufficientMoneyException e) {
            e.printStackTrace();
        }
    }
//
//    public static void watchAddress(){
//        Wallet toWatch = null;
//        DeterministicKey watchingKey = toWatch.getWatchingKey();
//        String s = watchingKey.serializePubB58(getParams());
//        long creationTimeSeconds = watchingKey.getCreationTimeSeconds();
//
//
//        DeterministicKey key = DeterministicKey.deserializeB58(null, "key data goes here",getParams());
//
//        Wallet wallet = Wallet.fromWatchingKey(getParams(), key);
//
//
//        NetworkParameters params = TestNet3Params.get();
//
//        DeterministicSeed seed = new DeterministicSeed(new SecureRandom(),128,"password", Utils.currentTimeSeconds());
//        wallet = Wallet.fromSeed(params,seed);
//
//        //tobytes
//        byte[] bytes = MnemonicCode.toSeed(new ArrayList<>(), passphrase);
//
//    }
//
//    public static void test(){
//        NetworkParameters params = TestNet3Params.get();
//        DeterministicSeed seed = new DeterministicSeed(new SecureRandom(),128,"123456",Utils.currentTimeSeconds());
//        List<String> mnemonicCode = seed.getMnemonicCode();
////        LogUtil.e("mnemonicCode"+mnemonicCode);
////       byte[] bytes = MnemonicCode.toSeed(mnemonicCode, "123456");
//        Wallet wallet = Wallet.fromSeed(params,seed);
//    }
//
//    public static void test2(ECKey ceKey){
//        //MainNetParams.get()
//        //TestNet3Params.get();
//        NetworkParameters params =TestNet3Params.get();
//        String s = ceKey.toAddress(params).toBase58().toString();
//        String privateKeyAsWiF = ceKey.getPrivateKeyAsWiF(params);// 私钥， WIF(Wallet Import Format)
////        LogUtil.e(privateKeyAsWiF+"=========="+s);
//    }
//
//
//
    //通过私钥拿到eckey
//    public static ECKey getECkey(String prikey){
//        //MainNetParams.get()
//        //TestNet3Params.get();
//        NetworkParameters params =TestNet3Params.get();
//        ECKey key = DumpedPrivateKey.fromBase58(params, prikey).getKey();
//        return key;
//    }
//
//
//    //通过助记词导入新钱包
//    public static Wallet createWallet(String seedCode,String password) {
//        //MainNetParams.get()
//        //TestNet3Params.get();
//        NetworkParameters params =TestNet3Params.get();
//
//
//        KeyChainGroup kcg;
//        DeterministicSeed deterministicSeed = null;
//        try {
//            deterministicSeed = new DeterministicSeed(seedCode, null, password, Utils.currentTimeSeconds());
//        } catch (UnreadableWalletException e) {
//            e.printStackTrace();
//        }
//        kcg = new KeyChainGroup(params, deterministicSeed);
//        Wallet wallet = new Wallet(params, kcg);
//        return wallet;
//    }
//
//    //创建新钱包。
//    public static Wallet createWallet2() {
//        //MainNetParams.get()
//        //TestNet3Params.get();
//        NetworkParameters params =TestNet3Params.get();
//        KeyChainGroup kcg = new KeyChainGroup(params);
//        Wallet wallet = new Wallet(params, kcg);
//        wallet.getParams().getId();
//
//        return wallet;
//
//    }
//

    public static void main(String[] args) throws Exception {

//        //NetworkParameters params = RegTestParams.get();
//        NetworkParameters params = MainNetParams.get();
//
//        Wallet wallet = createWallet(params,"tuna biology crawl bone bread chalk light there pattern borrow afraid inherit","");
//        //myrDAeDxa2TA2s5LUQcBnxKrRZz57XaUHo
//        //myrDAeDxa2TA2s5LUQcBnxKrRZz57XaUHo
//       // System.out.println(wallet.getImportedKeys().get(0).toAddress(params).toBase58());
//        System.out.println(wallet.getKeyChainSeed().getMnemonicCode());


    }
    public static Wallet createWallet(NetworkParameters params,String seedCode,String password) {
        KeyChainGroup kcg;
        DeterministicSeed deterministicSeed = null;
        try {
            deterministicSeed = new DeterministicSeed(seedCode, null, password, Utils.currentTimeSeconds());
        } catch (UnreadableWalletException e) {
            e.printStackTrace();
        }
        kcg = new KeyChainGroup(params, deterministicSeed);
        Wallet wallet = new Wallet(params, kcg);
        return wallet;

    }


//
//    //加载本地文件
//    public static WalletAppKit getWalletKit(Context context){
//        return getWalletKit(context,"");
//    }
//
//
//    public static void setDownListener(WalletAppKit walletAppKit){
//        walletAppKit.setDownloadListener(new DownloadProgressTracker() {
//            @Override
//            protected void progress(double pct, int blocksSoFar, Date date) {
//                super.progress(pct, blocksSoFar, date);
//                int percentage = (int) pct;
//                LogUtil.e(percentage+"percentage");
//            }
//
//            @Override
//            protected void doneDownload() {
//                super.doneDownload();
//                String myAddress = walletAppKit.wallet().currentReceiveAddress().toBase58();
//                String s = walletAppKit.wallet().getBalance().toFriendlyString();
//                BtcData btcData = new BtcData(myAddress,s);
//                RxBus.getInstance().send(new MessageModel(BTC_DATA,btcData));
//                LogUtil.e(myAddress+"=="+s);
//
//            }
//        });
//    }
////

//
//    public void Test() {
//
//        ECKey k1 = new ECKey(); // some random key
//
//        // encrypting a key
//        KeyCrypter crypter1 = new KeyCrypterScrypt();
//
//        KeyParameter aesKey1 = crypter1.deriveKey("some arbitrary passphrase");
//        ECKey k2 = k1.encrypt(crypter1, aesKey1);
//        //System.out.println(k2.isEncrypted()); // true
//
//        // decrypting a key
//        KeyCrypter crypter2 = k2.getKeyCrypter();
//        KeyParameter aesKey2 = crypter2.deriveKey("some arbitrary passphrase");
//        ECKey k3 = k2.decrypt(aesKey2);
//
//        //System.out.println(k1.equals(k3));  // true
//    }
//
//
//    public static void closedWallet(){
//        AppContext.walletAppKit.stopAsync();
//        AppContext.walletAppKit.awaitTerminated();
//    }
//
//    public static void test2(){
//        DeterministicKey deterministicKey = AppContext.walletAppKit.wallet().getWatchingKey().dropPrivateBytes();
//        deterministicKey = HDKeyDerivation.createMasterPubKeyFromBytes(deterministicKey.getPubKey(), deterministicKey.getChainCode());
//        String xPublicKey = deterministicKey.serializePubB58(getParams());
//        String  privateKey= AppContext.walletAppKit.wallet().getKeyByPath(DeterministicKeyChain.ACCOUNT_ZERO_PATH).getPrivateKeyAsWiF(getParams());
//        Log.e("key", xPublicKey.toString());
//        Log.e("privatekey", privateKey.toString());
//        if (getParams() == RegTestParams.get()) {
//            AppContext.walletAppKit.connectToLocalHost();
//        }
//    }

//    public static void si(String privateKey,String recipientAddress,String amount){
//        SendRequest request = SendRequest.to(Address.fromBase58(getParams(),
//                recipientAddress), Coin.parseCoin(amount));
//        Signingtrasaction(privateKey,request.tx.getHashAsString());
//    }
//
//    public static  void Signingtrasaction(String wif, String msg) {
//        try {
//            // creating a key object from WiF
//            DumpedPrivateKey dpk = DumpedPrivateKey.fromBase58(getParams(), wif);
//            ECKey key = dpk.getKey();
//            // checking our key object
//            // NetworkParameters main = MainNetParams.get();
//            String check = key.getPrivateKeyAsWiF(getParams());
//            System.out.println(wif.equals(check));  // true
//            Log.e("wif check", String.valueOf(wif.equals(check)));
//            // creating Sha object from string
//            Sha256Hash hash = Sha256Hash.wrap(msg);
//            // creating signature
//            ECKey.ECDSASignature sig = key.sign(hash);
//            // encoding
//            byte[] res = sig.encodeToDER();
//            // converting to hex
//            //String hex = DatatypeConverter.printHexBinary(res);
//            // String hex = new String(res);
//            String hex = android.util.Base64.encodeToString(res, 16);
//            Log.e("sigendTransiction", hex.toString());
//            Log.e("decrypttx",""+ Hex.decode(sig.encodeToDER()));
//        } catch (Exception e) {   //signingkey = ecdsa.from_string(privateKey.decode('hex'), curve=ecdsa.SECP256k1)
////            Log.e("signing exception", e.getMessage().toString());
//        }
//    }

    public static void creataeWalletBySeed() throws Exception{
        String seedCode = "yard impulse luxury drive today throw farm pepper survey wreck glass federal";
        long creationtime = 1409478661L;
        DeterministicSeed seed = new DeterministicSeed(seedCode, null, "", creationtime);

        NetworkParameters params = TestNet3Params.get();
        Wallet restoredWallet = Wallet.fromSeed(params, seed);

        System.out.println(restoredWallet.currentReceiveAddress());



    }
}


