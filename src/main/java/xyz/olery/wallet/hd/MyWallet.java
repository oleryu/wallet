package xyz.olery.wallet.hd;

public class MyWallet {
    private String name;
    private String type;
    private String keytype;
    private String addr;
    private String keytext;

    public MyWallet(String name, String type, String keytype, String addr, String keytext) {
        this.name = name;
        this.type = type;
        this.keytype = keytype;
        this.addr = addr;
        this.keytext = keytext;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getKeytype() {
        return keytype;
    }

    public String getAddr() {
        return addr;
    }

    public String getKeytext() {
        return keytext;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setKeytype(String keytype) {
        this.keytype = keytype;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public void setKeytext(String keytext) {
        this.keytext = keytext;
    }
}
