/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imagethreshncrop;
import java.net.URL;
import org.opencv.core.*;
/*import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Size;
*/
import java.io.*;

/*import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileReader;
*/
import java.util.ArrayList;
import static org.opencv.highgui.Highgui.imread;
import static org.opencv.highgui.Highgui.imwrite;
import org.opencv.imgproc.Imgproc;
import static org.opencv.imgproc.Imgproc.threshold;
import edu.wpi.first.wpilibj.networktables.*;
//import java.io.FileNotFoundException;
import java.net.NoRouteToHostException;
import java.util.List;
/**
 *
 * @author Citrus Circuits
 */
public class ImageThreshNCrop {
    static {
        //System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        System.load("C:\\Users\\Developer\\Downloads\\opencv\\build\\java\\x86\\opencv_java248.dll");
    }
    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        GetFromText();
       //ImageLoad();
       //OnRobitTest();
       OnSystemTest("newImage.jpg");
       //FromCameraTest();
    }
    //variables for the different threshold values that will be used to threshold the image
    static int hThreshHigh;
    static int hThreshLow;
    static int sThreshHigh;
    static int sThreshLow;
    static int vThreshHigh;
    static int vThreshLow;
    static int startPos;
    static int whitePixUpperBound;
    static double leftSideStartPoint;
    static double leftSideCropDistance;
    static double ystartpos;
    static double ycropdistance;
    static double mincolsforhot;
    static boolean startonrightside;
    
    public static void ImageLoad() throws IOException {
        try
        {
            SaveImage("http://10.16.78.11/jpg/image.jpg", "newImage.jpg"); //Saves the image from the URL
            Mat img;
            img = imread("newImage.jpg");
            imwrite("InitialImage.jpg", img);
        }
        catch(NoRouteToHostException f) 
        {
            System.out.println("Not connected to camera");
            Mat img = imread("TestImage.jpg");
            imwrite("newImage.jpg", img);
        }
    }
    public static void GetFromTextV2(){
        try 
        {
            BufferedReader reader = new BufferedReader(new FileReader("values.txt")); //Finds the right document
            String line;
            String [] lineParts;
            try
            {
                while ((line = reader.readLine()) != null) { //while there is stuff on the line you are reading
                    lineParts = line.split(" ");
                    if(lineParts[0].equals("HUE_HIGH"))
                    {
                        System.out.println("H^");
                        hThreshHigh = Integer.parseInt(lineParts[1]);
                    }
                    else if(lineParts[0].equals("HUE_LOW"))
                    {
                        System.out.println("Hv");
                        hThreshLow = Integer.parseInt(lineParts[1]);
                    }
                    else if(lineParts[0].equals("SAT_HIGH"))
                    {
                        System.out.println("S^");
                        sThreshHigh = Integer.parseInt(lineParts[1]);
                    }
                    else if(lineParts[0].equals("SAT_LOW"))
                    {
                        
                        System.out.println("Sv");
                        sThreshLow = Integer.parseInt(lineParts[1]);
                    }
                    else if(lineParts[0].equals("VAL_HIGH"))
                    {
                        System.out.println("V^");
                        vThreshHigh = Integer.parseInt(lineParts[1]);
                    }
                    else if(lineParts[0].equals("VAL_LOW"))
                    {
                        System.out.println("Vv");
                        vThreshLow = Integer.parseInt(lineParts[1]);
                    }
                    else if(lineParts[0].equals("STARTPOS"))
                    {
                        if(lineParts[1].equals("LEFT"))
                        {
                            startPos = 1;
                        }
                        else if(lineParts[1].equals("RIGHT"))
                        {
                            startPos = 3;
                        }
                        else
                        {
                            startPos = 1;
                        }
                    }
                    else if(lineParts[0].equals("WHITEUPPERBOUND")) //number of while pixles that tells us if the goal is hot
                    {
                        whitePixUpperBound = Integer.parseInt(lineParts[1]);
                    }
                    else if(lineParts[0].equals("LEFT_CROP")) //range 0-1 (% of image)
                    {
                        leftSideStartPoint = Double.parseDouble(lineParts[1]);
                    }
                    else if(lineParts[0].equals("LEFTCROP_WIDTH")) //range 0-1 (% of image) this + LEFT_CROP <= 1
                    {
                        leftSideCropDistance = Double.parseDouble(lineParts[1]);
                    }
                    else if(lineParts[0].equals("Y_STARTPOS"))
                    {
                        ystartpos = Double.parseDouble(lineParts[1]);
                    }
                    else if(lineParts[0].equals("YCROP_DOWN"))
                    {
                        ycropdistance = Double.parseDouble(lineParts[1]);
                    }
                    else if(lineParts[0].equals("MINCOLSFORHOT"))
                    {
                        mincolsforhot = Double.parseDouble(lineParts[1]);
                    }
                }
            }
            catch(IOException f)
            {
                System.out.println("Reading values error");
            }
        }
        catch(FileNotFoundException e)
        {
            System.out.println("No values file");
        }
}
    public static void GetFromText(){
        try 
        {
            BufferedReader reader = new BufferedReader(new FileReader("values.txt"));
            String line;
            String [] lineParts;
            try
            {
                while ((line = reader.readLine()) != null) {
                    lineParts = line.split(" ");
                    if(lineParts[0].equals("STARTSIDE")) 
                    {
                        if(lineParts[1].equals("RIGHT"))
                        {
                            startonrightside = true;
                        }
                        else startonrightside = false;
                    }
                    if(lineParts[0].equals("HUE_HIGH") && !startonrightside)
                    {
                        hThreshHigh = Integer.parseInt(lineParts[1]);
                    }
                    else if(lineParts[0].equals("HUE_LOW") && !startonrightside)
                    {
                        hThreshLow = Integer.parseInt(lineParts[1]);
                    }
                    else if(lineParts[0].equals("SAT_HIGH") && !startonrightside)
                    {
                        sThreshHigh = Integer.parseInt(lineParts[1]);
                    }
                    else if(lineParts[0].equals("SAT_LOW") && !startonrightside)
                    {
                        sThreshLow = Integer.parseInt(lineParts[1]);
                    }
                    else if(lineParts[0].equals("VAL_HIGH") && !startonrightside)
                    {
                        vThreshHigh = Integer.parseInt(lineParts[1]);
                    }
                    else if(lineParts[0].equals("VAL_LOW") && !startonrightside)
                    {
                        vThreshLow = Integer.parseInt(lineParts[1]);
                    }
                    else if(lineParts[0].equals("RIGHT_HUE_HIGH") && startonrightside)
                    {
                        hThreshHigh = Integer.parseInt(lineParts[1]);
                    }
                    else if(lineParts[0].equals("RIGHT_HUE_LOW") && startonrightside)
                    {
                        hThreshLow = Integer.parseInt(lineParts[1]);
                    }
                    else if(lineParts[0].equals("RIGHT_SAT_HIGH") && startonrightside)
                    {
                        sThreshHigh = Integer.parseInt(lineParts[1]);
                    }
                    else if(lineParts[0].equals("RIGHT_SAT_LOW") && startonrightside)
                    {
                        sThreshLow = Integer.parseInt(lineParts[1]);
                    }
                    else if(lineParts[0].equals("RIGHT_VAL_HIGH") && startonrightside)
                    {
                        vThreshHigh = Integer.parseInt(lineParts[1]);
                    }
                    else if(lineParts[0].equals("RIGHT_VAL_LOW") && startonrightside)
                    {
                        vThreshLow = Integer.parseInt(lineParts[1]);
                    }
                    else if(lineParts[0].equals("WHITEUPPERBOUND")) //number of while pixles that tells us if the goal is hot
                    {
                        whitePixUpperBound = Integer.parseInt(lineParts[1]);
                    }
                    else if(lineParts[0].equals("LEFT_CROP")) //range 0-1 (% of image)
                    {
                        leftSideStartPoint = Double.parseDouble(lineParts[1]);
                    }
                    else if(lineParts[0].equals("LEFTCROP_WIDTH")) //range 0-1 (% of image) this + LEFT_CROP <= 1
                    {
                        leftSideCropDistance = Double.parseDouble(lineParts[1]);
                    }
                    else if(lineParts[0].equals("Y_STARTPOS"))
                    {
                        ystartpos = Double.parseDouble(lineParts[1]);
                    }
                    else if(lineParts[0].equals("YCROP_DOWN"))
                    {
                        ycropdistance = Double.parseDouble(lineParts[1]);
                    }
                    else if(lineParts[0].equals("MINCOLSFORHOT"))
                    {
                        mincolsforhot = Double.parseDouble(lineParts[1]);
                    }
                }
            }
            catch(IOException f)
            {
                System.out.println("Reading values error");
            }
        }
        catch(FileNotFoundException e)
        {
            System.out.println("No values file");
        }
}
    public static int DirectionTestV2(Mat MatrixName){
        imwrite("DirectionTest.jpg", MatrixName);
        double totalPixelsRight;
        totalPixelsRight = (double)MatrixName.rows() * (double)MatrixName.cols(); //finds the total number of pixels in an image
        double blackPixelsRight;
        blackPixelsRight = (double)totalPixelsRight - (double)Core.countNonZero(MatrixName); //non zeros are WHITE
        System.out.println("Total pixels right: " + totalPixelsRight);
        System.out.println("Black Pixels Right: " + blackPixelsRight);
        System.out.println("Non Zeros Right: " + Core.countNonZero(MatrixName));
        if(totalPixelsRight - blackPixelsRight < whitePixUpperBound) //Only ONE vision target seen, TODO Numbers
            {
                    System.out.println("HOT RIGHT");
                    return 2;
            }
        else if(totalPixelsRight - blackPixelsRight >= whitePixUpperBound)//TWO vision targets seen, TODO Numbers
            {
                    System.out.println("HOT LEFT");
                    return 1;
            }
        return 0;
    }
    
    
    public static int DirectionTest(Mat MatrixName){
        imwrite("DirectionTest.jpg", MatrixName);
        int numNonZero = 0;
        for(int i = 0; i<MatrixName.rows(); i++)
        {
            Mat column = MatrixName.col(i);
            if(Core.countNonZero(column) > 0)
            {
                numNonZero++;
            }
        }
        System.out.println("Columns: " + numNonZero);
        if (numNonZero > mincolsforhot)
        {
            System.out.println("HOT LEFT");
            return 1;
        }
        System.out.println("HOT RIGHT");
        return 2;
    }
    
    public static Mat Thresh(String imageName){
        
        int shift = 100; //TODO numbers
        //int shift = 0;
        int selfLow = (hThreshLow + shift) * 180/256; //for easy inputs in the text document since Hue is annoying and on the bottom of the scale
        System.out.println("selfLow: " + selfLow);
        int selfHigh = (hThreshHigh + shift) * 180/256;
        System.out.println("selfHigh: " + selfHigh);
        if(selfLow >= 180)
        {
            selfLow = selfLow - 180;
        }
        if(selfHigh >= 180)
        {
            selfHigh = selfHigh - 180;
        }
        /*if(selfLow > selfHigh)
=======
        if(selfLow > selfHigh)
>>>>>>> b8a34e28f74f34080debc917453cabb62310f705
        {
            int x = selfHigh;
            selfHigh = selfLow;
            selfLow = x;
<<<<<<< HEAD
        }*/
        if(selfLow == selfHigh)
        {
            selfHigh++;
        }
        //selfLow = 255 - selfLow;
        //selfHigh = 255 - selfHigh;
        int hHigh = selfLow; //we need to reverse them because of the 255 - selfLow and 255 - selfHigh
        int hLow = selfHigh;
        //178 here is 255 in NI vision assistent
        //int sLow = 255 - sThreshLow;
        int sLow = sThreshLow;
        //int sHigh = 255 - sThreshHigh;
        int sHigh = sThreshHigh;
        
        Mat original = imread(imageName);
        Mat hue = new Mat();
        Mat saturation = new Mat();
        Mat value = new Mat();
        Mat threshedimage = new Mat();
        List<Mat> SourceForThresh = new ArrayList<>(); //Array lists for hue, saturation and value per threshold
        List<Mat> LowThreshDestination = new ArrayList<>();
        List<Mat> HighThreshDestination = new ArrayList<>(0);
        
        for(int i = 0; i < 3; i++)
        {
            LowThreshDestination.add(new Mat());
        }
        for(int i = 0; i < 3; i++)
        {
            HighThreshDestination.add(new Mat());
        }
        System.out.println(hThreshHigh+ ", " + hThreshLow + ", " + sThreshHigh + ", " + sThreshLow + ", " + vThreshHigh + ", " + vThreshLow);
        Imgproc.cvtColor(original, original, Imgproc.COLOR_RGB2HSV);
        //imwrite("Intermediate.jpg", original);
        Core.split(original, SourceForThresh);
        //imwrite("SourceThresh0.jpg", SourceForThresh.get(0));
        //imwrite("SourceThresh1.jpg", SourceForThresh.get(1));
        //imwrite("SourceThresh2.jpg", SourceForThresh.get(2));
        
        threshold(SourceForThresh.get(0), LowThreshDestination.get(0), selfLow, 255, 0); //binary to register what's above 
        threshold(SourceForThresh.get(1), LowThreshDestination.get(1), sLow, 255, 0);
        threshold(SourceForThresh.get(2), LowThreshDestination.get(2), vThreshLow, 255, 0); 
        //highest color value we're thresholding
        threshold(SourceForThresh.get(0), HighThreshDestination.get(0), selfHigh, 255, 1); //inverted binary to register what's below 
        threshold(SourceForThresh.get(1), HighThreshDestination.get(1), sHigh, 255, 1);
        threshold(SourceForThresh.get(2), HighThreshDestination.get(2), vThreshHigh, 255, 1);
        //Smashing all the different channels together forcefully
        if(selfLow < selfHigh)
        {
            Core.bitwise_and(LowThreshDestination.get(0), HighThreshDestination.get(0), hue);
        }
        else
        {
            Core.bitwise_or(LowThreshDestination.get(0), HighThreshDestination.get(0), hue);
        }
        Core.bitwise_and(LowThreshDestination.get(1), HighThreshDestination.get(1), saturation);
        Core.bitwise_and(LowThreshDestination.get(2), HighThreshDestination.get(2), value);
        
        System.out.println("selfLow: " + selfLow);
        System.out.println("selfHigh: " + selfHigh);

        
        imwrite("Saturation.jpg", saturation);
        imwrite("Value.jpg", value);
        imwrite("Hue.jpg", hue);
        Core.bitwise_and(hue, value, threshedimage); //Smash together hue, saturation and value
        Core.bitwise_and(saturation, threshedimage, threshedimage);
        imwrite("ThreshedImage.jpg", threshedimage);
        return threshedimage;
    }
    public static Mat Crop(Mat original) {
        //imwrite("Original.jpg", original);
        Size originalsize = original.size();
        int ySize = (int)originalsize.height;
        int xSize = (int)originalsize.width;
        double yCrop = ySize*ystartpos;
        double xStartingpoint = (leftSideStartPoint*xSize); //the left 
        //double yStartingPoint = (bottomCropStartPoint*ySize);
        double heightCrop = ySize*ycropdistance; //to exclude bumper reflections
        double secondarycrop = ((leftSideCropDistance)*xSize);
        Rect straightAhead = new Rect((int)xStartingpoint, (int)yCrop, (int)secondarycrop, (int)heightCrop);
        Mat cropStraightAhead = new Mat(original, straightAhead); //unalteredorinal should be jasmineBlur
        //imwrite("CroppedTest.jpg", cropStraightAhead);
        return cropStraightAhead;
    }
    public static Mat CameraCrop(String ImageSource) { //for cropping without thresholding
        Mat source = imread(ImageSource);
        Size sourcesize = source.size();
        int sourceHeight = (int)sourcesize.height;//Find height of image
        int sourceWidth = (int)sourcesize.width; //find width of image
        //double yCrop = 0;
        double startingpoint = (leftSideStartPoint*sourceWidth); //the left side
        double heightCrop = sourceHeight*ystartpos; //exclude bumpers
        double secondarycrop = ((leftSideCropDistance)*sourceWidth);
        Rect straightAhead = new Rect ((int)startingpoint, (int)heightCrop, (int)secondarycrop, (sourceHeight-(int)heightCrop));
        Mat cropStraightAhead = new Mat(source, straightAhead); //unalteredorinal should be jasmineBlur
        imwrite("CameraCropped.jpg", cropStraightAhead);
        return cropStraightAhead;
    }
    public static void OnSystemTest(String ImageSource){ //for using preloaded image on the computer
       Mat me;
       me = Thresh(ImageSource);
       me = Crop(me);
       DirectionTest(me);
    }
    public static void OnRobitTest() throws IOException{ //for using image when robot is enabled
        boolean initialLoop = true;
        System.out.println("On");
        NetworkTable.setClientMode();
        NetworkTable.setIPAddress("10.16.78.2");    //find network tables
        NetworkTable datatable = NetworkTable.getTable("datatable"); //make table
        while(initialLoop)
        {
            double init = datatable.getNumber("Enabled", 0); //check to see if we are enabled or not, default is NO
            System.out.println("Are we enabled? " + init);
            if(init == 1) { //once we are enabled...
                initialLoop = false;
                String imageUrl = "http://10.16.78.11/jpg/image.jpg" ; //get image
                String destinationFile;
                destinationFile = "newImage.jpg"; //make image
                SaveImage(imageUrl, destinationFile);
                Mat img = imread("newImage.jpg");
                //imwrite("Purple.jpg", img);
                Mat me;
                me = Thresh("newImage.jpg"); //thresh image
                me = Crop(me); //crop image
                imwrite("ThreshCropped.jpg", me);
                int printme = DirectionTest(me);
                System.out.println(printme);
                if(printme == 1)
                {
                    datatable.putString("Direction: ", "GO LEFT");
                    System.out.println("GO LEFT");
                }
                else if(printme == 2)
                {
                    datatable.putString("Direction: ", "GO RIGHT");
                    System.out.println("GO RIGHT");
                }
                else
                {
                    datatable.putString("Direction: ", "INCONCLUSIVE");
                    System.out.println("INCONCLUSIVE");
                }
            }
        }
    }
    public static void FromCameraTest() throws IOException{ //for testing while using an image from the computer
        try
        {
            String imageUrl = "http://10.16.78.11/jpg/image.jpg" ; //get image from URL
            String destinationFile = "newImage.jpg"; //make image
            SaveImage(imageUrl, destinationFile);
            Mat img = imread("newImage.jpg");
            //imwrite("InitialImage.jpg", img);
            Mat me;
            //me = CameraCrop("newImage.jpg"); // cropping without thresholding
            me = Thresh("newImage.jpg"); //threshing image
            me = Crop(me); //cropping image with thresholds
            DirectionTest(me);
        }
        catch(NoRouteToHostException f) 
        {
            System.out.println("Not connected to camera");
        }
    }
public static void SaveImage (String imageUrl, String destinationFile) throws IOException { //saves the image from the URL
        System.out.println("In SaveImage");
        URL image = new URL(imageUrl);
        //System.out.println("Got URL Object");
        OutputStream os;
        try (InputStream is = image.openStream()) {
            //System.out.println("Created inputstream");
            os = new FileOutputStream(destinationFile);
            System.out.println("Createdoutputstream");
            byte[] b = new byte[2048];
            int length;
            while((length = is.read(b)) != -1){
                os.write(b, 0, length);
            }
        }
        os.close();    
}
private static void receive_image(String imageUrl, String destinationFile) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}