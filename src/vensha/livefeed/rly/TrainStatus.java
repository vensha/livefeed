package vensha.livefeed.rly;

import vensha.livefeed.Entity;

public class TrainStatus extends Entity {
public String trainNo;
public String getTrainNo() {
	return trainNo;
}

public void setTrainNo(String trainNo) {
	this.trainNo = trainNo;
}

public String getStartDate() {
	return startDate;
}

public void setStartDate(String startDate) {
	this.startDate = startDate;
}

public String getTrainName() {
	return trainName;
}

public void setTrainName(String trainName) {
	this.trainName = trainName;
}

public String getTrainSource() {
	return trainSource;
}
public void setTrainSource(String trainSource) {
	this.trainSource = trainSource;
}

public String getTrainDestination() {
	return trainDestination;
}

public void setTrainDestination(String trainDestination) {
	this.trainDestination = trainDestination;
}

public String getTrainType() {
	return trainType;
}

public void setTrainType(String trainType) {
	this.trainType = trainType;
}

public String getEventType() {
	return eventType;
}

public void setEventType(String eventType) {
	this.eventType = eventType;
}

public String getCancelledFrom() {
	return cancelledFrom;
}

public void setCancelledFrom(String cancelledFrom) {
	this.cancelledFrom = cancelledFrom;
}

/**
 * @return the cancelledTo
 */
public String getCancelledTo() {
	return cancelledTo;
}

/**
 * @param cancelledTo the cancelledTo to set
 */
public void setCancelledTo(String cancelledTo) {
	this.cancelledTo = cancelledTo;
}

/**
 * @return the divertedFrom
 */
public String getDivertedFrom() {
	return divertedFrom;
}

/**
 * @param divertedFrom the divertedFrom to set
 */
public void setDivertedFrom(String divertedFrom) {
	this.divertedFrom = divertedFrom;
}

/**
 * @return the divertedTo
 */
public String getDivertedTo() {
	return divertedTo;
}

/**
 * @param divertedTo the divertedTo to set
 */
public void setDivertedTo(String divertedTo) {
	this.divertedTo = divertedTo;
}

/**
 * @return the rescheduledBy
 */
public String getRescheduledBy() {
	return rescheduledBy;
}

/**
 * @param rescheduledBy the rescheduledBy to set
 */
public void setRescheduledBy(String rescheduledBy) {
	this.rescheduledBy = rescheduledBy;
}

/**
 * @return the rescheduledTime
 */
public String getRescheduledTime() {
	return rescheduledTime;
}

/**
 * @param rescheduledTime the rescheduledTime to set
 */
public void setRescheduledTime(String rescheduledTime) {
	this.rescheduledTime = rescheduledTime;
}

public String startDate="na";
public String trainName="na";
public String trainSource="na";
public String trainDestination="na";
public String trainType="na";    
public String eventType="na" ; //Cancelled|Rescheduled|Diverted"  
//if partially cancelled
public String cancelledFrom="na";
public String cancelledTo="na";
//if diverted
public String divertedFrom="na";
public String divertedTo="na";
//if rescheduled, you will see these additiona attributes   
public String rescheduledBy="na";  
public String rescheduledTime="na";


public static final String FULLY_CANCELLED = "Fully_Cancelled";
public static final String PARTIALLY_CANCELLED = "Partially_Cancelled";
public static final String RESCHEDULED = "Rescheduled";
public static final String DIVERTED = "Diverted";


@Override
public String getId() {
	return trainNo + "_" + startDate + "_" + eventType;
}

@Override
public int hashCode() {
	return getId().hashCode();
}

@Override
public boolean equals(Object other) {
	if (other instanceof TrainStatus) {
	   return getId().equals(((TrainStatus)other).getId());
	}else{
		return false;
	}
}




}
