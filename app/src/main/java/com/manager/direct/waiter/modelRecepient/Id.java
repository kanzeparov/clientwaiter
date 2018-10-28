package com.manager.direct.waiter.modelRecepient;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Id {

    @SerializedName("receipts")
    @Expose
    private List<Receipt> receipts = null;

    public List<Receipt> getReceipts() {
        return receipts;
    }

    public void setReceipts(List<Receipt> receipts) {
        this.receipts = receipts;
    }

}
