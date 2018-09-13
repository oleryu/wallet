package xyz.olery.wallet;

import org.bitcoinj.core.*;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.store.BlockStoreException;
import org.bitcoinj.store.SPVBlockStore;
import org.bitcoinj.wallet.Wallet;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class BitcoinTest {

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


    //发送交易
    public static void send(Wallet wallet, String recipientAddress, String amount){
        //MainNetParams.get()
        //TestNet3Params.get();
        NetworkParameters params = TestNet3Params.get();
        Address targetAddress  = Address.fromBase58(params, recipientAddress);
        // Do the send of 1 BTC in the background. This could throw InsufficientMoneyException.
        SPVBlockStore blockStore = null;
        try {
            blockStore = new SPVBlockStore(params, getBLockFile());
        } catch (BlockStoreException e) {
            e.printStackTrace();
        }
        BlockChain chain = null;
        try {
            chain = new BlockChain(params, wallet,blockStore);
            PeerGroup peerGroup = new PeerGroup(params, chain);
            try {
                Wallet.SendResult result = wallet.sendCoins(peerGroup, targetAddress, Coin.parseCoin(amount));
                // Save the wallet to disk, optional if using auto saving (see below).
                //wallet.saveToFile(....);
                // Wait for the transaction to propagate across the P2P network, indicating acceptance.
                try {
                    Transaction transaction = result.broadcastComplete.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                return;
            } catch (InsufficientMoneyException e) {
                e.printStackTrace();
            }
        } catch (BlockStoreException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {

    }
}
