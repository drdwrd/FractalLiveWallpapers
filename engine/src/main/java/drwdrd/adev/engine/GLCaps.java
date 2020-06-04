package drwdrd.adev.engine;

import android.opengl.GLES20;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class GLCaps {
    private static GLCaps instance = null;

    private String vendor;
    private String renderer;
    private String version;
    private String glslVersion;
    private String extensions;
    private int maxTextureSize;
    private int maxCombinedTextureImageUnits;
    private int maxCubeMapTextureSize;
    private int maxFragmentUniformVectors;
    private int maxRenderbufferSize;
    private int maxTextureImageUnits;
    private int maxVaryingVectors;
    private int maxVertexAttribs;
    private int maxVertexTextureImageUnits;
    private int maxVertexUniformVectors;
    private int maxViewportWidth;
    private int maxViewportHeight;
    private int minAliasedLineWidth;
    private int maxAliasedLineWidth;
    private int minAliasedPointSize;
    private int maxAliasedPointSize;


    private GLCaps() {
        init();
    }

    public static GLCaps getInstance() {
        if (instance == null) {
            instance = new GLCaps();
        }
        return instance;
    }

    private void init() {
        int[] param = new int[2];

        vendor = GLES20.glGetString(GLES20.GL_VENDOR);
        renderer = GLES20.glGetString(GLES20.GL_RENDERER);
        version = GLES20.glGetString(GLES20.GL_VERSION);
        glslVersion = GLES20.glGetString(GLES20.GL_SHADING_LANGUAGE_VERSION);
        extensions = GLES20.glGetString(GLES20.GL_EXTENSIONS);

        GLES20.glGetIntegerv(GLES20.GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS, param, 0);
        maxCombinedTextureImageUnits = param[0];

        GLES20.glGetIntegerv(GLES20.GL_MAX_CUBE_MAP_TEXTURE_SIZE, param, 0);
        maxCubeMapTextureSize = param[0];

        GLES20.glGetIntegerv(GLES20.GL_MAX_FRAGMENT_UNIFORM_VECTORS, param, 0);
        maxFragmentUniformVectors = param[0];

        GLES20.glGetIntegerv(GLES20.GL_MAX_RENDERBUFFER_SIZE, param, 0);
        maxRenderbufferSize = param[0];

        GLES20.glGetIntegerv(GLES20.GL_MAX_TEXTURE_IMAGE_UNITS, param, 0);
        maxTextureImageUnits = param[0];

        GLES20.glGetIntegerv(GLES20.GL_MAX_TEXTURE_SIZE, param, 0);
        maxTextureSize = param[0];

        GLES20.glGetIntegerv(GLES20.GL_MAX_VARYING_VECTORS, param, 0);
        maxVaryingVectors = param[0];

        GLES20.glGetIntegerv(GLES20.GL_MAX_VERTEX_ATTRIBS, param, 0);
        maxVertexAttribs = param[0];

        GLES20.glGetIntegerv(GLES20.GL_MAX_VERTEX_TEXTURE_IMAGE_UNITS, param, 0);
        maxVertexTextureImageUnits = param[0];

        GLES20.glGetIntegerv(GLES20.GL_MAX_VERTEX_UNIFORM_VECTORS, param, 0);
        maxVertexUniformVectors = param[0];

        GLES20.glGetIntegerv(GLES20.GL_MAX_VIEWPORT_DIMS, param, 0);
        maxViewportWidth = param[0];
        maxViewportHeight = param[1];

        GLES20.glGetIntegerv(GLES20.GL_ALIASED_LINE_WIDTH_RANGE, param, 0);
        minAliasedLineWidth = param[0];
        maxAliasedLineWidth = param[1];

        GLES20.glGetIntegerv(GLES20.GL_ALIASED_POINT_SIZE_RANGE, param, 0);
        minAliasedPointSize = param[0];
        maxAliasedPointSize = param[1];
    }


    public int getMaxTextureSize() {
        return maxTextureSize;
    }

    public int getMaxCombinedTextureUnits() {
        return maxCombinedTextureImageUnits;
    }

    public int getMaxCubeMapTextureSize() {
        return maxCubeMapTextureSize;
    }

    public int getMaxFragmentUniformVectors() {
        return maxFragmentUniformVectors;
    }

    public int getMaxRenderbufferSize() {
        return maxRenderbufferSize;
    }

    public int getMaxTextureImageUnits() {
        return maxTextureImageUnits;
    }

    public int getMaxVaryingVectors() {
        return maxVaryingVectors;
    }

    public int getMaxVertexAttribs() {
        return maxVertexAttribs;
    }

    public int getMaxVertexTextureImageUnits() {
        return maxVertexTextureImageUnits;
    }

    public int getMaxVertexUniformVectors() {
        return maxVertexUniformVectors;
    }

    public int getMaxViewportWidth() {
        return maxViewportWidth;
    }

    public int getMaxViewportHeight() {
        return maxViewportHeight;
    }

    public int getMinAliasedLineWidth() {
        return minAliasedLineWidth;
    }

    public int getMaxAliasedLineWidth() {
        return maxAliasedLineWidth;
    }

    public int getMinAliasedPointSize() {
        return minAliasedPointSize;
    }

    public int getMaxAliasedPointSize() {
        return maxAliasedPointSize;
    }

    public boolean isExtensionSuported(String extension) {
        // the case-insensitive pattern we want to search for
        Pattern p=Pattern.compile(extension,Pattern.CASE_INSENSITIVE);
        Matcher m=p.matcher(this.extensions);
        if(m.find())
            return true;
        return false;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("OpenGL Caps:\n");
        sb.append("GL Vendor: ").append(vendor).append("\n");
        sb.append("GL Renderer: ").append(renderer).append("\n");
        sb.append("GL Version: ").append(version).append("\n");
        sb.append("GLSL version: ").append(glslVersion).append("\n");
        sb.append("GL Extensions: ").append(extensions).append("\n");
        sb.append("Max Combined Texture Image Units: ").append(maxCombinedTextureImageUnits).append("\n");
        sb.append("Max Cube Map Texture Size: ").append(maxCubeMapTextureSize).append("\n");
        sb.append("Max Fragment Uniform Vectors: ").append(maxFragmentUniformVectors).append("\n");
        sb.append("Max Renderbuffer Size: ").append(maxRenderbufferSize).append("\n");
        sb.append("Max Texture Image Units: ").append(maxTextureImageUnits).append("\n");
        sb.append("Max Texture Size: ").append(maxTextureSize).append("\n");
        sb.append("Max Varying Vectors: ").append(maxVaryingVectors).append("\n");
        sb.append("Max Vertex Attribs: ").append(maxVertexAttribs).append("\n");
        sb.append("Max Vertex Texture Image Units: ").append(maxVertexTextureImageUnits).append("\n");
        sb.append("Max Vertex Uniform Vectors: ").append(maxVertexUniformVectors).append("\n");
        sb.append("Max Viewport Width: ").append(maxViewportWidth).append("\n");
        sb.append("Max Viewport Height: ").append(maxViewportHeight).append("\n");
        sb.append("Min Aliased Line Width: ").append(minAliasedLineWidth).append("\n");
        sb.append("Max Aliased Line Width: ").append(maxAliasedLineWidth).append("\n");
        sb.append("Min Aliased Point Size: ").append(minAliasedPointSize).append("\n");
        sb.append("Max Aliased Point Width: ").append(maxAliasedPointSize).append("\n");
        return sb.toString();
    }
}