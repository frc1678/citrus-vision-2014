/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imagethreshncrop;
import java.net.URL;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileReader;
import static org.opencv.highgui.Highgui.imread;
import static org.opencv.highgui.Highgui.imwrite;
import org.opencv.imgproc.Imgproc;
import static org.opencv.imgproc.Imgproc.threshold;
import edu.wpi.first.wpilibj.networktables.*;
import java.io.FileNotFoundException;
import java.net.NoRouteToHostException;
import java.util.List;
/**
 *
 * @author Citrus Circuits
 */
public class ImageThreshNCrop {
    static {
        //System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        System.load("C:\\opencv\\build\\java\\x86\\opencv_java248.dll");
    }
    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        GetFromText();
       //ImageLoad();
       //OnRobitTest();
       //OnSystemTest("newImage.jpg");
       FromCameraTest();
    }
    static int hThreshHigh;
    static int hThreshLow;
    static int sThreshHigh;
    static int sThreshLow;
    static int vThreshHigh;
    static int vThreshLow;
    static int startPos;
    static int whitePixUpperBound;
    static double leftsidestartpoint;
    static double leftsidecropdistance;
    static double ystartpos; //for percentage of area cropped on the photo. Used in cropping images
    
    public static void ImageLoad() throws IOException {
        try
        {
            SaveImage("http://10.16.78.11/jpg/image.jpg", "newImage.jpg");
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
                    if(lineParts[0].equals("HUE_HIGH"))
                    {
                        hThreshHigh = Integer.parseInt(lineParts[1]);
                    }
                    else if(lineParts[0].equals("HUE_LOW"))
                    {
                        hThreshLow = Integer.parseInt(lineParts[1]);
                    }
                    else if(lineParts[0].equals("SAT_HIGH"))
                    {
                        sThreshHigh = Integer.parseInt(lineParts[1]);
                    }
                    else if(lineParts[0].equals("SAT_LOW"))
                    {
                        sThreshLow = Integer.parseInt(lineParts[1]);
                    }
                    else if(lineParts[0].equals("VAL_HIGH"))
                    {
                        vThreshHigh = Integer.parseInt(lineParts[1]);
                    }
                    else if(lineParts[0].equals("VAL_LOW"))
                    {
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
                    else if(lineParts[0].equals("WHITEUPPERBOUND"))
                    {
                        whitePixUpperBound = Integer.parseInt(lineParts[1]);
                    }
                    else if(lineParts[0].equals("DISTANCE_FROM_WALL"))
                    {
                        leftsidestartpoint = Double.parseDouble(lineParts[1]);
                    }
                    else if(lineParts[0].equals("DISTANCE_FROM_DISTANCE_FROM_WALL"))
                    {
                        leftsidecropdistance = Double.parseDouble(lineParts[1]);
                    }
                    else if(lineParts[0].equals("Y_STARTPOS"))
                    {
                        ystartpos = Double.parseDouble(lineParts[1]);
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
    public static int DirectionTest(Mat MatrixName){
        imwrite("DirectionTest.jpg", MatrixName);
        double totalPixelsRight;
        totalPixelsRight = (double)MatrixName.rows() * (double)MatrixName.cols();
        double blackPixelsRight;
        blackPixelsRight = (double)totalPixelsRight - (double)Core.countNonZero(MatrixName); //non zeros are WHITE
        //System.out.println("Black Pixels Right: " + blackPixelsRight);
        if(totalPixelsRight - blackPixelsRight < whitePixUpperBound) //Only ONE vision target seen, TODO Numbers
            {
                    System.out.println("GO RIGHT");
                    return 2;
            }
        else if(totalPixelsRight - blackPixelsRight >= whitePixUpperBound)//TWO vision targets seen, TODO Numbers
            {
                    System.out.println("GO LEFT");
                    return 1;
            }
        return 0;
    }
    public static Mat Thresh(String imageName){
        Mat original = imread(imageName);
        Mat hue = new Mat();
        Mat saturation = new Mat();
        Mat value = new Mat();
        Mat threshedimage = new Mat();
        List<Mat> SourceForThresh = new ArrayList<>();
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
        imwrite("Intermediate.jpg", original);
        Core.split(original, SourceForThresh);
        //lowest color value
        threshold(SourceForThresh.get(0), LowThreshDestination.get(0), hThreshLow, 255, 0); //binary to register what's above
        threshold(SourceForThresh.get(1), LowThreshDestination.get(1), sThreshLow, 255, 0);
        threshold(SourceForThresh.get(2), LowThreshDestination.get(2), vThreshLow, 255, 0); 
        //highest color value
        threshold(SourceForThresh.get(0), HighThreshDestination.get(0), hThreshHigh, 255, 1); //inverted binary to register what's below
        threshold(SourceForThresh.get(1), HighThreshDestination.get(1), sThreshHigh, 255, 1);
        threshold(SourceForThresh.get(2), HighThreshDestination.get(2), vThreshHigh, 255, 1);
        Core.bitwise_and(LowThreshDestination.get(0), HighThreshDestination.get(0), hue);
        Core.bitwise_and(LowThreshDestination.get(1), HighThreshDestination.get(1), saturation);
        Core.bitwise_and(LowThreshDestination.get(2), HighThreshDestination.get(2), value);
        Core.bitwise_and(saturation, value, threshedimage);
        imwrite("ThreshedImage.jpg", threshedimage);
        return threshedimage;
    }
    public static Mat Crop(Mat original) {
        imwrite("Original.jpg", original);
        Size originalsize = original.size();
        int ySize = (int)originalsize.height;
        int xSize = (int)originalsize.width;
        double yCrop = 0;
        double startingpoint = (leftsidestartpoint*xSize); //the left 
        double heightCrop = ySize*ystartpos; //to exclude bumper reflections
        double secondarycrop = ((leftsidecropdistance)*xSize);
        Rect straightAhead = new Rect((int)startingpoint, (int)heightCrop, (int)secondarycrop, (ySize-(int)heightCrop));
        Mat cropStraightAhead = new Mat(original, straightAhead); //unalteredorinal should be jasmineBlur
        imwrite("CroppedTest.jpg", cropStraightAhead);
        return cropStraightAhead;
    }
    public static Mat CameraCrop(String ImageSource) {
        Mat source = imread(ImageSource);
        imwrite("Source.jpg", source);
        Size sourcesize = source.size();
        int sourceHeight = (int)sourcesize.height;
        int sourceWidth = (int)sourcesize.width;
        double yCrop = 0;
        double startingpoint = (leftsidestartpoint*sourceWidth); //the left side
        double heightCrop = sourceHeight*ystartpos; //exclude bumpers
        double secondarycrop = ((leftsidecropdistance)*sourceWidth);
        Rect straightAhead = new Rect ((int)startingpoint, (int)heightCrop, (int)secondarycrop, (sourceHeight-(int)heightCrop));
        Mat cropStraightAhead = new Mat(source, straightAhead); //unalteredorinal should be jasmineBlur
        imwrite("CameraCropped.jpg", cropStraightAhead);
        return cropStraightAhead;
    }
    public static void OnSystemTest(String ImageSource){
       Mat me;
       me = Thresh(ImageSource);
       me = Crop(me);
       DirectionTest(me);
    }
    public static void OnRobitTest() throws IOException{
        boolean initialLoop = true;
        System.out.println("On");
        NetworkTable.setClientMode();
        NetworkTable.setIPAddress("10.16.78.2");    
        NetworkTable datatable = NetworkTable.getTable("datatable");
        while(initialLoop)
        {
            double init = datatable.getNumber("Enabled", 0);
            System.out.println("Are we enabled? " + init);
            if(init == 1) {
                initialLoop = false;
                String imageUrl = "http://10.16.78.11/jpg/image.jpg" ;
                String destinationFile;
                destinationFile = "newImage.jpg";
                SaveImage(imageUrl, destinationFile);
                Mat img = imread("newImage.jpg");
                imwrite("Purple.jpg", img);
                Mat me;
                me = Thresh("newImage.jpg");
                me = Crop(me);
                imwrite("ThreshCropped.jpg", me);
                DirectionTest(me);
                System.out.println(DirectionTest(me));
                if(DirectionTest(me) == 1)
                {
                    datatable.putString("Direction: ", "GO LEFT");
                    System.out.println("GO LEFT");
                }
                else if(DirectionTest(me) == 2)
                {
                    datatable.putString("Direction: ", "GO RIGHT");
                    System.out.println("GO RIGHT");
                }
                else if(DirectionTest(me) == 1)
                {
                    datatable.putString("Direction: ", "INCONCLUSIVE");
                    System.out.println("INCONCLUSIVE");
                }
            }
        }
    }
    public static void FromCameraTest() throws IOException{
        try
        {
            String imageUrl = "http://10.16.78.11/jpg/image.jpg" ;
            String destinationFile = "newImage.jpg";
            SaveImage(imageUrl, destinationFile);
            Mat img = imread("newImage.jpg");
            imwrite("InitialImage.jpg", img);
            Mat me;
            me = CameraCrop("newImage.jpg");
            //me = Thresh("newImage.jpg");
            //me = Crop(me);
            //DirectionTest(me);
        }
        catch(NoRouteToHostException f) 
        {
            System.out.println("Not connected to camera");
        }
    }
public static void SaveImage (String imageUrl, String destinationFile) throws IOException {
        System.out.println("In SaveImage");
        URL image = new URL(imageUrl);
        System.out.println("Got URL Object");
        OutputStream os;
        try (InputStream is = image.openStream()) {
            System.out.println("Created inputstream");
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