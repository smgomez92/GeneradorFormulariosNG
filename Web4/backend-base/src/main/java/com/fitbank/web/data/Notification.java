package com.fitbank.web.data;

import java.util.LinkedList;
import java.util.List;

public class Notification {

    private List<NotificationItem> items = new LinkedList<NotificationItem>();

    public List<NotificationItem> getItems() {
        return items;
    }

    public void setItems(List<NotificationItem> items) {
        this.items = items;
    }

    public void addItem(NotificationItem item) {
        items.add(item);
    }

}
