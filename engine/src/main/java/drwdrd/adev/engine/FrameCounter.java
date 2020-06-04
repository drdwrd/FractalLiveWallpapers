package drwdrd.adev.engine;


public class FrameCounter {

    public FrameCounter(double measuretime) {
        this.measureTime = measuretime;
    }

    public void start() {
        timer.start();
        framesCounter = 0;
    }

    public void tick() {
        framesCounter++;
        double time = timer.currentTime();
        if (time > measureTime) {
            currentFPS=(1000.0f*framesCounter)/(float)time;
            LogSystem.debug(EngineUtils.tag, String.format("FPS: %f", currentFPS));
            framesCounter = 0;
            timer.reset();
        }
    }

    public float getFPS() {
        return currentFPS;
    }

    private long framesCounter;
    private double measureTime;
    private TimeCounter timer = new TimeCounter(1.0);
    private float currentFPS = 0;
}