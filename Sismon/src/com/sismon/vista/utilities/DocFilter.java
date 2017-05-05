package com.sismon.vista.utilities;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author jgcastillo
 */
public class DocFilter extends FileFilter{

    @Override
    public boolean accept(File f) {
        if(f.isDirectory()){
            return true;
        }
        
        String extension = Utils.getExtension(f);
        if(extension != null){
            if(extension.equals(Utils.doc)
                    || extension.equals(Utils.docx)){
                return true;
            } else {
                return false;
            }
        }
        
        return false;
    }

    @Override
    public String getDescription() {
        return "Sólo documentos word";
    }
    
}
