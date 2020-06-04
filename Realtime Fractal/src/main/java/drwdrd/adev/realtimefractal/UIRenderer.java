package drwdrd.adev.realtimefractal;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.opengl.GLES20;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.SizeF;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.Window;

import java.util.ArrayList;


import drwdrd.adev.engine.EngineUtils;
import drwdrd.adev.engine.Font;
import drwdrd.adev.engine.GLBufferObject;
import drwdrd.adev.engine.GLTexture;
import drwdrd.adev.engine.LogSystem;
import drwdrd.adev.engine.Mesh;
import drwdrd.adev.engine.ProgramObject;
import drwdrd.adev.engine.ShaderObject;
import drwdrd.adev.engine.TextFileReader;
import drwdrd.adev.engine.TextureSampler;
import drwdrd.adev.engine.TextureUnit;
import drwdrd.adev.engine.VertexArray;
import drwdrd.adev.engine.matrix3f;
import drwdrd.adev.engine.vector2f;
import drwdrd.adev.engine.vector4f;
import drwdrd.adev.realtimefractal.UI.Menu;
import drwdrd.adev.realtimefractal.preferences.FractalSceneEditorActivity;
import drwdrd.adev.ui.RotationGestureDetector;
import drwdrd.adev.realtimefractal.UI.IconManager;
import drwdrd.adev.realtimefractal.UI.Button;

public class UIRenderer {

    //sets viewport translates coordinates from pixelspace to opengl
    private class UiViewport {
        private int screenWidth = 1;
        private int screenHeight = 1;
        private vector2f aspect = new vector2f(1.0f, 1.0f);
        private RectF fractalRect = new RectF(-2.0f, -2.0f, 2.0f, 2.0f);

        public UiViewport(int width, int height) {
            screenWidth = width;
            screenHeight = height;
            if (width > height) {
                aspect = new vector2f((float) height / (float) width, 1.0f);
            } else {
                aspect = new vector2f(1.0f, (float) width / (float) height);
            }
        }

        public void set() {
            GLES20.glViewport(0, 0, screenWidth, screenHeight);
        }

        public vector2f aspect() {
            return aspect;
        }

        public vector2f pixelSpaceToViewport(float px, float py) {
            float x = 2.0f * (px - windowFrameRect.left) / windowFrameRect.width() - 1.0f;
            float y = -2.0f * (py - windowFrameRect.top) / windowFrameRect.height() + 1.0f;
            return new vector2f(x, y);
        }

        public vector2f pixelSpaceToFractalCoord(float px, float py) {
            float x = fractalRect.width() * (px - windowRect.left) / windowRect.width() + fractalRect.left;
            float y = fractalRect.height() * (py - windowRect.top) / windowRect.height() + fractalRect.top;
            return new vector2f(x, y);
        }

        public vector2f fractalCoordToPixelSpace(float px, float py) {
            float x = windowRect.width() * (px - fractalRect.left) / fractalRect.width() + windowRect.left;
            float y = windowRect.height() * (py - fractalRect.top) / fractalRect.height() + windowRect.top;
            return new vector2f(x, y);
        }
    }

    protected Context context = null;
    protected RealtimeFractalRenderer renderer = null;
    protected UiViewport viewport = null;
    protected IconManager iconManager = null;
    protected Font font = null;

    protected Mesh buttonMeshData = null;

    protected String vertexProgramSource = null;
    protected String fragmentProgramSource = null;

    protected ProgramObject program = null;
    protected VertexArray buttonVertexArray = null;

    //toolbar
    protected TextureUnit toolBarTexture = null;

    protected Menu uiElements = null;

    protected boolean initialized = false;

    protected UiState uiState = UiState.Play;

    private int currentSceneNodeIndex = -1;

    private vector2f currentParamC = null;
    private vector2f currentCenterPoint = null;
    private float currentScaleFactor = 1.0f;
    private float currentRotationAngle = 0.0f;
    private vector2f fractalMoveStartPoint = null;
    private vector2f fractalMoveEndPoint = null;

    private ScaleGestureDetector scaleGestureDetector = null;
    private RotationGestureDetector rotationGestureDetector = null;
    private int activePointerId = -1;
    private boolean isMoveInProgress = false;

    //ui elements size in pixels, calculated per screen depends on dpi
    private Rect windowRect = null;         //main window rect
    private Rect toolbarRect = null;         // toolbar rect
    private Rect toolbarButtonSize = null;   //toolbar button size
    private int buttonSpacing = 0;
    private RectF nodeRect = null; //new RectF(0.0f,0.0f,0.09f,0.09f);
    private RectF node2Rect = null; //new RectF(0.0f,0.0f,0.05f,0.05f);

    private Rect windowFrameRect = null;

    public enum UiState {

        Edit, Move, Play

    }

    private class ButtonId {
        public final static int Move = 1;
        public final static int Play = 2;
        public final static int AddNode = 3;
        public final static int RemoveNode = 4;
    }

    public UIRenderer() {

    }

    public void onCreate(RealtimeFractalRenderer renderer, Context context) {

        this.renderer = renderer;
        this.context = context;
        this.iconManager = new IconManager();

        Bitmap b = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        b.eraseColor(0xff0f0f0f);
        GLTexture tex = new GLTexture();
        tex.createTexture2DFromBitmap(b, true);

        toolBarTexture = new TextureUnit(tex, new TextureSampler(GLTexture.Target.Texture2D));

        font = new Font();
        if (!font.loadFont(context, "fonts/default", true)) {
            if (!font.loadFont(context, "default", false)) {
                font = Font.DistanceFieldFontGenerator.generateFont("sans-serif", Typeface.NORMAL, 256, 8, 32, 32);
                font.saveFont(context, "default");
            }
        }


        buttonMeshData = Mesh.MeshGenerator.createSimplePlane();

        vertexProgramSource = TextFileReader.readFromAssets(context, "shaders/ui.vert");
        fragmentProgramSource = TextFileReader.readFromAssets(context, "shaders/ui.frag");

        uiElements = new Menu();
        Menu.MenuItem edit = uiElements.add(ButtonId.Play, "play.png", "pause.png", true);
        edit.add(ButtonId.Move, "move.png", "move.png", true);
        edit.add(ButtonId.AddNode, "add.png", "add.png", false);
        edit.add(ButtonId.RemoveNode, "remove.png", "remove.png", false);
        uiElements.setOnItemClickedListener(new Menu.OnItemClickedListener() {
            @Override
            public void onItemClicked(Button item) {
                onButtonPressed(item);
            }
        });


        scaleGestureDetector = new ScaleGestureDetector(context, new ScaleListener());
        rotationGestureDetector = new RotationGestureDetector(new RotationListener());

    }

    public void onDestroy() {

    }

    public boolean isInitialized() {
        return initialized;
    }

    public void onRelease() {
        initialized = false;
        if (iconManager != null) {
            iconManager.delete();
        }
        if (font != null) {
            font.delete();
        }
        if (program != null) {
            program.delete();
            program = null;
        }
        if (buttonVertexArray != null) {
            buttonVertexArray.delete();
            buttonVertexArray = null;
        }
    }

    public void onInitialize() {
        windowFrameRect = ((FractalSceneEditorActivity) context).getWindowRect();

        if (windowFrameRect.width() < windowFrameRect.height()) {

            int h = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, context.getResources().getDisplayMetrics());

            toolbarRect = new Rect(windowFrameRect.left, windowFrameRect.top, windowFrameRect.right, h + windowFrameRect.top);
            windowRect = new Rect(windowFrameRect.left, toolbarRect.bottom, windowFrameRect.right, windowFrameRect.bottom);
            int sz = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48, context.getResources().getDisplayMetrics());
            toolbarButtonSize = new Rect(0, 0, sz, sz);
            buttonSpacing = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, context.getResources().getDisplayMetrics());

            int nsz = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 18, context.getResources().getDisplayMetrics());
            float nszx = 2.0f * nsz / windowFrameRect.width();
            float nszy = 2.0f * nsz / windowFrameRect.height();
            nodeRect = new RectF(0.0f, 0.0f, nszx, nszy);
            node2Rect = new RectF(0.0f, 0.0f, 0.5f * nszx, 0.5f * nszy);

        } else {

            int h = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, context.getResources().getDisplayMetrics());

            toolbarRect = new Rect(windowFrameRect.left, windowFrameRect.top, windowFrameRect.left + h, windowFrameRect.bottom);
            windowRect = new Rect(toolbarRect.right, windowFrameRect.top, windowFrameRect.right, windowFrameRect.bottom);
            int sz = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48, context.getResources().getDisplayMetrics());
            toolbarButtonSize = new Rect(0, 0, sz, sz);
            buttonSpacing = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, context.getResources().getDisplayMetrics());

            int nsz = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 18, context.getResources().getDisplayMetrics());
            float nszx = 2.0f * nsz / windowFrameRect.width();
            float nszy = 2.0f * nsz / windowFrameRect.height();
            nodeRect = new RectF(0.0f, 0.0f, nszx, nszy);
            node2Rect = new RectF(0.0f, 0.0f, 0.5f * nszx, 0.5f * nszy);
        }


        iconManager.init(context, "icons");

        font.create();

        buttonVertexArray = new VertexArray(buttonMeshData.createVertexBuffer(GLBufferObject.Usage.StaticDraw), buttonMeshData.createIndexBuffer(GLBufferObject.Usage.StaticDraw));


        ShaderObject vertexProgram = new ShaderObject("vertexProgram", ShaderObject.ShaderType.VertexShader);
        vertexProgram.compile(vertexProgramSource);

        ShaderObject fragmentProgram = new ShaderObject("fragmentProgram", ShaderObject.ShaderType.FragmentShader);
        fragmentProgram.compile(fragmentProgramSource);

        program = new ProgramObject("ui.prog");
        program.attachShader(vertexProgram);
        program.attachShader(fragmentProgram);
        program.bindAttribLocation(0, "position");
        program.link();

        vertexProgram.delete();
        fragmentProgram.delete();

        initialized = true;
    }

    public void setViewport(int width, int height) {
        if (height == 0) {
            height = 1;
        }
        viewport = new UiViewport(width, height);
    }

    public void onRender(float time) {

        viewport.set();


        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        program.bind();
        buttonVertexArray.bind();


        vector2f p1 = viewport.pixelSpaceToViewport(toolbarRect.left, toolbarRect.top);
        vector2f p2 = viewport.pixelSpaceToViewport(toolbarRect.right, toolbarRect.bottom);

        float sx = 0.5f * (p2.ex - p1.ex);
        float sy = -0.5f * (p2.ey - p1.ey);

        matrix3f model = new matrix3f();
        model.loadIdentity();
        model.setTranslationPart(p1.ex + sx, p1.ey - sy);
        model.setScalePart(sx, sy);


        program.setUniformValue("color", new vector4f(1.0f, 1.0f, 1.0f, 0.5f));
        program.setUniformValue("modelMatrix", model);

        toolBarTexture.bind(0);
        program.setSampler("icon", 0);

        buttonVertexArray.draw();

        toolBarTexture.release();

        int cc = 0;
        for (Menu.MenuItem item : uiElements.getCurrentMenu().getSubMenu()) {
            Button button = item.getButton();

            if (windowFrameRect.width() < windowFrameRect.height()) {
                button.setRectangle(buttonSpacing + cc * (toolbarButtonSize.width() + buttonSpacing) + windowFrameRect.left, buttonSpacing + windowFrameRect.top, toolbarButtonSize.width() + cc * (toolbarButtonSize.width() + buttonSpacing) + windowFrameRect.left, toolbarButtonSize.height() + windowFrameRect.top);
            } else {
                button.setRectangle(buttonSpacing + windowFrameRect.left, buttonSpacing + cc * (toolbarButtonSize.height() + buttonSpacing) + windowFrameRect.top, toolbarButtonSize.width() + windowFrameRect.left, toolbarButtonSize.height() + cc * (toolbarButtonSize.height() + buttonSpacing) + windowFrameRect.top);
            }

            p1 = viewport.pixelSpaceToViewport(button.getRectangle().left, button.getRectangle().top);
            p2 = viewport.pixelSpaceToViewport(button.getRectangle().right, button.getRectangle().bottom);

            sx = 0.5f * (p2.ex - p1.ex);
            sy = -0.5f * (p2.ey - p1.ey);

            model = new matrix3f();
            model.loadIdentity();
            model.setTranslationPart(p1.ex + sx, p1.ey - sy);
            model.setScalePart(sx, sy);

            TextureUnit textureUnit;

            program.setUniformValue("color", vector4f.fromColor(button.getBackgroundColor()));
            textureUnit = iconManager.getIcon(button.getIcon());

            program.setUniformValue("modelMatrix", model);

            textureUnit.bind(0);
            program.setSampler("icon", 0);

            buttonVertexArray.draw();

            textureUnit.release();
            cc++;
        }

        //draw nodes
        GLES20.glBlendFunc(GLES20.GL_ONE_MINUS_DST_COLOR, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        if (uiState == UiState.Edit || uiState == UiState.Play) {

            FractalScene editedFractalScene = RealtimeFractalService.getFractalSettings().fractalScene;

            model = new matrix3f();

            FractalScene.SceneNode currentNode = editedFractalScene.getCurrentSceneNode();
            if (currentNode != null) {

                sx = 0.5f * node2Rect.width();
                sy = 0.5f * node2Rect.height();


                model.loadIdentity();

                vector2f pt = viewport.fractalCoordToPixelSpace(currentNode.paramC.ex, currentNode.paramC.ey);
                pt = viewport.pixelSpaceToViewport(pt.ex, pt.ey);
                model.setTranslationPart(pt.ex + node2Rect.left, pt.ey + node2Rect.top);

                model.setScalePart(sx, sy);

                program.setUniformValue("color", new vector4f(1.0f, 1.0f, 1.0f, 1.0f));

                program.setUniformValue("modelMatrix", model);

                TextureUnit t = iconManager.getIcon("node.png");
                t.bind(0);
                program.setSampler("icon", 0);

                buttonVertexArray.draw();

                t.release();
            }

            int idx = 0;


            sx = 0.5f * nodeRect.width();
            sy = 0.5f * nodeRect.height();

            //CME avoiding ;)
            ArrayList<FractalScene.SceneNode> nodes = new ArrayList<>(editedFractalScene.getNodes());

            for (FractalScene.SceneNode node : nodes) {

                model.loadIdentity();
                vector2f pt = viewport.fractalCoordToPixelSpace(node.paramC.ex, node.paramC.ey);
                pt = viewport.pixelSpaceToViewport(pt.ex, pt.ey);
                model.setTranslationPart(pt.ex + nodeRect.left, pt.ey + nodeRect.top);
                model.setScalePart(sx, sy);

                if (currentSceneNodeIndex == idx) {
                    program.setUniformValue("color", new vector4f(1.0f, 1.0f, 1.0f, 1.0f));

                } else {
                    program.setUniformValue("color", new vector4f(0.5f, 0.5f, 0.5f, 1.0f));
                }

                program.setUniformValue("modelMatrix", model);

                TextureUnit t = iconManager.getIcon("node.png");

                t.bind(0);
                program.setSampler("icon", 0);

                buttonVertexArray.draw();

                t.release();
                idx++;

            }

        }

        buttonVertexArray.release();
        program.release();

        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        font.bind(0);
        String fps = String.format("%.0f", renderer.getFrameCounter().getFPS());
        vector2f scale = new vector2f(0.05f, 0.05f);
        scale.mul(viewport.aspect());
        font.drawText(fps, scale.ex, scale.ey, 0.8f, 0.975f);
        font.release();

        GLES20.glDisable(GLES20.GL_BLEND);
    }

    public boolean onTouchEvent(MotionEvent event) {
        LogSystem.debug("MotionEvent = " + event.getActionMasked(), "(" + event.getX() + "," + event.getY() + ")");

        // get masked (not specific to a pointer) action
        int maskedAction = event.getActionMasked();

        if (maskedAction == MotionEvent.ACTION_DOWN) {
            activePointerId = event.getPointerId(0);
        }

        //filter out all events handled by ui elements
        if (handleButtons(event.getX(), event.getY(), maskedAction) == true) {
            LogSystem.debug("UIRenderer", "Button event");
            return true;
        }

        if (!(event.getY() >= windowRect.top &&
                event.getY() <= windowRect.bottom &&
                event.getX() >= windowRect.left &&
                event.getX() <= windowRect.right)) {
            return false;
        }


        if (uiState == UiState.Move) {
            scaleGestureDetector.onTouchEvent(event);
            rotationGestureDetector.onTouchEvent(event);
        }

        FractalScene editedFractalScene = RealtimeFractalService.getFractalSettings().fractalScene;

        switch (maskedAction) {
            case MotionEvent.ACTION_DOWN:
                if (uiState == UIRenderer.UiState.Edit) {
                    vector2f pt = viewport.pixelSpaceToFractalCoord(event.getX(), event.getY());
                    LogSystem.debug("MotionEvent.ACTION_DOWN", "(" + pt.ex + "," + pt.ey + ")");
                    currentParamC = new vector2f(pt.ex, pt.ey);
                    editedFractalScene.getSceneNode(currentSceneNodeIndex).paramC = currentParamC;
                } else if (uiState == UIRenderer.UiState.Move) {
                    if (!scaleGestureDetector.isInProgress() && !rotationGestureDetector.isInProgress() && !isMoveInProgress) {
                        vector2f pt = viewport.pixelSpaceToViewport(event.getX(), event.getY());
                        LogSystem.debug("MotionEvent.ACTION_DOWN", "(" + pt.ex + "," + pt.ey + ")");
                        isMoveInProgress = true;
                        fractalMoveStartPoint = new vector2f(pt.ex, pt.ey);
                        fractalMoveEndPoint = new vector2f(pt.ex, pt.ey);
                    }
                }
                return true;
            case MotionEvent.ACTION_MOVE: {
                int pointerIndex = event.findPointerIndex(activePointerId);
                if (uiState == UIRenderer.UiState.Edit) {
                    vector2f npt = viewport.pixelSpaceToFractalCoord(event.getX(pointerIndex), event.getY(pointerIndex));
                    LogSystem.debug("MotionEvent.ACTION_MOVE", "(" + npt.ex + "," + npt.ey + ")");
                    currentParamC = new vector2f(npt.ex, npt.ey);
                    editedFractalScene.getSceneNode(currentSceneNodeIndex).paramC = currentParamC;
                } else if (uiState == UIRenderer.UiState.Move) {
                    if (!scaleGestureDetector.isInProgress() && !rotationGestureDetector.isInProgress() && isMoveInProgress) {
                        vector2f npt = viewport.pixelSpaceToViewport(event.getX(pointerIndex), event.getY(pointerIndex));
                        LogSystem.debug("MotionEvent.ACTION_MOVE", "(" + npt.ex + "," + npt.ey + ")");
                        fractalMoveEndPoint = new vector2f(npt.ex, npt.ey);
                        vector2f dv = vector2f.sub(fractalMoveStartPoint, fractalMoveEndPoint);
                        //rotate vector to old axis
                        matrix3f rotationMatrix = new matrix3f();
                        rotationMatrix.setRotation(currentRotationAngle);
                        dv = vector2f.rotated(dv, rotationMatrix);
                        currentCenterPoint.add(vector2f.mul(currentScaleFactor, dv));
                        fractalMoveStartPoint = fractalMoveEndPoint;
                        editedFractalScene.getSceneNode(currentSceneNodeIndex).centerPoint = currentCenterPoint;
                    }
                }
            }
            return true;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                LogSystem.debug("MotionEvent.ACTION_UP", " ");
                isMoveInProgress = false;
                activePointerId = -1;
                return true;
            case MotionEvent.ACTION_POINTER_UP: {
                int pointerIndex = (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                int pointerId = event.getPointerId(pointerIndex);
                if (pointerId == activePointerId) {
                    //TODO: poprawić wciaż nie działa tak jak trzeba
                    // This was our active pointer going up. Choose a new
                    // active pointer and adjust accordingly.
                    int newPointerIndex = (pointerIndex == 0 ? 1 : 0);
                    activePointerId = event.getPointerId(newPointerIndex);
                    if (uiState == UiState.Edit) {
                        vector2f npt = viewport.pixelSpaceToFractalCoord(event.getX(newPointerIndex), event.getY(newPointerIndex));
                        LogSystem.debug("MotionEvent.ACTION_POINTER_UP", "(" + npt.ex + "," + npt.ey + ")");
                        currentParamC = new vector2f(npt.ex, npt.ey);
                        editedFractalScene.getSceneNode(currentSceneNodeIndex).paramC = currentParamC;
                    } else if (uiState == UiState.Move) {
                        vector2f npt = viewport.pixelSpaceToViewport(event.getX(newPointerIndex), event.getY(newPointerIndex));
                        LogSystem.debug("MotionEvent.ACTION_POINTER_UP", "(" + npt.ex + "," + npt.ey + ")");
                        isMoveInProgress = false;
                        fractalMoveStartPoint = new vector2f(npt.ex, npt.ey);
                        fractalMoveEndPoint = new vector2f(npt.ex, npt.ey);
                    }
                }
            }
            return true;
        }
        return false;
    }

    private boolean handleButtons(float x, float y, int action) {
        if (uiElements.onTouchEvent(x, y, action) == true) {
            return true;
        }
        if (uiState == UiState.Edit) {
            if (action == MotionEvent.ACTION_DOWN) {
                int idx = 0;
                FractalScene editedFractalScene = RealtimeFractalService.getFractalSettings().fractalScene;
                for (FractalScene.SceneNode node : editedFractalScene.getNodes()) {
                    vector2f pt = viewport.pixelSpaceToFractalCoord(x, y);
                    if (contains(node.paramC, pt.ex, pt.ey, 0.1f) == true) {
                        LogSystem.debug("Node selected: ", Integer.toString(idx));
                        currentSceneNodeIndex = idx;
                        editSceneNode(idx);
                        return true;
                    }
                    idx++;
                }
            }

        }
        return false;
    }


    private void onButtonPressed(Button button) {
        FractalScene editedFractalScene = RealtimeFractalService.getFractalSettings().fractalScene;
        switch (button.getId()) {
            case ButtonId.Move:
                if (button.isChecked()) {
                    uiState = UiState.Move;
                    editSceneNode(currentSceneNodeIndex);
                } else {
                    uiState = UiState.Edit;
                    editSceneNode(currentSceneNodeIndex);
                }
                break;
            case ButtonId.Play:
                //apply and play
                if (button.isChecked()) {
                    uiState = UiState.Edit;
                    editSceneNode(currentSceneNodeIndex);
                } else {
                    uiState = UiState.Play;
                    editedFractalScene.recalculateNodeTimers();
                    renderer.startScene(editedFractalScene);
                }
                break;
            case ButtonId.AddNode:
                if (uiState == UiState.Edit) {
                    currentSceneNodeIndex = editedFractalScene.appendSceneNode(new vector2f(0.0f, 0.0f), new vector2f(0.0f, 0.0f), 2.0f, 0.0f);
                    editSceneNode(currentSceneNodeIndex);
/*                    if (editedFractalScene.getSceneNodesCount() == 1) {
                        currentSceneNodeIndex = editedFractalScene.addSceneNode(0.0f, new vector2f(0.0f, 0.0f), new vector2f(0.0f, 0.0f), 2.0f, 0.0f);
                        editSceneNode(currentSceneNodeIndex);
                    } else {
                        FractalScene.SceneNode scene=editedFractalScene.getCurrentSceneNode();
                        if(scene!=null) {
                            currentSceneNodeIndex = editedFractalScene.addSceneNode(scene);
                            editSceneNode(currentSceneNodeIndex);
                        }
                    }*/
                }
                break;
            case ButtonId.RemoveNode:
                if (uiState == UiState.Edit) {
                    if (editedFractalScene.getSceneNodesCount() > 1) {
                        editedFractalScene.removeSceneNode(currentSceneNodeIndex);
                        currentSceneNodeIndex = Math.max(currentSceneNodeIndex - 1, 0);
/*                        if (editedFractalScene.getSceneNodesCount() == 1) {
                            editedFractalScene.getSceneNode(0).startTime = 0.0f;
                        }*/
                    }
                }
                break;
            default:
                uiState = UiState.Play;
                break;
        }
        LogSystem.debug("Button pressed: ", Integer.toString(button.getId()));
    }

    //sets ui to editing scene node of given index
    private void editSceneNode(int index) {
        LogSystem.debug("editSceneNode", "(" + index + ")");
        FractalScene editedFractalScene = RealtimeFractalService.getFractalSettings().fractalScene;
        EngineUtils.Assert(index < editedFractalScene.getSceneNodesCount(), "Invalid index");
        currentSceneNodeIndex = ((index == -1) ? 0 : index);
        currentParamC = new vector2f(editedFractalScene.getSceneNode(currentSceneNodeIndex).paramC);
        currentCenterPoint = new vector2f(editedFractalScene.getSceneNode(currentSceneNodeIndex).centerPoint);
        currentScaleFactor = editedFractalScene.getSceneNode(currentSceneNodeIndex).scale;
        currentRotationAngle = editedFractalScene.getSceneNode(currentSceneNodeIndex).rotation;

        FractalScene scene = new FractalScene();
        scene.addSceneNode(editedFractalScene.getSceneNode(currentSceneNodeIndex));
        renderer.startScene(scene);

    }

    private static boolean contains(vector2f pt, float x, float y, float r) {
        if (pt.ex - r < x && x < pt.ex + r &&
                pt.ey - r < y && y < pt.ey + r) {
            return true;
        }
        return false;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            currentScaleFactor *= detector.getScaleFactor();
            // Don't let the object get too small or too large.
            currentScaleFactor = Math.max(0.0001f, Math.min(currentScaleFactor, 10.0f));
            LogSystem.debug(RealtimeFractalService.tag, "onScale(): scaleFactor = " + currentScaleFactor);
            FractalScene editedFractalScene = RealtimeFractalService.getFractalSettings().fractalScene;
            editedFractalScene.getSceneNode(currentSceneNodeIndex).scale = currentScaleFactor;
            isMoveInProgress = false;
            return true;
        }
    }

    private class RotationListener implements RotationGestureDetector.OnRotationGestureListener {

        @Override
        public void OnRotation(RotationGestureDetector rotationDetector) {
            currentRotationAngle -= rotationDetector.getDeltaRotationAngle();
            LogSystem.debug(RealtimeFractalService.tag, "onRotation(): rotationAngle = " + currentRotationAngle);
            FractalScene editedFractalScene = RealtimeFractalService.getFractalSettings().fractalScene;
            editedFractalScene.getSceneNode(currentSceneNodeIndex).rotation = currentRotationAngle;
            isMoveInProgress = false;
        }
    }
}
