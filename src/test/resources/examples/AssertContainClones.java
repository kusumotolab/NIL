class AssertContainClones {
    int method1() {
        if (dst == null)
            throw new NullPointerException();
        if (position < 0)
            throw new IllegalArgumentException("Negative position");
        if (!readable)
            throw new NonReadableChannelException();
        ensureOpen();
        int n = 0;
        int ti = -1;
        try {
            begin();
            if (!isOpen())
                return -1;
            ti = threads.add();
            do {
                n = IOUtil.read(fd, dst, position, nd, positionLock);
            }
            while ((n == IOStatus.INTERRUPTED) && isOpen());
            return IOStatus.normalize(n);
        } finally {
            threads.remove(ti);
            end(n > 0);
            assert IOStatus.check(n); // assert here
        }
    }

    // Type-1 clone
    int method2() {
        if (dst == null)
            throw new NullPointerException();
        if (position < 0)
            throw new IllegalArgumentException("Negative position");
        if (!readable)
            throw new NonReadableChannelException();
        ensureOpen();
        int n = 0;
        int ti = -1;
        try {
            begin();
            if (!isOpen())
                return -1;
            ti = threads.add();
            do {
                n = IOUtil.read(fd, dst, position, nd, positionLock);
            }
            while ((n == IOStatus.INTERRUPTED) && isOpen());
            return IOStatus.normalize(n);
        } finally {
            threads.remove(ti);
            end(n > 0);
            assert IOStatus.check(n);
        }
    }
}
