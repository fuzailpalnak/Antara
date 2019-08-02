package in.antara.antara;

public class DistanceUtility {

    private static final float KNOWNSIDE = 500.0f ; //mm . 19.68 inch
    private static final float SENSORHEIGHT = 3.42f; //# in mm
    private static final float FOCALLENGTH = 3.57f; //mm

    // To get sensor height
    // https://stackoverflow.com/questions/8104252/how-to-get-camera-sensor-size-in-android-device
    // https://stackoverflow.com/questions/8921502/how-to-get-sensor-size-on-android/32012448

    public Double calculateDistanceInMM(int fullImageHeight,Double squareSideHeight){
        try{
            return (FOCALLENGTH * KNOWNSIDE * fullImageHeight) / (squareSideHeight * SENSORHEIGHT);
        }

        catch(Exception ex){
            ex.printStackTrace();
            return 0.0;
        }
    }

    public Double convertDistanceFromMMToInches(Double distanceInMM){
        try{
            return distanceInMM * 0.0393701f;
        }

        catch(Exception ex){
            ex.printStackTrace();
            return 0.0;
        }
    }

    public Double convertDistanceFromInchesToCM(Double distanceInInches){
        try{
            return distanceInInches * 2.54f;
        }

        catch(Exception ex){
            ex.printStackTrace();
            return 0.0;
        }
    }

    public Double calculateDistanceBetween2Points(Double x1,Double y1,Double x2,Double y2){
        try{
            Double distance;
            distance = Math.sqrt(Math.pow((Math.abs(x2-x1)),2) + Math.pow((Math.abs(y2-y1)),2));

            return distance;
        }
        catch(Exception ex){
            ex.printStackTrace();
            return 0.0;
        }
    }


}
