package com.socion.session.utils;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


public class ImageUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageUtils.class);

    private ImageUtils() {
    }

    public static String convertAttestaionPdfToImg(String sourceDir, String destinationDirectory) {
        String destinationDir = destinationDirectory;
        File sourceFile = new File(sourceDir);
        String fileName = sourceFile.getName().replace(".pdf", ".jpg");
        try (final PDDocument document = PDDocument.load(new File(sourceDir))) {
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            for (int page = 0; page < document.getNumberOfPages(); ++page) {
                BufferedImage bim = pdfRenderer.renderImageWithDPI(page, 300, ImageType.RGB).getSubimage(282, 215, 738, 995);
                String file = destinationDir + fileName;
                ImageIOUtil.writeImage(bim, file, 300);
            }



        } catch (IOException e) {
            LOGGER.error("Exception while trying to create pdf document {} ", e);
        }
        return destinationDir + "/" + fileName;
    }


    public static void cropImage(String sourceDir){
        String desDir;
        try {
            BufferedImage originalImgage = ImageIO.read(new File(sourceDir));
            BufferedImage SubImgage = originalImgage.getSubimage(50, 50, 700, 700);
            File outputfile = new File(sourceDir);
            ImageIO.write(SubImgage, "png", outputfile);

            System.out.println("Image cropped successfully: "+outputfile.getPath());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}