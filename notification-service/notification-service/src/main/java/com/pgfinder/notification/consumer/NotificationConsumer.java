package com.pgfinder.notification.consumer;

import com.pgfinder.notification.event.BookingEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class NotificationConsumer {

    @KafkaListener(topics = "booking-events", groupId = "notification-group")
    public void consumeBookingEvent(BookingEvent event) {
        System.out.println("\n=======================================================");
        System.out.println("📩 [KAFKA EVENT CONSUMED] Topic: booking-events");
        System.out.println("Booking ID  : #" + event.getBookingId());
        System.out.println("Status      : " + event.getStatus());
        System.out.println("User ID     : " + event.getUserId());
        System.out.println("Bed ID      : " + event.getBedId());
        System.out.println("Dates       : " + event.getCheckInDate() + " to " + event.getCheckOutDate());

        if ("PENDING".equalsIgnoreCase(event.getStatus())) {
            System.out.println("📱 [SMS DISPATCHED]: Booking request received. Status: PENDING confirmation.");
        } else if ("CONFIRMED".equalsIgnoreCase(event.getStatus())) {
            System.out.println("🎉 [WHATSAPP DISPATCHED]: Your booking for Bed #" + event.getBedId() + " is CONFIRMED!");
            System.out.println("📧 [EMAIL TO OWNER]: New booking confirmed for Bed #" + event.getBedId());
        } else if ("CANCELLED".equalsIgnoreCase(event.getStatus())) {
            System.out.println("⚠️ [SMS DISPATCHED]: Booking #" + event.getBookingId() + " has been CANCELLED.");
        }
        System.out.println("=======================================================\n");
    }
}