package io.github.HackSC_19_Fall.facialrecognition;

import org.bytedeco.javacpp.*;
import org.opencv.core.*;
import org.opencv.features2d.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;

import javax.swing.JOptionPane;
import java.awt.Font;
import java.io.File;
import java.util.Objects;

/**
 * Demo (main) class to run functions.
 *
 * @group: Enji Li, Bairen Chen, Xinran Gao, Yunhan Mao
 */
class FacialRecognition
{
    /**
     * Directory on the disk containing faces
     */
    private static final File DATABASE = new File("Database");

    public static void main(String[] args)
    {
        Loader.load(opencv_java.class);
        capture();
        System.exit(0);
    }
    

    private static void capture()
    {
        File classifier = new File("lbpcascade_frontalface_improved.xml");
        // get a classfier from the api of lbpcascade_frontalface_improved.xml
        
        //if unable to retrieve the api, return error
        if (!classifier.exists())
        {
            displayFatalError("Unable to find classifier!");
            return;
        }

        //initialize "faceDetector" variable as an object under Cas..., using the classier
        //we got from the api and insert it into Cas
        CascadeClassifier faceDetector = new CascadeClassifier(classifier.toString());
        
        //another imported library, used to open the camera
        VideoCapture camera = new VideoCapture(0);

        //if unable to open camera, return error
        if (!camera.isOpened())
        {
            displayFatalError("No camera detected!");
            return;
        }

        
        //if database doesn't exist, create the database 
        if (!DATABASE.exists())
            DATABASE.mkdir();

        //initialize a frame
        VideoFrame frame = new VideoFrame();

        //if we have the framing detector open and camera open, do below
        while (frame.isOpen() && camera.isOpened())
        {
        	//capture the image
            Mat rawImage = new Mat();
            
            //read in whatever camera captures into rawimage
            camera.read(rawImage);
         
            //mat is a picture pool
            
            //generate a new image using detectFaces, within 
            Mat newImage = detectFaces(rawImage, faceDetector, frame);
            
            //give it to the users
            frame.showImage(newImage);
        }

        camera.release();
    }

    private static Mat detectFaces(Mat image, CascadeClassifier faceDetector, VideoFrame frame)
    {
    	//do not know what it is
        MatOfRect faceDetections = new MatOfRect();
        
        //a function that maybe ...
        faceDetector.detectMultiScale(image, faceDetections);
        
        //an array of faces detected, a function imported from MatOfRect
        //named faces
        Rect[] faces = faceDetections.toArray();
        
        //,...
        boolean shouldSave = frame.shouldSave();
        
        //get a name
        String name = frame.getFileName();
        
        
        Scalar color = frame.getTextColor();

        //for elements in the array, faces
        for (Rect face : faces)
        {
        	
            Mat croppedImage = new Mat(image, face);

            //we save the images
            if (shouldSave)
                saveImage(croppedImage, name);
            
            //
            //annotated the faces on the frame
            Imgproc.putText(image, "Name: " + identifyFace(croppedImage), face.tl(), Font.BOLD, 3, color);
            
            //在image上识别人脸，修改并保存至原image object
            Imgproc.rectangle(image, face.tl(), face.br(), color);
        }

        //how many faces detected
        int faceCount = faces.length;
        
        //topLeft corner displayed message
        String message = faceCount + " Student" + (faceCount == 1 ? "" : "s") + " detected!";
        Imgproc.putText(image, message, new Point(3, 25), Font.BOLD, 2, color);

        return image;
    }

    private static String identifyFace(Mat image)
    {
    	//parameters for identify faces
        int errorThreshold = 3;
        int mostSimilar = -1;
        File mostSimilarFile = null;
        
        //not to be changed
        for (File capture : Objects.requireNonNull(DATABASE.listFiles()))
        {
            int similarities = compareFaces(image, capture.getAbsolutePath());

            if (similarities > mostSimilar)
            {
                mostSimilar = similarities;
                mostSimilarFile = capture;
            }
        }

        //if identify the face, return their label (name)
        if (mostSimilarFile != null && mostSimilar > errorThreshold)
        {
            String faceID = mostSimilarFile.getName();
            String delimiter = faceID.contains(" (") ? "(" : ".";
            return faceID.substring(0, faceID.indexOf(delimiter)).trim();
        }
        //if face is not in the system, return "Unregistered"
        else
            return "Unregistered";
    }
//inspired from https://docs.opencv.org/3.4/db/d39/classcv_1_1DescriptorMatcher.html
    private static int compareFaces(Mat currentImage, String fileName)
    {
        Mat compareImage = Imgcodecs.imread(fileName);
        ORB orb = ORB.create();
        int similarity = 0;

        MatOfKeyPoint keypoints1 = new MatOfKeyPoint();
        MatOfKeyPoint keypoints2 = new MatOfKeyPoint();
        orb.detect(currentImage, keypoints1);
        orb.detect(compareImage, keypoints2);

        Mat descriptors1 = new Mat();
        Mat descriptors2 = new Mat();
        orb.compute(currentImage, keypoints1, descriptors1);
        orb.compute(compareImage, keypoints2, descriptors2);

        if (descriptors1.cols() == descriptors2.cols())
        {
            MatOfDMatch matchMatrix = new MatOfDMatch();
            DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING).match(descriptors1, descriptors2, matchMatrix);

            for (DMatch match : matchMatrix.toList())
                if (match.distance <= 50)
                    similarity++;
        }

        return similarity;
    }

    //file the captured frames
    private static void saveImage(Mat image, String name)
    {
    	
        File destination;
        String extension = ".png";
        String baseName = DATABASE + File.separator + name;
        File basic = new File(baseName + extension);

        if (!basic.exists())
            destination = basic;
        else
        {
            int index = 0;

            do
                destination = new File(baseName + " (" + index++ + ")" + extension);
            while (destination.exists());
        }

        Imgcodecs.imwrite(destination.toString(), image);
    }

    private static void displayFatalError(String message)
    {
        JOptionPane.showMessageDialog(null, message, "Fatal Error", JOptionPane.ERROR_MESSAGE);
    }
}