package net.regnology.lucy.domain.enumeration;

/**
 * Shipment methods that can be used:
 * <ul>
 * <li>{@link #DOWNLOAD}</li>
 * <li>{@link #EXPORT}</li>
 * </ul>
 */
public enum ShipmentMethod {
    DOWNLOAD("download"),
    EXPORT("export");

    private final String value;

    ShipmentMethod(String value) {
        this.value = value;
    }

    public static ShipmentMethod getShipmentMethodByValue(String value) {
        for (ShipmentMethod shipmentMethod : ShipmentMethod.values()) {
            if (shipmentMethod.getValue().equalsIgnoreCase(value)) return shipmentMethod;
        }
        return null;
    }

    public String getValue() {
        return value;
    }
}
