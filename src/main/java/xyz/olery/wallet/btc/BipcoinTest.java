package xyz.olery.wallet.btc;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.crypto.HDUtils;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.wallet.DeterministicKeyChain;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.Wallet;

import java.io.File;
import java.math.BigInteger;

public class BipcoinTest {
//    public static void main(String[] args) throws Exception {
//        //TestNet3Params
//        //MainNetParams
//        //RegTestParams
//        NetworkParameters params = TestNet3Params.get();
//        String wordsList = "one misery space industry hen mistake typical prison plunge yellow disagree arm";
//        DeterministicSeed deterministicSeed = new DeterministicSeed(wordsList, null, "", 0L);
//        DeterministicKeyChain deterministicKeyChain = DeterministicKeyChain.builder().seed(deterministicSeed).build();
//        BigInteger privKey = deterministicKeyChain.getKeyByPath(HDUtils.parsePath("44H / 1H / 0H / 0 / 0"), true).getPrivKey();
//        ECKey key = ECKey.fromPrivate(privKey);
//        Address address = key.toAddress(params);
//
//        System.out.println(address.toBase58());
//
//        Wallet wallet = new Wallet(TestNet3Params.get());
//        //File walletFile = new File(result.get("addressFromKey")+".test.wallet");
//        wallet.importKey(key);
//        wallet.freshReceiveAddress();
//        System.out.println(wallet.currentReceiveAddress());
//        System.out.println(wallet.getKeyChainSeed().getMnemonicCode());
//        System.out.println(wallet.getImportedKeys().get(0).toAddress(params).toBase58());
//
//    }

    public static void main(String[] args) throws Exception{
        String seedCode = "yard impulse luxury drive today throw farm pepper survey wreck glass federal";
        long creationtime = 1409478661L;
        DeterministicSeed seed = new DeterministicSeed(seedCode, null, "", creationtime);

        NetworkParameters params = TestNet3Params.get();
        Wallet restoredWallet = Wallet.fromSeed(params, seed);

        System.out.println(restoredWallet.currentReceiveAddress());



    }


}
