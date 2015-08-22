package com.icarus;

import com.icarus.booking.Booking;
import com.icarus.flights.Quote;

import java.math.BigDecimal;

/**
 * Created by Dmitri on 14.08.2015.
 */
public interface BookingFactory {
    public Booking createBooking(BigDecimal totalPrice, Quote quote, long timeNow, String userAuthToken);
}

