/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package imagesave.n.load;
//import com.googlecode.javacv.cpp.opencv_highgui;
//import com.googlecode.javacv.cpp.opencv_core.IplImage;
import java.io.File;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
/**
 *
 * @author Citrus Circuits
 */
import static org.opencv.highgui.Highgui.imread;
import static org.opencv.highgui.Highgui.imwrite;
public class ImageSaveNLoad {
   
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
        Mat img = imread("picture1win1loss.jpg");
        //System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        //Mat m  = Mat.eye(3, 3, CvType.CV_8UC1);
        //System.out.println("m = " + m.dump());
        //System.out.println(new File(".").getAbsolutePath());
        imwrite("Purple.jpg", img);
    } 
}