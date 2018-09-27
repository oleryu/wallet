package xyz.olery.wallet.eth;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

/**
 * 观察 链上区块
 * @oleryu.xyz
 */
public class BlockListener {
    public static void startup() throws Exception{
        Web3j web3j = Web3j.build(new HttpService("http://192.168.10.161:8545"));

        web3j.blockObservable(false).subscribe(tx -> {
            System.out.println("----------------------> " + tx.getBlock().getNumber() +"-->" +tx.getBlock().getHash());
        });
    }
    public static void main(String[] args) throws Exception {
        startup();
    }


}
