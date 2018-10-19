package xyz.olery.wallet.btc;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.concurrent.ExecutionException;

import org.bitcoinj.core.*;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.store.BlockStoreException;
import org.bitcoinj.store.SPVBlockStore;
import org.bitcoinj.wallet.Wallet;
import wf.bitcoin.javabitcoindrpcclient.BitcoinJSONRPCClient;
import wf.bitcoin.javabitcoindrpcclient.BitcoindRpcClient;
import wf.bitcoin.javabitcoindrpcclient.BitcoindRpcClient.BlockChainInfo;
import wf.bitcoin.javabitcoindrpcclient.BitcoindRpcClient.MiningInfo;

/**
 *
 * Bitcoind Sample
 *
 * @author Bruno Candido Volpato da Cunha
 *
 * from  https://github.com/brunocvcunha/bitcoind-java-client-sample.git
 *
 *
 */
public class BitcoindRpcCli {


    public static void main(String[] args) throws Exception {

//        wf.bitcoin.javabitcoindrpcclient.BitcoindRpcClient rpcClient = new BitcoinJSONRPCClient("http://bitcoin:local321@192.168.10.107:19031");
        //rpcClient.setTxFee(new BigDecimal(0.001).setScale(3, BigDecimal.ROUND_DOWN));
        //admin1
        wf.bitcoin.javabitcoindrpcclient.BitcoindRpcClient rpcClient = new BitcoinJSONRPCClient("http://admin2:123@192.168.124.2:19011");


        MiningInfo info = rpcClient.getMiningInfo();
        System.out.println("Mining Information");
        System.out.println("------------------");
        System.out.println("Chain......: " + info.chain());
        System.out.println("Blocks.....: " + info.blocks());
        System.out.println("Difficulty.: " + info.difficulty());
        System.out.println("Hash Power.: " + new BigDecimal(info.networkHashps()).toPlainString());

        System.out.println(">>>>>>>>>>>>>>.." + rpcClient.getInfo().balance());

        //String address = rpcClient.getNewAddress("Learning-Bitcoin-from-the-Command-Line");
        //System.out.println("New Address: " + address);

//        String privKey = rpcClient.dumpPrivKey(address);
//        System.out.println("Priv Key: " + privKey);

        BlockChainInfo chainInfo = rpcClient.getBlockChainInfo();
        String blockHash = rpcClient.getBlockHash(chainInfo.blocks());




        System.out.println("Blockchain Info " + chainInfo);
        System.out.println("Block Hash: " + blockHash);

//        System.out.println("Balance: " + rpcClient.getBalance("2N81cVg1AtRssWuhDKDMdYXevZq19hV9jC1",0));
//        System.out.println("Balance: " + rpcClient.getBalance("2N81cVg1AtRssWuhDKDMdYXevZq19hV9jC1"));
        System.out.println("Balance: " + rpcClient.getBalance(""));



        //2MvZr3u2N4jte8v1qdcUs3Qmot1VDxLGRkT
        //make sendfrom1 ADDRESS=2MvZr3u2N4jte8v1qdcUs3Qmot1VDxLGRkT AMOUNT=10
    }

    public static class BitcoinTest {

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
}
