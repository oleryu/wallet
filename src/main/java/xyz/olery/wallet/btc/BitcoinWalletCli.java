package xyz.olery.wallet.btc;

import org.bitcoinj.core.*;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.RegTestParams;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.SendRequest;
import org.bitcoinj.wallet.Wallet;
import xyz.olery.wallet.hd.MnemonicToKey;

import java.io.File;
import java.net.InetAddress;
import java.util.List;

public class BitcoinWalletCli {
    public static void main(String[] args) throws Exception {
        String seedCode = "tuna biology crawl bone bread chalk light there pattern borrow afraid inherit";

        DeterministicSeed seed = new DeterministicSeed(seedCode, null, "", 1409478661L);
        NetworkParameters params = RegTestParams.get();
        Wallet wallet = Wallet.fromSeed(params,seed);

        System.out.println(wallet.getBalance());
        String recipientAddress = "2MzbD13iVYwH3v78RsjVw9fpA5HvEGkVMcQ";
        String amount = "0.01";
        SendRequest request = SendRequest.to(Address.fromBase58(params, recipientAddress), Coin.parseCoin(amount));
        wallet.completeTx(request);
        wallet.commitTx(request.tx);
        //Transaction tx = request.tx;

        InetAddress remoteHost = InetAddress.getByName("192.168.227.138");



        wallet.sendCoins(request);
    }



}
