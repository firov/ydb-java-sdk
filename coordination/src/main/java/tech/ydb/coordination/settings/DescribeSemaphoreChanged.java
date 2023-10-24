package tech.ydb.coordination.settings;

import java.util.Objects;

public class DescribeSemaphoreChanged {
    private final boolean dataChanged;
    private final boolean ownersChanged;
    private final boolean connectionWasFailed;

    public DescribeSemaphoreChanged(boolean dataChanged, boolean ownersChanged, boolean connectionWasFailed) {
        this.dataChanged = dataChanged;
        this.ownersChanged = ownersChanged;
        this.connectionWasFailed = connectionWasFailed;
    }

    public boolean isDataChanged() {
        return dataChanged;
    }

    public boolean isOwnersChanged() {
        return ownersChanged;
    }

    public boolean isConnectionWasFailed() {
        return connectionWasFailed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DescribeSemaphoreChanged)) {
            return false;
        }
        DescribeSemaphoreChanged that = (DescribeSemaphoreChanged) o;
        return dataChanged == that.dataChanged && ownersChanged == that.ownersChanged &&
                connectionWasFailed == that.connectionWasFailed;
    }

    @Override
    public int hashCode() {
        return Objects.hash(dataChanged, ownersChanged, connectionWasFailed);
    }

    @Override
    public String toString() {
        return "DescribeSemaphoreChanged{" +
                "dataChanged=" + dataChanged +
                ", ownersChanged=" + ownersChanged +
                ", connectionWasFailed=" + connectionWasFailed +
                '}';
    }
}