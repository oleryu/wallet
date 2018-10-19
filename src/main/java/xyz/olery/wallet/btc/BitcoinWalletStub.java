package xyz.olery.wallet.btc;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import org.bitcoinj.core.*;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.SendRequest;
import org.bitcoinj.wallet.UnreadableWalletException;
import org.bitcoinj.wallet.Wallet;
import org.bitcoinj.wallet.listeners.WalletCoinsReceivedEventListener;
import xyz.olery.wallet.hd.MnemonicToKey;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

public class BitcoinWalletStub {

    public static void main(String[] args) throws Exception {
        String seedcode = "tuna biology crawl bone bread chalk light there pattern borrow afraid inherit";
        NetworkParameters params = MainNetParams.get();
//        NetworkParameters params = LocalRegTestParams.get();
        WalletAppKit walletAppKit = getWalletKit(params,seedcode,"");

        Wallet wallet = walletAppKit.wallet();

        boolean nosend = true;
        while (true) {
            try {
                System.out.println("|ImportKeySize    |:" + wallet.getImportedKeys().size());
                System.out.println("|WatchedAddresses |:" + wallet.getWatchedAddresses().size());
                System.out.println("|RecvAddr         |:" + wallet.currentReceiveAddress().toString());
                System.out.println("|WalletBalance    |:" + wallet.getBalance());

                System.out.println(wallet.getImportedKeys().get(0).toAddress(params).toBase58());
                Thread.sleep(1000);
                if(wallet.getBalance().isGreaterThan(Coin.parseCoin("10"))) {
                    if(nosend) {
                        send(params,walletAppKit,"2ND27LasznyE3ct9ijEdKeNhyviX6mbPETt","1");
                        nosend = false;
                    }

                };
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static  String send(NetworkParameters params,WalletAppKit walletAppKit, String recipientAddress, String amount){
        String err = "";

        if(recipientAddress.equals("") || recipientAddress.equals("Scan recipient QR")) {
            err = "Select recipient";
            return err;
        }
        if(amount.equals("") | Double.parseDouble(amount) <= 0) {
            err = "Select valid amount";
            return err;
        }

        if(walletAppKit.wallet().getBalance().isLessThan(Coin.parseCoin(amount))) {
            err = "You got not enough coins";
            return err;
        }

        SendRequest request = SendRequest.to(Address.fromBase58(params, recipientAddress), Coin.parseCoin(amount));

        try {
            walletAppKit.wallet().completeTx(request);
            walletAppKit.wallet().commitTx(request.tx);


            walletAppKit.peerGroup().broadcastTransaction(request.tx).broadcast();
            return "";
        } catch (InsufficientMoneyException e) {
            e.printStackTrace();
            return  e.getMessage();
        }
    }

    /**
     * 通过助记词
     * @param params
     * @param seedcode
     * @return
     */
    public static WalletAppKit getWalletKit(NetworkParameters params,String seedcode,String passphrase){
        if(null == passphrase) {
            passphrase = "";
        }
        WalletAppKit walletAppKit = new WalletAppKit(params, new File("D://tmp"), "") {
            @Override
            protected void onSetupCompleted() {
                if (wallet().getImportedKeys().size() < 1) {
                    try {
                        wallet().importKey(MnemonicToKey.eckeyTest());
                        wallet().freshReceiveAddress();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };

//        walletAppKit.setAutoSave(true);
//        walletAppKit.setBlockingStartup(false);
//
//        setDownListener(walletAppKit);
        if (params == LocalRegTestParams.get()) {
            InetAddress localHost = null;
            try {
                localHost = InetAddress.getByName("192.168.10.107");
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }

            walletAppKit.setPeerNodes(new PeerAddress(params, localHost, 19000));
        }

        if(seedcode != null){
            try {
                DeterministicSeed seed = new DeterministicSeed(seedcode, null, passphrase, Utils.currentTimeSeconds());
                walletAppKit.restoreWalletFromSeed(seed);
            } catch (UnreadableWalletException e) {
                e.printStackTrace();
            }

        }
        walletAppKit.startAsync();
        walletAppKit.awaitRunning();
        return  walletAppKit;
    }

    /**
     * 通过Wallet 获取 助记词
     * @param wallet
     * @return
     */
    public static List<String> getSeedWordsFromWallet(Wallet wallet){
        DeterministicSeed seed = wallet.getKeyChainSeed();
        return  seed.getMnemonicCode();
    }
}


