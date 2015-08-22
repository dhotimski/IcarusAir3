package com.icarus;


import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

import com.icarus.booking.Booking;
import com.icarus.booking.BookingSystem;
import com.icarus.flights.Offer;
import com.icarus.flights.Quote;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
/**
 * Created by Dmitri on 22.08.2015.
 */
public class BookingServiceTest {
    long confirmationTimeTimestamp = 4444;
    long searchTimeTimestamp =5555;
    BigDecimal priceBeforeSurcharge = new BigDecimal(100);
    BigDecimal totalPrice = new BigDecimal(105);
    String origin = "Rome";
    String destination = "London";
    Offer offer = new Offer(origin + "-" + destination,priceBeforeSurcharge);

    Quote quote = new Quote(offer,searchTimeTimestamp);
    String userAuthToken = "alex";
    Booking booking = new Booking(totalPrice,quote,confirmationTimeTimestamp,userAuthToken );
    BookingService bookingSystem = BookingSystem.getInstance();
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));


    }

    @After
    public void cleanUpStreams() {
        System.setOut(null);
        System.setErr(null);
    }
    @Test
    public void checkBookingSystem(){
        bookingSystem.process(booking);
        String msg = outContent.toString();
        assertThat(msg, allOf(containsString(origin),
                              containsString(destination),
                              containsString(userAuthToken),
                              containsString(String.valueOf(totalPrice)),
                              containsString(String.valueOf(new Date(this.confirmationTimeTimestamp)))) );
    }
}
