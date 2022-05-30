package com.giang.topdf.model;

public class EditItem {
    private int itemId;
    private String itemName;

    public EditItem(int itemId, String itemName) {
        this.itemId = itemId;
        this.itemName = itemName;
    }

    public int getItemId() {
        return itemId;
    }

    public String getItemName() {
        return itemName;
    }
}
