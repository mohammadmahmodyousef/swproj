package com.vrms.notification;

import com.vrms.domain.Rental;

public interface NotificationService {

    void sendRentalAccepted(Rental rental,String message);

    void sendExpiryReminder(Rental rental,String message);

    void sendRentalExpired(Rental rental,String message);

    void sendRentalExtended(Rental rental,String message);

    void sendRentalReturned(Rental rental,String message);
}