package store;

import store.BaseDirectory;

/**
 * Created by CAndres on 5/11/2016.
 */
public abstract class RAMDirectory extends BaseDirectory {

    public RAMDirectory(LockFactory lockFactory) {
        super(lockFactory);
    }
}
