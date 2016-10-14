package de.qaware.chronix.client.benchmark.resultpresenter;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by mcqueen666 on 10.10.16.
 */
@XmlRootElement
public class TimeSeriesStatistics {

    private String date;
    private List<String> measurements;
    // data set stats
    private long totalSizeInBytes;
    private int numberOfTimeSeries;
    private long numberOfTotalPoints;
    private long minNumberOfPointsPerTimeSeries;
    private long maxNumberOfPointsPerTimeSeries;
    private long averagePointsPerTimeSeries;
    private long medianPointsPerTimeSeries;

    // data value stats
    private double minValueOfAllTimeSeries;
    private double maxValueOfAllTimeSeries;
    private double meanValueofAllTimeSeries;
    private double medianOfAllMeanValues;
    private double meanSampleCovarianceOfAllTimeSeries;
    private double medianSampleCovarianceOfAllTimeSeries;


    public TimeSeriesStatistics(){

    }

    public long getTotalSizeInBytes() {
        return totalSizeInBytes;
    }

    public int getNumberOfTimeSeries() {
        return numberOfTimeSeries;
    }

    public long getNumberOfTotalPoints() {
        return numberOfTotalPoints;
    }

    public long getMinNumberOfPointsPerTimeSeries() {
        return minNumberOfPointsPerTimeSeries;
    }

    public long getMaxNumberOfPointsPerTimeSeries() {
        return maxNumberOfPointsPerTimeSeries;
    }

    public long getAveragePointsPerTimeSeries() {
        return averagePointsPerTimeSeries;
    }

    public long getMedianPointsPerTimeSeries() {
        return medianPointsPerTimeSeries;
    }

    public void setTotalSizeInBytes(long totalSizeInBytes) {
        this.totalSizeInBytes = totalSizeInBytes;
    }

    public void setNumberOfTimeSeries(int numberOfTimeSeries) {
        this.numberOfTimeSeries = numberOfTimeSeries;
    }

    public void setNumberOfTotalPoints(long numberOfTotalPoints) {
        this.numberOfTotalPoints = numberOfTotalPoints;
    }

    public void setMinNumberOfPointsPerTimeSeries(long minNumberOfPointsPerTimeSeries) {
        this.minNumberOfPointsPerTimeSeries = minNumberOfPointsPerTimeSeries;
    }

    public void setMaxNumberOfPointsPerTimeSeries(long maxNumberOfPointsPerTimeSeries) {
        this.maxNumberOfPointsPerTimeSeries = maxNumberOfPointsPerTimeSeries;
    }

    public void setAveragePointsPerTimeSeries(long averagePointsPerTimeSeries) {
        this.averagePointsPerTimeSeries = averagePointsPerTimeSeries;
    }

    public void setMedianPointsPerTimeSeries(long medianPointsPerTimeSeries) {
        this.medianPointsPerTimeSeries = medianPointsPerTimeSeries;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<String> getMeasurements() {
        return measurements;
    }

    public void setMeasurements(List<String> measurements) {
        this.measurements = measurements;
    }

    public double getMinValueOfAllTimeSeries() {
        return minValueOfAllTimeSeries;
    }

    public void setMinValueOfAllTimeSeries(double minValueOfAllTimeSeries) {
        this.minValueOfAllTimeSeries = minValueOfAllTimeSeries;
    }

    public double getMaxValueOfAllTimeSeries() {
        return maxValueOfAllTimeSeries;
    }

    public void setMaxValueOfAllTimeSeries(double maxValueOfAllTimeSeries) {
        this.maxValueOfAllTimeSeries = maxValueOfAllTimeSeries;
    }

    public double getMeanValueofAllTimeSeries() {
        return meanValueofAllTimeSeries;
    }

    public void setMeanValueofAllTimeSeries(double meanValueofAllTimeSeries) {
        this.meanValueofAllTimeSeries = meanValueofAllTimeSeries;
    }

    public double getMedianOfAllMeanValues() {
        return medianOfAllMeanValues;
    }

    public void setMedianOfAllMeanValues(double medianOfAllMeanValues) {
        this.medianOfAllMeanValues = medianOfAllMeanValues;
    }

    public double getMeanSampleCovarianceOfAllTimeSeries() {
        return meanSampleCovarianceOfAllTimeSeries;
    }

    public void setMeanSampleCovarianceOfAllTimeSeries(double meanSampleCovarianceOfAllTimeSeries) {
        this.meanSampleCovarianceOfAllTimeSeries = meanSampleCovarianceOfAllTimeSeries;
    }

    public double getMedianSampleCovarianceOfAllTimeSeries() {
        return medianSampleCovarianceOfAllTimeSeries;
    }

    public void setMedianSampleCovarianceOfAllTimeSeries(double medianSampleCovarianceOfAllTimeSeries) {
        this.medianSampleCovarianceOfAllTimeSeries = medianSampleCovarianceOfAllTimeSeries;
    }
}