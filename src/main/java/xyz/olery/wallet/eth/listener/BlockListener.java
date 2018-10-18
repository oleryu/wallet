package xyz.olery.wallet.eth.listener;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

/**
 * 观察 链上区块
 *
 * @ oleryu.xyz
 */
public class BlockListener {
    public static void startup() throws Exception{

        Web3j web3j = Web3j.build(new HttpService("http://192.168.10.168:8545"));

        web3j.blockObservable(false).subscribe(tx -> {
            System.out.println("----------------------> " + tx.getBlock().getNumber());
        });
    }
    public static void main(String[] args) throws Exception {
        startup();
    }


}
