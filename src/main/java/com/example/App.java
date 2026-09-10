package com.example;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
class InvalidEmergencyException extends Exception {
 public InvalidEmergencyException(String message) {
 super(message);
 }
}
class NoAmbulanceAvailableException extends Exception {
 public NoAmbulanceAvailableException(String message) {
 super(message);
 }
}
class Ambulance {
 private final String id;
 private final String type;
 private final String driver;
 private double distance;
 private String status;
 public Ambulance(String id, String type, String driver, double distance) {
 if (id == null || id.trim().isEmpty()) {
 throw new IllegalArgumentException("Ambulance ID cannot be empty");
 }
 if (type == null || type.trim().isEmpty()) {
 throw new IllegalArgumentException("Ambulance type cannot be empty");
 }
 if (driver == null || driver.trim().isEmpty()) {
 throw new IllegalArgumentException("Driver details cannot be empty");
 }
 if (distance < 0) {
 throw new IllegalArgumentException("Distance cannot be negative");
 }
 this.id = id;
 this.type = type;
 this.driver = driver;
 this.distance = distance;
 this.status = "Available";
 }
 public String getId() {
 return id;
 }
 public String getType() {
 return type;
 }
 public String getDriver() {
 return driver;
 }
 public double getDistance() {
 return distance;
 }
 public String getStatus() {
 return status;
 }
 public boolean isAvailable() {
 return "Available".equals(status);
 }
 public void setDistance(double distance) {
 if (distance < 0) {
 throw new IllegalArgumentException("Distance cannot be negative");
 }
 this.distance = distance;
 }
 public void dispatch() {
 status = "Dispatched";
 }
 public void enRoute() {
 status = "En Route";
 }
 public void patientPickedUp() {
 status = "Patient Picked Up";
 }
 public void hospitalArrived() {
 status = "Hospital Arrived";
 }
 public void makeAvailable() {
 status = "Available";
 }
}
class EmergencyRequest {
 private final String patientId;
 private final String emergencyType;
 private final String hospital;
 private final String ambulanceType;
 private final double distance;
 private final String priority;
 public EmergencyRequest(String patientId, String emergencyType,
 String hospital, String ambulanceType,
 double distance, String priority)
 throws InvalidEmergencyException {
 if (patientId == null || patientId.trim().isEmpty()
 || emergencyType == null || emergencyType.trim().isEmpty()
 || hospital == null || hospital.trim().isEmpty()
 || ambulanceType == null || ambulanceType.trim().isEmpty()) {
 throw new InvalidEmergencyException(
 "Required emergency information is missing");
 }
 if (distance < 0) {
 throw new InvalidEmergencyException(
 "Emergency distance cannot be negative");
 }
 if (!isValidPriority(priority)) {
 throw new InvalidEmergencyException(
 "Priority must be Critical, High, Moderate, or Normal");
 }
 if (!isValidAmbulanceType(ambulanceType)) {
 throw new InvalidEmergencyException(
 "Ambulance type must be Basic, Advanced, or ICU");
 }
 this.patientId = patientId;
 this.emergencyType = emergencyType;
 this.hospital = hospital;
 this.ambulanceType = ambulanceType;
 this.distance = distance;
 this.priority = priority;
 }
 public String getPatientId() {
 return patientId;
 }
 public String getEmergencyType() {
 return emergencyType;
 }
 public String getHospital() {
 return hospital;
 }
 public String getAmbulanceType() {
 return ambulanceType;
 }
 public double getDistance() {
 return distance;
 }
 public String getPriority() {
 return priority;
 }
 public static boolean isValidPriority(String priority) {
 if (priority == null) {
 return false;
 }
 String value = priority.trim().toLowerCase();
 return value.equals("critical")
 || value.equals("high")
 || value.equals("moderate")
 || value.equals("normal");
 }
 public static boolean isValidAmbulanceType(String type) {
 if (type == null) {
 return false;
 }
 String value = type.trim().toLowerCase();
 return value.equals("basic")
 || value.equals("advanced")
 || value.equals("icu");
 }
 public static int priorityValue(String priority) {
 switch (priority.trim().toLowerCase()) {
 case "critical":
 return 4;
 case "high":
 return 3;
 case "moderate":
 return 2;
 case "normal":
 return 1;
 default:
 throw new IllegalArgumentException("Invalid priority");
 }
 }
}
public class App {
 private final List<Ambulance> ambulances =
 new ArrayList<Ambulance>();
 private final List<EmergencyRequest> pendingRequests =
 new ArrayList<EmergencyRequest>();
 private final List<String> emergencyHistory =
 new ArrayList<String>();
 public void addAmbulance(Ambulance ambulance) {
 if (ambulance == null) {
 throw new IllegalArgumentException("Ambulance cannot be null");
 }
 ambulances.add(ambulance);
 }
 public List<Ambulance> getAmbulances() {
 return Collections.unmodifiableList(ambulances);
 }
 public List<EmergencyRequest> getPendingRequests() {
 return Collections.unmodifiableList(pendingRequests);
 }
 public List<String> getEmergencyHistory() {
 return Collections.unmodifiableList(emergencyHistory);
 }
 public void addEmergency(EmergencyRequest request) {
 if (request == null) {
 throw new IllegalArgumentException("Emergency request cannot be null");
 }
 pendingRequests.add(request);
 sortRequestsByPriority();
 emergencyHistory.add(
 "Emergency " + request.getPatientId()
 + " registered - "
 + request.getPriority());
 }
 private void sortRequestsByPriority() {
 Collections.sort(
 pendingRequests,
 new Comparator<EmergencyRequest>() {
 @Override
 public int compare(EmergencyRequest a,
 EmergencyRequest b) {
 int priorityComparison =
 Integer.compare(
 EmergencyRequest.priorityValue(
 b.getPriority()),
 EmergencyRequest.priorityValue(
 a.getPriority()));
 if (priorityComparison != 0) {
 return priorityComparison;
 }
 return Double.compare(
 a.getDistance(),
 b.getDistance());
 }
 });
 }
 public Ambulance allocateNextEmergency()
 throws NoAmbulanceAvailableException {
 sortRequestsByPriority();
 for (int i = 0; i < pendingRequests.size(); i++) {
 EmergencyRequest request = pendingRequests.get(i);
 Ambulance bestAmbulance =
 findBestAmbulance(request);
 if (bestAmbulance != null) {
 bestAmbulance.dispatch();
 pendingRequests.remove(i);
 emergencyHistory.add(
 "Patient " + request.getPatientId()
 + " assigned to ambulance "
 + bestAmbulance.getId());
 return bestAmbulance;
 }
 }
 throw new NoAmbulanceAvailableException(
 "No suitable ambulance is currently available");
 }
 private Ambulance findBestAmbulance(
 EmergencyRequest request) {
 Ambulance best = null;
 for (Ambulance ambulance : ambulances) {
 if (!ambulance.isAvailable()) {
 continue;
 }
 if (!ambulance.getType().equalsIgnoreCase(
 request.getAmbulanceType())) {
 continue;
 }
 if (best == null
 || ambulance.getDistance()
 < best.getDistance()) {
 best = ambulance;
 }
 }
 return best;
 }
 public int automaticallyAllocateAvailableAmbulances() {
 int allocatedCount = 0;
 while (!pendingRequests.isEmpty()) {
 try {
 allocateNextEmergency();
 allocatedCount++;
 } catch (NoAmbulanceAvailableException e) {
 break;
 }
 }
 return allocatedCount;
 }
 public static double calculateArrivalTime(double distanceKm) {
 if (distanceKm < 0) {
 throw new IllegalArgumentException(
 "Distance cannot be negative");
 }
 double averageSpeedKmPerHour = 40.0;
 return (distanceKm / averageSpeedKmPerHour) * 60.0;
 }
 public static void main(String[] args) {
 App system = new App();
 system.addAmbulance(
 new Ambulance(
 "AMB001",
 "Basic",
 "Ravi",
 4.0));
 system.addAmbulance(
 new Ambulance(
 "AMB002",
 "Advanced",
 "Kumar",
 7.0));
 system.addAmbulance(
 new Ambulance(
 "AMB003",
 "ICU",
 "Arun",
 3.0));
 Scanner scanner = new Scanner(System.in);
 System.out.println(
 "===== EMERGENCY AMBULANCE DISPATCH SYSTEM =====");
 System.out.print("Enter Patient ID: ");
 String patientId = scanner.nextLine();
 System.out.print("Enter Emergency Type: ");
 String emergencyType = scanner.nextLine();
 System.out.print("Enter Hospital: ");
 String hospital = scanner.nextLine();
 System.out.print(
 "Enter Ambulance Type (Basic/Advanced/ICU): ");
 String ambulanceType = scanner.nextLine();
 System.out.print("Enter Emergency Distance (km): ");
 double distance = scanner.nextDouble();
 scanner.nextLine();
 System.out.print(
 "Enter Priority (Critical/High/Moderate/Normal): ");
 String priority = scanner.nextLine();
 try {
 EmergencyRequest request =
 new EmergencyRequest(
 patientId,
 emergencyType,
 hospital,
 ambulanceType,
 distance,
 priority);
 system.addEmergency(request);
 Ambulance assigned =
 system.allocateNextEmergency();
 System.out.println();
 System.out.println("===== EMERGENCY DETAILS =====");
 System.out.println(
 "Patient ID : "
 + request.getPatientId());
 System.out.println(
 "Emergency Type : "
 + request.getEmergencyType());
 System.out.println(
 "Hospital : "
 + request.getHospital());
 System.out.println(
 "Priority : "
 + request.getPriority());
 System.out.println(
 "Distance : "
 + request.getDistance()
 + " km");
 System.out.println();
 System.out.println(
 "===== AMBULANCE ALLOCATED =====");
 System.out.println(
 "Ambulance ID : "
 + assigned.getId());
 System.out.println(
 "Ambulance Type : "
 + assigned.getType());
 System.out.println(
 "Driver : "
 + assigned.getDriver());
 System.out.println(
 "Status : "
 + assigned.getStatus());
 System.out.printf(
 "Estimated Arrival: %.2f minutes%n",
 calculateArrivalTime(
 request.getDistance()));
 } catch (InvalidEmergencyException e) {
 System.out.println(
 "ERROR: " + e.getMessage());
 } catch (NoAmbulanceAvailableException e) {
 System.out.println(
 "ERROR: " + e.getMessage());
 } finally {
 scanner.close();
 }
 }
}
