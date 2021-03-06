package xyz.olery.wallet.btc;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import org.bitcoinj.core.*;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.SendRequest;
import org.bitcoinj.wallet.UnreadableWalletException;
import org.bitcoinj.wallet.Wallet;
import org.bitcoinj.wallet.listeners.WalletCoinsReceivedEventListener;
import xyz.olery.wallet.hd.MnemonicToKey;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

public class BitcoinWallet {

    public static void main(String[] args) throws Exception {
        String seedcode = "tuna biology crawl bone bread chalk light there pattern borrow afraid inherit";

        BitcoinWallet.start(seedcode);
    }

    public static void start(String seedcode) throws Exception {

        NetworkParameters params = LocalRegTestParams.get();
        WalletAppKit walletAppKit = getWalletKit(params,seedcode,"");

        Wallet wallet = walletAppKit.wallet();
        System.out.println(wallet.getBalance());


        while (true) {
            try {
                System.out.println("|ImportKeySize    |:" + wallet.getImportedKeys().size());
                System.out.println("|WatchedAddresses |:" + wallet.getWatchedAddresses().size());
                System.out.println("|RecvAddr         |:" + wallet.currentReceiveAddress().toString());
                System.out.println("|WalletBalance    |:" + wallet.getBalance());

                System.out.println(wallet.getImportedKeys().get(0).toAddress(params).toBase58());
                Thread.sleep(5000);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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

        WalletAppKit walletAppKit = new WalletAppKit(params, new File("D://bitcoinT"), "") {
            @Override
            protected void onSetupCompleted() {
                if (wallet().getImportedKeys().size() < 1) {

                    try {
                        wallet().importKey(MnemonicToKey.eckeyTest());
                        wallet().freshReceiveAddress();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //wallet().importKey(new ECKey());
                }

                wallet().allowSpendingUnconfirmedTransactions();
                setupWalletListeners(wallet());
//                ECKey ecKey = wallet().getImportedKeys().get(0);
//
////                //打印助记词
//                List<String> seedWordsFromWallet = getSeedWordsFromWallet(wallet());
////                for(int i = 0; i < seedWordsFromWallet.size(); i ++) {
////                    System.out.println(seedWordsFromWallet.get(i));
////                }
//                //当前地址
//                String s1 = wallet().currentReceiveAddress().toBase58();
//
//                String privateKeyAsWiF = wallet().currentReceiveKey().getPrivateKeyAsWiF(params);

            }
        };

        walletAppKit.setAutoSave(true);
        walletAppKit.setBlockingStartup(false);
//
//        setDownListener(walletAppKit);
        if (params == LocalRegTestParams.get()) {

            InetAddress localHost = null;
            try {
                localHost = InetAddress.getByName("192.168.124.2");
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            ;

            walletAppKit.setPeerNodes(new PeerAddress(params, localHost, 19000));
        }


        if(seedcode != null){
            try {

                //DeterministicKey key = getDeterministicKey(seedCode,passphrase,strKeypath);
                //DeterministicSeed seed = new DeterministicSeed(seedcode, null, passphrase,Utils.currentTimeSeconds());
                //DeterministicSeed seed = new DeterministicSeed(seedcode, null, "", 1409478661L);
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

    public  static void  setupWalletListeners(Wallet wallet) {
        wallet.addCoinsReceivedEventListener(new CoinsReceived());
//        wallet.addCoinsReceivedEventListener((wallet1, tx, prevBalance, newBalance) -> {
//            String s = wallet.getBalance().toFriendlyString();
//            String s1 = "";
//            if(tx.getPurpose() == Transaction.Purpose.UNKNOWN) {
//                s1 = newBalance.minus(prevBalance).toFriendlyString();
//            }
//        });
//        wallet.addCoinsSentEventListener((wallet12, tx, prevBalance, newBalance) -> {
//            String s = wallet.getBalance().toFriendlyString();
//            String s1 = "Sent "+prevBalance.minus(newBalance).minus(tx.getFee()).toFriendlyString();
//        });
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

class CoinsReceived implements WalletCoinsReceivedEventListener {
    public void onCoinsReceived(final Wallet wallet, final Transaction transaction, Coin prevBalance, Coin newBalance) {
        final Coin value = transaction.getValueSentToMe(wallet);
        System.out.println("Received tx for " + value.toFriendlyString() + ": " + transaction);
        System.out.println("Previous balance is " + prevBalance.toFriendlyString());
        System.out.println("New estimated balance is " + newBalance.toFriendlyString());
        System.out.println("Coin received, wallet balance is :" + wallet.getBalance());
        Futures.addCallback(transaction.getConfidence().getDepthFuture(1), new FutureCallback<TransactionConfidence>() {
            public void onSuccess(TransactionConfidence result) {
                System.out.println("Transaction confirmed, wallet balance is :" + wallet.getBalance());
            }
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        });
    }
}