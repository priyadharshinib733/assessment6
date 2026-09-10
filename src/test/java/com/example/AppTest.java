package com.example;
import org.junit.Test;
import static org.junit.Assert.*;
public class AppTest {
 @Test
 public void testPriorityValues() {
 assertEquals(
 4,
 EmergencyRequest.priorityValue("Critical"));
 assertEquals(
 3,
 EmergencyRequest.priorityValue("High"));
 assertEquals(
 2,
 EmergencyRequest.priorityValue("Moderate"));
 assertEquals(
 1,
 EmergencyRequest.priorityValue("Normal"));
 }
 @Test
 public void testArrivalTime() {
 assertEquals(
 15.0,
 App.calculateArrivalTime(10.0),
 0.001);
 }
 @Test
 public void testNearestMatchingAmbulanceIsSelected()
 throws Exception {
 App system = new App();
 system.addAmbulance(
 new Ambulance(
 "A1",
 "Basic",
 "Driver1",
 8.0));
 system.addAmbulance(
 new Ambulance(
 "A2",
 "Basic",
 "Driver2",
 3.0));
 EmergencyRequest request =
 new EmergencyRequest(
 "P1",
 "Accident",
 "Hospital",
 "Basic",
 10.0,
 "Critical");
 system.addEmergency(request);
 Ambulance assigned =
 system.allocateNextEmergency();
 assertNotNull(assigned);
 assertEquals("A2", assigned.getId());
 assertEquals(
 "Dispatched",
 assigned.getStatus());
 }
 @Test
 public void testAmbulanceCannotBeAssignedTwice()
 throws Exception {
 App system = new App();
 system.addAmbulance(
 new Ambulance(
 "A1",
 "ICU",
 "Driver1",
 2.0));
 EmergencyRequest first =
 new EmergencyRequest(
 "P1",
 "Heart Attack",
 "Hospital",
 "ICU",
 5.0,
 "Critical");
 EmergencyRequest second =
 new EmergencyRequest(
 "P2",
 "Accident",
 "Hospital",
 "ICU",
 4.0,
 "High");
 system.addEmergency(first);
 system.addEmergency(second);
 Ambulance assigned =
 system.allocateNextEmergency();
 assertEquals("A1", assigned.getId());
 try {
 system.allocateNextEmergency();
 fail("Expected no ambulance to be available");
 } catch (NoAmbulanceAvailableException expected) {
 // Expected exception
 }
 }
 @Test
 public void testAmbulanceStateTransitions() {
 Ambulance ambulance =
 new Ambulance(
 "A1",
 "Basic",
 "Driver",
 1.0);
 ambulance.dispatch();
 assertEquals(
 "Dispatched",
 ambulance.getStatus());
 ambulance.enRoute();
 assertEquals(
 "En Route",
 ambulance.getStatus());
 ambulance.patientPickedUp();
 assertEquals(
 "Patient Picked Up",
 ambulance.getStatus());
 ambulance.hospitalArrived();
 assertEquals(
 "Hospital Arrived",
 ambulance.getStatus());
 ambulance.makeAvailable();
 assertEquals(
 "Available",
 ambulance.getStatus());
 }
 @Test(expected = InvalidEmergencyException.class)
 public void testInvalidEmergency() throws Exception {
 new EmergencyRequest(
 "",
 "Accident",
 "Hospital",
 "Basic",
 5.0,
 "Critical");
 }
 @Test
 public void testAutomaticAllocation()
 throws Exception {
 App system = new App();
 system.addAmbulance(
 new Ambulance(
 "A1",
 "Basic",
 "Driver",
 2.0));
 EmergencyRequest request =
 new EmergencyRequest(
 "P1",
 "Accident",
 "Hospital",
 "Basic",
 5.0,
 "High");
 system.addEmergency(request);
 assertEquals(
 1,
 system.automaticallyAllocateAvailableAmbulances());
 assertEquals(
 0,
 system.getPendingRequests().size());
 }
 @Test
 public void testCriticalEmergencyGetsPriority()
 throws Exception {
 App system = new App();
 system.addAmbulance(
 new Ambulance(
 "A1",
 "Basic",
 "Driver",
 2.0));
 EmergencyRequest normal =
 new EmergencyRequest(
 "P1",
 "Accident",
 "Hospital",
 "Basic",
 1.0,
 "Normal");
 EmergencyRequest critical =
 new EmergencyRequest(
 "P2",
 "Heart Attack",
 "Hospital",
 "Basic",
 10.0,
 "Critical");
 system.addEmergency(normal);
 system.addEmergency(critical);
 Ambulance assigned =
 system.allocateNextEmergency();
 assertEquals("A1", assigned.getId());
 assertEquals(
 1,
 system.getPendingRequests().size());
 assertEquals(
 "P1",
 system.getPendingRequests()
 .get(0)
 .getPatientId());
 }
 @Test
 public void testNoAmbulanceForWrongType()
 throws Exception {
 App system = new App();
 system.addAmbulance(
 new Ambulance(
 "A1",
 "Basic",
 "Driver",
 2.0));
 EmergencyRequest request =
 new EmergencyRequest(
 "P1",
 "Heart Attack",
 "Hospital",
 "ICU",
 5.0,
 "Critical");
 system.addEmergency(request);
 try {
 system.allocateNextEmergency();
 fail("Expected NoAmbulanceAvailableException");
 } catch (NoAmbulanceAvailableException expected) {
 // Expected exception
 }
 assertEquals(
 1,
 system.getPendingRequests().size());
 }
}
