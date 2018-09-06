package com.hengpu.wallet;


import java.io.IOException;
import java.security.SecureRandom;
import java.util.List;

import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.UnreadableWalletException;

/*
* 获取随机助记词
* */
public class MnemonicCode {
    public static void main(String[] args) throws UnreadableWalletException, IOException {
        // TODO Auto-generated method stub
        //spoon tackle camp vintage present soldier trap sample over team foil force
        String passphrase = "";
        SecureRandom secureRandom = new SecureRandom();
        long creationTimeSeconds = System.currentTimeMillis() / 1000;
        DeterministicSeed deterministicSeed = new DeterministicSeed(secureRandom, 128, passphrase, creationTimeSeconds);
        List<String> mnemonicCode = deterministicSeed.getMnemonicCode();
        System.out.println(String.join(" ", mnemonicCode));
    }
}