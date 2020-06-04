package drwdrd.adev.engine;

import android.os.SystemClock;


public class TimeCounter {

    public TimeCounter(double timeScale) {
        this.timeScale = timeScale;
    }

    public TimeCounter(double timeScale, double cycleDuration) {
        this.timeScale = timeScale;
        this.cycleDuration = cycleDuration;
    }

    public void setTimeScale(double timeScale) {
        this.timeScale = timeScale;
    }

    public double getTimeScale() {
        return timeScale;
    }

    public void setCycleDuration(double cycleDuration) {
        this.cycleDuration = cycleDuration;
        LogSystem.debug(EngineUtils.tag, String.format("Timer duration set to: %f", cycleDuration));
    }

    public void start() {
        startTime = SystemClock.uptimeMillis();
        endTime = startTime;
        lastTime = startTime;
    }

    public void reset() {
        startTime = 0;
        endTime = 0;
        lastTime = 0;
        currentTime = 0.0;
        start();
    }

    public void stop() {
        endTime = SystemClock.uptimeMillis();
    }

    public double delta() {
        long currentTime = SystemClock.uptimeMillis();
        double deltaTime = timeScale * (currentTime - lastTime);
        lastTime = currentTime;
        return deltaTime;
    }

    public double tick() {
        currentTime += delta();
        if (currentTime > cycleDuration) {
            currentTime -= cycleDuration;
            LogSystem.debug(EngineUtils.tag, "Timer duration expired...");
        }
        return currentTime;
    }

    public double time() {
        return timeScale * (endTime - startTime);
    }

    public double currentTime() {
        return timeScale * (SystemClock.uptimeMillis() - startTime);
    }

    private long startTime = 0;
    private long endTime = 0;
    private long lastTime = 0;
    private double currentTime = 0.0;
    private double timeScale = 1.0;
    private double cycleDuration = 1000.0;
}