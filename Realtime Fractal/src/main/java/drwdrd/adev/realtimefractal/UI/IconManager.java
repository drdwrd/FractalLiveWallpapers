package drwdrd.adev.realtimefractal.UI;

import android.content.Context;

import java.io.IOException;
import java.util.HashMap;

import drwdrd.adev.engine.GLTexture;
import drwdrd.adev.engine.TextureSampler;
import drwdrd.adev.engine.TextureUnit;

public class IconManager {

        protected HashMap<String,TextureUnit> icons = new HashMap<>();

    public IconManager() {

    }

    //initializes all icons
    public boolean init(Context context, String dir) {
        try {
            String[] fileLists=context.getAssets().list(dir);
            for(String iconName: fileLists) {
                GLTexture texture = new GLTexture();
                texture.createTexture2DFromAssets(context, dir + "/" + iconName, true);
                icons.put(iconName,new TextureUnit(texture,new TextureSampler(GLTexture.Target.Texture2D)));
            }
        } catch(IOException io) {
            io.printStackTrace();
            return false;

        }
        return true;
    }

    public void delete() {
        for(TextureUnit icon: icons.values()) {
            icon.delete();
        }
        icons.clear();
    }

    public TextureUnit getIcon(String iconName) {
        if(icons.containsKey(iconName))
            return icons.get(iconName);
        return null;
    }

}
