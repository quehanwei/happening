package blue.happening.mesh;

public abstract class RemoteDevice implements IRemoteDevice {

    private final String uuid;
    private long lastSeen;
    private SlidingWindow echoSlidingWindow;
    private SlidingWindow receiveSlidingWindow;
    private MeshDevice meshDevice;

    public RemoteDevice(String uuid) {
        this.uuid = uuid;
        meshDevice = new MeshDevice();
        meshDevice.setUuid(uuid);
        lastSeen = System.currentTimeMillis();
        echoSlidingWindow = new SlidingWindow();
        receiveSlidingWindow = new SlidingWindow();
    }

    SlidingWindow getEchoSlidingWindow() {
        return echoSlidingWindow;
    }

    SlidingWindow getReceiveSlidingWindow() {
        return receiveSlidingWindow;
    }

    public final String getUuid() {
        return uuid;
    }

    long getLastSeen() {
        return lastSeen;
    }

    void setLastSeen(long lastSeen) {
        this.lastSeen = lastSeen;
    }

    boolean isExpired() {
        long expirationMillis = MeshHandler.DEVICE_EXPIRATION * 1000L;
        return System.currentTimeMillis() - lastSeen > expirationMillis;
    }

    public final float getEq() {
        return ((float) echoSlidingWindow.size()) / MeshHandler.SLIDING_WINDOW_SIZE;
    }

    public final float getRq() {
        return ((float) receiveSlidingWindow.size()) / MeshHandler.SLIDING_WINDOW_SIZE;
    }

    public final float getTq() {
        if (getEq() / getRq() > 1) {
            // XXX Eq should not exceed Rq
            return 1f;
        } else {
            return getEq() / getRq();
        }
    }

    MeshDevice getMeshDevice() {
        meshDevice.setQuality(getTq());
        meshDevice.setLastSeen(getLastSeen());
        return meshDevice;
    }

    public abstract boolean sendMessage(Message message);

    public abstract boolean remove();

    @Override
    public int compareTo(IRemoteDevice other) {
        return Float.compare(
                this.getTq(),
                other.getTq());
    }

    @Override
    public final boolean equals(Object object) {
        if (object == null) {
            return false;
        } else if (!(object instanceof RemoteDevice)) {
            return false;
        }
        return ((RemoteDevice) object).getUuid().equals(getUuid());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + getUuid();
    }
}
