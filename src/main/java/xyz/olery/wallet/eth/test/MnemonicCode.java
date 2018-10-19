package xyz.olery.wallet.eth.test;

import xyz.olery.wallet.eth.account.HDWalletAccount;
import xyz.olery.wallet.hd.MnemonicCodeUtil;

public class MnemonicCode {
    public static void main(String[] args) throws  Exception {
        String seedCode = MnemonicCodeUtil.getMnemonicCode();
        System.out.println("|seedCode|: " + seedCode);
        String ethKeypath = "M/44H/60H/0H/0/0";
        String passphrase = "";

        HDWalletAccount walletAccount = new HDWalletAccount(seedCode,ethKeypath,passphrase);
        System.out.println("|ethAddress|: " + walletAccount.ethAddress());
//         System.out.println(MnemonicCodeUtil.getMnemonicCodeByDic());
    }
}
