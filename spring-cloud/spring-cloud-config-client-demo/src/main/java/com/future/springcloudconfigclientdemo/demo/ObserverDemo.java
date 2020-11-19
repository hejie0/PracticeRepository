package com.future.springcloudconfigclientdemo.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ObserverDemo {

    private static Logger log = LoggerFactory.getLogger(ObserverDemo.class);

    public static void main(String[] args) {
        MyObservable observable = new MyObservable();

        observable.addObserver(new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                log.info("{}", arg);
            }
        });

        observable.addObserver((o, arg) -> {
            log.info("{}", arg);
        });

        observable.setChanged();
        //发布者通知，订阅者是被动感知（推模式）
        observable.notifyObservers();

        echoIterator();
    }

    private static void echoIterator() {
        List<Integer> values = Arrays.asList(1, 2, 3);
        Iterator<Integer> integerIterator = values.iterator();
        while (integerIterator.hasNext()){ //通过循环，主动去获取
            log.info("{}", integerIterator.next());
        }
    }

    static class MyObservable extends Observable {
        @Override
        protected synchronized void setChanged() {
            super.setChanged();
        }
    }
}
