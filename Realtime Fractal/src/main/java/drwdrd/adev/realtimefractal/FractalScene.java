package drwdrd.adev.realtimefractal;


import java.util.ArrayList;

import drwdrd.adev.engine.vector2f;

public class FractalScene {

    public class SceneNode {

        public SceneNode(float startTime,vector2f paramC,vector2f centerPoint,float scale,float rotation) {
            this.startTime=startTime;
            this.paramC=paramC;
            this.centerPoint=centerPoint;
            this.scale=scale;
            this.rotation=rotation;
        }

        public vector2f paramC;
        public vector2f centerPoint;
        public float scale;
        public float rotation;
        public float startTime;
    }

    public FractalScene() {
        scene=new ArrayList<SceneNode>();
    }

    public void setLooping(boolean loop) { this.loop=loop; }

    public SceneNode getCurrentSceneNode() { return currentSceneNode; }

    public ArrayList<SceneNode> getNodes() { return scene; }

    public int addSceneNode(float startTime,vector2f paramC,vector2f centerPoint,float scale,float rotation) {
        SceneNode node=new SceneNode(startTime,paramC,centerPoint,scale,rotation);
        return addSceneNode(node);
    }

    public int addSceneNode(SceneNode sceneNode) {
        int i=0;
        while(i<scene.size()) {
            if(scene.get(i).startTime<=sceneNode.startTime) {
                i++;
            } else {
                break;
            }
        }
        scene.add(i,sceneNode);
        return i;
    }

    public int appendSceneNode(vector2f paramC,vector2f centerPoint,float scale,float rotation) {
        SceneNode node=new SceneNode(0.0f,paramC,centerPoint,scale,rotation);
        return appendSceneNode(node);
    }

    public int appendSceneNode(SceneNode sceneNode) {
        scene.add(sceneNode);
        return scene.size()-1;
    }

    public void recalculateNodeTimers() {
        float totalDist=0.0f;
        for(int i=1;i<scene.size();i++) {
            totalDist+=Math.max(vector2f.sub(scene.get(i).paramC,scene.get(i-1).paramC).length(), 0.01f);
        }
        scene.get(0).startTime=0.0f;
        float dist=0.0f;
        for(int i=1;i<scene.size();i++) {
            dist+=Math.max(vector2f.sub(scene.get(i).paramC,scene.get(i-1).paramC).length(), 0.01f);
            scene.get(i).startTime=dist/totalDist;
        }
    }

    public SceneNode removeSceneNode(int index) {
        return scene.remove(index);
    }

    public SceneNode getSceneNode(int index) { return scene.get(index); }

    public int getSceneNodesCount() { return scene.size(); }

    public void deleteSceneNode(int index) {
        scene.remove(index);
    }

    public void clear() {
        scene.clear();
    }

    public SceneNode playScene(float time) {
        SceneNode startNode=null;
        SceneNode endNode=null;
        if(scene.size()==1) {
            startNode=scene.get(0);
            endNode=scene.get(0);
        }
        else {
            int idx=findStartingNode(time);
//            LogSystem.debug(RealtimeFractalService.tag,"findStartingNode(" + time + ") = " + idx);
            idx=Math.max(idx,0);
            startNode=scene.get(idx);
            if(loop==true) {            //TODO: not working due to findStartingNode() bug/strange behavior
                endNode = scene.get( idx<scene.size()-1 ? idx+1 : 0 );
            }
            else {
                endNode = scene.get( idx<scene.size()-1 ? idx+1 : scene.size()-1 );
            }

        }
        float d=0.0f;
        float dt=endNode.startTime-startNode.startTime;
        if(dt>0.000000001f) {
            d = (time - startNode.startTime) / (endNode.startTime - startNode.startTime);
        }
        vector2f paramC=vector2f.mix(endNode.paramC,startNode.paramC,d);
        vector2f centerPoint=vector2f.mix(endNode.centerPoint,startNode.centerPoint,d);
        float scale=d*endNode.scale+(1.0f-d)*startNode.scale;
        float rotation=d*endNode.rotation+(1.0f-d)*startNode.rotation;
        currentSceneNode=new SceneNode(time,paramC,centerPoint,scale,rotation);
        return currentSceneNode;
    }

    private int findStartingNode(float time) {
        for(int i=0;i<scene.size();i++) {
            if(time<scene.get(i).startTime) {
                return i-1;
            }
        }
        return scene.size()-1;
    }

    private ArrayList<SceneNode> scene=null;
    private SceneNode currentSceneNode=null;
    private boolean loop=true;
}
