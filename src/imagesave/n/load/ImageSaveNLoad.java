/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package imagesave.n.load;
//import com.googlecode.javacv.cpp.opencv_highgui;
//import com.googlecode.javacv.cpp.opencv_core.IplImage;
import static com.sun.org.apache.xalan.internal.lib.ExsltMath.max;
import java.io.File;
import java.util.Collections;
import static java.util.Collections.max;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
//import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import java.util.Vector;
import static org.opencv.core.Core.split;
/**
 *
 * @author Citrus Circuits
 */
import static org.opencv.highgui.Highgui.imread;
import static org.opencv.highgui.Highgui.imwrite;
import org.opencv.imgproc.Imgproc;
import static org.opencv.imgproc.Imgproc.cvtColor;
import static org.opencv.imgproc.Imgproc.threshold;
import static sun.swing.MenuItemLayoutHelper.max;

public class ImageSaveNLoad {
   
    //Color Detection
    /**
     * @param args the command line arguments
     */
    
    static {
        //System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        System.load("C:\\opencv\\build\\java\\x86\\opencv_java248.dll");
    }
    
    public static void main(String[] args) {
        System.out.println("Hello World!");
        // TODO code application logic here
        Mat img = imread("Robots.jpg");
        //System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        //Mat m  = Mat.eye(3, 3, CvType.CV_8UC1);
        //System.out.println("m = " + m.dump());
        //System.out.println(new File(".").getAbsolutePath());
        imwrite("Purple.jpg", img);
        find_bumpers("Robots.jpg", "r");
    } 
    
    public static void find_bumpers(String imageName, String targetColor){
        
       
        Mat original = imread(imageName);
        Size s = original.size();
        int ySize = (int)s.height;
        int xSize = (int)s.width;
        
        System.out.println("Height: " + s.height);
        System.out.println("Width: " + s.width);
        
   
        
        double yCrop = (ySize - 0.3*ySize);
        double xCrop = 0;
        double heightCrop = (0.3*ySize);
        double widthCrop = xSize;
        
        System.out.println("Starting Y Position: " + yCrop);
        System.out.println("Starting X Position: " + xCrop);
        System.out.println("Y size after crop: " + heightCrop);
        System.out.println("X size after crop: " + widthCrop);
        
        int r = 0;
        int g = 0;
        int b = 0;
        
        Rect rectCrop = new Rect((int)xCrop, (int)yCrop, (int)widthCrop, (int)heightCrop);
        //Rect rectCrop = new Rect(2, 23, 380, 42);
        Mat imCrop = new Mat(original, rectCrop);
        
        //imwrite("Lilac.jpg", imCrop);
        
        if (targetColor.equals("r"))
        {
            r = 219;
            g = 94;
            b = 92;
        }
        
        if (targetColor.equals("b"))
        {
            r = 40;
            g = 61;
            b = 140;
        }
        
        Mat modImage = new Mat();
        Imgproc.cvtColor(imCrop, modImage, Imgproc.COLOR_RGB2HSV);
        //Imgproc.cvtColor(imCrop, modImage, Imgproc.COLOR_RGB2BGR);
        //Imgproc.cvtColor(imCrop, modImage, Imgproc.COLOR_BGR2HSV);
        //threshold(modImage, modImage, 20, 255, 1);
        //threshold(modImage, modImage, 170, 255, 0);
        Imgproc.cvtColor(modImage, modImage, Imgproc.COLOR_RGB2GRAY);
        
        
        threshold(modImage, modImage, 170, 255, 0);
        
        //imwrite("Lilac.jpg", modImage);
        //Vector<Mat> bgr_planes = null;
        //split(modImage, bgr_planes);
        imwrite("Lilac.jpg", modImage);
        
      
        
        
        

        
        
        
        
    }

}