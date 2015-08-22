package com.icarus;

import com.icarus.booking.Booking;
import com.icarus.flights.Offer;
import com.icarus.flights.Quote;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.Duration;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;

/**
 * Created by Dmitri on 22.08.2015.
 */
public class IcarusAirTest {

    @Rule
    public JUnitRuleMockery context = new JUnitRuleMockery();
    BookingFactory bookingFactory = context.mock(BookingFactory.class);
    BookingService stubBookingSystem = context.mock(BookingService.class);
    OnlineTicketingSystem ticketingSystem = new OnlineTicketingSystem();
    String userAuthToken = "tom@example.com";
    private String origin;
    private String destination;
    Quote quote;
    public BigDecimal ticketPriceBeforeSurcharge;
    public long timeOffer;
    public Offer offer;
    public long timeConfirmation;
    long timeElapsed;
    BigDecimal processingChargeExpected;
    BigDecimal totalPriceExpected;
    Date date;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    public void prepareData(List<Offer> searchResults, long timeBeforeConfirmationInMinutes) {
        offer = searchResults.get(0);
        String[] str = offer.toString().split("[:-]+");
        origin = extractCityName(str[1]);
        destination = extractCityName(str[2]);
        quote = ticketingSystem.getQuotes().get(offer.id);

        ticketPriceBeforeSurcharge = offer.price;

        timeOffer = quote.timestamp;
        Duration duration = Duration.ofMinutes(timeBeforeConfirmationInMinutes);
        Clock newClock = Clock.offset(Clock.systemDefaultZone(), duration);

        ticketingSystem.setConfirmTicketsTimer(Clock.fixed(newClock.instant(), ZoneId.systemDefault()));
        timeConfirmation = ticketingSystem.getConfirmTicketsTimer().millis();
        timeElapsed = timeConfirmation - timeOffer;


        date = new Date(timeConfirmation);
    }



    public void calculateTotalPrice() {
        totalPriceExpected = ticketPriceBeforeSurcharge.add(processingChargeExpected).setScale(2, RoundingMode.HALF_UP);// how to get it?
    }

    private static boolean priceAcceptable(BigDecimal price) {
        return true;
    }

    private String extractCityName(String str) {
        return str.substring(1, str.length() - 1);
    }

    public void checkExpectations() {
        setExpectations();
        ticketingSystem.confirmBooking(offer.id, userAuthToken);
    }
    public void setExpectations() {
        context.checking(new Expectations() {
                             {
                                 oneOf(bookingFactory).createBooking(totalPriceExpected, quote, timeConfirmation, userAuthToken);
                                 oneOf(stubBookingSystem).process(ticketingSystem.getCompleteBooking());
                             }
                         }
        );
    }
    public void validateResultProgressiveAlgorithmStubBookingService(int timeBeforeConfirmationInMinutes) {
        List<Offer> searchResults = ticketingSystem.searchForTickets("", "");
        prepareData(searchResults, timeBeforeConfirmationInMinutes);
        if (timeBeforeConfirmationInMinutes <= 2) {
            processingChargeExpected = new BigDecimal(0);
        } else if (timeBeforeConfirmationInMinutes <= 10) {
            processingChargeExpected = new BigDecimal(10).min(ticketPriceBeforeSurcharge.multiply(new BigDecimal("0.05")).setScale(2, RoundingMode.HALF_UP));
        } else processingChargeExpected = new BigDecimal(20);
        calculateTotalPrice();
       checkExpectations();
    }

    public void validateResultProgressiveAlgorithmRealBookingService(int timeBeforeConfirmationInMinutes) {
        List<Offer> searchResults = ticketingSystem.searchForTickets("", "");
        prepareData(searchResults, timeBeforeConfirmationInMinutes);
        if (timeBeforeConfirmationInMinutes <= 2) {
            processingChargeExpected = new BigDecimal(0);
        } else if (timeBeforeConfirmationInMinutes <= 10) {
            processingChargeExpected = new BigDecimal(10).min(ticketPriceBeforeSurcharge.multiply(new BigDecimal("0.05")).setScale(2, RoundingMode.HALF_UP));
        } else processingChargeExpected = new BigDecimal(20);
        calculateTotalPrice();

        setUpStreams();
       ticketingSystem.confirmBooking(offer.id,userAuthToken);
        checkBookingSystem();
        cleanUpStreams();

        }




    public void cleanUpStreams() {
        System.setOut(null);
        System.setErr(null);
    }
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));

}
    public void checkBookingSystem(){

        String msg = outContent.toString();
        assertThat(msg, allOf(containsString(origin),
                containsString(destination),
                containsString(userAuthToken),
                containsString(String.valueOf(totalPriceExpected)),
                containsString(String.valueOf(new Date(this.timeConfirmation)))) );
    }}
