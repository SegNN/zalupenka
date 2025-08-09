/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.utils.math;

public class Counter {
    private long lastMS;

    private Counter() {
        this.reset();
    }

    public static Counter create() {
        return new Counter();
    }

    public void reset() {
        this.lastMS = System.currentTimeMillis();
    }

    public long elapsedTime() {
        return System.currentTimeMillis() - this.lastMS;
    }

    public boolean hasReached(long time) {
        return this.elapsedTime() >= time;
    }

    public boolean hasReached(long time, boolean reset) {
        boolean hasElapsed;
        boolean bl = hasElapsed = this.elapsedTime() >= time;
        if (hasElapsed && reset) {
            this.reset();
        }
        return hasElapsed;
    }

    public boolean hasReached(double ms) {
        return (double)this.elapsedTime() >= ms;
    }

    public boolean delay(long ms) {
        boolean hasDelayElapsed;
        boolean bl = hasDelayElapsed = this.elapsedTime() - ms >= 0L;
        if (hasDelayElapsed) {
            this.reset();
        }
        return hasDelayElapsed;
    }
}

