package com.callicoder.goparking.handler;

import static com.callicoder.goparking.utils.MessageConstants.*;

import com.callicoder.goparking.domain.Car;
import com.callicoder.goparking.domain.ParkingLot;
import com.callicoder.goparking.domain.ParkingSlot;
import com.callicoder.goparking.domain.Ticket;
import com.callicoder.goparking.exceptions.CarAlreadyExistsException;
import com.callicoder.goparking.exceptions.InvalidLeaveSlotException;
import com.callicoder.goparking.exceptions.ParkingLotFullException;
import com.callicoder.goparking.exceptions.SlotNotFoundException;
import com.callicoder.goparking.utils.StringUtils;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ParkingLotCommandHandler {

    private ParkingLot parkingLot;

    public void createParkingLot(int numSlots) {
        if (isParkingLotCreated()) {
            System.out.println(PARKING_LOT_ALREADY_CREATED);
            return;
        }

        try {
            parkingLot = new ParkingLot(numSlots);
            System.out.println(
                String.format(PARKING_LOT_CREATED_MSG, parkingLot.getNumSlots())
            );
        } catch (IllegalArgumentException ex) {
            System.out.println("Bad input: " + ex.getMessage());
        }
    }

    public void park(String registrationNumber, String color) {
        if (!isParkingLotCreated()) {
            System.out.println(PARKING_LOT_NOT_CREATED);
            return;
        }
        //TODO: VALIDATION FOR DUPLICATE VEHICLE
        try {
            Car car = new Car(registrationNumber, color);
            Ticket ticket = parkingLot.reserveSlot(car);
            System.out.println(
                String.format(
                    PARKING_SLOT_ALLOCATED_MSG,
                    ticket.getSlotNumber()
                )
            );
        } catch (IllegalArgumentException ex) {
            System.out.println("Bad input: " + ex.getMessage());
        } catch (ParkingLotFullException ex) {
            System.out.println(ex.getMessage());
        } catch (CarAlreadyExistsException ex){
            System.out.println(DUPLICATE_VEHICLE_MESSAGE);
        }
    }

    public void leave(int slotNumber){
        if (!isParkingLotCreated()) {
            System.out.println(PARKING_LOT_NOT_CREATED);
            return;
        }
        try{
            ParkingSlot parkingSlot = parkingLot.leaveSlot(slotNumber);
            System.out.println("Slot number " + slotNumber + " is free");
        } catch (InvalidLeaveSlotException e) {
            System.out.println(e.getMessage());
        } catch (SlotNotFoundException e){
            System.out.println(e.getMessage());
        }

    }

    public void getRegistrationNumbersByColor(String color){
        if (!isParkingLotCreated()) {
            System.out.println(PARKING_LOT_NOT_CREATED);
            return;
        }
        List<String> registrationNumbers = parkingLot.getRegistrationNumbersByColor(color);
        if(!registrationNumbers.isEmpty())
            System.out.println(String.join(", ",registrationNumbers));
        else
            System.out.println(NOT_FOUND);
    }

    public void getSlotNumbersByColor(String color){
        if (!isParkingLotCreated()) {
            System.out.println(PARKING_LOT_NOT_CREATED);
            return;
        }
        List<Integer> slotNumbers = parkingLot.getSlotNumbersByColor(color);
        List<String> slotsInString = slotNumbers.stream().map(Object::toString).collect(Collectors.toList());
        if(!slotNumbers.isEmpty())
            System.out.println(String.join(", ", slotsInString));
        else
            System.out.println(NOT_FOUND);
    }

    public void getSlotNumberForRegistrationNumber(String registrationNumber){
        if (!isParkingLotCreated()) {
            System.out.println(PARKING_LOT_NOT_CREATED);
            return;
        }
        Optional<Integer> slotNumber = parkingLot.getSlotNumberByRegistrationNumber(registrationNumber);
        if(slotNumber.isPresent()){
            System.out.println(slotNumber.get());
        }else {
            System.out.println(NOT_FOUND);
        }

    }

    public void status() {
        if (!isParkingLotCreated()) {
            System.out.println(PARKING_LOT_NOT_CREATED);
            return;
        }

        System.out.println(SLOT_NO + "    " + REGISTRATION_NO + "    " + Color);
        parkingLot
            .getOccupiedSlots()
            .forEach(
                parkingSlot -> {
                    System.out.println(
                        StringUtils.rightPadSpaces(
                            Integer.toString(parkingSlot.getSlotNumber()),
                            SLOT_NO.length()
                        ) +
                        "    " +
                        StringUtils.rightPadSpaces(
                            parkingSlot.getCar().getRegistrationNumber(),
                            REGISTRATION_NO.length()
                        ) +
                        "    " +
                        parkingSlot.getCar().getColor()
                    );
                }
            );
    }

    private boolean isParkingLotCreated() {
        if (parkingLot == null) {
            return false;
        }
        return true;
    }
}
