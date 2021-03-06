package java.util.stream;

import java.util.Spliterator;
import java.util.concurrent.CountedCompleter;
import java.util.concurrent.ForkJoinPool;

abstract class AbstractTask<P_IN, P_OUT, R, K extends AbstractTask<P_IN, P_OUT, R, K>> extends CountedCompleter<R> {
    static final int LEAF_TARGET = (ForkJoinPool.getCommonPoolParallelism() << 2);
    protected final PipelineHelper<P_OUT> helper;
    protected K leftChild;
    private R localResult;
    protected K rightChild;
    protected Spliterator<P_IN> spliterator;
    protected long targetSize;

    protected abstract R doLeaf();

    protected abstract K makeChild(Spliterator<P_IN> spliterator);

    protected AbstractTask(PipelineHelper<P_OUT> helper, Spliterator<P_IN> spliterator) {
        super(null);
        this.helper = helper;
        this.spliterator = spliterator;
        this.targetSize = 0;
    }

    protected AbstractTask(K parent, Spliterator<P_IN> spliterator) {
        super(parent);
        this.spliterator = spliterator;
        this.helper = parent.helper;
        this.targetSize = parent.targetSize;
    }

    public static long suggestTargetSize(long sizeEstimate) {
        long est = sizeEstimate / ((long) LEAF_TARGET);
        return est > 0 ? est : 1;
    }

    protected final long getTargetSize(long sizeEstimate) {
        long j = this.targetSize;
        long s = j;
        if (j != 0) {
            return s;
        }
        j = suggestTargetSize(sizeEstimate);
        this.targetSize = j;
        return j;
    }

    public R getRawResult() {
        return this.localResult;
    }

    protected void setRawResult(R result) {
        if (result != null) {
            throw new IllegalStateException();
        }
    }

    protected R getLocalResult() {
        return this.localResult;
    }

    protected void setLocalResult(R localResult) {
        this.localResult = localResult;
    }

    protected boolean isLeaf() {
        return this.leftChild == null;
    }

    protected boolean isRoot() {
        return getParent() == null;
    }

    protected K getParent() {
        return (AbstractTask) getCompleter();
    }

    public void compute() {
        Spliterator<P_IN> rs = this.spliterator;
        long sizeEstimate = rs.estimateSize();
        long sizeThreshold = getTargetSize(sizeEstimate);
        boolean forkRight = false;
        Spliterator<P_IN> rs2 = rs;
        K task = this;
        while (sizeEstimate > sizeThreshold) {
            Spliterator<P_IN> trySplit = rs2.trySplit();
            Spliterator<P_IN> ls = trySplit;
            if (trySplit == null) {
                break;
            }
            K makeChild = task.makeChild(ls);
            K leftChild = makeChild;
            task.leftChild = makeChild;
            makeChild = task.makeChild(rs2);
            K rightChild = makeChild;
            task.rightChild = makeChild;
            task.setPendingCount(1);
            if (forkRight) {
                forkRight = false;
                rs2 = ls;
                task = leftChild;
                makeChild = rightChild;
            } else {
                forkRight = true;
                task = rightChild;
                makeChild = leftChild;
            }
            makeChild.fork();
            sizeEstimate = rs2.estimateSize();
        }
        task.setLocalResult(task.doLeaf());
        task.tryComplete();
    }

    public void onCompletion(CountedCompleter<?> countedCompleter) {
        this.spliterator = null;
        this.rightChild = null;
        this.leftChild = null;
    }

    protected boolean isLeftmostNode() {
        K node = this;
        while (node != null) {
            K parent = node.getParent();
            if (parent != null && parent.leftChild != node) {
                return false;
            }
            node = parent;
        }
        return true;
    }
}
