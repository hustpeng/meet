package org.jivesoftware.smack.roster;

/**
 * The subscription status of a roster item. An optional element that indicates
 * the subscription status if a change request is pending.
 */
public class RosterPacketItemStatus {

    /**
     * Request to subcribe.
     */
    public static final RosterPacketItemStatus SUBSCRIPTION_PENDING = new RosterPacketItemStatus("subscribe");

    /**
     * Request to unsubscribe.
     */
    public static final RosterPacketItemStatus UNSUBSCRIPTION_PENDING = new RosterPacketItemStatus("unsubscribe");
    private final String value;

    /**
     * Returns the item status associated with the specified string.
     *
     * @param value the item status.
     */
    private RosterPacketItemStatus(String value) {
        this.value = value;
    }

    public static RosterPacketItemStatus fromString(String value) {
        if (value == null) {
            return null;
        }
        value = value.toLowerCase();
        if ("unsubscribe".equals(value)) {
            return UNSUBSCRIPTION_PENDING;
        } else if ("subscribe".equals(value)) {
            return SUBSCRIPTION_PENDING;
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        return value;
    }
}