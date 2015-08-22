package com.icarus;

import com.icarus.*;
import com.icarus.flights.Offer;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.jmock.Expectations;
import java.time.Instant;
import com.icarus.flights.Quote;
import org.junit.*;
import sun.security.jca.GetInstance;

import java.math.BigDecimal;

import java.math.RoundingMode;
import java.time.Clock;
import java.time.Duration;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;


import static org.junit.Assert.assertTrue;

/**
 * Created by Dmitri on 19.08.2015.
 */
public class FixedChargeAlgorithm_RealDatabase_StubBookingService_FastTime_Test extends IcarusAirTest {

    @Before
    public void setUp() {

        ticketingSystem.setBookingService(stubBookingSystem);
        ticketingSystem.setBookingCreator(bookingFactory);
    }


    @Test
    public void try_to_confirm_tickets_for_nonExistent_flight() throws Exception {
        List<Offer> searchResults = ticketingSystem.searchForTickets("Moscow", "N");
        assertTrue(searchResults.isEmpty()) ;
    }

    @Test(expected=IllegalStateException.class)
    public void confirm_tickets_after_timeout() throws Exception {

        List<Offer> searchResults = ticketingSystem.searchForTickets("", "");
        prepareData(searchResults,21);

        ticketingSystem.confirmBooking(offer.id, userAuthToken);
    }



   @Test
    public void confirm_tickets_within_time_limit() throws Exception {
       List<Offer> searchResults = ticketingSystem.searchForTickets("", "");
       prepareData(searchResults,9);
       processingChargeExpected = ProgressiveChargeAlgorithm.STANDARD_PROCESSING_CHARGE;
       calculateTotalPrice();
       checkExpectations();

    }


    private static boolean priceAcceptable(BigDecimal price) {
        return true;
    }

}



