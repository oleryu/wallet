package xyz.olery.wallet.util;


import java.io.IOException;
import java.security.SecureRandom;
import java.util.List;

import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.UnreadableWalletException;

/*
* 生成随机助记词
* */
public class MnemonicCode {

    public static String getMnemonicCode() {
        //spoon tackle camp vintage present soldier trap sample over team foil force
        String passphrase = "";
        SecureRandom secureRandom = new SecureRandom();
        long creationTimeSeconds = System.currentTimeMillis() / 1000;
        DeterministicSeed deterministicSeed = new DeterministicSeed(secureRandom, 128, passphrase, creationTimeSeconds);
        List<String> mnemonicCode = deterministicSeed.getMnemonicCode();

        return String.join(" ", mnemonicCode);
    }
    public static void main(String[] args) throws UnreadableWalletException, IOException {
        System.out.println(MnemonicCode.getMnemonicCode());
    }
}