package com.callicoder.goparking.domain;

import com.callicoder.goparking.exceptions.CarAlreadyExistsException;
import com.callicoder.goparking.exceptions.InvalidLeaveSlotException;
import com.callicoder.goparking.exceptions.ParkingLotFullException;
import com.callicoder.goparking.exceptions.SlotNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

public class ParkingLot {

    private final int numSlots;
    private final int numFloors;
    private SortedSet<ParkingSlot> availableSlots = new TreeSet<>();
    private Set<ParkingSlot> occupiedSlots = new HashSet<>();

    public ParkingLot(int numSlots) {
        if (numSlots <= 0) {
            throw new IllegalArgumentException(
                "Number of slots in the Parking Lot must be greater than zero."
            );
        }

        // Assuming Single floor since only numSlots are specified in the input.
        this.numSlots = numSlots;
        this.numFloors = 1;

        for (int i = 0; i < numSlots; i++) {
            ParkingSlot parkingSlot = new ParkingSlot(i + 1, 1);
            this.availableSlots.add(parkingSlot);
        }
    }

    public synchronized Ticket reserveSlot(Car car) {
        if (car == null) {
            throw new IllegalArgumentException("Car must not be null");
        }

        if (this.isFull()) {
            throw new ParkingLotFullException();
        }
        Optional<ParkingSlot> existingSlot = this.occupiedSlots.stream()
                .filter(s-> s.getCar().getRegistrationNumber().equalsIgnoreCase(car.getRegistrationNumber()))
                .findFirst();
        if (existingSlot.isPresent()){
            throw new CarAlreadyExistsException("Car with registration number: " + car.getRegistrationNumber() + " already exists in the lot.");
        }
        ParkingSlot nearestSlot = this.availableSlots.first();

        nearestSlot.reserve(car);
        this.availableSlots.remove(nearestSlot);
        this.occupiedSlots.add(nearestSlot);

        return new Ticket(
            nearestSlot.getSlotNumber(),
            car.getRegistrationNumber(),
            car.getColor()
        );
    }

    public ParkingSlot leaveSlot(int slotNumber) {
        if(slotNumber > numSlots)
            throw new SlotNotFoundException(slotNumber);
        Optional<ParkingSlot> parkingSlotOptional = this.occupiedSlots
                .stream().filter(s-> s.getSlotNumber() == slotNumber).findFirst();
        if(parkingSlotOptional.isPresent()){
            ParkingSlot parkingSlot = parkingSlotOptional.get();
            occupiedSlots.remove(parkingSlot);
            availableSlots.add(parkingSlot);
            parkingSlot.clear();
            return parkingSlotOptional.get();
        }else {
            throw new InvalidLeaveSlotException("The slot number: " + slotNumber + " is not occupied yet");
        }
    }

    public boolean isFull() {
        return this.availableSlots.isEmpty();
    }

    public List<String> getRegistrationNumbersByColor(String color) {
        return this.occupiedSlots.stream()
                .filter(s-> s.getCar().getColor().equalsIgnoreCase(color))
                .map(s-> s.getCar().getRegistrationNumber()).collect(Collectors.toList());
    }

    public List<Integer> getSlotNumbersByColor(String color) {
       return this.occupiedSlots.stream()
                .filter(s-> s.getCar().getColor().equalsIgnoreCase(color))
                .map(s->s.getSlotNumber()).collect(Collectors.toList());
    }

    public Optional<Integer> getSlotNumberByRegistrationNumber(
        String registrationNumber
    ) {
        return this.occupiedSlots
                .stream()
                .filter(s-> s.getCar().getRegistrationNumber().equals(registrationNumber))
                .map(s-> s.getSlotNumber())
                .findFirst();
    }

    public int getNumSlots() {
        return numSlots;
    }

    public int getNumFloors() {
        return numFloors;
    }

    public SortedSet<ParkingSlot> getAvailableSlots() {
        return availableSlots;
    }

    public Set<ParkingSlot> getOccupiedSlots() {
        return occupiedSlots;
    }
}
