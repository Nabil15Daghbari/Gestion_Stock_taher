package GestionStocke.File;

import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Component;

import GestionStocke.Exception.UnsupportedFileTypeException;

@Component
public class FileFilter {
    public String determineContentType(String filePath) throws UnsupportedFileTypeException {
        String fileExtension = FilenameUtils.getExtension(filePath);
        String contentType = null;
        switch (fileExtension.toLowerCase()) {
            case "jpeg":
            case "jpg":
                contentType = "image/jpeg";
                break;
            case "png":
                contentType = "image/png";
                break;
            case "gif":
                contentType = "image/gif";
                break;
            // Ajoutez d'autres cas pour les formats d'image supplémentaires que vous souhaitez gérer
            default:
                throw new UnsupportedFileTypeException("Type de fichier non pris en charge : " + fileExtension);
        }

        return contentType;
    }
}
