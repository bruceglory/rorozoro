package com.cczero.rorozoro.test;

import com.cczero.rorozoro.resource.ResourceManager;
import io.netty.util.Recycler;
import io.netty.util.concurrent.FastThreadLocalThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LearnTest {
    static Logger logger = LoggerFactory.getLogger(LearnTest.class);

    private static final Recycler<User> recycler = new Recycler<User>() {


        @Override
        protected User newObject(Handle<User> handle) {
            return new User(handle);
        }
    };

    private static final Recycler<User> recycler2 = new Recycler<User>() {
        @Override
        protected User newObject(Handle<User> handle) {
            return new User(handle);
        }
    };

    private static class User {
        private Recycler.Handle<User> handle;

        public User(Recycler.Handle<User> handle) {
            this.handle = handle;
        }

        public void recycle() {
            handle.recycle(this);
        }

    }
    public static void main(String[] args) {
        FastThreadLocalThread task = new FastThreadLocalThread() {
            @Override
            public void run() {
                logger.error("task-thread-name"+Thread.currentThread().getName());
                User user = recycler.get();
                User user2 = recycler.get();


                System.out.println(user == user2);
                user.recycle();
                user2.recycle();

                User user1 = recycler.get();
                System.out.println(user == user1);
            }
        };
        task.start();
    }
}
