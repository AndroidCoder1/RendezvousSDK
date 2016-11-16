package com.rancard.rndvusdk.models;

import com.rancard.rndvusdk.RendezvousLogActivity;

import java.io.Serializable;

/**
 * Created by: Robert Wilson.
 * Date: Feb 21, 2016
 * Time: 7:04 PM
 * Package: com.multimedia.joyonline.models
 * Project: JoyOnline-Android
 */
public class EventLog implements Serializable
{
    public static final String TAG = EventLog.class.getSimpleName();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    private Long id = 0l;

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    private Long itemId = 0l;

    public RendezvousLogActivity getLogActivity() {
        return logActivity;
    }

    public void setLogActivity(RendezvousLogActivity logActivity) {
        this.logActivity = logActivity;
    }

    private RendezvousLogActivity logActivity;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EventLog)) return false;

        EventLog eventLog = (EventLog) o;

        if (getId() != null ? !getId().equals(eventLog.getId()) : eventLog.getId() != null)
            return false;
        if (getItemId() != null ? !getItemId().equals(eventLog.getItemId()) : eventLog.getItemId() != null)
            return false;
        return getLogActivity() == eventLog.getLogActivity();

    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getItemId() != null ? getItemId().hashCode() : 0);
        result = 31 * result + (getLogActivity() != null ? getLogActivity().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "EventLog{" +
                "id=" + id +
                ", itemId=" + itemId +
                ", logActivity=" + logActivity +
                '}';
    }
}
