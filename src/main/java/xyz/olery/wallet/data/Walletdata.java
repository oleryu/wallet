package xyz.olery.wallet.data;


import net.sf.json.JSONObject;

import java.util.ArrayList;

public class Walletdata {
    private String dentityId;
    private String dentityName;
    private ArrayList<MyWallet> walletlist;

    private String sign;

    public void setDentityId(String dentityId) {
        this.dentityId = dentityId;
    }

    public void setDentityName(String dentityName) {
        this.dentityName = dentityName;
    }

    public void setWalletlist(ArrayList<MyWallet> walletlist) {
        this.walletlist = walletlist;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getDentityId() {
        return dentityId;
    }

    public String getDentityName() {
        return dentityName;
    }

    public ArrayList<MyWallet> getWalletlist() {
        return walletlist;
    }

    public String getSign() {
        return sign;
    }

    public static void main(String[] args) {
        String name = "";
        String type = "";
        String keytype = "";
        String addr = "";
        String keytext = "";


        Walletdata walletdata = new Walletdata();
        MyWallet wallet = new MyWallet(name, type, keytype, addr, keytext);
        ArrayList list = new ArrayList();
        list.add(wallet);
        walletdata.setWalletlist(list);
        walletdata.setDentityId("YUHL");
        walletdata.setDentityName("");
        walletdata.setSign("");


        JSONObject jsonObject = JSONObject.fromObject(walletdata);

        System.out.println(jsonObject.toString());
        //jsonObject.get("");

    }
}
