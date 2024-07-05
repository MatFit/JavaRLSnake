package game.utils;

import javax.swing.*;
import java.awt.*;

public class ImageFactory {
    public ImageFactory(){ }
    public static Image imageTypeFactory(String imageType){
        String imagePath = "src/resource/";

        switch (imageType.toLowerCase()) {
            case "food":
                imagePath += "food.png";
                break;
            case "head":
                imagePath += "head.png";
                break;
            case "observation":
                imagePath += "observation.png";
                break;
            case "tail":
                imagePath += "tail.png";
                break;
            default:
                throw new IllegalArgumentException("imageType not found");
        }

        return new ImageIcon(imagePath).getImage();
    }
}
