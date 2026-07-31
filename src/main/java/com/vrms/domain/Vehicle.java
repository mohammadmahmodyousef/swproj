package com.vrms.domain;

public class Vehicle {

    private final String id;
    private final String name;
    private final String model;
    private VehicleStatus status;

    public Vehicle(String id, String name, String model, VehicleStatus status) {
        this.id = id;
        this.name = name;
        this.model = model;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getModel() {
        return model;
    }

    public VehicleStatus getStatus() {
        return status;
    }

    public void setStatus(VehicleStatus status) {
        this.status = status;
    }

    public boolean isAvailable() {
        return status == VehicleStatus.AVAILABLE;
    }

    public String getType() {
        return "Vehicle";
    }

    public void validateRental(int customerAge, boolean hasSpecialLicense) {
     // Base Vehicle class provides a default validation implementation with no extra age or license restrictions.
     // Specific vehicle subclasses (e.g., Truck) override this method to enforce specific rules.
    }

    protected String getExtraData() {
        return "";
    }

    public String toFileLine() {
        return id + "|" + name + "|" + model + "|" + status + "|" + getType() + "|" + getExtraData();
    }

    public static Vehicle fromFileLine(String line) {
        String[] data = line.split("\\|", -1);

        if (data.length == 4) {
            return new Vehicle(data[0], data[1], data[2], VehicleStatus.valueOf(data[3]));
        }

        if (data.length < 5) {
            throw new IllegalArgumentException("Invalid vehicle data");
        }

        String id = data[0];
        String name = data[1];
        String model = data[2];
        VehicleStatus status = VehicleStatus.valueOf(data[3]);
        String type = data[4];
        String extraData = data.length >= 6 ? data[5] : "";

        if (type.equals("Car")) {
            return new Car(id, name, model, status);
        }

        if (type.equals("Motorcycle")) {
            return new Motorcycle(id, name, model, status);
        }

        if (type.equals("Van")) {
            return new Van(id, name, model, status);
        }

        if (type.equals("Truck")) {
            return new Truck(id, name, model, status);
        }

        if (type.equals("Electric Vehicle")) {
            int batteryLevel = extraData.isEmpty() ? 0 : Integer.parseInt(extraData);
            return new ElectricVehicle(id, name, model, status, batteryLevel);
        }

        return new Vehicle(id, name, model, status);
    }

    @Override
    public String toString() {
        if (getType().equals("Vehicle")) {
            return id + " - " + name + " - " + model + " - " + status;
        }

        return id + " - " + name + " - " + model + " - " + getType() + " - " + status;
    }
}