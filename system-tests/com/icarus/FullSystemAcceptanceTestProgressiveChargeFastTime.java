package com.icarus;

import com.icarus.flights.Offer;
import org.junit.Before;
import org.junit.Test;

import java.time.Clock;
import java.time.Duration;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created by Dmitri on 22.08.2015.
 */
public class FullSystemAcceptanceTestProgressiveChargeFastTime extends IcarusAirTest {


    @Before
    public void setUp() {

        ticketingSystem.setChargeAlgorithm(new ProgressiveChargeAlgorithm());

    }


    @Test
    public void try_to_confirm_tickets_for_nonExistent_flight() throws Exception {
        List<Offer> searchResults = ticketingSystem.searchForTickets("Moscow", "N");
        assertTrue(searchResults.isEmpty());
    }


    @Test(expected = IllegalStateException.class)
    public void confirm_tickets_after_timeout() throws Exception {

        List<Offer> searchResults = ticketingSystem.searchForTickets("", "");
        Offer offer = searchResults.get(0);

        ticketingSystem.setConfirmTicketsTimer(Clock.offset(Clock.systemDefaultZone(), Duration.ofMinutes(21)));

        ticketingSystem.confirmBooking(offer.id, userAuthToken);
    }


    @Test
    public void confirm_tickets_within_fast_time_limit() throws Exception {
        validateResultProgressiveAlgorithmRealBookingService(1);
    }

    @Test
    public void confirm_tickets_within_average_time_limit() throws Exception {
        validateResultProgressiveAlgorithmRealBookingService(5);
    }

    @Test
    public void confirm_tickets_within_slow_time_limit() throws Exception {
        validateResultProgressiveAlgorithmRealBookingService(15);
    }


}
