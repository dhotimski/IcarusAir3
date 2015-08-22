package com.icarus;

import com.icarus.flights.Offer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Dmitri on 22.08.2015.
 */
public class FakeDatabase implements FlightAvailability {

        private final List<Ticket> tickets = new ArrayList();

        @Override
        public List<Offer> searchFor(String origin, String destination) {
            ArrayList results = new ArrayList();
            Iterator i$ = this.tickets.iterator();

            while(i$.hasNext()) {
                FakeDatabase.Ticket ticket = (FakeDatabase.Ticket)i$.next();
                if(ticket.hasOrigin(origin) && ticket.hasDestination(destination)) {
                    results.add(new Offer(ticket.origin + " - " + ticket.destination, ticket.unitPrice));
                }
            }

            return results;
        }



        public FakeDatabase() {
            this.addAllTickets();
        }




        private void addAllTickets() {
            this.tickets.add(new FakeDatabase.Ticket("London Heathrow", "Milan", "134.00"));
            this.tickets.add(new FakeDatabase.Ticket("London Heathrow", "New York JFK", "562.50"));
            this.tickets.add(new FakeDatabase.Ticket("London Heathrow", "San Francisco", "771.10"));

        }

        static class Ticket {
            private final String origin;
            private final String destination;
            private final BigDecimal unitPrice;

            public Ticket(String origin, String destination, String unitPrice) {
                this.origin = origin;
                this.destination = destination;
                this.unitPrice = new BigDecimal(unitPrice);
            }

            public boolean equals(Object o) {
                if(this == o) {
                    return true;
                } else if(o != null && this.getClass() == o.getClass()) {
                    FakeDatabase.Ticket ticket = (FakeDatabase.Ticket)o;
                    return !this.origin.equals(ticket.origin)?false:(!this.destination.equals(ticket.destination)?false:this.unitPrice.equals(ticket.unitPrice));
                } else {
                    return false;
                }
            }

            public int hashCode() {
                int result = this.origin.hashCode();
                result = 31 * result + this.destination.hashCode();
                result = 31 * result + this.unitPrice.hashCode();
                return result;
            }

            public String toString() {
                return "Ticket : " + this.origin + " ==> " + this.destination + " £" + this.unitPrice;
            }

            public boolean hasOrigin(String origin) {
                return this.origin.toLowerCase().contains(origin.toLowerCase());
            }

            public boolean hasDestination(String destination) {
                return this.destination.toLowerCase().contains(destination.toLowerCase());
            }
        }
    }

