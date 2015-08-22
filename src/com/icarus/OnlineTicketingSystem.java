package com.icarus;

import com.icarus.booking.Booking;
import com.icarus.booking.BookingSystem;
import com.icarus.flights.Offer;
import com.icarus.flights.Quote;
import com.icarus.flights.FlightDatabase;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.util.*;

public class OnlineTicketingSystem implements TicketService {

    private static final long MAX_QUOTE_AGE_MILLIS = 20 * 60 * 1000;

    private ChargeAlgorithm chargeAlgorithm =new FixedChargeAlgorithm();
    private Booking completeBooking;
    private BookingFactory bookingCreator = new BookingFactoryImpl() ;

    private Clock searchTicketsTimer = Clock.systemDefaultZone();
    private Clock confirmTicketsTimer = Clock.systemDefaultZone();
    private BookingService bookingService = BookingSystem.getInstance();

    public FlightAvailability getDatabase() {
        return database;
    }

    public void setDatabase(FlightAvailability database) {
        this.database = database;
    }

    private FlightAvailability database= FlightDatabase.getInstance();

    private Map<UUID, Quote> quotes = new HashMap<UUID, Quote>();

    @Override
    public List<Offer> searchForTickets(String origin, String destination) {

        List<Offer> searchResults = database.searchFor(origin, destination);
        recordSearchResults(searchResults);
        return searchResults;
    }

    private void recordSearchResults(List<Offer> searchResults) {
        for (Offer offer : searchResults) {
            quotes.put(offer.id, new Quote(offer, searchTicketsTimer.millis()));
        }
    }

    @Override
    public void confirmBooking(UUID id, String userAuthToken) {

        if (!quotes.containsKey(id)) {
            throw new NoSuchElementException("Offer ID is invalid");
        }

        Quote quote = quotes.get(id);
        long timeNow = confirmTicketsTimer.millis();
        long elapsedTime = timeNow - quote.timestamp;
        if ( elapsedTime> MAX_QUOTE_AGE_MILLIS) {
            throw new IllegalStateException("Quote expired, please get a new price");
        }

        BigDecimal offerPrice = quote.offer.price;
        BigDecimal processingCharge   = chargeAlgorithm.processingCharge(elapsedTime, offerPrice);
        BigDecimal totalPrice = offerPrice.add(processingCharge).setScale(2, RoundingMode.HALF_UP);

        completeBooking = bookingCreator.createBooking(totalPrice, quote, timeNow, userAuthToken);

        bookingService.process(completeBooking);
    }


    public Booking getCompleteBooking() {
        return completeBooking;
    }

    public void setCompleteBooking(Booking completeBooking) {
        this.completeBooking = completeBooking;
    }
    public BookingService getBookingService() {
        return bookingService;
    }

    public void setBookingService(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    public BookingFactory getBookingCreator() {
        return bookingCreator;
    }

    public void setBookingCreator(BookingFactory bookingCreator) {
        this.bookingCreator = bookingCreator;
    }

    public ChargeAlgorithm getChargeAlgorithm() {
        return chargeAlgorithm;
    }

    public void setChargeAlgorithm(ChargeAlgorithm chargeAlgorithm) {
        this.chargeAlgorithm = chargeAlgorithm;
    }
    public Clock getConfirmTicketsTimer() {
        return confirmTicketsTimer;
    }

    public void setConfirmTicketsTimer(Clock confirmTicketsTimer) {
        this.confirmTicketsTimer = confirmTicketsTimer;
    }

    public Clock getSearchTicketsTimer() {
        return searchTicketsTimer;
    }

    public void setSearchTicketsTimer(Clock searchTicketsTimer) {
        this.searchTicketsTimer = searchTicketsTimer;
    }

    public Map<UUID, Quote> getQuotes() {
        return quotes;
    }

    public void setQuotes(Map<UUID, Quote> quotes) {
        this.quotes = quotes;
    }
}