package in.antara.antara;

/**
 * Created by vermap on 8/29/2017.
 */
public class DistanceUtility {

    private static final float KNOWNSIDE = 500.0f ; //mm . 19.68 inch
    private static final float SENSORHEIGHT = 3.42f; //# in mm

    public Double calculateDistanceInMM(float focalLength,int fullImageHeight,Double squareSideHeight){
        try{
            Double distanceInMM = (focalLength * KNOWNSIDE * fullImageHeight) / (squareSideHeight * SENSORHEIGHT);
            return distanceInMM;
        }

        catch(Exception ex){
            ex.printStackTrace();
            return 0.0;
        }
    }

    public Double convertDistanceFromMMToInches(Double distanceInMM){
        try{
            Double distanceInInches = distanceInMM * 0.0393701f;
            return distanceInInches;
        }

        catch(Exception ex){
            ex.printStackTrace();
            return 0.0;
        }
    }

    public Double convertDistanceFromInchesToCM(Double distanceInInches){
        try{
            Double distanceInCM = distanceInInches * 2.54f;
            return distanceInCM;
        }

        catch(Exception ex){
            ex.printStackTrace();
            return 0.0;
        }
    }

    public Double calculateDistanceBetween2Points(Double x1,Double y1,Double x2,Double y2){
        try{
            Double distance;
            distance = Math.sqrt(Math.pow((x2-x1),2) + Math.pow((y2-y1),2));

            return distance;
        }
        catch(Exception ex){
            ex.printStackTrace();
            return 0.0;
        }
    }


}
